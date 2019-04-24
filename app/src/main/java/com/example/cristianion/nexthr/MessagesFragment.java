package com.example.cristianion.nexthr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cristianion.nexthr.Adapters.ConversationAdapter;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Models.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;

public class MessagesFragment extends Fragment {


    private DocumentReference db = FirebaseFirestore.getInstance().collection("companies").document(currentCompany.id);
    private List<String> convs = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.messages_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final AutoCompleteTextView search = view.findViewById(R.id.empSearch);
        final ProgressBar progressBar = view.findViewById(R.id.messagesProgress);
        final RecyclerView recyclerView = view.findViewById(R.id.messagesRecycler);
        final ConversationAdapter adapter = new ConversationAdapter(getContext(),convs);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final View messagesView = view.findViewById(R.id.view);
        showProgress(true,messagesView,progressBar);

        db.collection("employees").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final List<Employee> employees = queryDocumentSnapshots.toObjects(Employee.class);
                ArrayList<String> employeeNames = new ArrayList<>();

                for (Employee employee : employees){
                    employeeNames.add(employee.firstName + " " + employee.lastName);
                }
                ArrayAdapter<String> employeeAdapter = new ArrayAdapter<String>(view.getContext(),android.R.layout.select_dialog_item,employeeNames);
                search.setThreshold(1);
                search.setAdapter(employeeAdapter);
                search.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        for(Employee employee : employees){
                            if(search.getText().toString().equals(employee.firstName + " " + employee.lastName)){
                                if(employee.id.equals(currentEmployee.id)){
                                    search.setText("");
                                    return;
                                }
                                Intent intent = new Intent(getActivity(),ConversationActivity.class);
                                intent.putExtra("employeeId",employee.id);
                                intent.putExtra("employeeName",employee.firstName + " " +  employee.lastName);
                                startActivityForResult(intent,1);
                                break;
                            }
                        }
                    }
                });

            }
        });

        Query sender = db.collection("messages").whereEqualTo("from",currentEmployee.id).orderBy("sentAt", Query.Direction.DESCENDING);
        Query receiver = db.collection("messages").whereEqualTo("to",currentEmployee.id).orderBy("sentAt", Query.Direction.DESCENDING);
        Task senderTask = sender.get();
        Task receiverTask = receiver.get();

        Task combined = Tasks.whenAllSuccess(senderTask,receiverTask).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                Set<String> conversations = new HashSet<>();
                ArrayList<Message> tba = new ArrayList<>();
                for(Object object : objects ){
                    QuerySnapshot snapshot = (QuerySnapshot) object;
                    List<Message> messages = snapshot.toObjects(Message.class);
                    tba.addAll(messages);
                }
                Collections.sort(tba, new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        if(o1.sentAt.equals(o2.sentAt)){
                            return 0;
                        } else {
                            return o1.sentAt.compareTo(o2.sentAt);
                        }
                    }
                });
                for(Message message : tba){
                    if(!message.from.equals(currentEmployee.id)){
                        conversations.add(message.from);
                    } else {
                        conversations.add(message.to);
                    }
                }
                showProgress(false,messagesView,progressBar);
                convs.addAll(conversations);
                Collections.reverse(convs);
                adapter.notifyDataSetChanged();

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.Frame,new MessagesFragment()).commit();
        }
    }
}
