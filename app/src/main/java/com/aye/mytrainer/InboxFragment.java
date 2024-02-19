package com.aye.mytrainer;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Users;
import com.aye.mytrainer.ViewHolder.UserViewHolder;
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

public class InboxFragment extends Fragment {

//    private DatabaseReference userRef;
//    RecyclerView recyclerView;
//    RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_inbox,container,false);

//        userRef = FirebaseDB.getDbConnection().child("Users");
//
//        recyclerView = rootView.findViewById(R.id.recycler_menu);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(getContext());
//        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
//                .setQuery(userRef,Users.class)
//                .build();
//
//        FirebaseRecyclerAdapter<Users,UserViewHolder> adapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull Users model) {
//                holder.memtextName.setText(model.getU_name());
//                holder.memtextMail.setText(model.getU_email());
////                holder.memimageView.setI
//            }
//
//            @NonNull
//            @Override
//            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout,parent,false);
//                UserViewHolder holder = new UserViewHolder(view);
//
//                return holder;
//            }
//        };
//
//        recyclerView.setAdapter(adapter);
//        adapter.startListening();
//    }


}
