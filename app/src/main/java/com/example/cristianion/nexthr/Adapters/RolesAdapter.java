package com.example.cristianion.nexthr.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
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

import org.w3c.dom.Text;

import java.util.List;

import static com.example.cristianion.nexthr.Utils.UtilFunc.showError;

public class RolesAdapter extends
        RecyclerView.Adapter<RolesAdapter.ViewHolder> {

    private List<Role> mRoles;
    private LayoutInflater mInflater;
    private AdapterView.OnItemClickListener mClickListenr;

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
    public RolesAdapter(List<Role> roles){
        mRoles = roles;
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

        TextView roleName = viewHolder.roleName;
        roleName.setText(role.name);
        FontAwesome editButton = viewHolder.editButton;
        FontAwesome deleteButton = viewHolder.deleteButton;
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showError(viewHolder.context,role.name);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mRoles.size();
    }



}
