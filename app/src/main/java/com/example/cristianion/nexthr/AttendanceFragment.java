package com.example.cristianion.nexthr;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.cristianion.nexthr.Models.Attendance;
import com.example.cristianion.nexthr.Models.Holiday;
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
import com.twinkle94.monthyearpicker.picker.YearMonthPickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class AttendanceFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.atendance_activity,null);
    }




    private static Attendance searchByDay(List<Attendance> attendances,String day){
        Log.wtf(day,day);
        for (Attendance current : attendances){
            if(current.day.equals(day)){
                return current;
            }
        }
        return null;
    }

    private void fillAttendanceTable(final String stringDate, String employeeId, final GridLayout layout, final ProgressBar progressBar) throws ParseException {

        showProgress(true,layout,progressBar);
        layout.removeAllViews();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            layout.setColumnCount(5);
        }
        db.collection("companies").document(currentCompany.id).collection("attendances")
                .whereEqualTo("employeeId",employeeId).whereLessThan("day",stringDate+"-31")
                .whereGreaterThan("day",stringDate+"-01").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                final List<Attendance> attendances = queryDocumentSnapshots.toObjects(Attendance.class);
                db.collection("companies").document(currentCompany.id).collection("holidays")
                        .whereEqualTo("employeeId",currentEmployee.id).whereEqualTo("status","Approved").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<Holiday> holidays = queryDocumentSnapshots.toObjects(Holiday.class);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar cal = Calendar.getInstance();
                        Date date = null;
                        try {
                            date = dateFormat.parse(stringDate+"-01");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        cal.setTime(date);
                        cal.set(Calendar.DAY_OF_MONTH,1);
                        int myMonth = cal.get(Calendar.MONTH);

                        int day = 1;
                        while (myMonth == cal.get(Calendar.MONTH)){
                            if(!(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)){
                                RelativeLayout relativeLayout = new RelativeLayout(getContext());
                                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                lp.setMargins(7,7,7,7);
                                lp.width=(int) getResources().getDimension(R.dimen.attendance_item_width);
                                relativeLayout.setLayoutParams(lp);
                                relativeLayout.setPadding(5,5,5,5);

                                TextView attendanceDay = new TextView(getContext());
                                attendanceDay.setTextColor(Color.WHITE);
                                attendanceDay.setTextSize(28);
                                attendanceDay.setText(day+"");
                                RelativeLayout.LayoutParams lp_day = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                lp_day.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                attendanceDay.setLayoutParams(lp_day);
                                attendanceDay.setId(View.generateViewId());

                                TextView duration = new TextView(getContext());
                                duration.setTextSize(24);
                                duration.setTextColor(Color.WHITE);

                                RelativeLayout.LayoutParams lp_duration = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                lp_duration.addRule(RelativeLayout.BELOW,attendanceDay.getId());
                                lp_duration.addRule(RelativeLayout.CENTER_HORIZONTAL);
                                duration.setLayoutParams(lp_duration);

                                Attendance currentAttendance;
                                if(day > 9) {
                                    currentAttendance = searchByDay(attendances, stringDate + "-" + day);
                                } else {
                                    currentAttendance = searchByDay(attendances, stringDate + "-" + "0"+day);
                                }
                                if(currentAttendance != null){
                                    if(currentAttendance.leaveTime.length() == 0) {
                                        relativeLayout.setBackgroundResource(R.color.bright_orange);
                                        duration.setText(currentAttendance.arrivalTime+"-"+"?????");
                                    } else {
                                        relativeLayout.setBackgroundResource(R.color.bright_blue);
                                        duration.setText(currentAttendance.arrivalTime+"-"+currentAttendance.leaveTime);
                                    }
                                } else {
                                    relativeLayout.setBackgroundResource(R.color.bright_red);
                                    duration.setText(R.string.notFound);


                                }
                                for(Holiday holiday : holidays){
                                    for (String date1 : holiday.dates) {
                                        if (date1.equals(String.format(Locale.US, "%s-%d", stringDate, day))) {
                                            relativeLayout.setBackgroundResource(R.color.colorPrimaryDark);
                                            duration.setText("Holiday");
                                            break;
                                        }
                                    }
                                }
                                relativeLayout.addView(attendanceDay);
                                relativeLayout.addView(duration);
                                layout.addView(relativeLayout);


                            }

                            day++;
                            cal.add(Calendar.DAY_OF_MONTH,1);
                        }
                        showProgress(false,layout,progressBar);
                    }
                });

            }
        });



    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ProgressBar progressBar = view.findViewById(R.id.AttendanceProgress);
        Calendar calendar = Calendar.getInstance();
        final TextView arrival = view.findViewById(R.id.ArrivalTime);
        final TextView leave = view.findViewById(R.id.LeavingTime);

        arrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        arrival.setText(String.format(Locale.US,"%02d:%02d", hourOfDay, minute));
                        if(arrival.getText().toString().compareTo(leave.getText().toString()) > 0 && !leave.getText().toString().isEmpty()){
                            showSnackbarError(getView(),"Arrival time cannot be greater than leave time!");
                            arrival.setText("");
                        }
                    }
                },0,0,false);
                timePickerDialog.show();
            }
        });
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int hourOfDay, int minute) {
                        leave.setText(String.format(Locale.US,"%02d:%02d", hourOfDay, minute));
                        if(arrival.getText().toString().compareTo(leave.getText().toString()) > 0){
                            showSnackbarError(view,"Arrival time cannot be greater than leave time!");
                            leave.setText("");
                        }

                    }
                },0,0,false);
                timePickerDialog.show();
            }
        });

        final TextView attendanceFor = view.findViewById(R.id.attendanceFor);
        attendanceFor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    fillAttendanceTable(attendanceFor.getText().toString(),currentEmployee.id,(GridLayout)view.findViewById(R.id.AttendanceTable),progressBar);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        attendanceFor.setText(dateFormat.format(calendar.getTime()));


        final YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(getContext(),new YearMonthPickerDialog.OnDateSetListener(){
            @Override
            public void onYearMonthSet(int year, int month) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

                attendanceFor.setText(dateFormat.format(calendar.getTime()));

            }
        });

        //search for today attendance
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String date = df.format(c.getTime());
        db.collection("companies").document(currentEmployee.companyId).collection("attendances").
                whereEqualTo("day",date).whereEqualTo("employeeId",currentEmployee.id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot data : Objects.requireNonNull(task.getResult())){
                        Attendance foundAttendance = data.toObject(Attendance.class);
                        ((TextView) view.findViewById(R.id.ArrivalTime)).setText(foundAttendance.arrivalTime);
                        ((TextView) view.findViewById(R.id.LeavingTime)).setText(foundAttendance.leaveTime);
                        break;
                    }
                }
            }
        });


        Button sign = view.findViewById(R.id.signAttendanceButton);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String arrivalTime = ((TextView) view.findViewById(R.id.ArrivalTime)).getText().toString();
                final String leaveTime = ((TextView) view.findViewById(R.id.LeavingTime)).getText().toString();
                if(arrivalTime.length() == 0){
                    showError(getContext(),"Arrival time cannot be empty!");
                    return;
                }
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String date = df.format(c.getTime());
                final Attendance attendance = new Attendance(UUID.randomUUID().toString(),arrivalTime,leaveTime,currentEmployee.id,date);
                db.collection("companies").document(currentCompany.id).collection("attendances")
                        .whereEqualTo("day",date).whereEqualTo("employeeId",currentEmployee.id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(!Objects.requireNonNull(task.getResult()).isEmpty()){
                                for(QueryDocumentSnapshot data : task.getResult()){
                                    Attendance foundAttendance = data.toObject(Attendance.class);
                                    if(attendance.leaveTime.length() == 0){
                                        showError(getContext(),"Leaving time is required because you previously set the arrival time!");
                                        return;
                                    }
                                    db.collection("companies").document(currentCompany.id)
                                            .collection("attendances").document(foundAttendance.id).set(attendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            try {
                                                assert getFragmentManager() != null;
                                                getFragmentManager().beginTransaction().replace(R.id.Frame,AttendanceFragment.class.newInstance()).commit();
                                            } catch (IllegalAccessException e) {
                                                e.printStackTrace();
                                            } catch (java.lang.InstantiationException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    break;
                                }
                            } else {
                                db.collection("companies").document(currentCompany.id)
                                        .collection("attendances").document(attendance.id).set(attendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        try {
                                            assert getFragmentManager() != null;
                                            getFragmentManager().beginTransaction().replace(R.id.Frame,AttendanceFragment.class.newInstance()).commit();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        } catch (java.lang.InstantiationException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });

            }
        });

        attendanceFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearMonthPickerDialog.show();
            }
        });

    }
}
