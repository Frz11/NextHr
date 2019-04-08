package com.example.cristianion.nexthr;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Role;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class EditRoleActivity extends AppCompatActivity {

    private DocumentReference db = FirebaseFirestore.getInstance().collection("companies")
            .document(currentCompany.id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_role);
        setTitle(R.string.edit_role);
        final ProgressBar progressBar = findViewById(R.id.EditRoleProgress);
        final View roleView = findViewById(R.id.EditRoleView);
        final TextView name = findViewById(R.id.Role);
        final TextView minSal = findViewById(R.id.minSalary);
        final TextView maxSal = findViewById(R.id.maxSalary);
        final Button save =  findViewById(R.id.SaveRoleButton);

        showProgress(true,roleView,progressBar);
        db.collection("roles").document(getIntent().getStringExtra("roleId")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Role role  = documentSnapshot.toObject(Role.class);
                if(role == null){
                    return;
                }
                name.setText(role.name);
                minSal.setText(role.minSalary);
                maxSal.setText(role.maxSalary);
                showProgress(false,roleView,progressBar);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        closeKeyboard();
                        String roleName = name.getText().toString();
                        String xSal = maxSal.getText().toString();
                        String iSal = minSal.getText().toString();
                        if(roleName.length() == 0){
                            showSnackbarError(roleView,"Role name is required!");
                            return;
                        }
                        if(xSal.isEmpty()){
                            showSnackbarError(roleView,"Max salary is required!");
                            return;
                        }
                        if(iSal.isEmpty()){
                            showSnackbarError(roleView,"Min salary is required");
                            return;
                        }
                        if(Float.parseFloat(iSal) > Float.parseFloat(xSal)){
                            showSnackbarError(roleView,"Min salary cannot be greater than max salary");
                            return;
                        }
                        showProgress(true,roleView,progressBar);
                        role.name = roleName;
                        role.maxSalary = xSal;
                        role.minSalary = iSal;
                        db.collection("roles").document(getIntent().getStringExtra("roleId")).set(role)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                            }
                        });
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
