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
import android.widget.RelativeLayout;

import com.example.cristianion.nexthr.Adapters.DepartmentAdapter;
import com.example.cristianion.nexthr.Models.Department;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;

public class DepartmentsFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    RecyclerView departmentsRecycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle(R.string.departments);
        return inflater.inflate(R.layout.departments_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProgressBar progressBar = view.findViewById(R.id.DepartmentsProgress);
        departmentsRecycler = view.findViewById(R.id.DepartmentsRecycler);
        showProgress(true,departmentsRecycler,progressBar);
        db.collection("companies").document(currentCompany.id).collection("departments").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Department> departments = queryDocumentSnapshots.toObjects(Department.class);

                        showProgress(false,departmentsRecycler,progressBar);
                        DepartmentAdapter departmentAdapter = new DepartmentAdapter(departments,getFragmentManager());
                        departmentsRecycler.setAdapter(departmentAdapter);
                        departmentsRecycler.setLayoutManager(new LinearLayoutManager(view.getContext()));
                        departmentAdapter.notifyDataSetChanged();
                    }
                });



        FontAwesome newDepartment = view.findViewById(R.id.newDepButton);
        newDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddDepartmentActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            assert getFragmentManager() != null;
            departmentsRecycler.getRecycledViewPool().clear();
            getFragmentManager().beginTransaction().replace(R.id.Frame,new DepartmentsFragment()).commit();
        }
    }
}
