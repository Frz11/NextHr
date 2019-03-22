package com.example.cristianion.nexthr.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Models.Role;
import com.example.cristianion.nexthr.Models.UserRole;
import com.example.cristianion.nexthr.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.List;
import java.util.Random;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.Global.currentEmployee;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.ViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Employee> employees;
    private LayoutInflater mInflater;
    private FragmentManager fragmentManager;
    private int lastPosition = -1;


    public EmployeesAdapter(List<Employee> employees,FragmentManager fragmentManager){
        this.employees = employees;
        this.fragmentManager = fragmentManager;
    }

    private void setAnimation(View view,int position){
        if(position > lastPosition){
            ScaleAnimation anim = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            anim.setDuration(new Random().nextInt(501));
            view.startAnimation(anim);
            lastPosition = position;
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View employeeView = inflater.inflate(R.layout.layout_employees_item,viewGroup,false);

        ViewHolder viewHolder = new ViewHolder(employeeView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        setAnimation(viewHolder.itemView,i);
        final Employee employee = employees.get(i);

        final TextView name = viewHolder.name;
        final TextView role = viewHolder.role;
        final ImageView image = viewHolder.profileImage;
        final RelativeLayout viewLayout = viewHolder.viewLayout;
        final Context context = viewHolder.context;

        db.collection("companies").document(currentCompany.id).collection("user_role")
                .whereEqualTo("userId",employee.id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot data : queryDocumentSnapshots){
                    UserRole ur = data.toObject(UserRole.class);
                    db.collection("companies").document(currentCompany.id).collection("roles")
                            .whereEqualTo("id",ur.roleId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot data : queryDocumentSnapshots){
                                if(role.getText().toString().length() > 24){
                                    break;
                                }
                                Role role1 = data.toObject(Role.class);
                                if(role.getText().toString().length() == 0){
                                    role.setText(role1.name);
                                    continue;
                                }
                                role.setText(String.format("%s,%s", role.getText().toString(), role1.name));
                            }
                        }
                    });
                }
                name.setText(String.format("%s %s", employee.lastName, employee.firstName));
                viewLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showError(context,"edit");
                    }
                });
            }
        });



    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name,role;
        ImageView profileImage;
        Context context;
        RelativeLayout viewLayout;
        ViewHolder(View itemView){
            super(itemView);
            context = itemView.getContext();
            name = itemView.findViewById(R.id.name);
            role = itemView.findViewById(R.id.role);
            profileImage = itemView.findViewById(R.id.profImage);
            viewLayout = itemView.findViewById(R.id.viewLayout);

        }
    }
}
