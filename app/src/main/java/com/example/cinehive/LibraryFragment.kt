package com.example.cinehive

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinehive.adapters.LibraryMovieAdapter
import com.example.cinehive.dataclasses.Movie
import com.example.cinehive.viewmodels.LibraryViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import com.google.gson.Gson
import java.io.File
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.cinehive.data.local.MovieEntity
import com.example.cinehive.data.local.toMovie
import com.google.gson.reflect.TypeToken

class LibraryFragment : Fragment() {
    private val viewModel: LibraryViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibraryMovieAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var exportButton: Button
    private lateinit var importbutton: Button
    private var currentTab = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.library_recycler_view)
        tabLayout = view.findViewById(R.id.tab_layout)
        exportButton = view.findViewById(R.id.export_button)
        importbutton = view.findViewById(R.id.import_button)

        exportButton.setOnClickListener{
            exportMoviesToJson()
        }

        importbutton.setOnClickListener {
            openFilePicker()
        }

        setupRecyclerView()
        setupTabs()
        observeCurrentTabData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                importJsonFromFile(uri)
            }
        }
    }

    private fun importJsonFromFile(uri: Uri) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val jsonString = inputStream?.bufferedReader().use { it?.readText() }

                if (!jsonString.isNullOrEmpty()) {
                    val movies = parseJsonToMovies(jsonString)
                    saveMoviesToDatabase(movies)
                    Toast.makeText(context, "Import successful!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to read file", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error importing file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun parseJsonToMovies(jsonString: String): List<MovieEntity> { // Reading from json to import
        val gson = Gson()
        val listType = object : TypeToken<List<Map<String, Any?>>>() {}.type
        val movieList = gson.fromJson<List<Map<String, Any?>>>(jsonString, listType)

        return movieList.mapNotNull { movieMap ->
            val movieId = (movieMap["id"] as? Double)?.toInt() ?: 0
            if (movieId > 0) {
                MovieEntity(
                    id = movieId,
                    title = movieMap["title"] as String,
                    releaseDate = movieMap["releaseDate"] as String,
                    voteAverage = (movieMap["rating"] as? Double) ?: 0.0,
                    overview = movieMap["overview"] as String,
                    isFavorite = movieMap["isFavorite"] as? Boolean ?: false,
                    isWatched = movieMap["isWatched"] as? Boolean ?: false,
                    posterPath = movieMap["posterPath"] as? String ?: "",
                    backdropPath = movieMap["backdropPath"] as? String ?: "",
                    voteCount = (movieMap["voteCount"] as? Double)?.toInt() ?: 0,
                    rating = (movieMap["ratingScore"] as? Double)?.toInt(),
                    addedDate = (movieMap["addedDate"] as? Double)?.toLong() ?: System.currentTimeMillis()
                )
            } else {
                null
            }
        }
    }

    private suspend fun saveMoviesToDatabase(movies: List<MovieEntity>) {
        for (movie in movies) {
            val existingMovie = viewModel.getMovieById(movie.id)
            if(movie.rating != null){
                viewModel.addMovieRating(movie.toMovie(), movie.isFavorite, movie.isWatched, movie.rating)
            }else{
                viewModel.addMovie(movie.toMovie(), movie.isFavorite, movie.isWatched)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = LibraryMovieAdapter(
            onFavoriteClick = { movieId -> handleFavoriteClick(movieId) },
            onWatchedClick = { movieId -> handleWatchedClick(movieId) },
            onRatingChanged = { movieId, rating -> viewModel.rateMovie(movieId, rating) },
            onMovieClick = { movie -> navigateToMovieDetail(movie) }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun handleFavoriteClick(movieId: Int) {
        lifecycleScope.launch {
            val movieEntity = viewModel.getMovieById(movieId)
            movieEntity?.let { movie ->
                if (!movie.isWatched && movie.rating == null) {
                    // If no other attributes are set, remove the movie entirely
                    viewModel.removeFromLibrary(movieId)
                } else {
                    // Otherwise just toggle the favorite status
                    viewModel.toggleFavorite(movieId)
                }
            }
        }
    }

    private fun handleWatchedClick(movieId: Int) {
        lifecycleScope.launch {
            val movieEntity = viewModel.getMovieById(movieId)
            movieEntity?.let { movie ->
                if (!movie.isFavorite && movie.rating == null) {
                    // If no other attributes are set, remove the movie entirely
                    viewModel.removeFromLibrary(movieId)
                } else {
                    // Otherwise just toggle the watched status
                    viewModel.toggleWatched(movieId)
                }
            }
        }
    }

    private fun navigateToMovieDetail(movie: Movie) {
        val action = LibraryFragmentDirections.actionLibraryFragmentToMovieDetailFragment(movie)
        findNavController().navigate(action)
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"))
        tabLayout.addTab(tabLayout.newTab().setText("Favorites"))
        tabLayout.addTab(tabLayout.newTab().setText("Watched"))
        tabLayout.addTab(tabLayout.newTab().setText("Rated"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.position?.let { position ->
                    if (currentTab != position) {
                        currentTab = position
                        observeCurrentTabData()
                    }
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeCurrentTabData() {
        // Remove any existing observers
        viewModel.allMovies.removeObservers(viewLifecycleOwner)
        viewModel.favoriteMovies.removeObservers(viewLifecycleOwner)
        viewModel.watchedMovies.removeObservers(viewLifecycleOwner)
        viewModel.ratedMovies.removeObservers(viewLifecycleOwner)

        // Observe the appropriate LiveData based on current tab
        when (currentTab) {
            0 -> viewModel.allMovies.observe(viewLifecycleOwner) { movies ->
                adapter.submitList(movies)
            }
            1 -> viewModel.favoriteMovies.observe(viewLifecycleOwner) { movies ->
                adapter.submitList(movies)
            }
            2 -> viewModel.watchedMovies.observe(viewLifecycleOwner) { movies ->
                adapter.submitList(movies)
            }
            3 -> viewModel.ratedMovies.observe(viewLifecycleOwner) { movies ->
                adapter.submitList(movies)
            }
        }
    }

    private fun toJson(data: List<Map<String, Any?>>): String {
        return Gson().toJson(data)
    }

    private fun saveJsonToFile(jsonString: String) {
        val fileName = "FavoriteMovies_${System.currentTimeMillis()}.json"
        val file = File(requireContext().getExternalFilesDir(null), fileName)
        file.writeText(jsonString)

        // Get file URI
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )

        // Create Intent
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Open sharing pop up
        startActivity(Intent.createChooser(shareIntent, "Share JSON File"))
    }

    private fun exportMoviesToJson() { // Exporting database as a json format
        viewModel.getMoviesDirect { movies ->
            if (movies.isNotEmpty()) {
                val jsonArray = mutableListOf<Map<String, Any?>>()

                for (movie in movies) {
                    val movieData = mapOf(
                        "id" to movie.id,
                        "title" to movie.title,
                        "releaseDate" to movie.releaseDate,
                        "rating" to movie.voteAverage,
                        "overview" to movie.overview,
                        "posterPath" to movie.posterPath,
                        "backdropPath" to movie.backdropPath,
                        "voteCount" to movie.voteCount,
                        "ratingScore" to movie.rating,
                        "addedDate" to movie.addedDate,
                        "isWatched" to movie.isWatched,
                        "isFavorite" to movie.isFavorite
                    )
                    jsonArray.add(movieData)
                }

                val jsonString = toJson(jsonArray)
                saveJsonToFile(jsonString)
            } else {
                Toast.makeText(context, "No movies in the database to export!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun openFilePicker() { // Intent used to fick json for import
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
        }
        startActivityForResult(intent, FILE_PICKER_REQUEST_CODE)
    }

    companion object {
        private const val FILE_PICKER_REQUEST_CODE = 123
    }

}