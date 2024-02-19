package com.aye.mytrainer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aye.mytrainer.Model.Meals;
import com.aye.mytrainer.R;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DoneMAdapter extends BaseAdapter {

    private Context context;
    ArrayList<Meals> mealPlans = new ArrayList<Meals>();
    LayoutInflater inflater;

    public DoneMAdapter(Context context, ArrayList<Meals> mealPlans) {
        this.context = context;
        this.mealPlans = mealPlans;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return mealPlans.size();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.a_donemeals_item, null);
        TextView txttitle = view.findViewById(R.id.dtitle);
        TextView txttime = view.findViewById(R.id.dtime);
        TextView txtmeals = view.findViewById(R.id.dmeals);
        TextView txtcalories = view.findViewById(R.id.dcalories);
        TextView comTime = view.findViewById(R.id.comTime);
//        ImageButton btnStatus = view.findViewById(R.id.btnStatus);

        txttitle.setText(mealPlans.get(i).getTitle());
        txttime.setText(mealPlans.get(i).getTime());
        txtmeals.setText(mealPlans.get(i).getMeal());
        txtcalories.setText(mealPlans.get(i).getCalories());
        comTime.setText(mealPlans.get(i).getCom_time());

        return view;
    }
}
