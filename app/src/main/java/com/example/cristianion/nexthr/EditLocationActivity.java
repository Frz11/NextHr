package com.example.cristianion.nexthr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Location;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class EditLocationActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_location);
        super.onCreate(savedInstanceState);
        setTitle(R.string.edit_location);
        final TextView streetInput = findViewById(R.id.streetInput);
        final TextView cityInput = findViewById(R.id.cityInput);
        final TextView postalCodeInput = findViewById(R.id.postalInput);
        final TextView countryInput = findViewById(R.id.countryInput);
        final TextView countyInput = findViewById(R.id.countyInput);
        final Button saveButton = findViewById(R.id.SaveLocationButton);
        final RelativeLayout editForm = findViewById(R.id.editLocationForm);
        final ProgressBar progressBar = findViewById(R.id.editLocationProgress);
        showProgress(true,editForm,progressBar);
        String locationId = getIntent().getStringExtra("locationId");
        db.collection("companies").document(currentCompany.id).collection("locations")
                .document(locationId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    final Location currentLocation = Objects.requireNonNull(task.getResult()).toObject(Location.class);
                    if(currentLocation != null) {
                        streetInput.setText(currentLocation.street);
                        cityInput.setText(currentLocation.city);
                        postalCodeInput.setText(currentLocation.postalCode);
                        countryInput.setText(currentLocation.country);
                        countyInput.setText(currentLocation.county);
                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                closeKeyboard();
                                if(streetInput.getText().toString().isEmpty()){
                                    showSnackbarError(editForm,"Street is required");
                                    return;
                                }
                                if(cityInput.getText().toString().isEmpty()){
                                    showSnackbarError(editForm,"City is required!");
                                    return;
                                }
                                if(countryInput.getText().toString().isEmpty()){
                                    showSnackbarError(editForm,"Country is required!");
                                    return;
                                }
                                if(countyInput.getText().toString().isEmpty()){
                                    showSnackbarError(editForm,"County is required!");
                                    return;
                                }
                                showProgress(true, editForm, progressBar);

                                currentLocation.street = streetInput.getText().toString();
                                currentLocation.city = cityInput.getText().toString();
                                currentLocation.postalCode = postalCodeInput.getText().toString();
                                currentLocation.country = countryInput.getText().toString();
                                currentLocation.county = countyInput.getText().toString();
                                db.collection("companies").document(currentCompany.id)
                                        .collection("locations").document(currentLocation.id)
                                        .set(currentLocation).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        finish();
                                    }
                                });
                            }
                        });
                        showProgress(false,editForm,progressBar);
                    } else {
                        showProgress(false,editForm,progressBar);
                        showError(getApplicationContext(),getString(R.string.dbError));
                    }

                } else {
                    showProgress(false,editForm,progressBar);
                    showError(getApplicationContext(),getString(R.string.dbError));
                }

            }
        });


    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
