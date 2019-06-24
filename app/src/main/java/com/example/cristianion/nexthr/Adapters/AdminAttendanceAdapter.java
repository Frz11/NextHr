package com.example.cristianion.nexthr.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.example.cristianion.nexthr.EditAttendanceActivity;
import com.example.cristianion.nexthr.MenuActivity;
import com.example.cristianion.nexthr.Models.Attendance;
import com.example.cristianion.nexthr.Models.Department;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Random;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;

public class AdminAttendanceAdapter extends RecyclerView.Adapter<AdminAttendanceAdapter.ViewHolder> {

    private List<Attendance> attendances;
    private Context context;
    private int lastPosition = -1;
    private DocumentReference db = FirebaseFirestore.getInstance().collection("companies").document(currentCompany.id);

    public AdminAttendanceAdapter(List<Attendance> attendances, Context context){
        super();
        this.attendances = attendances;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.admin_att_item,viewGroup,false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Attendance attendance = attendances.get(i);
        setAnimation(viewHolder.itemView,i);
        Log.wtf(attendance.day,attendance.employeeId);
        viewHolder.start.setText(attendance.arrivalTime);
        viewHolder.end.setText(attendance.leaveTime);
        db.collection("employees").document(attendance.employeeId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Employee employee = documentSnapshot.toObject(Employee.class);
                if(employee != null){
                    viewHolder.employeeName.setText(employee.lastName + " " + employee.firstName);
                    db.collection("departments").document(employee.departmentId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Department department = documentSnapshot.toObject(Department.class);
                            if(department != null){
                                viewHolder.department.setText(department.name);
                            }
                        }
                    });
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(context, EditAttendanceActivity.class);
                            intent.putExtra("employeeName",employee.firstName + " " + employee.lastName);
                            intent.putExtra("attendanceId",attendance.id);
                            ((Activity) context).startActivityForResult(intent, MenuActivity.editAttendanceCode);
                        }
                    });
                    viewHolder.horizontal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, EditAttendanceActivity.class);
                            intent.putExtra("employeeName",employee.firstName + " " + employee.lastName);
                            intent.putExtra("attendanceId",attendance.id);
                            ((Activity) context).startActivityForResult(intent, MenuActivity.editAttendanceCode);
                        }
                    });
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return attendances.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView employeeName;
        TextView department;
        TextView start;
        TextView end;
        View view;
        View horizontal;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.employee_name);
            department = itemView.findViewById(R.id.department);
            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.end);
            view = itemView.findViewById(R.id.view);
            horizontal = itemView.findViewById(R.id.horizontal);
        }

    }

    private void setAnimation(View view,int position){
        if(position > lastPosition){
            ScaleAnimation anim = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
anim.setDuration(new Random().nextInt(1000));view.startAnimation(anim);
            lastPosition = position;
        }
    }
}
