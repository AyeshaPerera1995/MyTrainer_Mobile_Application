package com.aye.mytrainer;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Schedule;
import com.aye.mytrainer.Privilege.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleFragment extends Fragment {

    private DatePicker picker;
    private TextView textview1;
    private String notes;
    private DatabaseReference tsRef;
    private Schedule s;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule,container,false);

        textview1=(TextView)view.findViewById(R.id.textView1);
        picker=(DatePicker)view.findViewById(R.id.datePicker);

        tsRef = FirebaseDB.getDbConnection().child("Schedules");
        final Query query = tsRef.child(Prevalent.currentOnlineUser.getU_id()).orderByChild("timestamp");

        picker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                notes = "Scheduled Events :\n\n";
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                s = ds.getValue(Schedule.class);
                                if (s.getDate().equals(getCurrentDate())) {
                                    notes += "◉. "+s.getNote()+"\n";

                                }
                            }

                            textview1.setText(notes);

                        }else{
//                            Toast.makeText(getActivity(), "Snapshot not exists", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return view;
    }

    public String getCurrentDate(){
        StringBuilder builder=new StringBuilder();
        builder.append(picker.getDayOfMonth()+"/");
        builder.append((picker.getMonth() + 1)+"/");//month is 0 based
        builder.append(picker.getYear());

        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMM yyyy");

        Date date = null;
        String formatDate = null;

        try {
            date = inputFormat.parse(builder.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formatDate = outputFormat.format(date);
        return formatDate;
    }
}
