# CineHive

CineHive is a brand new Android application that allows you to explore, manage, and interact with real-time movie library. With features like movie categorization, JSON-based import/export functionality, and seamless navigation, CineHive provides an intuitive experience for movie enthusiasts to organize and enjoy their collections.

## Features

### Movie Library Management
- **Tabbed Interface**: Organize movies into categories:
  - All Movies
  - Favorites
  - Watched
  - Rated
- **Movie Details**: View detailed information about each movie, including title, release date, overview, and user ratings.

### JSON Import/Export
- **Export Movies**: Export the entire movie library to a JSON file for backup or sharing.
- **Import Movies**: Import movie data from a shared JSON file and update the local database, including all categories (favorites, watched, and rated).


### User Interface
- **RecyclerView**: Displays movies dynamically with an elegant list layout.
- **TabLayout**: Quickly switch between movie categories.
- **Buttons**: Export and import buttons for seamless JSON file management.

## Requirements
- Android Studio is needed to view the project.
- One emulator must be installed in Android Studio.

## Project Structure

### Core Components

#### `LibraryFragment.kt`
- Handles the main logic for managing and displaying movies.
- Includes methods for importing/exporting JSON, interacting with the RecyclerView, and toggling movie statuses.

#### `HomeFragment.kt`
- Manages the home screen of the application, showcasing trending, top-rated, popular, and upcoming movies.
- Uses RecyclerView with HomeMovieAdapter for dynamic data binding and smooth scrolling behavior.

#### `fragment_library.xml`
- Defines the UI layout for the movie library screen, including the TabLayout, RecyclerView, and buttons.

#### `LibraryViewModel.kt`
- Connects the UI with the repository to fetch and update movie data using LiveData and coroutines.

#### `LibraryRepository.kt`
- Manages database operations by interacting with `MovieDao`.
- Provides methods to fetch, update, and manipulate movie data.

#### `MovieDao.kt`
- Defines SQL queries and database operations using Room annotations.
- Supports fetching movies by categories and general database operations.

#### `MovieEntity.kt`
- Represents the movie data model in the Room database.
- Includes fields for movie attributes like `isFavorite`, `isWatched`, and `rating`.

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/EvenBaba/CineHive.git
   ```
2. Open the project in Android Studio.
3. Build and run the project on an emulator or connected device.

## How To Use

### Manage Movies
- Use the tabs to switch between categories.
- Tap a movie to view its details.
- Use action buttons to rate, favorite or mark a movie as watched.

### Exporting/Importing Movies
1. Press the "Export" button in the Library page lets you share the local database as a json format.
2. Press the "Import" button to choose the json file to import shared datase to the local machine.

## Contributors
- Furkan Ozdemir (Made connection with Api, MovieDetailFragment)
- Gokalp Sagnak (Viewing upcoming movies, several bug fixings)
- Vahit Eren Pinar (Import/Export functionality, rating movies)
- Selim Sandal (Contributed UI, SearchView, updated LibraryFragment)


Feel free to reach out with any feedback or suggestions!
We are open to anyone who wants to contribute.


### API Key
`5549363d27271dc02d9b132b11bdc491`

### API Read Access Token
`eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI1NTQ5MzYzZDI3MjcxZGMwMmQ5YjEzMmIxMWJkYzQ5MSIsIm5iZiI6MTczMjc5MjQ0Ny44ODAzOTY4LCJzdWIiOiI2NzQ4NTAxYjhiYjg0YWI4MDhjZjkyOGQiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.P_nMCe9otpAiII_j6rQSHIBI98WZUiQoXTclBjNUfrI`