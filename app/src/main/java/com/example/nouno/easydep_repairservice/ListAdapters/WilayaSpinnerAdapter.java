package com.example.nouno.easydep_repairservice.ListAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nouno.easydep_repairservice.Data.SearchSuggestion;
import com.example.nouno.easydep_repairservice.R;

import java.util.ArrayList;

/**
 * Created by nouno on 26/05/2017.
 */

public class WilayaSpinnerAdapter extends ArrayAdapter<String> {
    public WilayaSpinnerAdapter (Context context, String[] wilayas)
    {
        super(context,0,wilayas);
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent,false);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        return getCustomView(position,convertView,parent,true);
    }

    public View getCustomView (int position, @Nullable View convertView, @NonNull ViewGroup parent,boolean color)
    {
        String wilaya = getItem(position);
        View item = convertView;
        if (item ==null)
        {
            if (!color)
            item = LayoutInflater.from(getContext()).inflate(R.layout.wilaya_spinner_layout ,parent, false);
            else
            {
                item = LayoutInflater.from(getContext()).inflate(R.layout.wilaya_spinner_green_layout ,parent, false);
            }
        }

        TextView mainText = (TextView)item.findViewById(R.id.textview);
        mainText.setText(wilaya);

        //return super.getView(position, convertView, parent);
        return item;
    }
}
