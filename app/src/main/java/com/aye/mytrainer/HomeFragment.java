package com.aye.mytrainer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Slides;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    private Animation fade_in, fade_out;
    private ViewFlipper viewFlipper;

    private DatabaseReference slideRef;
    private Slides values;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        //slide show
        viewFlipper = (ViewFlipper) view.findViewById(R.id.bckgrndViewFlipper1);
        fade_in = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.fade_out);

        slideRef = FirebaseDB.getDbConnection().child("Slides");
        slideRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        values = ds.getValue(Slides.class);

                        ImageView s1 = new ImageView(getActivity());
                        s1.setMaxHeight(510);
                        s1.setScaleType(ImageView.ScaleType.FIT_XY);
                        Picasso.with(getActivity()).load(values.getS_image()).into(s1);
                        Glide.with(getActivity()).load(values.getS_image()).into(s1);
                        viewFlipper.addView(s1);
                    }
                }else{
                    Toast.makeText(getActivity(), "Not Exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewFlipper.setInAnimation(fade_in);
        viewFlipper.setOutAnimation(fade_out);
        //sets auto flipping
        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.startFlipping();
        //slide show

        ImageButton btnworkout = (ImageButton) view.findViewById(R.id.btnworkout);
        btnworkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),MyWorkouts.class));
            }
        });

        ImageButton btnmaelplan = (ImageButton) view.findViewById(R.id.btnmealplan);
        btnmaelplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),MealPlan.class));
            }
        });

        ImageButton btntimeschedule = (ImageButton) view.findViewById(R.id.btntimeschedule);
        btntimeschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),TimeSchedule.class));
            }
        });

        ImageButton btnrequest = (ImageButton) view.findViewById(R.id.btnrequest);
        btnrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),SendRequest.class));
            }
        });

        ImageButton btngallery = (ImageButton) view.findViewById(R.id.btngallery);
        btngallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AppGallery.class));
            }
        });

        ImageButton btntrainer = (ImageButton) view.findViewById(R.id.btntrainer);
        btntrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),Charts.class));
            }
        });

        return view;

    }



}
