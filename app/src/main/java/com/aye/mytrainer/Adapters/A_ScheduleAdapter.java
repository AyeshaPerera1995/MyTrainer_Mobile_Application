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

import com.aye.mytrainer.Model.Schedule;
import com.aye.mytrainer.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class A_ScheduleAdapter extends ArrayAdapter<Schedule> {

    private Context context;
    ArrayList<Schedule> schdules = new ArrayList<Schedule>();
    ArrayList<String> userList = new ArrayList<String>();
    private DatabaseReference mRef;
    private StorageReference storageReference,storageReference2;
    private TextView txtrequest, txtDate, txtUser;
    private Button btnapprove, btnreject, btnpending;
    String current_date,current_time, user_name = "";

    public A_ScheduleAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Schedule> objects, ArrayList<String> userlist) {
        super(context, resource, objects);
        this.schdules = objects;
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


        return v;
    }

}
