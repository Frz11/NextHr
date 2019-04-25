package com.example.cristianion.nexthr;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

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
        final FontAwesome send = findViewById(R.id.sendButton);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final String employeeId = getIntent().getStringExtra("employeeId");
        final String eName= getIntent().getStringExtra("employeeName");
        setTitle(eName);

        //employeeName.setText(eName);
        final Task receiver = db.collection("messages").whereEqualTo("from",employeeId)
                .whereEqualTo("to",currentEmployee.id).orderBy("sentAt", Query.Direction.DESCENDING).get();
        Task sender = db.collection("messages").whereEqualTo("from",currentEmployee.id)
                .whereEqualTo("to",employeeId).orderBy("sentAt", Query.Direction.DESCENDING).get();


        Tasks.whenAllSuccess(receiver,sender).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                Log.wtf("order",objects.size() + "");
                for (Object object : objects) {
                    ArrayList<Message> messages1 = (ArrayList<Message>) ((QuerySnapshot) object).toObjects(Message.class);
                    //Collections.reverse(messages1);
                    for(Message message : messages1){
                        Log.wtf(message.message,message.sentAt);
                        messages.add(message);
                    }
                }
                Collections.sort(messages, new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        if(o1.sentAt.equals(o2.sentAt)){
                            return 0;
                        } else {
                            return o1.sentAt.compareTo(o2.sentAt);
                        }
                    }
                });
                adapter.notifyDataSetChanged();
                if(messages.size()  > 0) {
                    recyclerView.smoothScrollToPosition(messages.size() - 1);
                }
                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        Calendar cal = Calendar.getInstance();
                        String messageText = messageBox.getText().toString();
                        if(!messageText.isEmpty()){
                            messageBox.setText("");
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            assert imm != null;
                            imm.hideSoftInputFromWindow(messageBox.getWindowToken(),InputMethodManager.HIDE_IMPLICIT_ONLY);
                            Message message = new Message();
                            message.id = UUID.randomUUID().toString();
                            message.from = currentEmployee.id;
                            message.to = employeeId;
                            message.message = messageText;
                            message.sentAt = dateFormat.format(cal.getTime()).toString();

                            messages.add(message);
                            adapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(messages.size() -1);

                            db.collection("messages").document(message.id).set(message);

                        }
                    }
                });

            }
        });

        final AtomicBoolean isStart = new AtomicBoolean(true);
        //for incoming new messages!
        db.collection("messages").whereEqualTo("from",employeeId).whereEqualTo("to",currentEmployee.id)
                .orderBy("sentAt", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(isStart.get()){
                    isStart.set(false);
                    return;
                }
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
