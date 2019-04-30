package com.example.cristianion.nexthr;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Attendance;
import com.example.cristianion.nexthr.Models.Holiday;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.twinkle94.monthyearpicker.picker.YearMonthPickerDialog;

import org.w3c.dom.Text;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;
import static com.example.cristianion.nexthr.Utils.UtilFunc.numberAnimation;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class SalaryFragment extends Fragment {

    private DocumentReference db = FirebaseFirestore.getInstance().collection("companies")
            .document(currentCompany.id);
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_salary,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).setTitle("Salary");
        Calendar calendar = Calendar.getInstance();

        if (currentEmployee.salary.isEmpty() || currentEmployee.salary.equals("0")){
             showSnackbarError(view,"Salary not set! You cannot use this functionality yet!");
             return;
        }
        final ProgressBar progressBar = view.findViewById(R.id.SalaryProgress);
        final View salaryView = view.findViewById(R.id.SalaryView);
        salaryView.setVisibility(View.VISIBLE);
        final TextView salaryFor = view.findViewById(R.id.salaryFor);
        final TextView salH = view.findViewById(R.id.salPerHour);
        final TextView salary = view.findViewById(R.id.salary);
        final TextView hours = view.findViewById(R.id.workedHours);
        salH.setText(currentEmployee.salary + "/hour");
        salaryFor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateSalary(salaryFor.getText().toString(),salary,hours,salaryView,progressBar);
            }
        });
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        salaryFor.setText(dateFormat.format(calendar.getTime()));
        final YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(getContext(),new YearMonthPickerDialog.OnDateSetListener(){
            @Override
            public void onYearMonthSet(int year, int month) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

                salaryFor.setText(dateFormat.format(calendar.getTime()));

            }
        });
        salaryFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearMonthPickerDialog.show();
            }
        });


    }

    private void calculateSalary(final String date, final TextView salary, final TextView hours, final View view, final ProgressBar progressBar){
        showProgress(true,view,progressBar);
        db.collection("attendances")
                .whereEqualTo("employeeId",currentEmployee.id).whereLessThan("day",date+"-31")
                .whereGreaterThan("day",date+"-01").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                final List<Attendance> attendances = queryDocumentSnapshots.toObjects(Attendance.class);
                db.collection("holidays").whereEqualTo("employeeId",currentEmployee.id).whereEqualTo("status","Approved")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        double totalHours = 0;
                        double totalSalary = 0;
                        List<Holiday> holidays = queryDocumentSnapshots.toObjects(Holiday.class);
                        for (Holiday holiday : holidays){
                            for (String dateH : holiday.dates){
                                if(dateH.substring(0,7).equals(date)){
                                    totalHours += 8;
                                    totalSalary += (Double.parseDouble(currentEmployee.salary))*8;
                                }
                            }
                        }

                        for(Attendance attendance : attendances){
                            boolean cont = true;
                            if(!attendance.arrivalTime.isEmpty() && !attendance.leaveTime.isEmpty() && !attendance.day.isEmpty()){
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm");
                                for(Holiday holiday : holidays){
                                    for (String date1 : holiday.dates){
                                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                                        try {
                                            Date attDate = dateFormat.parse(attendance.day);
                                            Date holiDate = dateFormat.parse(date1);
                                            Log.wtf(attendance.day,date1);
                                            if(attDate.compareTo(holiDate) == 0){
                                                cont = false;
                                            }
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                if(!cont){
                                    continue;
                                }
                                try {
                                    Date date1 = format.parse(attendance.day + " " + attendance.arrivalTime);
                                    Date date2 = format.parse(attendance.day + " " + attendance.leaveTime);
                                    Log.wtf(attendance.day,date1.getTime() + "--" + date2.getTime());

                                    double diff = date2.getTime() - date1.getTime();
                                    double seconds = diff / 1000;
                                    double minutes = (seconds / 60);
                                    double hours = (minutes / 60);

                                    totalHours += hours;
                                    totalSalary += (Double.parseDouble(currentEmployee.salary))*hours;
                                    Log.wtf(attendance.day,minutes + "xx" + hours);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        int h = (int)(totalHours*60)/60;
                        int m = (int)(totalHours*60)%60;
                        salary.setText(String.format(Locale.US,"%.2f",totalSalary));
                        hours.setText(String.format("from %s hours and %s minutes of work", h,m));
                        showProgress(false,view,progressBar);
                        numberAnimation(salary);
                    }
                });


            }
        });

    }
}
