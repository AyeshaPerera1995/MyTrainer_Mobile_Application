package com.aye.mytrainer.Adapters;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentPagerAdapter;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.BreakfastFragment;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.DinnerFragment;
import com.aye.mytrainer.LunchFragment;
import com.aye.mytrainer.MealPlan;
import com.aye.mytrainer.Model.Meals;
import com.aye.mytrainer.Model.Report;
import com.aye.mytrainer.Model.Workouts;
import com.aye.mytrainer.Privilege.Prevalent;
import com.aye.mytrainer.R;
import com.aye.mytrainer.SnacksFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MealAdapter extends ArrayAdapter<Meals> {
    private Context context;
    ArrayList<Meals> mealPlans = new ArrayList<Meals>();
    private DatabaseReference mRef;
    private StorageReference storageReference,storageReference2;
    private ImageView mimage;
    String current_date,current_time,ImgName = "";


    public MealAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Meals> objects) {
        super(context, resource, objects);
        this.mealPlans = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater =(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.meal_item,null);

        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        current_time = currentTime.format(calendar.getTime());

        mimage =v.findViewById(R.id.mealImage);
        mimage.setBackgroundResource(R.drawable.noimage);
        final TextView title = v.findViewById(R.id.title);
        TextView time = v.findViewById(R.id.time);
        Button btntitle = v.findViewById(R.id.btntitle);
        TextView name = v.findViewById(R.id.name);
        TextView calories = v.findViewById(R.id.calories);
        final Button btncomplete = v.findViewById(R.id.btncomplte);
        final Button btndone = v.findViewById(R.id.btndone);
        btndone.setVisibility(View.VISIBLE);

        if (mealPlans.get(position).getStatus().equals(current_date)){
            btndone.setVisibility(View.INVISIBLE);
            btncomplete.setVisibility(View.VISIBLE);
            btncomplete.setEnabled(false);
        }

        ImgName = mealPlans.get(position).getTitle()+".jpg";
        storageReference = FirebaseStorage.getInstance().getReference().child("Meal Images").child(ImgName);
        Glide.with(context).load(storageReference).into(mimage);

        title.setText(mealPlans.get(position).getTitle());
        time.setText(mealPlans.get(position).getTime());
        btntitle.setText(mealPlans.get(position).getTitle());
        name.setText(mealPlans.get(position).getMeal());
        calories.setText(mealPlans.get(position).getCalories());

        mRef = FirebaseDB.getDbConnection().child("MealPlans");
        final Query statusQuery = mRef.child(Prevalent.currentOnlineUser.getU_id()).orderByChild("title").equalTo(mealPlans.get(position).getTitle());

        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btndone.setVisibility(View.INVISIBLE);
                btncomplete.setVisibility(View.VISIBLE);
                btncomplete.setEnabled(false);

                    statusQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                Meals values = ds.getValue(Meals.class);
                                if (values.getTitle().equals(mealPlans.get(position).getTitle())) {
                                    ds.getRef().child("status").setValue(current_date);
                                    ds.getRef().child("com_time").setValue(current_time);


                                    //  Add data to Reports
                                    final Query statusQuery = FirebaseDB.getDbConnection().child("Reports").child(Prevalent.currentOnlineUser.getU_id()).orderByChild("date").equalTo(current_date);
                                    statusQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            int get_calories = 0;
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                                    Report values = ds.getValue(Report.class);
                                                    get_calories = Integer.parseInt(values.getGet_calories());
//                                                    Toast.makeText(getContext(), get_calories+"", Toast.LENGTH_SHORT).show();

                                                    int calorie_count = get_calories + Integer.parseInt(mealPlans.get(position).getCalories());
                                                    ds.getRef().child("get_calories").setValue(calorie_count+"");

                                                    SweetAlertDialog alertDialog = new SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE);
                                                    alertDialog.setTitleText("You have got "+mealPlans.get(position).getCalories()+" Calories.");
                                                    alertDialog.show();
                                                    Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                                                    btn.setBackgroundColor(ContextCompat.getColor(context,R.color.darkBlue));

                                                }

                                            }else{
//                                                Toast.makeText(getContext(), "no snapshot", Toast.LENGTH_SHORT).show();

                                                DateTimeFormatter df = DateTimeFormatter .ofPattern("d MMM yyyy");
                                                LocalDate parse = LocalDate.parse(current_date, df);

                                                HashMap<String,Object> reportMap = new HashMap<String,Object>();
                                                reportMap.put("date",current_date);
                                                reportMap.put("burn_calories", "0");
                                                reportMap.put("get_calories",mealPlans.get(position).getCalories());
                                                reportMap.put("key",parse.getDayOfMonth());
                                                reportMap.put("status","0");

                                                FirebaseDB.getDbConnection().child("Reports").child(Prevalent.currentOnlineUser.getU_id()).child(current_date).setValue(reportMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            SweetAlertDialog alertDialog = new SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE);
                                                            alertDialog.setTitleText("You have got "+mealPlans.get(position).getCalories()+" Calories.");
                                                            alertDialog.show();
                                                            Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                                                            btn.setBackgroundColor(ContextCompat.getColor(context,R.color.darkBlue));

                                                        }
                                                    }
                                                });

                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    // Add data to Reports


                                }

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


            }
        });
        return v;
    }
}
