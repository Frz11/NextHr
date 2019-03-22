package com.example.cristianion.nexthr;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cristianion.nexthr.Adapters.EmployeesAdapter;
import com.example.cristianion.nexthr.Models.Employee;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;

public class EmployeesFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_employees,null);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
            }

        });
    }

}
