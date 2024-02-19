package com.aye.mytrainer;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class A_SheduleDates extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private String UserKey,UserImage;
    private Button selectedDate, btnSaveNote;
    private EditText txtNote;
    private String current_date;
    private DatabaseReference scheduleRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_shedule_dates);

        Bundle b = getIntent().getExtras();
        UserKey = b.getString("UserKey");
        UserImage = b.getString("UserImage");

        scheduleRef = FirebaseDB.getDbConnection().child("Schedules");

        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        selectedDate = findViewById(R.id.btnChangeDate);
        selectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(A_SheduleDates.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();


                Toast.makeText(A_SheduleDates.this, "set", Toast.LENGTH_SHORT).show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(0);
                cal.set(year,month,day,0,0,0);
                Date chosenDate = cal.getTime();

                DateFormat df_medium_uk = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
                String date = df_medium_uk.format(chosenDate);
                selectedDate.setText(date);

            }
        };


        txtNote = findViewById(R.id.txtNote);
        btnSaveNote = findViewById(R.id.btnSaveNote);

        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(selectedDate.getText().toString()) | selectedDate.getText().toString().equals("Choose Date")){
                    Toast.makeText(A_SheduleDates.this, "Please select a Date", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(txtNote.getText().toString())){
                    Toast.makeText(A_SheduleDates.this, "Please fill the Note", Toast.LENGTH_SHORT).show();
                }else {

                    HashMap<String, Object> scheduleMap = new HashMap<String, Object>();
                    scheduleMap.put("date", selectedDate.getText().toString());
                    scheduleMap.put("note", txtNote.getText().toString());
                    scheduleMap.put("created_date", current_date);
                    scheduleMap.put("timestamp", ServerValue.TIMESTAMP);

                    scheduleRef.child(UserKey).push().setValue(scheduleMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SweetAlertDialog alertDialog = new SweetAlertDialog(A_SheduleDates.this, SweetAlertDialog.SUCCESS_TYPE);
                                alertDialog.setTitleText("Schedule Note saved successfully.");
                                alertDialog.show();
                                Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                                btn.setBackgroundColor(ContextCompat.getColor(A_SheduleDates.this, R.color.darkBlue));
                                txtNote.setText("");
                                selectedDate.setText("Choose Date");
                            }
                        }
                    });

                }

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_schedule, menu);

        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_SheduleDates.this, A_TraineesHome.class);
                i.putExtra("UserKey",UserKey);
                i.putExtra("UserImage",UserImage);
                startActivity(i);
                return false;
            }
        });

        MenuItem action_schedulelist = menu.findItem(R.id.action_schedulelist);
        action_schedulelist.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(A_SheduleDates.this, A_ScheduleList.class);
                i.putExtra("UserKey",UserKey);
                i.putExtra("UserImage",UserImage);
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
