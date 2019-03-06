package com.example.cristianion.nexthr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristianion.nexthr.Models.Company;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Models.Role;
import com.example.cristianion.nexthr.Models.UserRole;
import com.example.cristianion.nexthr.Utils.Global;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import static com.example.cristianion.nexthr.Utils.UtilFunc.isEmailValid;
import static com.example.cristianion.nexthr.Utils.UtilFunc.isValidDate;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;

public class FirstAdminActivity extends AppCompatActivity {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("companies");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_admin);
        final Company company = new Company(getIntent().getStringExtra("companyId"),getIntent().getStringExtra("companyName"));
        Button firstAdminButton = findViewById(R.id.firstAdminButton);
        firstAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String lastName = ((TextView)findViewById(R.id.LastName)).getText().toString();
                final String firstName = ((TextView)findViewById(R.id.FirstName)).getText().toString();
                final String email = ((TextView)findViewById(R.id.Email)).getText().toString();
                final String birthday = ((TextView)findViewById(R.id.Birthday)).getText().toString();
                final String password = ((TextView)findViewById(R.id.Password)).getText().toString();
                String confirmPassword = ((TextView)findViewById(R.id.ConfirmPassword)).getText().toString();
                final String phone = ((TextView)findViewById(R.id.Phone)).getText().toString();

                if(lastName.length() == 0){
                    showError(getApplicationContext(),"Last name is required!");
                    return;
                }
                if(firstName.length() == 0){
                    showError(getApplicationContext(),"First name is required!");
                    return;
                }
                if(email.length() == 0){
                    showError(getApplicationContext(),"Email is required!");
                    return;
                }
                if(!isEmailValid(email)){
                    showError(getApplicationContext(),"Invalid email address");
                    return;
                }
                if(phone.length() == 0){
                    showError(getApplicationContext(),"Phone is required!");
                    return;
                }
                if(birthday.length() == 0){
                    showError(getApplicationContext(),"Birthday is required!");
                    return;
                }
                if(!isValidDate(birthday)){
                    showError(getApplicationContext(), "Birthday is an invalid date!");
                    return;
                }
                if(password.length() == 0){
                    showError(getApplicationContext(),"Password is required");
                    return;
                }
                if(!password.equals(confirmPassword)){
                    showError(getApplicationContext(),"Passwords don't match");
                    return;
                }
                //add admin role

                mDatabase.child(company.id).setValue(company).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //create admin role
                        final Role adminRole = new Role(UUID.randomUUID().toString(),"Admin");
                        mDatabase.child(company.id).child("roles").child(adminRole.id).setValue(adminRole).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                final Employee admin = new Employee(UUID.randomUUID().toString(),lastName,firstName,birthday,password,email,phone);
                                mDatabase.child(company.id).child("employees").child(admin.id).setValue(admin).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mDatabase.child(company.id).child("user_role").child(UUID.randomUUID().toString()).setValue(new UserRole(admin.id,adminRole.id));
                                        Toast.makeText(getApplicationContext(),"Log in with your company name and the admin account!",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(FirstAdminActivity.this,WelcomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }
                        });
                    }
                });

            }
        });

    }
}
