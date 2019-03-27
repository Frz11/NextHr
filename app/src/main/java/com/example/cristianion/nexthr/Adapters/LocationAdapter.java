package com.example.cristianion.nexthr.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.util.Util;
import com.example.cristianion.nexthr.EditLocationActivity;
import com.example.cristianion.nexthr.FontAwesome;
import com.example.cristianion.nexthr.LocationFragment;
import com.example.cristianion.nexthr.MenuActivity;
import com.example.cristianion.nexthr.Models.Location;
import com.example.cristianion.nexthr.R;
import com.example.cristianion.nexthr.Utils.UtilFunc;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Random;

import io.opencensus.internal.StringUtil;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Location> mLocations;
    private FragmentManager fragmentManager;
    private int lastPosition = -1;

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView street;
        TextView city;
        TextView info;
        FontAwesome deleteButton;
        Context context;
        RelativeLayout viewLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            city = itemView.findViewById(R.id.City);
            street = itemView.findViewById(R.id.street);
            info = itemView.findViewById(R.id.countryncounty);
            deleteButton = itemView.findViewById(R.id.deleteLocation);
            viewLayout = itemView.findViewById(R.id.locationRL);
        }
    }
    public LocationAdapter(List<Location> locations,FragmentManager manager){
        mLocations = locations;
        fragmentManager = manager;
    }
    @NonNull
    @Override
    public LocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context =  viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View locationView = inflater.inflate(R.layout.layout_location_item,viewGroup,false);

        return  new ViewHolder(locationView);
    }

    @Override
    public void onBindViewHolder(@NonNull final LocationAdapter.ViewHolder viewHolder, int i) {
        setAnimation(viewHolder.itemView,i);
        final Location location = mLocations.get(i);
        viewHolder.street.setText(UtilFunc.truncate(location.street,30));
        viewHolder.city.setText(UtilFunc.truncate(location.city,20));
        viewHolder.info.setText(UtilFunc.truncate(location.country+","+location.county,30));
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("companies").document(currentCompany.id)
                        .collection("locations").document(location.id).delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                fragmentManager.beginTransaction().replace(R.id.Frame,new LocationFragment())
                                        .commit();
                            }
                        });
            }
        });
        viewHolder.viewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(viewHolder.context, EditLocationActivity.class);
                edit.putExtra("locationId",location.id);
                ((Activity) viewHolder.context).startActivityForResult(edit,MenuActivity.editLocationCode);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLocations.size();
    }

    private void setAnimation(View view,int position){
        if(lastPosition < position){
            ScaleAnimation anim = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            anim.setDuration(new Random().nextInt(501));
            view.startAnimation(anim);
            lastPosition = position;
        }
    }
}
