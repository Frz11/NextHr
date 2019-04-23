package com.example.cristianion.nexthr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cristianion.nexthr.Adapters.MessagesAdapter;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Models.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;

public class ConversationActivity extends AppCompatActivity {


    private DocumentReference db = FirebaseFirestore.getInstance().collection("companies").document(currentCompany.id);
    private List<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        final RecyclerView recyclerView = findViewById(R.id.messagesRecycler);
        final MessagesAdapter adapter = new MessagesAdapter(getApplicationContext(),messages);
        final TextView messageBox = findViewById(R.id.messageBox);
        final TextView employeeName = findViewById(R.id.employee_name);
        final Button send = findViewById(R.id.sendButton);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final String employeeId = getIntent().getStringExtra("employeeId");
        final String eName= getIntent().getStringExtra("employeeName");
        employeeName.setText(eName);
        final Task receiver = db.collection("messages").whereEqualTo("from",employeeId)
                .whereEqualTo("to",currentEmployee.id).orderBy("sentAt", Query.Direction.DESCENDING).get();
        Task sender = db.collection("messages").whereEqualTo("from",currentEmployee.id)
                .whereEqualTo("to",employeeId).orderBy("sentAt", Query.Direction.DESCENDING).get();


        Tasks.whenAllSuccess(receiver,sender).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                for (Object object : objects) {
                    List<Message> messages1 = ((QuerySnapshot) object).toObjects(Message.class);
                    messages.addAll(messages1);
                }
                adapter.notifyDataSetChanged();
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        String messageText = messageBox.getText().toString();
                        if(!messageText.isEmpty()){
                            Message message = new Message();
                            message.id = UUID.randomUUID().toString();
                            message.from = currentEmployee.id;
                            message.to = employeeId;
                            message.message = messageText;
                            message.sentAt = dateFormat.format(cal.getTime()).toString();

                            messages.add(message);
                            adapter.notifyDataSetChanged();
                            db.collection("messages").document(message.id).set(message);

                        }
                    }
                });

            }
        });

        //for incoming new messages!
        db.collection("messages").whereEqualTo("from",employeeId).whereEqualTo("to",currentEmployee.id)
                .orderBy("sentAt", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                assert queryDocumentSnapshots != null;
                for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()){
                    switch (change.getType()){
                        case ADDED:
                            Message message = change.getDocument().toObject(Message.class);
                            messages.add(message);
                            adapter.notifyDataSetChanged();
                            break;

                    }
                }
            }
        });

    }
}
