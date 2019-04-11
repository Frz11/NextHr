package com.example.cristianion.nexthr.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.cristianion.nexthr.MenuActivity;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Utils.Notifications;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.atomic.AtomicBoolean;

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
            isStart = intent.getBooleanExtra("start", false);
            final AtomicBoolean aux = new AtomicBoolean(true);
            db.collection("companies").document(currentCompany.id).collection("employees")
                    .document(currentEmployee.id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        Log.wtf(aux.get()+"","get");
                        if(!aux.get()) {
                            assert documentSnapshot != null;
                            Employee employee = documentSnapshot.toObject(Employee.class);
                            Notifications.showNotification("Changes!", "Someone changed something in your profile...", getApplicationContext());
                        }
                        aux.set(false);
                }
            });
        }
        contor++;
        return super.onStartCommand(intent, flags, startId);
    }
}
