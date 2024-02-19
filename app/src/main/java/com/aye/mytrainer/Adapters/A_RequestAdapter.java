package com.aye.mytrainer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.A_RequestList;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Request;
import com.aye.mytrainer.Model.Users;
import com.aye.mytrainer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class A_RequestAdapter extends ArrayAdapter<Request> {

    private Context context;
    ArrayList<Request> requests = new ArrayList<Request>();
    ArrayList<String> userList = new ArrayList<String>();
    private DatabaseReference mRef;
    private StorageReference storageReference,storageReference2;
    private TextView txtrequest, txtDate, txtUser;
    private Button btnapprove, btnreject, btnpending;
    String current_date,current_time, user_name = "";

    public A_RequestAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Request> objects, ArrayList<String> userlist) {
        super(context, resource, objects);
        this.requests = objects;
        this.userList = userlist;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater =(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.requestlist_item,null);

        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        current_time = currentTime.format(calendar.getTime());

        txtrequest = v.findViewById(R.id.txtrequest);
        txtDate = v.findViewById(R.id.txtDate);
        txtUser = v.findViewById(R.id.txtUser);
        btnapprove = v.findViewById(R.id.btnapprove);
        btnreject = v.findViewById(R.id.btnreject);
        btnpending = v.findViewById(R.id.btnpending);

        if (requests.get(position).getStatus().equals("0")){ //pending
            btnpending.setVisibility(View.VISIBLE);
            btnapprove.setVisibility(View.INVISIBLE);
            btnreject.setVisibility(View.INVISIBLE);
        }else if (requests.get(position).getStatus().equals("1")){ //approve
            btnpending.setVisibility(View.INVISIBLE);
            btnapprove.setVisibility(View.VISIBLE);
            btnreject.setVisibility(View.INVISIBLE);
        }else if (requests.get(position).getStatus().equals("2")){ // reject
            btnpending.setVisibility(View.INVISIBLE);
            btnapprove.setVisibility(View.INVISIBLE);
            btnreject.setVisibility(View.VISIBLE);
        }

        txtrequest.setText(requests.get(position).getRequest());

        for (int i = 0; i < userList.size(); i++){
            String uid = userList.get(i).split(",")[0];
            if (uid.equals(requests.get(position).getUid())){
                txtUser.setText("Requested by : "+userList.get(i).split(",")[1]);
            }
        }

        if (requests.get(position).getStatus().equals("0")){
            txtDate.setText("Created on : "+requests.get(position).getCreated_date());
        }else if(requests.get(position).getStatus().equals("1")) {
            txtDate.setText("Approved on : "+requests.get(position).getApproved_date());
        }else if(requests.get(position).getStatus().equals("2")) {
            txtDate.setText("Rejected on : "+requests.get(position).getApproved_date());
        }

        btnpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseDB.getDbConnection().child("Requests").child(requests.get(position).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        for (final DataSnapshot ds: dataSnapshot.getChildren()) {
                            final Request values = ds.getValue(Request.class);
                            if (values.getRequest().equals(requests.get(position).getRequest())){

                                final SweetAlertDialog alertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                                alertDialog.setTitleText("Are you sure you want to approve this request?");
                                alertDialog.show();
                                Button btnConfirm = (Button) alertDialog.findViewById(R.id.confirm_button);
                                btnConfirm.setBackgroundColor(ContextCompat.getColor(context, R.color.darkBlue));
                                btnConfirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        ds.getRef().child("status").setValue("1");
                                        ds.getRef().child("approved_date").setValue(current_date);
                                        alertDialog.dismiss();
                                        Intent i = new Intent(context,A_RequestList.class);
                                        i.putExtra("AdminKey", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        context.startActivity(i);

                                    }
                                });
                                alertDialog.setConfirmText("YES");
                                alertDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        ds.getRef().child("status").setValue("2");
                                        ds.getRef().child("approved_date").setValue(current_date);
                                        alertDialog.dismiss();
                                        Intent i = new Intent(context,A_RequestList.class);
                                        i.putExtra("AdminKey", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        context.startActivity(i);
                                    }
                                });






                            }
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });


        return v;
    }

}
