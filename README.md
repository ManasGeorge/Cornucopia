# Cornucopia v1.0

## Release Notes

### v1.0

 * Pantry with integrated grocery list
 * Recipe discovery and recommendation system
 
## Installation Guide

### Android Application
Pre-requisites - [Android Studio](https://developer.android.com/studio/index.html)
Dependent libraries - All libraries are bundled with Android Studio
Download instructions - clone the GitHub repository
Build instructions - open "Cornucopia_App" in Android Studio
Installation of actual application - the Android app can be built directly to a mobile phone using Android Studio
Run instructions - After installation, all the user needs to do the launch the app is tap the icon

### Backend Server
Pre-requisites - heroku, python-2.7.13, pip packages listed in requirements.txt (`pip install -r requirements.txt`)
Download instructions - clone the Github repository
Build instructions - open "cornucopia_backend" in a terminal window
Run instructions - a `run.sh` script is provided, which can be used for local testing. The `load_fixtures.sh` script populates the database with items from the `api/fixtures` folder, and the `load_fixtures_heroku.sh` does the same on the heroku instance.
Deploy instructions - `push_heroku.sh` in the root directory should deploy the latest version of the backend codebase to heroku. [Instructions here](https://github.com/heroku/python-getting-started) to be followed for additional guidance.
Adding more data - The database is currently populated by items from `api/fixtures` for ease of development and changing schemas quickly. The `fixtures` folder contains `json` files for each of the models, containing sample data items to be loaded into the DB. This is the place to put new data items currently, and will be the case until the final DB population is finished.

