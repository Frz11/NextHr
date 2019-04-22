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
import android.widget.ProgressBar;

import com.example.cristianion.nexthr.Adapters.HolidaysReqAdapter;
import com.example.cristianion.nexthr.Models.Holiday;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;


public class HolidaysRequestsFragment extends Fragment {

    private List<Holiday> holidays = new ArrayList<>();
    private DocumentReference db = FirebaseFirestore.getInstance().collection("companies").document(currentCompany.id);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.holidays_requests_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View reqView = view.findViewById(R.id.holidays_req_view);
        final ProgressBar progressBar = view.findViewById(R.id.holidays_req_progress);
        final RecyclerView reqRecycler = view.findViewById(R.id.holiday_req_recycler);
        showProgress(true,reqView,progressBar);
        final HolidaysReqAdapter adapter = new HolidaysReqAdapter(holidays,view.getContext(),getFragmentManager());
        reqRecycler.setAdapter(adapter);
        reqRecycler.setLayoutManager(new LinearLayoutManager(view.getContext()));

        db.collection("holidays").whereEqualTo("managerId",currentEmployee.id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                holidays.addAll(queryDocumentSnapshots.toObjects(Holiday.class));
                adapter.notifyDataSetChanged();
                showProgress(false,reqView,progressBar);

            }
        });
    }
}
