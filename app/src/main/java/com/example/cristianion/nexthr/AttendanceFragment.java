package com.example.cristianion.nexthr;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Attendance;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.twinkle94.monthyearpicker.picker.YearMonthPickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;

public class AttendanceFragment extends Fragment {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("companies").child(currentCompany.id);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.atendance_activity,null);
    }





    private void fillAttendanceTable(String stringDate, String employeeId, GridLayout layout) throws ParseException {


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        Date date = dateFormat.parse(stringDate+"-01");
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH,1);
        int myMonth = cal.get(Calendar.MONTH);

        int day = 1;
        while (myMonth == cal.get(Calendar.MONTH)){
            if(!(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)){
                RelativeLayout relativeLayout = new RelativeLayout(getContext());
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(7,7,7,7);
                relativeLayout.setLayoutParams(lp);
                relativeLayout.setBackgroundResource(R.color.bright_red);
                relativeLayout.setPadding(5,5,5,5);

                TextView attendanceDay = new TextView(getContext());
                attendanceDay.setTextColor(Color.WHITE);
                attendanceDay.setTextSize(28);
                attendanceDay.setText(day+"");
                RelativeLayout.LayoutParams lp_day = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp_day.addRule(RelativeLayout.CENTER_HORIZONTAL);
                attendanceDay.setLayoutParams(lp_day);
                attendanceDay.setId(View.generateViewId());
                relativeLayout.addView(attendanceDay);

                TextView duration = new TextView(getContext());
                duration.setTextSize(24);
                duration.setTextColor(Color.WHITE);
                duration.setText(R.string.notFound);

                RelativeLayout.LayoutParams lp_duration = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp_duration.addRule(RelativeLayout.BELOW,attendanceDay.getId());
                duration.setLayoutParams(lp_duration);
                relativeLayout.addView(duration);


                layout.addView(relativeLayout);

                Log.wtf("inside",day+"");
            }

            Log.wtf("wtf",day+"");
            day++;
            cal.add(Calendar.DAY_OF_MONTH,1);
        }
        /*
        ArrayList<String> days = new ArrayList<>();
        String[] thisDate = date.split("-");
        final TableLayout attendanceTable = getView().findViewById(R.id.RolesTable);
        attendanceTable.removeAllViews();

        db.child("attendance").orderByChild("day").startAt(date).endAt(date+"\uf8ff").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Attendance foundAttendance = child.getValue(Attendance.class);
                        TableRow tr = new TableRow(getContext());
                        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        TextView attendanceText = new TextView(getContext());
                        attendanceText.setText("Date: " + foundAttendance.day + "\n Arrival: " + foundAttendance.arrivalTime + "\n Left: " + foundAttendance.leaveTime);
                        attendanceText.setTextSize(20);
                        attendanceText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
                        attendanceText.setTextColor(Color.BLACK);
                        tr.addView(attendanceText);
                        attendanceTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar calendar = Calendar.getInstance();

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
                    fillAttendanceTable(attendanceFor.getText().toString(),currentEmployee.id,(GridLayout)view.findViewById(R.id.AttendanceTable));
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
        db.child("attendance").orderByChild("day").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot child : dataSnapshot.getChildren()){
                        Attendance foundAttendance = child.getValue(Attendance.class);
                        if(foundAttendance.employeeId.equals(currentEmployee.id)){
                            ((TextView) view.findViewById(R.id.ArrivalTime)).setText(foundAttendance.arrivalTime);
                            ((TextView) view.findViewById(R.id.LeavingTime)).setText(foundAttendance.leaveTime);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                db.child("attendance").orderByChild("day").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            boolean found = false;
                            for(DataSnapshot child : dataSnapshot.getChildren()){
                                Attendance foundAttendance = child.getValue(Attendance.class);
                                //we found an attendance for today, assume updating leaving time
                                if(foundAttendance.employeeId.equals(attendance.employeeId)) {
                                    if(attendance.leaveTime.length() == 0){
                                        showError(getContext(),"Leaving time is required because you previously set the arrival time!");
                                        return;
                                    }
                                    found = true;
                                    child.getRef().child("leaveTime").setValue(attendance.leaveTime).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            try {
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
                            if(!found){
                                db.child("attendance").child(attendance.id).setValue(attendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        try {
                                            getFragmentManager().beginTransaction().replace(R.id.Frame,AttendanceFragment.class.newInstance()).commit();
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        } catch (java.lang.InstantiationException e) {
                                            e.printStackTrace();
                                        }                                    }
                                });
                            }
                        } else {
                            db.child("attendance").child(attendance.id).setValue(attendance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    try {
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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
