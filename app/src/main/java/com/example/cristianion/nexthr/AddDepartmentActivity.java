package com.example.cristianion.nexthr;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Department;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Models.Location;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class AddDepartmentActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_department);
        final View addDepartmentView = findViewById(R.id.AddDepartmentView);
        final ProgressBar progressBar = findViewById(R.id.AddDepartmentProgress);
        showProgress(false,addDepartmentView,progressBar);
        final AutoCompleteTextView managerInput = findViewById(R.id.departmentManagerInput);
        final AutoCompleteTextView locationInput = findViewById(R.id.departmentLocationInput);
        final TextView departmentName = findViewById(R.id.departmentNameInput);
        final Button add = findViewById(R.id.AddDepartmentButton);

        //get employees
        db.collection("companies").document(currentCompany.id).collection("employees").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Employee> employees = queryDocumentSnapshots.toObjects(Employee.class);
                        ArrayList<String> employeeNames = new ArrayList<>();
                        for (Employee employee : employees){
                            employeeNames.add(employee.firstName + " " + employee.lastName);
                        }
                        ArrayAdapter<String> employeeAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.select_dialog_item,employeeNames);
                        managerInput.setThreshold(1);
                        managerInput.setAdapter(employeeAdapter);
                        managerInput.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                        db.collection("companies").document(currentCompany.id).collection("locations").get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                List<Location> locations = queryDocumentSnapshots.toObjects(Location.class);
                                ArrayList<String> locationNames = new ArrayList<>();
                                for (Location location : locations){
                                    locationNames.add(location.city + "," + location.street + "," + location.county);
                                }
                                ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.select_dialog_item,locationNames);
                                locationInput.setThreshold(1);
                                locationInput.setAdapter(locationAdapter);
                                locationInput.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                                showProgress(false,addDepartmentView,progressBar);
                            }
                        });



                    }
                });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String dName =  departmentName.getText().toString();
                String dManager = managerInput.getText().toString();
                String dLocation = locationInput.getText().toString();

                if(dName.isEmpty()){
                    showSnackbarError(addDepartmentView,"Department name is required!");
                    return;
                }

                String[] managerName = dManager.split(" ");
                final String[] locationName = dLocation.split(",");
                if(managerName.length == 2 && locationName.length == 3) {
                    //we try to find the input for manager and location in db...
                    showProgress(true,addDepartmentView,progressBar);
                    db.collection("companies").document(currentCompany.id).collection("employees")
                            .whereEqualTo("firstName", managerName[0]).whereEqualTo("lastName",managerName[1]).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            final List<Employee> employees = queryDocumentSnapshots.toObjects(Employee.class);
                            if(employees.size() == 0){
                                showProgress(false,addDepartmentView,progressBar);
                                showSnackbarError(addDepartmentView,"The employee specified for manager wasn't found! Take a look again?");
                                return;
                            }
                            db.collection("companies").document(currentCompany.id).collection("locations")
                                    .whereEqualTo("city",locationName[0]).whereEqualTo("street",locationName[1]).whereEqualTo("county",locationName[2]).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<Location> locations = queryDocumentSnapshots.toObjects(Location.class);
                                    if (locations.size() == 0){
                                        showProgress(false,addDepartmentView,progressBar);
                                        showSnackbarError(addDepartmentView,"That location wasn't found! Take a look again?");
                                        return;
                                    }
                                    Department tbaDepartment = new Department(UUID.randomUUID().toString(),dName,employees.get(0).id,locations.get(0).id);
                                    db.collection("companies").document(currentCompany.id).collection("departments")
                                            .document(tbaDepartment.id).set(tbaDepartment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                finish();
                                            } else {
                                                showProgress(false,addDepartmentView,progressBar);
                                                showSnackbarError(addDepartmentView,"Something went wrong...");
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }  else {
                    showSnackbarError(addDepartmentView,"Manager or location format is wrong! Please take a look again");
                }
            }
        });
    }
}
