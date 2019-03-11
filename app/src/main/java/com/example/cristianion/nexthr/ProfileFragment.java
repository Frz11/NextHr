package com.example.cristianion.nexthr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.example.cristianion.nexthr.Models.Image;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;
import static com.example.cristianion.nexthr.Utils.UtilFunc.isEmailValid;
import static com.example.cristianion.nexthr.Utils.UtilFunc.isValidDate;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;

public class ProfileFragment extends Fragment {


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static final int PICK_IMAGE = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_layout,null);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView lastName = view.findViewById(R.id.profileLastName);
        final TextView firstName = view.findViewById(R.id.profileFirstName);
        final TextView email = view.findViewById(R.id.profileEmail);
        final TextView birthday = view.findViewById(R.id.profileBirthday);
        final TextView phone = view.findViewById(R.id.profilePhone);

        final ImageView profilePhoto = view.findViewById(R.id.imagePicker);
        db.collection("companies").document(currentEmployee.companyId).collection("images")
                .whereEqualTo("userId",currentEmployee.id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot data : Objects.requireNonNull(task.getResult())){
                        Image image = data.toObject(Image.class);
                        StorageReference storage = FirebaseStorage.getInstance().getReference("images/").child(currentCompany.id).child(image.id);
                        storage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Glide.with(view.getContext()).load(task.getResult()).into(profilePhoto);

                            }
                        });
                        break;
                    }
                }
            }
        });

        lastName.setText(currentEmployee.lastName);
        firstName.setText(currentEmployee.firstName);
        email.setText(currentEmployee.email);
        birthday.setText(currentEmployee.birthday);
        phone.setText(currentEmployee.phone);

        Button save = view.findViewById(R.id.profile_SaveProfileButton);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastName.getText().toString().length() == 0){
                    showError(view.getContext(),"Last Name is required!");
                    return;
                }
                if(firstName.getText().toString().length() == 0){
                    showError(view.getContext(),"First Name is required!");
                    return;
                }
                if(email.getText().toString().length() == 0){
                    showError(view.getContext(), "Email is required!");
                    return;
                }
                if(!isEmailValid(email.getText().toString())){
                    showError(getContext(),"Email is invalid!");
                }
                if(birthday.getText().toString().length() == 0){
                    showError(getContext(),"Birthday is required!");
                    return;
                }
                if(!isValidDate(birthday.getText().toString())){
                    showError(getContext(), "Birthday is an invalid date!");
                    return;
                }
                if(phone.getText().toString().length() == 0){
                    showError(getContext(),"Phone is required!");
                    return;
                }
                currentEmployee.firstName = firstName.getText().toString();
                currentEmployee.lastName = lastName.getText().toString();
                currentEmployee.email = email.getText().toString();
                currentEmployee.birthday = birthday.getText().toString();
                currentEmployee.phone = phone.getText().toString();
                db.collection("companies").document(currentEmployee.companyId).collection("employees")
                        .document(currentEmployee.id).set(currentEmployee).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Objects.requireNonNull(getFragmentManager()).beginTransaction().replace(R.id.Frame,new ProfileFragment()).commit();
                        } else {
                            showError(getContext(),"Something went wrong...");
                        }
                    }
                });
            }
        });

        Button changePassword = view.findViewById(R.id.profile_changePasswordButton);
        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),ChangePasswordActivity.class);
                startActivity(intent);
            }
        });

        (view.findViewById(R.id.imagePicker)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),PICK_IMAGE);
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK){
                if (data == null){
                    showError(getContext(),"Something went wrong...");
                    return;
                }
            final StorageReference storage = FirebaseStorage.getInstance().getReference("images/");
            final Image image = new Image(UUID.randomUUID().toString(),currentEmployee.id);
            db.collection("companies").document(currentEmployee.companyId).collection("images")
                    .whereEqualTo("userId",currentEmployee.id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot data : Objects.requireNonNull(task.getResult())){
                            Image image = data.toObject(Image.class);
                            data.getReference().delete();
                            storage.child(currentCompany.id).child(image.id).delete();
                        }
                        db.collection("companies").document(currentEmployee.companyId).collection("images")
                                .document(image.id).set(image).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    storage.child(currentCompany.id).child(image.id).putFile(Objects.requireNonNull(data.getData())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Objects.requireNonNull(getActivity()).recreate();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });

        }
    }
}
