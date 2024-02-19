package com.aye.mytrainer.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.A_RequestList;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Users;
import com.aye.mytrainer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.zip.Inflater;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    Context context;
    private ArrayList<Users> userlist;
    private RecyclerViewClickListener listener;
    private SweetAlertDialog alertDialog;
    private LayoutInflater inflater;

    private DatabaseReference userRef;
    private String UserKey;
    private String status;


    public UserAdapter(Context context, ArrayList<Users> userlist,RecyclerViewClickListener listener) {
        this.context = context;
        this.userlist = userlist;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.a_usermanage_item, parent, false);
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

            rowHolder.btnBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    alertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                    alertDialog.setTitleText("Do you want to Block this User ?");
                    alertDialog.show();
                    Button btnConfirm = (Button) alertDialog.findViewById(R.id.confirm_button);
                    btnConfirm.setBackgroundColor(ContextCompat.getColor(context, R.color.darkBlue));
                    btnConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            blockUser(userlist.get(position).getU_id());

                        }
                    });
                    alertDialog.setConfirmText("YES");
                    alertDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            alertDialog.dismiss();

                        }
                    });

//                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
//                    View mView = inflater.inflate(R.layout.dialog_block,null);
//
//                    TextView msg =(TextView) mView.findViewById(R.id.message);
//                    msg.setText("Do you want to Block this User ?");
//                    Button btnYes = mView.findViewById(R.id.btnYes);
//                    Button btnNo = mView.findViewById(R.id.btnNo);
//
//                    mBuilder.setView(mView);
//                    blockDialog = mBuilder.create();
//
//                    btnYes.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                           blockUser(userlist.get(position).getU_id());
//                        }
//                    });
//
//                    btnNo.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            blockDialog.dismiss();
//                        }
//                    });
//
//
//                    blockDialog.show();
                }
            });

        }

    }

    private void blockUser(final String uid) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().child(uid).child("u_status").setValue("2");
//                Toast.makeText(context, "Removed!", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        TextView name,email,btnStatus,btnBlock;

        public ViewHolder(@NonNull View itemView,RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            name =(TextView) itemView.findViewById(R.id.userName);
            email =(TextView) itemView.findViewById(R.id.userEmail);
            btnStatus =(TextView) itemView.findViewById(R.id.btnStatus);
            btnBlock =(TextView) itemView.findViewById(R.id.btnBlock);

        }


        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }


    }


}