package com.example.cristianion.nexthr;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cristianion.nexthr.Utils.AESUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;

public class ChangePasswordActivity extends AppCompatActivity {

    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("companies").child(currentCompany.id).child("employees").child(currentEmployee.id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Button changePassword = findViewById(R.id.SubmitPasswordChange);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView oldPassword = findViewById(R.id.oldPassword);
                final TextView newPassword = findViewById(R.id.newPassword);
                /*mDatabase.child("password").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String dbPass = dataSnapshot.getValue(String.class);
                        Log.wtf("e",dbPass);
                        try {
                            if(!AESUtils.encrypt(oldPassword.getText().toString()).equals(dbPass)){
                                showError(getApplicationContext(),"Password don't match!");
                                return;
                            }
                            if(newPassword.getText().toString().length() == 0){
                                showError(getApplicationContext(), "New password cannot be empty!");
                                return;
                            }
                            currentEmployee.password = AESUtils.encrypt(newPassword.getText().toString());
                            mDatabase.child("password").setValue(currentEmployee.password).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    finish();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/

            }
        });

        Button cancel = findViewById(R.id.CancelPasswordChange);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
