package com.aye.mytrainer.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.aye.mytrainer.A_UploadNewWorkouts;
import com.aye.mytrainer.A_ViewAlbum;
import com.aye.mytrainer.Databases.FirebaseDB;
import com.aye.mytrainer.Model.Gallery;
import com.aye.mytrainer.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ImageAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> imgList = new ArrayList();
    LayoutInflater inflater;
    String albumName, status;

    public ImageAdapter(Context context, ArrayList<String> imgList, String albumName, String status) {
        this.context = context;
        this.imgList = imgList;
        this.albumName = albumName;
        this.status = status;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.albumimageitem, null);
        ImageView img = (ImageView) view.findViewById(R.id.singleimageView);
        Picasso.with(context).load(imgList.get(i)).into(img);
        Glide.with(context).load(imgList.get(i)).into(img);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                View mView = inflater.inflate(R.layout.dialog_image,null);
                ImageView dialog_img =(ImageView) mView.findViewById(R.id.dialog_img);
//                dialog_img.setImageResource(Integer.parseInt(imgList.get(i)));
                Picasso.with(context).load(imgList.get(i)).into(dialog_img);
                Glide.with(context).load(imgList.get(i)).into(dialog_img);
                mBuilder.setView(mView);
                AlertDialog dialog = mBuilder.create();
                dialog.show();
            }
        });

 if (status.equals("true")){
     img.setOnLongClickListener(new View.OnLongClickListener() {
         @Override
         public boolean onLongClick(View view) {
//                Toast.makeText(context, "LOng click", Toast.LENGTH_SHORT).show();
             FirebaseDB.getDbConnection().child("Gallery").child(albumName).addListenerForSingleValueEvent(new ValueEventListener() {
                 @Override
                 public void onDataChange(final DataSnapshot dataSnapshot) {

                     for (final DataSnapshot ds: dataSnapshot.getChildren()) {
                         final Gallery values = ds.getValue(Gallery.class);
                         if (values.getImage_url().equals(imgList.get(i))){

                             final SweetAlertDialog alertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                             alertDialog.setTitleText("Are you sure you want to remove this image?");
                             alertDialog.show();
                             Button btnConfirm = (Button) alertDialog.findViewById(R.id.confirm_button);
                             btnConfirm.setBackgroundColor(ContextCompat.getColor(context, R.color.darkBlue));
                             alertDialog.setConfirmText("YES");
                             btnConfirm.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View view) {
                                     ds.getRef().removeValue();
                                     alertDialog.dismiss();
                                     Intent i = new Intent(context,A_ViewAlbum.class);
                                     i.putExtra("AlbumName",albumName);
                                     context.startActivity(i);

                                 }
                             });
                             alertDialog.setCancelButton("NO", new SweetAlertDialog.OnSweetClickListener() {
                                 @Override
                                 public void onClick(SweetAlertDialog sweetAlertDialog) {
                                     alertDialog.dismiss();
//                                        Intent i = new Intent(context, A_ViewAlbum.class);
//                                        i.putExtra("AlbumName",albumName);
//                                        context.startActivity(i);
                                 }
                             });

                         }
                     }

                 }

                 @Override
                 public void onCancelled(DatabaseError databaseError) {

                 }
             });

             return true;
         }
     });
 }

        return view;
    }
}
