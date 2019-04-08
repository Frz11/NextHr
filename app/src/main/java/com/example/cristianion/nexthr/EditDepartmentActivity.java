package com.example.cristianion.nexthr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class EditDepartmentActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_department);
        setTitle(R.string.edit_department);
        final ProgressBar progressBar = findViewById(R.id.EditDepProgress);
        final View editView = findViewById(R.id.EditDepView);
        final AutoCompleteTextView managerInput = findViewById(R.id.eDepManagerInput);
        final AutoCompleteTextView locationInput = findViewById(R.id.eDepLocationInput);
        final TextView name = findViewById(R.id.eDepNameInput);
        final Button save = findViewById(R.id.SaveDepartmentButton);
        final String departmentId = getIntent().getStringExtra("departmentId");
        showProgress(true,editView,progressBar);

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

                                    }
                                });

                        db.collection("companies").document(currentCompany.id).collection("departments")
                                .document(departmentId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                final Department dep = documentSnapshot.toObject(Department.class);
                                assert dep != null;
                                name.setText(dep.name);
                                db.collection("companies").document(currentCompany.id).collection("employees")
                                        .document(dep.managerId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Employee manager = documentSnapshot.toObject(Employee.class);
                                        assert manager != null;
                                        managerInput.setText(String.format("%s %s", manager.firstName, manager.lastName));
                                        db.collection("companies").document(currentCompany.id).collection("locations")
                                                .document(dep.locationId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Location location = documentSnapshot.toObject(Location.class);
                                                assert location != null;
                                                locationInput.setText(String.format("%s,%s,%s", location.city, location.street, location.county));
                                                showProgress(false,editView,progressBar);
                                            }
                                        });
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
                final String dName = name.getText().toString();
                String dManager = managerInput.getText().toString();
                String dLocation = locationInput.getText().toString();

                if(dName.isEmpty()){
                    showSnackbarError(editView,"Department name is required!");
                    return;
                }

                String[] managerName = dManager.split(" ");
                final String[] locationName = dLocation.split(",");
                if(managerName.length == 2 && locationName.length == 3) {
                    //we try to find the input for manager and location in db...
                    showProgress(true,editView,progressBar);
                    db.collection("companies").document(currentCompany.id).collection("employees")
                            .whereEqualTo("firstName", managerName[0]).whereEqualTo("lastName",managerName[1]).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            final List<Employee> employees = queryDocumentSnapshots.toObjects(Employee.class);
                            if(employees.size() == 0){
                                showProgress(false,editView,progressBar);
                                showSnackbarError(editView,"The employee specified for manager wasn't found! Take a look again?");
                                return;
                            }
                            db.collection("companies").document(currentCompany.id).collection("locations")
                                    .whereEqualTo("city",locationName[0]).whereEqualTo("street",locationName[1]).whereEqualTo("county",locationName[2]).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    List<Location> locations = queryDocumentSnapshots.toObjects(Location.class);
                                    if (locations.size() == 0){
                                        showProgress(false,editView,progressBar);
                                        showSnackbarError(editView,"That location wasn't found! Take a look again?");
                                        return;
                                    }
                                    Department tbaDepartment = new Department(departmentId,dName,employees.get(0).id,locations.get(0).id);
                                    db.collection("companies").document(currentCompany.id).collection("departments")
                                            .document(tbaDepartment.id).set(tbaDepartment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                finish();
                                            } else {
                                                showProgress(false,editView,progressBar);
                                                showSnackbarError(editView,"Something went wrong...");
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }  else {
                    showSnackbarError(editView,"Manager or location format is wrong! Please take a look again");
                }
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
