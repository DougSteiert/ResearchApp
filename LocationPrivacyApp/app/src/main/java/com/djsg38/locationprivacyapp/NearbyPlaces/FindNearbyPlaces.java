package com.djsg38.locationprivacyapp.NearbyPlaces;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import com.djsg38.locationprivacyapp.NearbyPlaces.PlaceAttributes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FindNearbyPlaces {
    ArrayList<PlaceAttributes> nearbyPlaces;

    public ArrayList<PlaceAttributes> findNearbyPlaces(Location location, String type /*List<String> typeStrings*/) throws ExecutionException, InterruptedException {
        String lat = String.valueOf(location.getLatitude());
        String lng = String.valueOf(location.getLongitude());
        String typesURL = "";
        /*String lastElement = typeStrings.get(typeStrings.size() - 1);
        for(String type : typeStrings) {
            if(type == lastElement) {
                typesURL += type;
            }
            else {
                typesURL += type + "|";
            }
        }*/
        typesURL += type;
        Log.i("TypesURL", typesURL);
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lng+"&radius=2000&type=" + typesURL + "&key=AIzaSyBVm17USBwIZQv24PfiIlvf4TOtXdD89Gg";

        nearbyPlaces = new ArrayList<>();

        String result = new GetPlaces().execute(url).get();
        nearbyPlaces = parseJson(result);

        Log.i("SizeOf", String.valueOf(nearbyPlaces.size()));
        return nearbyPlaces;
    }

    private class GetPlaces extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder builder = new StringBuilder();
                String line = "";

                while((line = reader.readLine()) != null) {
                    builder.append(line + "\n");
                }

                return builder.toString();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    ArrayList<PlaceAttributes> parseJson(String json) {
        try {
            JSONObject resultObject = new JSONObject(json);
            JSONArray placesArray = resultObject.getJSONArray("results");

            nearbyPlaces = new ArrayList<>();

            for(int p = 0; p < placesArray.length(); p++) {
                try {
                    PlaceAttributes attributes = new PlaceAttributes();
                    // Get place at this index
                    JSONObject placeObject = placesArray.getJSONObject(p);

                    // Get location information
                    JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");

                    attributes.lat = Double.valueOf(loc.getString("lat"));
                    //Log.i("Lat: ", loc.getString("lat"));
                    attributes.lng = Double.valueOf(loc.getString("lng"));
                    //Log.i("Lng: ", loc.getString("lng"));
                    attributes.name = placeObject.getString("name");
                    //Log.i("Name: ", placeObject.getString("name"));

                    nearbyPlaces.add(attributes);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return nearbyPlaces;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
