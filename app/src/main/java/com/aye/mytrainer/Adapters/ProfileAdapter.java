package com.aye.mytrainer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.Interface.RecyclerViewClickListener;
import com.aye.mytrainer.Model.Users;
import com.aye.mytrainer.R;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>{

    Context context;
    ArrayList<Users> profiles;
    RecyclerViewClickListener listener;


    public ProfileAdapter(Context context, ArrayList<Users> profiles,RecyclerViewClickListener listener) {
        this.context = context;
        this.profiles = profiles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.a_tpsingleitem, parent, false);
        return new ProfileViewHolder(v, listener);

//        return new ProfileViewHolder(LayoutInflater.from(context).inflate(R.layout.a_tpsingleitem,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {

        if (holder instanceof ProfileViewHolder) {
            ProfileViewHolder rowHolder = (ProfileViewHolder) holder;
            //set values of data here
            rowHolder.name.setText(profiles.get(position).getU_name());
            rowHolder.email.setText(profiles.get(position).getU_email());
            Picasso.with(context).load(profiles.get(position).getU_image()).into(rowHolder.image);
            Glide.with(context).load(profiles.get(position).getU_image()).into(rowHolder.image);
        }

    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private RecyclerViewClickListener mListener;
        TextView name,email;
        ImageView image;

        public ProfileViewHolder(@NonNull View itemView,RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);

            name =(TextView) itemView.findViewById(R.id.memtextName);
            email =(TextView) itemView.findViewById(R.id.memtextMail);
            image =(ImageView) itemView.findViewById(R.id.memimageView);

        }


        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }


    }


}


