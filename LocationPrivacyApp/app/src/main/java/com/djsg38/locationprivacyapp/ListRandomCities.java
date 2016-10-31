package com.djsg38.locationprivacyapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class ListRandomCities extends AppCompatActivity {

    GenerateNearbyCities cityGen;
    ArrayList<XMLAttributes> randLocs;
    ArrayList<String> cityNames;
    ArrayAdapter<String> arrayAdapter;
    ListView cities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_random_cities);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cities = (ListView) findViewById(R.id.cities);

        cityGen = new GenerateNearbyCities();
        randLocs = cityGen.generateLocations();
        cityNames = new ArrayList<>();

        for(XMLAttributes data : randLocs) {
            cityNames.add(data.getName());
        }

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cityNames);
        cities.setAdapter(arrayAdapter);
    }

}
