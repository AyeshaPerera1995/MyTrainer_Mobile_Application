package com.aye.mytrainer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aye.mytrainer.A_UploadGallery;
import com.aye.mytrainer.A_ViewAlbum;
import com.aye.mytrainer.AlbumImages;
import com.aye.mytrainer.Model.Gallery;
import com.aye.mytrainer.Objects.Album;
import com.aye.mytrainer.R;

import java.util.ArrayList;

public class AlbumAdapter extends ArrayAdapter {

    ArrayList<String> albumList = new ArrayList();
    ArrayList<Long> albumImageCountList = new ArrayList();
    Context context;
    int layout;
    private TextView albumNum, albumBtn, AN;

    public AlbumAdapter(Context context, int textViewResourceId, ArrayList<String> objects, ArrayList<Long> countObjects) {
        super(context, textViewResourceId, objects);
        albumList = objects;
        this.context =context;
        this.layout =textViewResourceId;
        this.albumImageCountList = countObjects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (layout){
            case R.layout.activity_appgallery:
                v = inflater.inflate(R.layout.appgalleryitem, null);
                final TextView user_albumName= (TextView)v.findViewById(R.id.albumTextView);
                ImageView imageView= (ImageView)v.findViewById(R.id.albumImage);
                user_albumName.setText(albumList.get(position)+"\n"+albumImageCountList.get(position)+" Images");

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(context, albumName.getText().toString(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context, AlbumImages.class);
                        i.putExtra("AlbumName",albumList.get(position));
                        context.startActivity(i);
                    }
                });

                break;

            case R.layout.a_activity_upload_gallery:
                v = inflater.inflate(R.layout.a_uploadgalleryitem, null);
                final TextView albumName = (TextView)v.findViewById(R.id.albumName);
                albumNum = (TextView)v.findViewById(R.id.albumNum);
                AN = (TextView)v.findViewById(R.id.AN);

                albumName.setText(albumList.get(position));
                albumNum.setText(albumImageCountList.get(position) +" Images");
                AN.setText(albumList.get(position));

                albumBtn= (TextView)v.findViewById(R.id.btnalbumView);
                albumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(context, albumName.getText().toString(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context, A_ViewAlbum.class);
                        i.putExtra("AlbumName",albumName.getText().toString());
                        context.startActivity(i);
                    }
                });


                break;

        }


return v;

    }
}
