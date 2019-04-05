package com.example.cristianion.nexthr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cristianion.nexthr.Models.Company;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Models.Role;
import com.example.cristianion.nexthr.Models.UserCompany;
import com.example.cristianion.nexthr.Models.UserRole;
import com.example.cristianion.nexthr.Utils.AESUtils;
import com.example.cristianion.nexthr.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;
import java.util.UUID;

import static com.example.cristianion.nexthr.Utils.UtilFunc.isEmailValid;
import static com.example.cristianion.nexthr.Utils.UtilFunc.isValidDate;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;

public class FirstAdminActivity extends AppCompatActivity {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("companies");
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_admin);
        setTitle(R.string.app_name);
        final Company company = new Company(getIntent().getStringExtra("companyId"),getIntent().getStringExtra("companyName"));
        final View firstAdminView = findViewById(R.id.FirstAdminView);
        final ProgressBar progressBar = findViewById(R.id.FirstAdminProgress);

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
                showProgress(true,firstAdminView,progressBar);

                auth.createUserWithEmailAndPassword(email,password).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            showError(getApplicationContext(),"Register failed: "
                                    +task.getException());
                            showProgress(false,firstAdminView,progressBar);
                        } else {
                            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    db.collection("companies").whereEqualTo("name",company.name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                boolean goOn = true;
                                                if(task.getResult() != null){
                                                    for (QueryDocumentSnapshot document : task.getResult()){
                                                        Company foundCompany = document.toObject(Company.class);
                                                        if(foundCompany.name.equals(company.name)){
                                                            goOn = true;
                                                        }
                                                    }
                                                }
                                                if (goOn){
                                                    db.collection("companies").document(company.id).set(company).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                                                            final Employee admin = new Employee(userId,lastName,firstName,birthday,email,phone,company.id);
                                                            admin.isAdmin = true;
                                                            db.collection("companies").document(company.id).collection("employees")
                                                                    .document(admin.id).set(admin).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {

                                                                                    UserCompany userCompany = new UserCompany(UUID.randomUUID().toString(),admin.id,company.id);
                                                                                    db.collection("UserCompany").document(UUID.randomUUID().toString()).set(userCompany).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void aVoid) {
                                                                                            showProgress(false,firstAdminView,progressBar);
                                                                                            Toast.makeText(getApplicationContext(),"Log in with your company name and the admin account!",Toast.LENGTH_LONG).show();
                                                                                            Intent intent = new Intent(FirstAdminActivity.this,WelcomeActivity.class);
                                                                                            startActivity(intent);
                                                                                            finish(); }
                                                                                    });
                                                                                }


                                                                            });

                                                                        }

                                                    });
                                                }
                                            } else {
                                                showError(getApplicationContext(),"Company name already in use!");
                                                showProgress(false,firstAdminView,progressBar);
                                            }
                                        }
                                    });
                                }
                            });

                        }

                    }
                });


            }
        });

    }
}
