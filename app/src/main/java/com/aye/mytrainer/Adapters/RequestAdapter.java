package com.aye.mytrainer.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.aye.mytrainer.Model.Request;
import com.aye.mytrainer.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RequestAdapter extends ArrayAdapter<Request> {

    private Context context;
    ArrayList<Request> requests = new ArrayList<Request>();
    private DatabaseReference mRef;
    private StorageReference storageReference,storageReference2;
    private TextView txtrequest, txtDate;
    private Button btnapprove, btnreject, btnpending;
    String current_date,current_time = "";


    public RequestAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Request> objects) {
        super(context, resource, objects);
        this.requests = objects;
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
        if (requests.get(position).getStatus().equals("0")){
            txtDate.setText("Created on : "+requests.get(position).getCreated_date());
        }else if(requests.get(position).getStatus().equals("1")) {
            txtDate.setText("Approved on : "+requests.get(position).getApproved_date());
        }else if(requests.get(position).getStatus().equals("2")) {
            txtDate.setText("Rejected on : "+requests.get(position).getApproved_date());
        }


        return v;
    }

}
