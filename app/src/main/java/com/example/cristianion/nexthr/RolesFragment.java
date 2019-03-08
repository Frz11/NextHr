package com.example.cristianion.nexthr;

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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static android.graphics.Color.WHITE;
import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;

public class RolesFragment extends Fragment {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("companies").child(currentCompany.id);

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
        mDatabase.child("roles").orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ArrayList<Role> roles = new ArrayList<>();
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        final Role foundRole = child.getValue(Role.class);
                        roles.add(foundRole);
                    }
                    RolesAdapter adapter = new RolesAdapter(roles);
                    rvRoles.setAdapter(adapter);
                    rvRoles.setLayoutManager(new LinearLayoutManager(view.getContext()));
                }
                Button button = view.findViewById(R.id.AddRoleButton);
                final TextView role = view.findViewById(R.id.Role);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String roleName = role.getText().toString();
                        if(roleName.length() == 0){
                            showError(getContext(),"Role name is required!");
                            return;
                        }
                        Role roleTBA = new Role(UUID.randomUUID().toString(),roleName);
                        mDatabase.child("roles").child(roleTBA.id).setValue(roleTBA).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.Frame,new RolesFragment());
                                fragmentTransaction.commit();
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}