package com.djsg38.locationprivacyapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

import com.djsg38.locationprivacyapp.models.Location;
import com.djsg38.locationprivacyapp.models.Session;

import io.realm.RealmList;
import io.realm.Realm;

public class GenerateNearbyCities {

    ArrayList<XMLAttributes> cityList;
    ArrayList<XMLAttributes> randLocs;
    HandleXML handleXML;
    Random rand;

    private Realm realm;
    Session session;

    public ArrayList<XMLAttributes> generateLocations(Integer numberLocs) {
        // obviously this query should change as a person moves...
        String url = "http://api.geonames.org/findNearbyPlaceName?lat=37.951424&lng=-91.768959&radius=150&maxRows=99999&username=dsteiert";
        rand = new Random();
        int randIndex;
        handleXML = new HandleXML();
        cityList = handleXML.fetchXML(url);
        randLocs = new ArrayList<>();

        if(realm == null) realm = Realm.getDefaultInstance();
        if(session == null) session = realm.where(Session.class).findFirst();

        double realDistanceMoved = calculateDistanceBetweenCities(session.getRealLocations());
        Log.i("RealDistance", String.valueOf(realDistanceMoved));

        int mockSize = session.getMockLocations().size();
        Log.i("MockSize", String.valueOf(mockSize));

        // (numberLocs - 1) ensures k-anonymity because your real location added with (k -1) other
        // values gives you k total locations to be hidden in
        while(randLocs.size() < numberLocs) {
            randIndex = rand.nextInt(cityList.size());

            if(cityList.get(randIndex).getDistance() >= 100) {
                //tempLoc can't be null, needs to be a Location of some kind
                Location tempLoc = new Location();
                tempLoc.setLat(cityList.get(randIndex).getLat());
                tempLoc.setLong(cityList.get(randIndex).getLng());
                if(mockSize > 1) {
                    double tempDistance = calculateFakeDistance(tempLoc, session.getMockLocations().get(mockSize - 1));
                    if(tempDistance >= (realDistanceMoved - 2) && tempDistance <= (realDistanceMoved + 2)) {
                        randLocs.add(cityList.get(randIndex));
                    }
                }
                else {
                    randLocs.add(cityList.get(randIndex));
                }
            }
        }

        realm.close();
        realm = null;

        return randLocs;
    }

    public double calculateFakeDistance(Location q, Location p) {
        double distanceTraveled = 0;

        double firstSub = (q.getLat() - p.getLat());
        double secondSub = (q.getLong() - p.getLong());

        double firstPower = Math.pow(firstSub, 2);
        double secondPower = Math.pow(secondSub, 2);

        double addition = firstPower + secondPower;

        distanceTraveled = Math.sqrt(addition);

        return distanceTraveled;
    }

    // Performs the Euclidean distances on the last two known real locations
    public double calculateDistanceBetweenCities(RealmList<Location> locations) {
        double distanceTraveled = 0;

        int sizeofLocations = locations.size();

        if(sizeofLocations > 1) {
            double firstSub = (locations.get(sizeofLocations - 2).getLat() - locations.get(sizeofLocations - 1).getLat());
            double secondSub = (locations.get(sizeofLocations - 2).getLong() - locations.get(sizeofLocations - 1).getLong());

            double firstPower = Math.pow(firstSub, 2);
            double secondPower = Math.pow(secondSub, 2);

            double addition = firstPower + secondPower;

            distanceTraveled = Math.sqrt(addition);

            return distanceTraveled;
        }

        return 0;
    }

    public class HandleXML {
        public ArrayList<XMLAttributes> cityList;

        private XmlPullParserFactory xmlFactoryObject;

        public void parseXML(XmlPullParser parser) {
            int event;
            String text = null;

            XMLAttributes temp = new XMLAttributes();

            try {
                event = parser.getEventType();

                while (event != XmlPullParser.END_DOCUMENT) {
                    String tagName = parser.getName();

                    switch (event) {
                        case XmlPullParser.START_TAG:
                            break;
                        case XmlPullParser.TEXT:
                            text = parser.getText();
                            break;
                        case XmlPullParser.END_TAG:
                            if (tagName.equals("topnonymName")) {
                                temp.toponymName = text;
                            } else if (tagName.equals("name")) {
                                temp.name = text;
                            } else if (tagName.equals("lat")) {
                                temp.lat = Double.valueOf(text);
                            } else if (tagName.equals("lng")) {
                                temp.lng = Double.valueOf(text);
                            } else if (tagName.equals("geonameId")) {
                                temp.geonameId = text;
                            } else if (tagName.equals("countryCode")) {
                                temp.countryCode = text;
                            } else if (tagName.equals("countryName")) {
                                temp.countryName = text;
                            } else if (tagName.equals("fcl")) {
                                temp.fcl = text;
                            } else if (tagName.equals("fcode")) {
                                temp.fcode = text;
                            } else if (tagName.equals("distance")) {
                                temp.distance = Double.valueOf(text);
                            } else if (tagName.equals("geoname")) {
                                cityList.add(temp);
                                temp = new XMLAttributes();
                            }
                    }
                    event = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public class FetchXMLTask extends AsyncTask<String, Void, ArrayList<XMLAttributes>> {

            @Override
            protected ArrayList<XMLAttributes> doInBackground(String... params) {
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(params[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = urlConnection.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = xmlFactoryObject.newPullParser();

                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(in, null);

                    parseXML(parser);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return cityList;
            }
        }

        public ArrayList<XMLAttributes> fetchXML(final String urlString) {
            FetchXMLTask task = new FetchXMLTask();
            cityList = new ArrayList<>();

            try {
                cityList = task.execute(urlString).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return cityList;
        }

    }
}
