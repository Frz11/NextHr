package com.example.cristianion.nexthr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cristianion.nexthr.Adapters.HolidaysAdapter;
import com.example.cristianion.nexthr.Models.Day;
import com.example.cristianion.nexthr.Models.Department;
import com.example.cristianion.nexthr.Models.Holiday;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showSnackbarError;

public class HolidaysFragment extends Fragment {


    private String monthYear;
    private DocumentReference db = FirebaseFirestore.getInstance().collection("companies").document(currentCompany.id);
    private View holidaysView;
    private ProgressBar progressBar;
    private ArrayList<Day> days = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.holidays_layout,container,false);
    }

    private String searchForDay(String day,List<Holiday> holidays){
        for(Holiday holiday:holidays){
            for(String date : holiday.dates){
                if(date.equals(monthYear+"-"+day)){
                    return holiday.status;
                }
            }
        }
        return "";
    }
    private void addToRecycler(final RecyclerView recyclerView, final String stringDate){


        showProgress(true,holidaysView,progressBar);
        db.collection("holidays").whereEqualTo("employeeId",currentEmployee.id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Holiday> holidays = queryDocumentSnapshots.toObjects(Holiday.class);
                days.clear();
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
                    String dayLongName = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                    Day day1 = new Day();
                    day1.day = ""+day;
                    day1.dayText = dayLongName;
                    if(!(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)) {
                        day1.freeDay = false;
                        day1.status = "";
                    } else {
                        day1.freeDay = true;
                        day1.status = "";
                    }
                    day1.status = searchForDay(day1.day,holidays);
                    days.add(day1);

                    day++;
                    cal.add(Calendar.DAY_OF_MONTH,1);
                }
                showProgress(false,holidaysView,progressBar);
                Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();



                //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextView holidaysLeft = view.findViewById(R.id.holidaysLeft);
        holidaysLeft.setText(currentEmployee.holidays + " days left");
        Calendar calendar = Calendar.getInstance();
        holidaysView = view.findViewById(R.id.holidaysView);
        progressBar = view.findViewById(R.id.holidaysProgress);
        final RecyclerView recyclerView = view.findViewById(R.id.holidaysRecycler);
        FontAwesome makeSubmission = view.findViewById(R.id.makeSubmission);
        final HolidaysAdapter holidaysAdapter = new HolidaysAdapter(getContext(),days);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),5));

        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }
        recyclerView.setAdapter(holidaysAdapter);
        final TextView holidaysDate = view.findViewById(R.id.holidaysDate);
        holidaysDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                addToRecycler(recyclerView,holidaysDate.getText().toString());
                monthYear = holidaysDate.getText().toString();
            }
        });
        final YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(getContext(),new YearMonthPickerDialog.OnDateSetListener(){
            @Override
            public void onYearMonthSet(int year, int month) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

                holidaysDate.setText(dateFormat.format(calendar.getTime()));

            }
        });
        holidaysDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearMonthPickerDialog.show();
            }
        });
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        holidaysDate.setText(dateFormat.format(calendar.getTime()));


        makeSubmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Day> selectedDays = holidaysAdapter.getSelectedDays();
                if(selectedDays.size() == 0){
                    new AlertDialog.Builder(getContext())
                            .setTitle("No days selected!")
                            .setMessage("You must select a day in order to make a holiday request!")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setNegativeButton(android.R.string.ok,null).show();

                } else {
                    final ArrayList<String> dates = new ArrayList<>();
                    if(currentEmployee.departmentId.isEmpty()){
                        showSnackbarError(view,"There was a problem with the department...");
                        return;
                    }
                    for (Day day : selectedDays){
                        dates.add(monthYear + "-" +day.day);
                    }
                    if(currentEmployee.holidays < selectedDays.size()){
                        showSnackbarError(view,"You don't have enough holidays left...");
                    } else {
                        currentEmployee.holidays -= selectedDays.size();
                        db.collection("employees").document(currentEmployee.id).set(currentEmployee);
                    }
                    showProgress(true,view,progressBar);
                    db.collection("departments").document(currentEmployee.departmentId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Department department = documentSnapshot.toObject(Department.class);
                            if(department == null){
                                showProgress(false,view,progressBar);
                                showSnackbarError(view,"There was a problem with the department...");
                            } else {
                                Holiday holiday = new Holiday(UUID.randomUUID().toString(),currentEmployee.id,dates,"Pending",department.managerId);
                                db.collection("holidays").document(holiday.id).set(holiday).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        showProgress(false,view,progressBar);
                                        assert getFragmentManager() != null;
                                        getFragmentManager().beginTransaction().replace(R.id.Frame,new HolidaysFragment()).commit();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });

    }

}
