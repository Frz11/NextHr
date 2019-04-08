package com.example.cristianion.nexthr;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Role;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.UUID;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static java.security.AccessController.getContext;

public class AddRoleActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_role);
        setTitle(R.string.add_role);
        final View addRoleView = findViewById(R.id.AddRoleView);
        final ProgressBar progressBar = findViewById(R.id.AddRoleProgress);
                Button button = findViewById(R.id.AddRoleButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeKeyboard();
                        final TextView role = findViewById(R.id.Role);
                        final TextView maxSalary = findViewById(R.id.maxSalary);
                        final TextView minSalary = findViewById(R.id.minSalary);
                        String roleName = role.getText().toString();
                        String xSal = maxSalary.getText().toString();
                        String iSal = minSalary.getText().toString();
                        if(roleName.length() == 0){
                            showError(getApplicationContext(),"Role name is required!");
                            return;
                        }
                        if(xSal.isEmpty()){
                            showError(getApplicationContext(),"Max salary is required!");
                            return;
                        }
                        if(iSal.isEmpty()){
                            showError(getApplicationContext(),"Min salary is required");
                            return;
                        }
                        if(Float.parseFloat(iSal) > Float.parseFloat(xSal)){
                            showError(getApplicationContext(),"Min salary cannot be greater than max salary");
                            return;
                        }
                        showProgress(true,addRoleView,progressBar);
                        Role roleTBA = new Role(UUID.randomUUID().toString(),roleName,iSal,xSal);
                        db.collection("companies").document(currentCompany.id).collection("roles")
                                .document(roleTBA.id).set(roleTBA).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    showProgress(false,addRoleView,progressBar);
                                    finish();
                                }
                            }
                        });

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
