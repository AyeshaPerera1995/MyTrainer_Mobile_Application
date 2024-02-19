package com.aye.mytrainer.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.A_UploadNewWorkouts;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Meals;
import com.aye.mytrainer.Model.Report;
import com.aye.mytrainer.Model.Workouts;
import com.aye.mytrainer.Objects.Exercise;
import com.aye.mytrainer.Privilege.Prevalent;
import com.aye.mytrainer.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import cn.pedant.SweetAlert.SweetAlertDialog;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class WorkoutAdapter extends ArrayAdapter<Workouts> {

    ArrayList<Workouts> workouts = new ArrayList<Workouts>();
    private DatabaseReference wRef;
    private Context context;
    private StorageReference storageReference,storageReference2;
    private ImageView wimage;
    private String ImgName = "";
    private String current_date;

    public WorkoutAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Workouts> objects) {
        super(context, resource, objects);
        this.workouts = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater =(LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.workout_item,null);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMM yyyy");
        current_date = currentDate.format(calendar.getTime());

        wimage =v.findViewById(R.id.wImage);
        wimage.setBackgroundResource(R.drawable.noimage);
        TextView wname = v.findViewById(R.id.wName);
        TextView wsets = v.findViewById(R.id.wSets);
        TextView wreps = v.findViewById(R.id.wReps);
        TextView wtime = v.findViewById(R.id.wTime);
        final TextView wbtnpending = v.findViewById(R.id.wbtnpending);

        ImgName = workouts.get(position).getName()+".gif";
        storageReference = FirebaseStorage.getInstance().getReference().child("Workout Images").child(ImgName);
        Glide.with(context).load(storageReference).into(wimage);

        wname.setText(workouts.get(position).getName());
        wsets.setText(workouts.get(position).getSets());
        wreps.setText(workouts.get(position).getReps());
        wtime.setText(workouts.get(position).getTime_period());
        if (workouts.get(position).getStatus().equals("0")){
            wbtnpending.setText("Pending");
            wbtnpending.setBackgroundResource(R.drawable.btn_circle_red);
        }
        if (workouts.get(position).getStatus().equals("1")){
            wbtnpending.setText("Done");
            wbtnpending.setBackgroundResource(R.drawable.btn_circle_green);
            wbtnpending.setEnabled(false);
        }


        wRef = FirebaseDB.getDbConnection().child("Workouts");
        final Query statusQuery = wRef.child(Prevalent.currentOnlineUser.getU_id()).orderByChild("name").equalTo(workouts.get(position).getName());

        wbtnpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (wbtnpending.getText().toString().equals("Pending")){
                    wbtnpending.setText("Done");
                    wbtnpending.setBackgroundResource(R.drawable.btn_circle_green);
                    wbtnpending.setEnabled(false);

                   statusQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                Workouts values = ds.getValue(Workouts.class);
                                if (values.getDate().equals(workouts.get(position).getDate())) {
                                    ds.getRef().child("status").setValue("1");

                                    //  Add data to Reports
                                    final Query statusQuery = FirebaseDB.getDbConnection().child("Reports").child(Prevalent.currentOnlineUser.getU_id()).orderByChild("date").equalTo(workouts.get(position).getDate());
                                    statusQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            int burn_calories = 0;
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                                    Report values = ds.getValue(Report.class);
                                                    burn_calories = Integer.parseInt(values.getBurn_calories());
//                                                    Toast.makeText(getContext(), burn_calories+"", Toast.LENGTH_SHORT).show();

                                                    int calorie_count = burn_calories + Integer.parseInt(workouts.get(position).getBurn_calories());
                                                    ds.getRef().child("burn_calories").setValue(calorie_count+"");

                                                    SweetAlertDialog alertDialog = new SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE);
                                                    alertDialog.setTitleText("Well Done! You have burned "+workouts.get(position).getBurn_calories()+" Calories.");
                                                    alertDialog.show();
                                                    Button btn = (Button) alertDialog.findViewById(R.id.confirm_button);
                                                    btn.setBackgroundColor(ContextCompat.getColor(context,R.color.darkBlue));

                                                }

                                            }else{
                                                Toast.makeText(getContext(), "no snapshot", Toast.LENGTH_SHORT).show();

                                                DateTimeFormatter df = DateTimeFormatter .ofPattern("d MMM yyyy");
                                                LocalDate parse = LocalDate.parse(workouts.get(position).getDate(), df);

                                                HashMap<String,Object> reportMap = new HashMap<String,Object>();
                                                reportMap.put("date",workouts.get(position).getDate());
                                                reportMap.put("burn_calories", workouts.get(position).getBurn_calories());
                                                reportMap.put("get_calories","0");
                                                reportMap.put("key",parse.getDayOfMonth());
                                                reportMap.put("status","0");

                                                FirebaseDB.getDbConnection().child("Reports").child(Prevalent.currentOnlineUser.getU_id()).child(workouts.get(position).getDate().toString()).setValue(reportMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            SweetAlertDialog alertDialog = new SweetAlertDialog(context,SweetAlertDialog.SUCCESS_TYPE);
                                                            alertDialog.setTitleText("Well Done! You have burned "+workouts.get(position).getBurn_calories()+" Calories.");
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

            }
        });
        return v;
    }
}
