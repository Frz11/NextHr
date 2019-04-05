package com.example.cristianion.nexthr;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Department;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Models.Role;
import com.example.cristianion.nexthr.Models.UserCompany;
import com.example.cristianion.nexthr.Utils.UtilFunc;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class AddEmployee extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
        FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://console.firebase.google.com/u/2/project/nexthr-d7eba/")
                .setApiKey("AIzaSyDUL0GoE3c_cSvZcCFbRhZt8NcrCRaxUTI")
                .setApplicationId("nexthr-d7eba").build();

        try {
            FirebaseApp myApp = FirebaseApp.initializeApp(getApplicationContext(), firebaseOptions, "NextHr");
            auth = FirebaseAuth.getInstance(myApp);
        } catch (IllegalStateException e) {
            auth = FirebaseAuth.getInstance(FirebaseApp.getInstance("AnyAppName"));
            return;
        }
        setTitle(R.string.add_employee);
        final TextView lastName = findViewById(R.id.EmpLastNameInput);
        final TextView firstName = findViewById(R.id.EmpFirstNameInput);
        final TextView email = findViewById(R.id.EmpEmailInput);
        final TextView phone = findViewById(R.id.EmpPhoneInput);
        final TextView bday = findViewById(R.id.EmpBirthdayInput);
        final AutoCompleteTextView department = findViewById(R.id.EmpDepInput);
        final AutoCompleteTextView role = findViewById(R.id.EmpRoleInput);
        final TextView salary = findViewById(R.id.EmpSalaryInput);
        final ProgressBar progressBar = findViewById(R.id.AddEmployeeProgress);
        final View empView = findViewById(R.id.AddEmployeeView);
        final Button add = findViewById(R.id.AddEmployeeButton);
        final CheckBox isAdmin = findViewById(R.id.EmpAdminInput);
        showProgress(true, empView, progressBar);

        //get departments for autocomplete
        db.collection("companies").document(currentCompany.id).collection("departments")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Department> departments = queryDocumentSnapshots.toObjects(Department.class);
                ArrayList<String> departmentNames = new ArrayList<>();
                for (Department currentDep : departments) {
                    departmentNames.add(currentDep.name);

                }
                ArrayAdapter<String> departmentsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, departmentNames);
                department.setThreshold(1);
                department.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                department.setAdapter(departmentsAdapter);
                db.collection("companies").document(currentCompany.id).collection("roles")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<Role> roles = queryDocumentSnapshots.toObjects(Role.class);
                        ArrayList<String> roleNames = new ArrayList<>();
                        for (Role current : roles) {
                            roleNames.add(current.name);
                        }
                        ArrayAdapter<String> rolesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, roleNames);
                        role.setThreshold(1);
                        role.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        role.setAdapter(rolesAdapter);
                        showProgress(false, empView, progressBar);
                    }
                });

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String eLastName = lastName.getText().toString();
                final String eFirstName = firstName.getText().toString();
                final String eEmail = email.getText().toString();
                final String ePhone = phone.getText().toString();
                final String eBday = bday.getText().toString();
                final String eDep = department.getText().toString();
                final String eSalary = salary.getText().toString();
                final String eRole = role.getText().toString();

                if (eLastName.isEmpty()) {
                    showSnackbarError(empView, "Last name is required!");
                    return;
                }
                if (eFirstName.isEmpty()) {
                    showSnackbarError(empView, "First name is required");
                    return;
                }
                if (eEmail.isEmpty()) {
                    showSnackbarError(empView, "Email is required!");
                    return;
                }
                if (!UtilFunc.isEmailValid(eEmail)) {
                    showSnackbarError(empView, "Email format is not valid!");
                    return;
                }
                if (ePhone.isEmpty()) {
                    showSnackbarError(empView, "Phone is required!");
                    return;
                }
                if (eBday.isEmpty()) {
                    showSnackbarError(empView, "Birthday is required!");
                    return;
                }
                if (!UtilFunc.isValidDate(eBday)) {
                    showSnackbarError(empView, "Birthday format is not valid!");
                    return;
                }
                if (eSalary.isEmpty() && !eRole.isEmpty()) {
                    showSnackbarError(empView, "Salary is required when role specified!");
                    return;
                }
                showProgress(true, empView, progressBar);
                //try to get departmentId and roleId;
                db.collection("companies").document(currentCompany.id).collection("departments")
                        .whereEqualTo("name", eDep).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final List<Department> departments = queryDocumentSnapshots.toObjects(Department.class);
                        if (departments.size() == 0 && !eDep.isEmpty()) {
                            showProgress(false, empView, progressBar);
                            showSnackbarError(empView, "That department wasn't found!Take a look again?");

                        } else {
                            showProgress(true, empView, progressBar);
                            db.collection("companies").document(currentCompany.id).collection("roles")
                                    .whereEqualTo("name", eRole).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<Role> roles = queryDocumentSnapshots.toObjects(Role.class);
                                    String roleId = "";
                                    if (roles.size() == 0 && !eRole.isEmpty()) {
                                        showProgress(false, empView, progressBar);
                                        showSnackbarError(empView, "That role wasn't found! Take a look again?");
                                        return;
                                    } else if(roles.size() > 0){
                                        roleId = roles.get(0).id;
                                    }
                                    if (!eSalary.isEmpty() && roles.size() > 0 &&(Float.parseFloat(eSalary) < Float.parseFloat(roles.get(0).minSalary) ||
                                            Float.parseFloat(eSalary) > Float.parseFloat(roles.get(0).maxSalary))) {
                                        showProgress(false, empView, progressBar);
                                        showSnackbarError(empView, "Salary not in range: " + roles.get(0).minSalary
                                                + "-" + roles.get(0).maxSalary + "!");
                                        return;

                                    }
                                    final Employee employee = new Employee(UUID.randomUUID().toString(), eLastName, eFirstName, eBday, eEmail, ePhone, currentCompany.id, roleId, eSalary, departments.get(0).id);

                                    auth.createUserWithEmailAndPassword(eEmail, UUID.randomUUID().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                employee.id = Objects.requireNonNull(task.getResult()).getUser().getUid();
                                                auth.sendPasswordResetEmail(eEmail);
                                                if (isAdmin.isChecked()) {
                                                    employee.isAdmin = true;
                                                }
                                                db.collection("companies").document(currentCompany.id).collection("employees")
                                                        .document(employee.id).set(employee).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        UserCompany userCompany = new UserCompany(UUID.randomUUID().toString(), employee.id, currentCompany.id);
                                                        db.collection("UserCompany").document(userCompany.id).set(userCompany).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                finish();
                                                            }
                                                        });
                                                    }
                                                });
                                            } else {
                                                showProgress(false, empView, progressBar);
                                                try {
                                                    throw Objects.requireNonNull(task.getException());
                                                } catch (FirebaseAuthWeakPasswordException e) {
                                                    //
                                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                                    email.setError(getString(R.string.error_invalid_email));
                                                    email.requestFocus();
                                                } catch (FirebaseAuthUserCollisionException e) {
                                                    email.setError(getString(R.string.error_user_exists));
                                                    email.requestFocus();
                                                } catch (Exception e) {
                                                    Log.e("error", e.getMessage());
                                                }

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
