package com.example.cristianion.nexthr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditDepartmentActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_department);
    }
}
