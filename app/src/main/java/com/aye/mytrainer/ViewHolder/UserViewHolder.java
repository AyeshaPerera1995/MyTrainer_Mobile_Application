package com.aye.mytrainer.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aye.mytrainer.R;

public class UserViewHolder{

    public TextView memtextName,memtextMail;
    public ImageView memimageView;

    public UserViewHolder(View itemView) {

        memimageView = (ImageView) itemView.findViewById(R.id.memimageView);
        memtextName = (TextView) itemView.findViewById(R.id.memtextName);
        memtextMail = (TextView) itemView.findViewById(R.id.memtextMail);
    }
}
