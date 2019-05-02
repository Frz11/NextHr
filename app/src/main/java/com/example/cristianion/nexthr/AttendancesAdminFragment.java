package com.example.cristianion.nexthr;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.cristianion.nexthr.Adapters.AdminAttendanceAdapter;
import com.example.cristianion.nexthr.Models.Attendance;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class AttendancesAdminFragment extends Fragment {


    private List<Attendance> attendances = new ArrayList<>();
    private RecyclerView recyclerView;
    private AdminAttendanceAdapter adapter;
    private  ProgressBar progressBar;


    private DocumentReference db = FirebaseFirestore.getInstance().collection("companies").document(currentCompany.id);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.attendances_admin_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView date = view.findViewById(R.id.date);
        final Calendar calendar = Calendar.getInstance();
        progressBar = view.findViewById(R.id.progressBar);
        adapter = new AdminAttendanceAdapter(attendances,view.getContext());
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String today = df.format(calendar.getTime());
        date.setText(today);
        getAttendanceList(today);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(String.format(Locale.US,"%d-%02d-%02d",year,month+1,dayOfMonth));
                        getAttendanceList(date.getText().toString());
                    }
                },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });


    }

    public void getAttendanceList(String date){

        showProgress(true,recyclerView,progressBar);
        db.collection("attendances").whereEqualTo("day",date).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                attendances.clear();
                attendances.addAll(queryDocumentSnapshots.toObjects(Attendance.class));
                for(Attendance attendance : attendances){
                    Log.wtf("att",attendance.day);
                }
                adapter.notifyDataSetChanged();
                showProgress(false,recyclerView,progressBar);
            }
        });
    }
}
