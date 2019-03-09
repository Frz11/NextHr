package com.example.cristianion.nexthr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Company;
import com.example.cristianion.nexthr.Models.Employee;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.cristianion.nexthr.Utils.AESUtils.encrypt;
import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;

public class LoginActivity extends AppCompatActivity {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("companies");
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() == null){
            db.orderByChild("employees/id").equalTo("").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,WelcomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String company = ((TextView)findViewById(R.id.Company)).getText().toString();
                final String email = ((TextView)findViewById(R.id.Email)).getText().toString();
                final String password = ((TextView) findViewById(R.id.Password)).getText().toString();
                if(company.length() == 0){
                    showError(getApplicationContext(),"Company name is required!");
                    return;
                }
                if(email.length() == 0){
                    showError(getApplicationContext(), "Email is required!");
                    return;
                }
                if(password.length() == 0){
                    showError(getApplicationContext(), "Password is required!");
                    return;
                }
                /*
                db.orderByChild("name").equalTo(company).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            Log.wtf("wtf","wtf2");
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                                Company current = snapshot.getValue(Company.class);
                               for(DataSnapshot employees :  snapshot.child("employees").getChildren() ){

                                   Employee employee = employees.getValue(Employee.class);
                                   Log.w("employee",employee.email+" "+employee.password);
                                   try {
                                       if(employee.password.equals(encrypt(password)) && employee.email.equals(email)){
                                           Log.wtf("wtf","wtf");
                                           currentCompany = current;
                                           currentEmployee = employee;
                                           Intent intent = new Intent(LoginActivity.this,MenuActivity.class);
                                           startActivity(intent);
                                           finish();
                                           return;
                                       }
                                   } catch (Exception e) {
                                       e.printStackTrace();
                                   }
                               }
                               showError(getApplicationContext(),"Incorrect email or password!");
                               return;

                            }
                        } else {
                            showError(getApplicationContext(),"No company found!");
                            return;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/
            }

        });

    }
}
