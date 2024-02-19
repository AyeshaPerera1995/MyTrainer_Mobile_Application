package com.aye.mytrainer.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Users;
import com.aye.mytrainer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.ViewHolder>{

    Context context;
    private ArrayList<Users> userlist;
    private RecyclerViewClickListener listener;
    private AlertDialog blockDialog;
    private LayoutInflater inflater;

    private DatabaseReference userRef;
    private String UserKey;
    private String status;


    public AdminAdapter(Context context, ArrayList<Users> userlist, RecyclerViewClickListener listener) {
        this.context = context;
        this.userlist = userlist;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.a_adminmanage_item, parent, false);
        return new ViewHolder(v, listener);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        userRef = FirebaseDB.getDbConnection().child("Users");

        if (holder instanceof ViewHolder) {
            final ViewHolder rowHolder = (ViewHolder) holder;
            //set values of data here
            rowHolder.name.setText(userlist.get(position).getU_name());
            rowHolder.email.setText(userlist.get(position).getU_email());

            if (userlist.get(position).getU_status().equals("0")){
                rowHolder.btnStatus.setText("Active");
                rowHolder.btnStatus.setBackgroundResource(R.drawable.btn_circle_green);

            }else if(userlist.get(position).getU_status().equals("1")){
                rowHolder.btnStatus.setText("Deactive");
                rowHolder.btnStatus.setBackgroundResource(R.drawable.btn_circle_red);
            }

            rowHolder.btnStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (rowHolder.btnStatus.getText().equals("Deactive")){
                        changeActiveStatus("0",userlist.get(position).getU_id());
                        rowHolder.btnStatus.setText("Active");
                        rowHolder.btnStatus.setBackgroundResource(R.drawable.btn_circle_green);
                    }else if(rowHolder.btnStatus.getText().equals("Active")){
                        changeActiveStatus("1",userlist.get(position).getU_id());
                        rowHolder.btnStatus.setText("Deactive");
                        rowHolder.btnStatus.setBackgroundResource(R.drawable.btn_circle_red);
                    }
                }
            });

        }

    }

    private void changeActiveStatus(String s, final String uid) {
        status = s;
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (status.equals("1")){
                    dataSnapshot.getRef().child(uid).child("u_status").setValue("1");
                }else if(status.equals("0")){
                    dataSnapshot.getRef().child(uid).child("u_status").setValue("0");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private RecyclerViewClickListener mListener;
        TextView name,email,btnStatus;

        public ViewHolder(@NonNull View itemView,RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            name =(TextView) itemView.findViewById(R.id.userName);
            email =(TextView) itemView.findViewById(R.id.userEmail);
            btnStatus =(TextView) itemView.findViewById(R.id.btnStatus);

        }


        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }


    }


}