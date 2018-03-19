package com.nhatton.myweather.ui.citylist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.nhatton.myweather.R;
import com.nhatton.myweather.db.WeatherDbService;
import com.nhatton.myweather.db.model.WeatherDomain;

import java.util.ArrayList;

public class CityListActivity extends AppCompatActivity {

    public static final String KEY_CITY_LIST = "city_list";

    private WeatherDbService dbAdapter;

    private CityListAdapter cityListAdapter;

    private ArrayList<String> selectedCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        ListView cityList = findViewById(R.id.city_list);

        dbAdapter = new WeatherDbService(this).open();
        ArrayList<WeatherDomain> list = dbAdapter.getAllWeather();
        selectedCities = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            selectedCities.add(list.get(i).getCity());
        }
        dbAdapter.close();

        cityListAdapter = new CityListAdapter(this, selectedCities);
        cityList.setAdapter(cityListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_city_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            Intent intent = new Intent();
            intent.putExtra(KEY_CITY_LIST, cityListAdapter.getCheckedList());
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
