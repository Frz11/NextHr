package com.example.cristianion.nexthr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.cristianion.nexthr.Adapters.RolesAdapter;
import com.example.cristianion.nexthr.Models.Role;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.graphics.Color.WHITE;
import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;

public class RolesFragment extends Fragment {

    //private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("companies").child(currentCompany.id);
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.roles_layout, null);
    }

    public void GoToEdit(Role role){

    }
    public void Delete(Role role){

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView rvRoles = (RecyclerView) view.findViewById(R.id.RoleRecycler);
        //get roles
        db.collection("companies").document(currentCompany.id).collection("roles")
                .orderBy("name").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    ArrayList<Role> roles = new ArrayList<>();
                    for (QueryDocumentSnapshot data : Objects.requireNonNull(task.getResult())){
                        Role foundRole = data.toObject(Role.class);
                        roles.add(foundRole);
                    }
                    RolesAdapter adapter = new RolesAdapter(roles,getFragmentManager());
                    rvRoles.setAdapter(adapter);
                    rvRoles.setLayoutManager(new LinearLayoutManager(view.getContext()));
                }
                (view.findViewById(R.id.newRoleButton)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),AddRoleActivity.class);
                        startActivityForResult(intent,1);
                    }
                });

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.Frame,new RolesFragment()).commit();
        }
    }
}