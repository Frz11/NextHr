package com.example.cristianion.nexthr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class EditEmployeeActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DocumentReference db = FirebaseFirestore.getInstance().collection("companies")
            .document(currentCompany.id);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);
        final TextView lastName = findViewById(R.id.EmpLastNameInput);
        final TextView firstName = findViewById(R.id.EmpFirstNameInput);
        final TextView email = findViewById(R.id.EmpEmailInput);
        final TextView phone = findViewById(R.id.EmpPhoneInput);
        final TextView bday = findViewById(R.id.EmpBirthdayInput);
        final AutoCompleteTextView department = findViewById(R.id.EmpDepInput);
        final AutoCompleteTextView role = findViewById(R.id.EmpRoleInput);
        final TextView salary = findViewById(R.id.EmpSalaryInput);
        final ProgressBar progressBar = findViewById(R.id.EditEmployeeProgress);
        final View empView = findViewById(R.id.EditEmployeeView);
        final Button save = findViewById(R.id.SaveEmployeeButton);
        final CheckBox isAdmin = findViewById(R.id.EmpAdminInput);
        final String employeeId = getIntent().getStringExtra("employeeId");
        showProgress(true, empView, progressBar);
        //get departments for autocomplete
        db.collection("employees").whereEqualTo("id",employeeId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final Employee employee = queryDocumentSnapshots.toObjects(Employee.class).get(0);
                if(employee == null){
                    return;
                }
                lastName.setText(employee.lastName);
                firstName.setText(employee.firstName);
                email.setText(employee.email);
                phone.setText(employee.phone);
                bday.setText(employee.birthday);
                salary.setText(employee.salary);
                if(employee.isAdmin){
                    isAdmin.setChecked(true);
                }
                db.collection("departments")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Department> departments = queryDocumentSnapshots.toObjects(Department.class);
                        ArrayList<String> departmentNames = new ArrayList<>();
                        for (Department currentDep : departments) {
                            departmentNames.add(currentDep.name);
                            if(currentDep.id.equals(employee.departmentId)){
                                department.setText(currentDep.name);
                            }


                        }
                        ArrayAdapter<String> departmentsAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, departmentNames);
                        department.setThreshold(0);
                        department.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        department.setAdapter(departmentsAdapter);
                        db.collection("roles")
                                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                                List<Role> roles = queryDocumentSnapshots.toObjects(Role.class);
                                ArrayList<String> roleNames = new ArrayList<>();
                                for (Role current : roles) {
                                    roleNames.add(current.name);
                                    if(current.id.equals(employee.roleId)){
                                        role.setText(current.name);
                                    }
                                }
                                ArrayAdapter<String> rolesAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, roleNames);
                                role.setThreshold(0);
                                role.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                role.setAdapter(rolesAdapter);
                                showProgress(false, empView, progressBar);
                            }
                        });

                    }
                });

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
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
                db.collection("departments")
                        .whereEqualTo("name", eDep).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        final List<Department> departments = queryDocumentSnapshots.toObjects(Department.class);
                        if (departments.size() == 0 && !eDep.isEmpty()) {
                            showProgress(false, empView, progressBar);
                            showSnackbarError(empView, "That department wasn't found!Take a look again?");

                        } else {
                            showProgress(true, empView, progressBar);
                            db.collection("roles")
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
                                    final Employee employee = new Employee(employeeId, eLastName, eFirstName, eBday, eEmail, ePhone, currentCompany.id, roleId, eSalary, departments.get(0).id);
                                    if(isAdmin.isChecked()){
                                        employee.isAdmin = true;
                                    }
                                    db.collection("employees").document(employee.id).set(employee).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            finish();
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
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
