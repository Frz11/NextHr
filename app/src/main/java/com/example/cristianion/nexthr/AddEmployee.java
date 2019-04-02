package com.example.cristianion.nexthr;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Department;
import com.example.cristianion.nexthr.Models.Role;
import com.example.cristianion.nexthr.Utils.UtilFunc;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class AddEmployee extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);
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
        showProgress(true,empView,progressBar);

        //get departments and roles for autocomplete
        db.collection("companies").document(currentCompany.id).collection("departments")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Department> departments = queryDocumentSnapshots.toObjects(Department.class);
                ArrayList<String> departmentNames = new ArrayList<>();
                for(Department currentDep : departments){
                    departmentNames.add(currentDep.name);

                }
                ArrayAdapter<String> departmentsAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.select_dialog_item,departmentNames);
                department.setThreshold(1);
                department.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                department.setAdapter(departmentsAdapter);

                db.collection("companies").document(currentCompany.id).collection("roles")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Role> roles = queryDocumentSnapshots.toObjects(Role.class);
                        ArrayList<String> roleNames = new ArrayList<>();
                        for (Role role : roles){
                            roleNames.add(role.name);
                        }
                        ArrayAdapter<String> rolesAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.select_dialog_item,roleNames);
                        role.setThreshold(1);
                        role.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        role.setAdapter(rolesAdapter);
                        showProgress(false,empView,progressBar);
                    }
                });
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eLastName = lastName.getText().toString();
                String eFirstName = firstName.getText().toString();
                String eEmail = email.getText().toString();
                String ePhone = phone.getText().toString();
                String eBday = bday.getText().toString();
                final String eDep = bday.getText().toString();
                String eRole = role.getText().toString();
                String eSalary = salary.getText().toString();

                if(eLastName.isEmpty()){
                    showSnackbarError(empView,"Last name is required!");
                    return;
                }
                if(eFirstName.isEmpty()){
                    showSnackbarError(empView,"First name is required");
                    return;
                }
                if(eEmail.isEmpty()){
                    showSnackbarError(empView,"Email is required!");
                    return;
                }
                if(!UtilFunc.isEmailValid(eEmail)){
                    showSnackbarError(empView,"Email format is not valid!");
                    return;
                }
                if(ePhone.isEmpty()){
                    showSnackbarError(empView,"Phone is required!");
                    return;
                }
                if(eBday.isEmpty()){
                    showSnackbarError(empView,"Birthday is required!");
                    return;
                }
                if(!UtilFunc.isValidDate(eBday)){
                    showSnackbarError(empView,"Birthday format is not valid!");
                    return;
                }
                if(eSalary.isEmpty()){
                    showSnackbarError(empView,"Salary is required!");
                    return;
                }
                showProgress(true,empView,progressBar);
                //try to get departmentId and roleId;
                db.collection("companies").document(currentCompany.id).collection("departments")
                .whereEqualTo("name",eDep).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Department> departments = queryDocumentSnapshots.toObjects(Department.class);
                        if(departments.size() == 0 && !eDep.isEmpty()){
                            showProgress(false,empView,progressBar);
                            showSnackbarError(empView,"That department wasn't found!Take a look again?");
                            return;
                        }

                    }
                });
                //Tasks.whenAllSuccess(new ArrayList<Task>());
            }
        });


    }
}
