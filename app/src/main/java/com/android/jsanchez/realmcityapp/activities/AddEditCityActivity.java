package com.android.jsanchez.realmcityapp.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.jsanchez.realmcityapp.R;
import com.android.jsanchez.realmcityapp.models.City;
import com.squareup.picasso.Picasso;

import io.realm.Realm;

public class AddEditCityActivity extends AppCompatActivity {

    private int cityId;
    private boolean isCreation;

    private City city;
    private Realm realm;

    private EditText editTextCityName;
    private EditText editTextCityDescription;
    private EditText editTextCityLink;
    private ImageView cityImage;
    private Button btnPreview;
    private FloatingActionButton fab;
    private RatingBar ratingBarCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_city);

        realm = Realm.getDefaultInstance();
        bindUIReferences();

        // Comprobar si va a ser una acción para editar o para creación
        if (getIntent().getExtras() != null) {
            cityId = getIntent().getExtras().getInt("id");
            isCreation = false;
        } else {
            isCreation = true;
        }

        setActivityTitle();

        if (!isCreation) {
            city = getCityById(cityId);
            bindDataToFields();
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEditNewCity();
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = editTextCityLink.getText().toString();
                if (link.length() > 0)
                    loadImageLinkForPreview(editTextCityLink.getText().toString());
            }
        });

    }

    private void bindUIReferences() {
        editTextCityName = findViewById(R.id.editTextCityName);
        editTextCityDescription = findViewById(R.id.editTextCityDescription);
        editTextCityLink = findViewById(R.id.editTextCityImage);
        cityImage = findViewById(R.id.imageViewPreview);
        btnPreview = findViewById(R.id.buttonPreview);
        fab = findViewById(R.id.FABEditCity);
        ratingBarCity = findViewById(R.id.ratingBarCity);
    }

    private void setActivityTitle() {
        String title = "Edit City";
        if (isCreation) title = "Create New City";
        setTitle(title);
    }

    private City getCityById(int cityId) {
        return realm.where(City.class).equalTo("id", cityId).findFirst();
    }

    private void bindDataToFields() {
        editTextCityName.setText(city.getName());
        editTextCityDescription.setText(city.getDescription());
        editTextCityLink.setText(city.getImage());
        loadImageLinkForPreview(city.getImage());
        ratingBarCity.setRating(city.getStars());
    }

    private void loadImageLinkForPreview(String link) {
        Picasso.with(this).load(link).fit().into(cityImage);
    }

    private boolean isValidDataForNewCity() {
        if (editTextCityName.getText().toString().length() > 0 &&
                editTextCityDescription.getText().toString().length() > 0 &&
                editTextCityLink.getText().toString().length() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(AddEditCityActivity.this, CityActivity.class);
        startActivity(intent);
    }


    private void addEditNewCity() {
        if (isValidDataForNewCity()) {
            String name = editTextCityName.getText().toString();
            String description = editTextCityDescription.getText().toString();
            String link = editTextCityLink.getText().toString();
            float stars = ratingBarCity.getRating();

            City city = new City(name, description, link, stars);
            // En caso de que sea una edición en vez de creación pasamos el ID
            if (!isCreation) city.setId(cityId);

            realm.beginTransaction();
            realm.copyToRealmOrUpdate(city);
            realm.commitTransaction();

            goToMainActivity();
        } else {
            Toast.makeText(this, "The data is not valid, please check the fields again", Toast.LENGTH_SHORT).show();
        }
    }
}

