package com.aye.mytrainer;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Adapters.WorkoutAdapter;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Schedule;
import com.aye.mytrainer.Model.Workouts;
import com.aye.mytrainer.Privilege.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ibotta.android.support.pickerdialogs.SupportedDatePickerDialog;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TimeSchedule extends AppCompatActivity {

    private DatePicker picker;
    private TextView textview1;
    private String UserKey, notes;
    private DatabaseReference tsRef;
    private Schedule s;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_schedule);

        textview1=(TextView)findViewById(R.id.textView1);
        picker=(DatePicker)findViewById(R.id.datePicker);


        UserKey = Prevalent.currentOnlineUser.getU_id();
        tsRef = FirebaseDB.getDbConnection().child("Schedules");
        final Query query = tsRef.child(UserKey).orderByChild("timestamp");

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
//                            Toast.makeText(TimeSchedule.this, "Snapshot not exists", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(TimeSchedule.this,Home.class);
                startActivity(i);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
