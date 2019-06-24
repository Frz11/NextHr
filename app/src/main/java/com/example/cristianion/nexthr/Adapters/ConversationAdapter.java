package com.example.cristianion.nexthr.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cristianion.nexthr.ConversationActivity;
import com.example.cristianion.nexthr.Models.Employee;
import com.example.cristianion.nexthr.Models.Image;
import com.example.cristianion.nexthr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Random;

import static com.example.cristianion.nexthr.Utils.Global.currentCompany;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private Context context;
    private List<String> conversationIds;
    private int lastPosition = -1;
    private DocumentReference db = FirebaseFirestore.getInstance().collection("companies").document(currentCompany.id);



    public ConversationAdapter(Context context,List<String> conversationIds){
        this.context = context;
        this.conversationIds = conversationIds;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.conversation_list_item,viewGroup,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        setAnimation(viewHolder.itemView,i);
        String conversationId = conversationIds.get(i);
        db.collection("employees").document(conversationId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                final Employee employee = documentSnapshot.toObject(Employee.class);
                viewHolder.employeeName.setText(employee.firstName + " " + employee.lastName);
                viewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ConversationActivity.class);
                        intent.putExtra("employeeId",employee.id);
                        intent.putExtra("employeeName",employee.firstName + " " + employee.lastName);
                        context.startActivity(intent);
                    }
                });
            }
        });

        db.collection("images").whereEqualTo("userId",conversationId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<Image> images = queryDocumentSnapshots.toObjects(Image.class);
                for (Image image : images){
                    StorageReference storage = FirebaseStorage.getInstance().getReference("images/").child(currentCompany.id).child(image.id);
                    storage.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Glide.with(context).load(task.getResult()).into(viewHolder.image);

                        }
                    });
                    break;
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return conversationIds.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        TextView employeeName;
        ImageView image;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            employeeName = itemView.findViewById(R.id.employee_name);
            image = itemView.findViewById(R.id.imageView3);
            view = itemView.findViewById(R.id.conversationView);
        }
    }

    public void setAnimation(View view,int position){
        if(lastPosition < position){
            ScaleAnimation anim = new ScaleAnimation(0.0f,1.0f,0.0f,1.0f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
anim.setDuration(new Random().nextInt(1000));view.startAnimation(anim);
            lastPosition = position;
        }
    }
}
