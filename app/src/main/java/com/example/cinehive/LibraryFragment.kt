package com.example.cinehive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cinehive.adapters.LibraryMovieAdapter
import com.example.cinehive.viewmodels.LibraryViewModel
import com.google.android.material.tabs.TabLayout

class LibraryFragment : Fragment() {
    private val viewModel: LibraryViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LibraryMovieAdapter
    private lateinit var tabLayout: TabLayout

    // Keep track of current tab to avoid resubscribing unnecessarily
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

        setupRecyclerView()
        setupTabs()

        // Start observing the initial tab's data
        observeCurrentTabData()
    }

    private fun setupRecyclerView() {
        adapter = LibraryMovieAdapter(
            onFavoriteClick = { movieId -> viewModel.toggleFavorite(movieId) },
            onWatchedClick = { movieId -> viewModel.toggleWatched(movieId) },
            onRatingChanged = { movieId, rating -> viewModel.rateMovie(movieId, rating) }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
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

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove observers when the view is destroyed
        viewModel.allMovies.removeObservers(viewLifecycleOwner)
        viewModel.favoriteMovies.removeObservers(viewLifecycleOwner)
        viewModel.watchedMovies.removeObservers(viewLifecycleOwner)
        viewModel.ratedMovies.removeObservers(viewLifecycleOwner)
    }
}