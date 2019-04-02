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
import android.widget.ProgressBar;

import com.example.cristianion.nexthr.Adapters.LocationAdapter;
import com.example.cristianion.nexthr.Models.Location;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;

public class LocationFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle(R.string.locations);
        return inflater.inflate(R.layout.layout_locations,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final View scrollView = view.findViewById(R.id.nestedScrollView);
        final ProgressBar progressBar = view.findViewById(R.id.LocationsProgress);
        showProgress(true,scrollView,progressBar);
        final RecyclerView rvLocations = (RecyclerView) view.findViewById(R.id.LocationsRecycler);
        db.collection("companies").document(currentCompany.id)
                .collection("locations").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Location> locations = queryDocumentSnapshots.toObjects(Location.class);
                LocationAdapter adapter = new LocationAdapter(locations,getFragmentManager());
                rvLocations.setAdapter(adapter);
                rvLocations.setLayoutManager(new LinearLayoutManager(view.getContext()));
                showProgress(false,scrollView,progressBar);
            }
        });
        view.findViewById(R.id.newLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),AddLocationActivity.class);
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.Frame,new LocationFragment()).commit();

        }
    }
}
