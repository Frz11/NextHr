package com.example.cristianion.nexthr;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.cristianion.nexthr.Models.Attendance;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Locale;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class EditAttendanceActivity extends AppCompatActivity {

    private DocumentReference db = FirebaseFirestore.getInstance().collection("companies").document(currentCompany.id);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_attendance);
        String employeeName = getIntent().getStringExtra("employeeName");
        final String attendanceId = getIntent().getStringExtra("attendanceId");
        Log.wtf(attendanceId,"id");
        TextView employee = findViewById(R.id.employee);
        employee.setText(employeeName);
        final TextView start = findViewById(R.id.start);
        final TextView leave = findViewById(R.id.leave);
        final TextView date = findViewById(R.id.date);
        final Calendar calendar = Calendar.getInstance();
        final Button save = findViewById(R.id.save);
        final Button remove = findViewById(R.id.delete);

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(EditAttendanceActivity.this)
                        .setTitle("Remove!")
                        .setMessage("Are you sure you want to remove this record?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton(android.R.string.cancel,null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection("attendances").document(attendanceId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        finish();
                                    }
                                });
                            }
                        }).show();
            }
        });

        db.collection("attendances").document(attendanceId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Attendance attendance = documentSnapshot.toObject(Attendance.class);
                if(attendance != null){
                    start.setText(attendance.arrivalTime);
                    leave.setText(attendance.leaveTime);
                    date.setText(attendance.day);

                    start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new TimePickerDialog(EditAttendanceActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    start.setText(String.format(Locale.US,"%02d:%02d",hourOfDay,minute));
                                }
                            },0,0,false).show();
                        }
                    });
                    leave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new TimePickerDialog(EditAttendanceActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    leave.setText(String.format(Locale.US,"%02d:%02d",hourOfDay,minute));
                                }
                            },0,0,false).show();
                        }
                    });
                    date.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DatePickerDialog(EditAttendanceActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                    date.setText(String.format(Locale.US,"%d-%02d-%02d",year,month,dayOfMonth));
                                }
                            },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                        }
                    });

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String arrival = start.getText().toString();
                            String lTime = leave.getText().toString();
                            String day = date.getText().toString();

                            if(arrival.isEmpty()){
                                showSnackbarError(v,"Arrival time is mandatory!");
                                return;
                            }
                            if(lTime.isEmpty()){
                                showSnackbarError(v,"Leave time is mandatory!");
                                return;
                            }

                            if(day.isEmpty()){
                                showSnackbarError(v,"Day is mandatory!");
                                return;
                            }

                            attendance.leaveTime = lTime;
                            attendance.arrivalTime = arrival;
                            attendance.day = day;
                            db.collection("attendances").document(attendance.id).set(attendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });




    }
}
