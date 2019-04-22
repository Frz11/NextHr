package com.example.cristianion.nexthr.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cristianion.nexthr.FontAwesome;
import com.example.cristianion.nexthr.HolidaysRequestsFragment;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Models.Holiday;
import com.example.cristianion.nexthr.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Random;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;

public class HolidaysReqAdapter extends RecyclerView.Adapter<HolidaysReqAdapter.ViewHolder> {

    private List<Holiday> holidays;
    private int lastPosition = -1;
    private DocumentReference db = FirebaseFirestore.getInstance().collection("companies").document(currentCompany.id);
    private Context context;
    private FragmentManager fragmentManager;

    public HolidaysReqAdapter(List<Holiday> holidays, Context context,FragmentManager fragmentManager){
        super();
        this.context = context;
        this.holidays = holidays;
        this.fragmentManager = fragmentManager;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View reqView = inflater.inflate(R.layout.holiday_request_item,viewGroup,false);


        return new ViewHolder(reqView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        setAnimation(viewHolder.itemView,i);
        final Holiday holiday = holidays.get(i);
        Log.wtf("wtf",holiday.employeeId);
        db.collection("employees").document(holiday.employeeId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Employee employee = documentSnapshot.toObject(Employee.class);
                if(employee == null){
                    return;
                }
                viewHolder.employeeName.setText(employee.lastName + " " + employee.firstName);
                if(holiday.status.equals("Approved")){
                    viewHolder.reqView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
                    viewHolder.approve.setVisibility(View.GONE);

                } else if(holiday.status.equals("Rejected")){
                    viewHolder.reqView.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_dark));
                    viewHolder.reject.setVisibility(View.GONE);

                }
                for (String date: holiday.dates) {
                    if(viewHolder.datesList.getText().toString().isEmpty()){
                        viewHolder.datesList.setText(date);
                    } else {
                        viewHolder.datesList.setText(viewHolder.datesList.getText().toString() + "\n" + date);
                    }
                }
                viewHolder.reqView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(viewHolder.datesList.getVisibility() == View.GONE){
                            viewHolder.datesList.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.datesList.setVisibility(View.GONE);
                        }
                    }
                });
                viewHolder.cdown.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(viewHolder.datesList.getVisibility() == View.GONE){
                            viewHolder.datesList.setVisibility(View.VISIBLE);
                        } else {
                            viewHolder.datesList.setVisibility(View.GONE);
                        }
                    }
                });
                viewHolder.approve.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(context)
                                .setTitle("Approve!")
                                .setMessage("Do you really want to approve this request?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        holiday.status = "Approved";
                                        db.collection("holidays").document(holiday.id).set(holiday)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        fragmentManager.beginTransaction().replace(R.id.Frame,new HolidaysRequestsFragment()).commit();
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton(android.R.string.no,null).show();
                    }
                });
                viewHolder.reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(context)
                                .setTitle("Reject!")
                                .setMessage("Do you really want to reject this request?")
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                            holiday.status = "Rejected";
                                            db.collection("holidays").document(holiday.id).set(holiday)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            fragmentManager.beginTransaction().replace(R.id.Frame,new HolidaysRequestsFragment()).commit();
                                                        }
                                                    });
                                    }
                                })
                                .setNegativeButton(android.R.string.no,null).show();
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return holidays.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView employeeName;
        FontAwesome approve;
        FontAwesome reject;
        FontAwesome cdown;
        View reqView;
        TextView datesList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.employee_name);
            approve = itemView.findViewById(R.id.approveButton);
            reject = itemView.findViewById(R.id.rejectButton);
            reqView = itemView.findViewById(R.id.view);
            datesList = itemView.findViewById(R.id.datesList);
            cdown = itemView.findViewById(R.id.cdown);
        }
    }
    public void setAnimation(View view,int position){
        if(lastPosition < position){
            ScaleAnimation anim = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            anim.setDuration(new Random().nextInt(501));
            view.startAnimation(anim);
            lastPosition = position;
        }
    }
}
