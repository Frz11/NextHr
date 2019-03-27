package com.example.cristianion.nexthr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Location;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.UUID;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;

public class AddLocationActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        final TextView street = findViewById(R.id.streetInput);
        final TextView city = findViewById(R.id.cityInput);
        final TextView postalCode = findViewById(R.id.postalInput);
        final TextView county = findViewById(R.id.countyInput);
        final TextView country = findViewById(R.id.countryInput);
        final View addLocationView = findViewById(R.id.AddLocationView);
        final ProgressBar progressBar = findViewById(R.id.AddLocationProgress);

        findViewById(R.id.AddLocationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(street.getText().toString().length() == 0){
                    showError(getApplicationContext(),"Street is required!");
                    return;
                }
                if(city.getText().toString().length() == 0){
                    showError(getApplicationContext(),"City is required!");
                    return;
                }
                if(county.getText().toString().length() == 0){
                    showError(getApplicationContext(),"County is required!");
                    return;
                }
                if(country.getText().toString().length() == 0){
                    showError(getApplicationContext(),"Country is required!");
                    return;
                }
                showProgress(true,addLocationView,progressBar);
                Location location = new Location(UUID.randomUUID().toString(),street.getText().toString(),postalCode.getText().toString(),
                        city.getText().toString(),county.getText().toString(),country.getText().toString());
                db.collection("companies").document(currentCompany.id).collection("locations")
                        .document(location.id).set(location).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showProgress(false,addLocationView,progressBar);
                        finish();
                    }
                });
            }
        });
    }
}
