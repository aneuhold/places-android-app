# Places Android App

## Setup

To run the application:

1. Clone the repo
1. Start up the JSON RPC server by running the following in its own terminal: `cd PlaceJsonRPCServer` then `ant execute.place.server`
1. Add a `keys.xml` file in `app/src/main/res/values/keys.xml` with it's own string API key for Google Maps. See below for an example.
1. Run the application through Android Studio or a device locally

### Example `keys.xml` file

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <string name="google_maps_api_key">InsertYourKeyHere</string>
</resources>
```

## Configuration

To change the server location, you can edit the `strings.xml` file where it says `default_url_string`.

## Usage

When editing a place, make sure to press "Done" at the bottom of the screen in order for the place data to save. Pressing the back arrow assumes a cancellation of the edits.

Syncing:

- When the app first starts up, it syncs with the remote JSON RPC server. This sync will take any places that have a name that isn't already in the SQL database, and copy those over with details. It will also take any names that are held locally and copy those over to the remote server with details. Any details that are updated on places with names that exist on both the remote server and the local database are not reflected. This is because the remote server doesn't have a way to track time of commit. So the "newness" of the data can't be tracked. To make up for this, the data can be pushed or pulled manually. See below:
- When pressing "Sync JSON Server to Local DB", all of the local data is cleared, and all of the information is pulled over from the JSON server.
- When pressing "Sync Local DB to JSON Server", all of the remote data is cleared and all of the information is pushed from the local data to the server.

The map can be used by pressing the "Map" option in the menu.

## Screenshots

<img src="https://i.imgur.com/EauNojW.png" alt="Places Android App Screenshot - Main Menu" width="400" />

<img src="https://i.imgur.com/GVsjJCO.png" alt="Places Android App Screenshot - Menu" width="400" />

<img src="https://i.imgur.com/FjA7zo8.png" alt="Places Android App Screenshot - Menu" width="400" />

<img src="https://i.imgur.com/DxLGxRT.png" alt="Places Android App Screenshot - Menu" width="400" />

<img src="https://i.imgur.com/31A8s26.png" alt="Places Android App Screenshot - Menu" width="400" />
