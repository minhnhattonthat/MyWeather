package com.nhatton.myweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

public class CityListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_list);

        ListView cityList = findViewById(R.id.city_list);

        CityListAdapter cityListAdapter = new CityListAdapter(this);

        cityList.setAdapter(cityListAdapter);

    }
}
