package com.example.cristianion.nexthr.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cristianion.nexthr.EditDepartmentActivity;
import com.example.cristianion.nexthr.FontAwesome;
import com.example.cristianion.nexthr.MenuActivity;
import com.example.cristianion.nexthr.Models.Department;
import com.example.cristianion.nexthr.R;
import com.example.cristianion.nexthr.Utils.UtilFunc;

import java.util.List;
import java.util.Random;

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder> {

    private List<Department> mDepartments;
    private FragmentManager fragmentManager;
    private int lastPosition = 1;

    public DepartmentAdapter(List<Department> departments,FragmentManager manager){
        mDepartments = departments;
        fragmentManager = manager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View departmentView = inflater.inflate(R.layout.departmentitem_layout,viewGroup,false);

        return new ViewHolder(departmentView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        setAnimation(viewHolder.itemView,i);
        final Department department = mDepartments.get(i);
        viewHolder.departmentName.setText(UtilFunc.truncate(department.name,30));
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        viewHolder.viewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(viewHolder.context, EditDepartmentActivity.class);
                intent.putExtra("departmentId",department.id);
                ((Activity) viewHolder.context).startActivityForResult(intent, MenuActivity.editDepartmentCode);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDepartments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView departmentName;
        FontAwesome deleteButton;
        RelativeLayout viewLayout;
        Context context;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            departmentName = itemView.findViewById(R.id.departmentName);
            deleteButton = itemView.findViewById(R.id.deleteDepartment);
            viewLayout = itemView.findViewById(R.id.departmentLayout);
            context = itemView.getContext();
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