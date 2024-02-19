package com.aye.mytrainer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Users;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MyTrainerFragment extends Fragment {

    private DatabaseReference adminRef;
    private StorageReference adminStorageRef;
    private Users values;

    private ImageView imgTrainer;
    private TextView trainerName, trainerDesc, trainerMobile, trainerEmail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mytrainer, container, false);

        imgTrainer = view.findViewById(R.id.imgTrainer);
        trainerName = view.findViewById(R.id.trainerName);
        trainerDesc = view.findViewById(R.id.trainerDesc);
        trainerMobile = view.findViewById(R.id.trainerMobile);
        trainerEmail = view.findViewById(R.id.trainerEmail);

        adminStorageRef = FirebaseDB.getStorageConnection().child("Admin Images");
        adminRef = FirebaseDB.getDbConnection().child("Users");

        //load Trainer Profile data .................................
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                values = dataSnapshot.child("pcNHvcwywzNXfGKuFeODejonWUE3").getValue(Users.class);
                Picasso.with(getContext()).load(values.getU_image()).into(imgTrainer);
                Glide.with(getContext()).load(values.getU_image()).into(imgTrainer);
                trainerName.setText(values.getU_name());
                trainerMobile.setText(values.getU_mobile());
                trainerDesc.setText(values.getU_desc());
                trainerEmail.setText(values.getU_email());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }


}
