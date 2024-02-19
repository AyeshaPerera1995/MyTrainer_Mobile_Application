package com.aye.mytrainer;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

public class GetCalReports extends AppCompatActivity {

    private DatabaseReference repRef;
    private String UserKey;
    private ArrayList<BarEntry> list;
    private com.aye.mytrainer.Model.Report r;
    private  ArrayList<String> xAxisLables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getcalreports);

        final BarChart barChart = findViewById(R.id.barChart);

        list = new ArrayList<>();
        xAxisLables = new ArrayList<>();

        repRef = FirebaseDB.getDbConnection().child("Reports");
        UserKey = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final Query query = repRef.child(UserKey).orderByChild("key");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list.clear();
                    xAxisLables.clear();
                    int i = 0;

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        r = ds.getValue(com.aye.mytrainer.Model.Report.class);
                        DateTimeFormatter df = DateTimeFormatter .ofPattern("d MMM yyyy");
                        LocalDate parse = LocalDate.parse(r.getDate(), df);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar calendar = Calendar.getInstance();
                        calendar.setFirstDayOfWeek(Calendar.MONDAY);
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                        ArrayList<String> days = new ArrayList<>();
                        for (int x = 0; x < 7; x++)
                        {
                            days.add(format.format(calendar.getTime()));
                            calendar.add(Calendar.DAY_OF_MONTH, 1);
                        }

                        if (days.contains(parse.toString())){
                            list.add(new BarEntry(i, Float.parseFloat(r.getGet_calories())));
                            xAxisLables.add(parse.getDayOfMonth()+" "+parse.getMonth());
                            i++;
                        }

                    }

//                    Toast.makeText(GetCalReports.this, list.size()+"", Toast.LENGTH_SHORT).show();
                    barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
                    BarDataSet barDataSet = new BarDataSet(list, "Get Calories");
                    barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                    barDataSet.setValueTextColor(Color.BLACK);

                    BarData barData = new BarData(barDataSet);

                    barChart.setFitBars(true);
                    barChart.setData(barData);
                    barChart.getDescription().setText("Get Calorie Report");
                    barChart.animateY(2000);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu,menu);
        MenuItem item3 = menu.findItem(R.id.action_back);
        item3.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent i = new Intent(GetCalReports.this,Charts.class);
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
