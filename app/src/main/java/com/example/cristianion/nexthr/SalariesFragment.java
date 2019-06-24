package com.example.cristianion.nexthr;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Attendance;
import com.example.cristianion.nexthr.Models.Department;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Models.Holiday;
import com.example.cristianion.nexthr.Utils.CSV;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.twinkle94.monthyearpicker.picker.YearMonthPickerDialog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showProgress;

public class SalariesFragment extends Fragment {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.salaries_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int permission = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        final TextView dateD = view.findViewById(R.id.date);
        final Button generate = view.findViewById(R.id.generateButton);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);

        final Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        dateD.setText(dateFormat.format(calendar.getTime()));

        final YearMonthPickerDialog yearMonthPickerDialog = new YearMonthPickerDialog(getContext(),new YearMonthPickerDialog.OnDateSetListener(){
            @Override
            public void onYearMonthSet(int year, int month) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");

                dateD.setText(dateFormat.format(calendar.getTime()));

            }
        });
        dateD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yearMonthPickerDialog.show();
            }
        });



        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd HH:mm");

                if(!isExternalStorageWritable()){
                    showError(getContext(),"External storage not available!");
                    return;
                }
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "salaries-nexthr");
                file.mkdirs();
                final String date = dateD.getText().toString();

                showError(getContext(),"The csv will be available in your documents folder, please wait!");
                showProgress(true,generate,progressBar);

                try {
                    final Writer writer = new FileWriter(file.getAbsoluteFile() + "/salaries" + date + ".csv");
                    String[] values = {
                            "Name", "Salary","Hours", "Month", "Department"
                    };
                    CSV.writeLine(writer, Arrays.asList(values));
                    Log.wtf("ok",date);
                    db.collection("companies").document(currentCompany.id).collection("employees").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            final List<Employee> employees = queryDocumentSnapshots.toObjects(Employee.class);
                            final int[] index = {0};
                            for(final Employee employee : employees){
                                Log.wtf(employee.firstName,employee.lastName);
                                 db.collection("companies").document(currentCompany.id).collection("attendances").whereGreaterThan("day",date + "-01")
                                        .whereLessThan("day", date + "-31").whereEqualTo("employeeId",employee.id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        final List<Attendance> attendances = queryDocumentSnapshots.toObjects(Attendance.class);
                                        final float[] salary = {0};
                                        final int[] hours = {0};
                                        for(Attendance attendance : attendances){
                                            Log.wtf(attendance.employeeId,attendance.day);
                                            try {
                                                Date date1 = format.parse(attendance.day + " " + attendance.arrivalTime);
                                                Date date2 = format.parse(attendance.day + " " + attendance.leaveTime);

                                                double diff = date2.getTime() - date1.getTime();
                                                double seconds = diff / 1000;
                                                double minutes = (seconds / 60);
                                                double hoursAux = (minutes / 60);

                                                hours[0] += hoursAux;
                                                salary[0] += (Double.parseDouble(employee.salary))*hoursAux;

                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                        Log.wtf("Mooving to holidays",employee.id);
                                        db.collection("companies").document(currentCompany.id).collection("holidays").whereEqualTo("employeeId",employee.id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                List<Holiday> holidays = queryDocumentSnapshots.toObjects(Holiday.class);
                                                for (Holiday holiday : holidays){
                                                    Log.wtf("holiday",holiday.id);
                                                    for (String dateH : holiday.dates){
                                                        if(dateH.substring(0,7).equals(date) && !findinAttendance(dateH,attendances)){
                                                            hours[0] += 8;
                                                            salary[0] += (Double.parseDouble(employee.salary))*8;
                                                        }
                                                    }
                                                }
                                                db.collection("companies").document(currentCompany.id).collection("departments").whereEqualTo("id",employee.departmentId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        Log.wtf("Mooving to departments",employee.id);
                                                        for(Department department: queryDocumentSnapshots.toObjects(Department.class)){
                                                            Log.wtf(employee.id,department.name);
                                                            String[] values = {employee.firstName+ " " + employee.lastName, ""+salary[0], "" + hours[0],date,department.name};
                                                            try {
                                                                CSV.writeLine(writer, Arrays.asList(values));
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }
                                                            break;
                                                        }
                                                        index[0]++;
                                                        if(index[0] == employees.size()){
                                                            showProgress(false,generate,progressBar);
                                                            try {
                                                                writer.close();
                                                            } catch (IOException e) {
                                                                e.printStackTrace();
                                                            }

                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });

                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean findinAttendance(String date, List<Attendance> attendances){
        for(Attendance attendance : attendances){
            if(date.equals(attendance.day)){
                return true;
            }
        }
        return false;
    }

}
