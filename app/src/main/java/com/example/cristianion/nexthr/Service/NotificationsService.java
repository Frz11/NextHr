package com.example.cristianion.nexthr.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.cristianion.nexthr.MenuActivity;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Models.Holiday;
import com.example.cristianion.nexthr.R;
import com.example.cristianion.nexthr.Utils.Notifications;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;

public class NotificationsService extends Service {
    public static Boolean isStart = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static int contor = 0;
    public NotificationsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.wtf("contor",contor+"");
        if(contor == 0) {
            //isStart = intent.getBooleanExtra("start", false);
            final AtomicBoolean isStartEmployeeChanges = new AtomicBoolean(true);
            db.collection("companies").document(currentCompany.id).collection("employees")
                    .document(currentEmployee.id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        Log.wtf(isStartEmployeeChanges.get()+"","get");
                        if(!isStartEmployeeChanges.get()) {
                            assert documentSnapshot != null;
                            currentEmployee = documentSnapshot.toObject(Employee.class);
                            //Notifications.showNotification("Changes!", "Someone changed something in your profile...", getApplicationContext());
                        }
                        isStartEmployeeChanges.set(false);
                }
            });
            final AtomicBoolean isStartHolidaysManager = new AtomicBoolean(true);
            db.collection("companies").document(currentCompany.id).collection("holidays")
                    .whereEqualTo("managerId",currentEmployee.id).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if(!isStartHolidaysManager.get()){
                        assert queryDocumentSnapshots != null;
                        for(DocumentChange change : queryDocumentSnapshots.getDocumentChanges()){
                            switch (change.getType()){
                                case ADDED:
                                    Notifications.showNotification("New holiday request!","Someone made a new holiday request!", getApplicationContext(),
                                            android.R.drawable.ic_dialog_info);
                                    break;
                            }
                        }
                    }
                    isStartHolidaysManager.set(false);
                }
            });
        }
        contor++;
        return super.onStartCommand(intent, flags, startId);
    }
}
