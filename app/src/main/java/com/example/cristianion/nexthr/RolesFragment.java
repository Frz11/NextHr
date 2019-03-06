package com.example.cristianion.nexthr;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Role;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        //get roles
        final TableLayout rolesTable = view.findViewById(R.id.RolesTable);
        rolesTable.removeAllViews();
        mDatabase.child("roles").orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        final Role foundRole = child.getValue(Role.class);

                        if(foundRole.name.equals("Admin")){
                            TableRow tr = new TableRow(view.getContext());
                            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                            TextView rolesText = new TextView(view.getContext());
                            rolesText.setText(foundRole.name);
                            rolesText.setTextSize(20);
                            rolesText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
                            rolesText.setTextColor(Color.BLACK);
                            tr.addView(rolesText);
                            rolesTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        } else {

                            TableRow tr = new TableRow(view.getContext());
                            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                            TextView roleText = new TextView(view.getContext());
                            roleText.setText(foundRole.name);
                            roleText.setTextSize(20);
                            roleText.setTextColor(Color.BLACK);
                            roleText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT,(float) 0.95));


                            Button deleteButton = new Button(view.getContext());
                            Button editButton = new Button(view.getContext());
                            deleteButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Delete(foundRole);
                                }
                            });
                            editButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GoToEdit(foundRole);
                                }
                            });
                            deleteButton.setBackgroundColor(Color.RED);
                            deleteButton.setText("x");
                            deleteButton.setTextColor(Color.WHITE);
                            deleteButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,(float) 0.020));
                            editButton.setBackgroundColor(Color.BLUE);
                            editButton.setText("Edit");
                            editButton.setTextColor(Color.WHITE);
                            editButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT,(float) 0.025));
                            tr.addView(roleText);
                            tr.addView(editButton);
                            tr.addView(deleteButton);
                            rolesTable.addView(tr, new TableLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));

                        }
                    }
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
                                Objects.requireNonNull(getActivity()).recreate();
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