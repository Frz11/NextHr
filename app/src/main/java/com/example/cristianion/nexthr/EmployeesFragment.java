package com.example.cristianion.nexthr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.cristianion.nexthr.Adapters.EmployeesAdapter;
import com.example.cristianion.nexthr.Models.Employee;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;

public class EmployeesFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle(R.string.employees);
        return inflater.inflate(R.layout.layout_employees,null);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View employeesView = view.findViewById(R.id.EmployeesView);
        final ProgressBar progressBar = view.findViewById(R.id.EmployeesProgress);
        final FontAwesome addEmployee = view.findViewById(R.id.AddEmployeeButton);
        addEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddEmployee.class);
                startActivityForResult(intent,1);
            }
        });
        showProgress(true,employeesView,progressBar);
        db.collection("companies").document(currentCompany.id).collection("employees").orderBy("id").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<Employee> employees = new ArrayList<>();
                for (QueryDocumentSnapshot data: queryDocumentSnapshots){
                    employees.add(data.toObject(Employee.class));
                }
                RecyclerView recyclerView = view.findViewById(R.id.EmployeesRecycler);
                EmployeesAdapter adapter = new EmployeesAdapter(employees,getFragmentManager());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                showProgress(false,employeesView,progressBar);
            }

        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.Frame,new EmployeesFragment()).commit();
        }
    }
}
