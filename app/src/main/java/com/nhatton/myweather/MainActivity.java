package com.nhatton.myweather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import static com.nhatton.myweather.CityListActivity.CITY_LIST_KEY;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_GET_LIST = 5;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private RecyclerView weatherListView;
    private WeatherAdapter weatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            weatherListView = findViewById(R.id.weather_list);

            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, CityListActivity.class);
                    startActivityForResult(intent, REQUEST_GET_LIST);
                }
            });

            weatherAdapter = new WeatherAdapter(2);
            weatherListView.setAdapter(weatherAdapter);

            WeatherLoader loader = new WeatherLoader(weatherAdapter, this);
            ArrayList<String> selectedCities = new ArrayList<>();
            selectedCities.add("Hanoi");
            selectedCities.add("Melbourne");
            loader.load(selectedCities);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GET_LIST && resultCode == RESULT_OK) {
            ArrayList<String> list = data.getStringArrayListExtra(CITY_LIST_KEY);
            for (final String city : list) {

                Thread t = new Thread() {
                    @Override
                    public void run() {
                        String result = WeatherAPI.getWeatherByCity(city);
                        Log.d(TAG, city + ": " + result);
                    }
                };

                t.start();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            recreate();
        }
    }

    private void checkListEmptyAndLoad() {
        boolean isEmpty = weatherListView.getAdapter().getItemCount() == 0;
        if (isEmpty) {
            weatherListView.setVisibility(View.GONE);
        } else {
            weatherListView.setVisibility(View.VISIBLE);
            WeatherLoader loader = new WeatherLoader(weatherAdapter, this);
            ArrayList<String> selectedCities = new ArrayList<>();
            selectedCities.add("Hanoi");
            selectedCities.add("Melbourne");
            loader.load(selectedCities);
        }
    }

    private void fetchData() {

    }

    private void loadFromDb() {

    }
}
