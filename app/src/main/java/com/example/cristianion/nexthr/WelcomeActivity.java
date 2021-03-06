package com.example.cristianion.nexthr;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristianion.nexthr.Models.Company;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WelcomeActivity extends AppCompatActivity {

    private List<Company> companies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //FirebaseApp.initializeApp();
        
        companies = new ArrayList<>();
        final ConstraintLayout main = findViewById(R.id.MainLayout);
        final ConstraintLayout newCompany = findViewById(R.id.newCompanyLayout);
        Button registerCompanyButton =  findViewById(R.id.buttonAddCompany);
        registerCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.GONE);
                newCompany.setVisibility(View.VISIBLE);

            }
        });
        Button login = findViewById(R.id.buttonLoginEmployee);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this,LoginNewActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button companySubmitButton = findViewById(R.id.buttonRegisterCompany);
        Button backButton1 = findViewById(R.id.buttonBackToMain1);

        companySubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView companyNameView = findViewById(R.id.companyName);
                String companyName = companyNameView.getText().toString();
                if(companyName.length()== 0){
                    Toast.makeText(getApplicationContext(),"Company name cannot be empty!",Toast.LENGTH_LONG).show();
                    return;
                }
                final Company company = new Company(UUID.randomUUID().toString(),companyName);
                Intent intent = new Intent(WelcomeActivity.this, FirstAdminActivity.class);
                intent.putExtra("companyId",company.id);
                intent.putExtra("companyName",company.name);
                startActivity(intent);
                finish();


            }
        });

        backButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.setVisibility(View.VISIBLE);
                newCompany.setVisibility(View.GONE);

            }
        });

    }
}
