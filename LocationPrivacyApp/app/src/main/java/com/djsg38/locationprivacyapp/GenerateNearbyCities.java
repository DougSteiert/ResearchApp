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

public class GenerateNearbyCities {

    ArrayList<XMLAttributes> cityList;
    ArrayList<XMLAttributes> randLocs;
    HandleXML handleXML;
    Random rand;

    public ArrayList<XMLAttributes> generateLocations() {
        String url = "http://api.geonames.org/findNearbyPlaceName?lat=37.951424&lng=-91.768959&radius=150&maxRows=99999&username=dsteiert";
        rand = new Random();
        int randIndex;
        handleXML = new HandleXML();
        cityList = handleXML.fetchXML(url);
        randLocs = new ArrayList<>();

        while(randLocs.size() < 5) {
            randIndex = rand.nextInt(cityList.size());
            if(cityList.get(randIndex).getDistance() >= 100) {
                randLocs.add(cityList.get(randIndex));
            }
        }

        return randLocs;
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
