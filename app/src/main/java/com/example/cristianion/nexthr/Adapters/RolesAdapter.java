package com.example.cristianion.nexthr.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cristianion.nexthr.EditRoleActivity;
import com.example.cristianion.nexthr.FontAwesome;
import com.example.cristianion.nexthr.MenuActivity;
import com.example.cristianion.nexthr.Models.Role;
import com.example.cristianion.nexthr.R;
import com.example.cristianion.nexthr.RolesFragment;
import com.example.cristianion.nexthr.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;
import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;

public class RolesAdapter extends
        RecyclerView.Adapter<RolesAdapter.ViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Role> mRoles;
    private LayoutInflater mInflater;
    private AdapterView.OnItemClickListener mClickListenr;
    private FragmentManager fragmentManager;
    private int lastPosition = -1;

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView roleName;
        FontAwesome deleteButton;
        Context context;
        RelativeLayout viewLayout;
        ViewHolder(View itemView){
            super(itemView);
            context = itemView.getContext();
            roleName = itemView.findViewById(R.id.RoleName);
            deleteButton = itemView.findViewById(R.id.DeleteRole);
            viewLayout = itemView.findViewById(R.id.viewLayout);
        }
    }
    public RolesAdapter(List<Role> roles,FragmentManager manager){
        mRoles = roles;
        fragmentManager = manager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View roleView = inflater.inflate(R.layout.layout_rolelistitem,viewGroup,false);

        return new ViewHolder(roleView);
    }
    private void setAnimation(View view,int position){
        if(position > lastPosition){
            ScaleAnimation anim = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            anim.setDuration(new Random().nextInt(501));
            view.startAnimation(anim);
            lastPosition = position;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RolesAdapter.ViewHolder viewHolder, int i) {
        setAnimation(viewHolder.itemView,i);
        final Role role = mRoles.get(i);

        final TextView roleName = viewHolder.roleName;
        final RelativeLayout viewLayout = viewHolder.viewLayout;
        roleName.setText(role.name);
        FontAwesome deleteButton = viewHolder.deleteButton;
        if(role.name.equals("Admin")){
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            viewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //go to edit
                    Intent intent = new Intent(viewHolder.context, EditRoleActivity.class);
                    intent.putExtra("roleId",role.id);
                    ((Activity) viewHolder.context).startActivityForResult(intent,MenuActivity.editRoleCode);
                }
            });
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(viewHolder.context)
                        .setTitle("Delete!")
                        .setMessage("Do you really want to delete this item?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(!role.name.equals("Admin")) {
                                    db.collection("companies").document(currentCompany.id).
                                            collection("roles").document(role.id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            fragmentManager.beginTransaction().replace(R.id.Frame, new RolesFragment()).commit();

                                        }
                                    });
                                } else{
                                    showError(viewHolder.context,"Admin role cannot be deleted!");
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no,null).show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return mRoles.size();
    }



}
