package com.nhatton.myweather.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nhatton.myweather.R;
import com.nhatton.myweather.db.WeatherDbService;
import com.nhatton.myweather.db.model.WeatherDomain;
import com.nhatton.myweather.network.WeatherLoader;
import com.nhatton.myweather.ui.citylist.CityListActivity;

import java.util.ArrayList;

import static com.nhatton.myweather.ui.citylist.CityListActivity.KEY_CITY_LIST;
import static java.lang.System.currentTimeMillis;

public class MainActivity extends AppCompatActivity implements WeatherLoader.WeatherLoadListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_GET_LIST = 5;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private static final long ONE_HOUR = 3600000;

    private WeatherDbService dbAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
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

            weatherAdapter = new WeatherAdapter(0);
            weatherListView.setAdapter(weatherAdapter);

            swipeRefreshLayout = findViewById(R.id.swipe_layout);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    checkListEmpty();
                    refreshData();
                }
            });

            dbAdapter = new WeatherDbService(this).open();

            loadFromDb();

            update();

            checkListEmpty();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GET_LIST && resultCode == RESULT_OK) {
            ArrayList<String> selectedCities = data.getStringArrayListExtra(KEY_CITY_LIST);

            fetchData(selectedCities);
            checkListEmpty();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_reset) {
            dbAdapter.dropTable();
            loadFromDb();
            checkListEmpty();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dbAdapter.close();
        super.onDestroy();
    }

    @Override
    public void onLoadComplete(ArrayList<WeatherDomain> cacheList) {
        ArrayList<WeatherDomain> list = dbAdapter.getAllWeather();
        ArrayList<String> selectedCities = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            selectedCities.add(list.get(i).getCity());
        }

        for (WeatherDomain item : cacheList) {
            if (selectedCities.contains(item.getCity())) {
                dbAdapter.updateWeather(item);
            } else {
                dbAdapter.insertWeather(item);
            }
        }

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoadError(String error) {
        Toast.makeText(this, "Error: " + error, Toast.LENGTH_SHORT).show();
    }

    private void checkListEmpty() {
        boolean isEmpty = weatherListView.getAdapter().getItemCount() == 0;
        weatherListView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void fetchData(ArrayList<String> selectedCities) {
        weatherAdapter = new WeatherAdapter(selectedCities.size());
        weatherListView.setAdapter(weatherAdapter);

        WeatherLoader loader = new WeatherLoader(weatherAdapter, this, this);
        loader.load(selectedCities);

    }

    private void refreshData() {
        WeatherLoader loader = new WeatherLoader(weatherAdapter, this, this);
        loader.load(loadFromDb());
    }

    private ArrayList<String> loadFromDb() {

        ArrayList<WeatherDomain> list = dbAdapter.getAllWeather();

        weatherAdapter = new WeatherAdapter(list.size());
        weatherListView.setAdapter(weatherAdapter);

        ArrayList<String> selectedCities = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            weatherAdapter.updateRow(i, list.get(i));
            selectedCities.add(list.get(i).getCity());
        }

        return selectedCities;
    }

    private void update() {

        ArrayList<WeatherDomain> list = dbAdapter.getAllWeather();

        SparseArray<String> citiesMap = new SparseArray<>();

        for (int i = 0; i < list.size(); i++) {
            if (currentTimeMillis() / 1000 - list.get(i).getTimeStamp() > ONE_HOUR * 3) {
                citiesMap.append(i, list.get(i).getCity());
            }
        }

        WeatherLoader loader = new WeatherLoader(weatherAdapter, this, this);
        loader.load(citiesMap);

    }
}
