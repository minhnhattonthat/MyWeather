package com.nhatton.myweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

public class CityListActivity extends AppCompatActivity {

    public static final String CITY_LIST_KEY = "city_list";
    private CityListAdapter cityListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        ListView cityList = findViewById(R.id.city_list);

        ArrayList<String> selectedCities = new ArrayList<>();
        selectedCities.add("Hanoi");
        selectedCities.add("Melbourne");

        cityListAdapter = new CityListAdapter(this, selectedCities);

        cityList.setAdapter(cityListAdapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent();
        intent.putExtra(CITY_LIST_KEY, cityListAdapter.getCheckedList());
        setResult(RESULT_OK, intent);
        finish();
        return true;
    }
}
