package com.example.cristianion.nexthr.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.example.cristianion.nexthr.FontAwesome;
import com.example.cristianion.nexthr.Models.Role;
import com.example.cristianion.nexthr.R;
import com.example.cristianion.nexthr.RolesFragment;
import com.example.cristianion.nexthr.Utils.Global;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.List;

import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;

public class RolesAdapter extends
        RecyclerView.Adapter<RolesAdapter.ViewHolder> {

    private DatabaseReference db = FirebaseDatabase.getInstance().getReference("companies").child(Global.currentCompany.id).child("roles");
    private List<Role> mRoles;
    private LayoutInflater mInflater;
    private AdapterView.OnItemClickListener mClickListenr;
    private FragmentManager fragmentManager;

    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView roleName;
        FontAwesome editButton,deleteButton;
        Context context;
        ViewHolder(View itemView){
            super(itemView);
            context = itemView.getContext();
            roleName = itemView.findViewById(R.id.RoleName);
            editButton = itemView.findViewById(R.id.EditRole);
            deleteButton = itemView.findViewById(R.id.DeleteRole);
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

        ViewHolder viewHolder = new ViewHolder(roleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RolesAdapter.ViewHolder viewHolder, int i) {
        final Role role = mRoles.get(i);

        final TextView roleName = viewHolder.roleName;
        roleName.setText(role.name);
        FontAwesome editButton = viewHolder.editButton;
        FontAwesome deleteButton = viewHolder.deleteButton;
        if(role.name.equals("Admin")){
            editButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        }
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showError(viewHolder.context,role.name);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!role.name.equals("Admin")) {
                    db.child(role.id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            fragmentManager.beginTransaction().replace(R.id.Frame, new RolesFragment()).commit();
                        }
                    });
                } else{
                    showError(viewHolder.context,"Admin role cannot be deleted!");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mRoles.size();
    }



}
