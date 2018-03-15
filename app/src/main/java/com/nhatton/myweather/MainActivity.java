package com.nhatton.myweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import static com.nhatton.myweather.CityListActivity.CITY_LIST_KEY;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_GET_LIST = 5;

    private RecyclerView weatherListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherListView = findViewById(R.id.weather_list);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CityListActivity.class);
                startActivityForResult(intent, REQUEST_GET_LIST);
            }
        });

        WeatherAdapter adapter = new WeatherAdapter(2);
        weatherListView.setAdapter(adapter);

        WeatherLoader loader = new WeatherLoader(adapter, this);
        ArrayList<String> selectedCities = new ArrayList<>();
        selectedCities.add("Hanoi");
        selectedCities.add("Melbourne");
        loader.load(selectedCities);

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

    private void checkListEmpty() {
        boolean isEmpty = weatherListView.getAdapter().getItemCount() == 0;
        weatherListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }
}
