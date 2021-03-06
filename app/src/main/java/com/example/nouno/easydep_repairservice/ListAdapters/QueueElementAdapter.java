package com.example.nouno.easydep_repairservice.ListAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nouno.easydep_repairservice.Data.QueueElement;
import com.example.nouno.easydep_repairservice.R;

import java.util.ArrayList;

/**
 * Created by nouno on 09/04/2017.
 */

public class QueueElementAdapter extends ArrayAdapter<QueueElement> {
    public QueueElementAdapter(Context context, ArrayList<QueueElement> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        QueueElement queueElement = getItem(position);
        View item = convertView;
        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.queue_element_list_item,parent, false);
        }
        TextView positionText = (TextView)item.findViewById(R.id.position_text);
        TextView nameText = (TextView)item.findViewById(R.id.name_text);
        TextView locationText = (TextView)item.findViewById(R.id.location_text);
        TextView durationText = (TextView)item.findViewById(R.id.duration_text);
        View queueLyout = item.findViewById(R.id.queue_layout);
        positionText.setText(queueElement.getPosition()+"");
        TextView interventionText = (TextView)item.findViewById(R.id.intervention_text);
        switch (queueElement.getPosition())
        {
            case 0 : positionText.setVisibility(View.GONE);
                interventionText.setVisibility(View.VISIBLE);
                queueLyout.setVisibility(View.GONE);
                break;
            case 1:positionText.setTextColor(getContext().getResources().getColor(R.color.greenPrimary));
                interventionText.setVisibility(View.GONE);positionText.setVisibility(View.VISIBLE);
                queueLyout.setVisibility(View.VISIBLE);
                break;
            case 2:positionText.setTextColor(getContext().getResources().getColor(R.color.orangePrimary));
                interventionText.setVisibility(View.GONE);positionText.setVisibility(View.VISIBLE);
                queueLyout.setVisibility(View.GONE);
                break;
            case 3:positionText.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_light));
                interventionText.setVisibility(View.GONE);positionText.setVisibility(View.VISIBLE);
                queueLyout.setVisibility(View.GONE);
                break;
            case 4:positionText.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
                interventionText.setVisibility(View.GONE);positionText.setVisibility(View.VISIBLE);
                queueLyout.setVisibility(View.GONE);
                break;
        }
        if (queueElement.getPosition()>4)
        {
            positionText.setTextColor(getContext().getResources().getColor(android.R.color.holo_red_dark));
            interventionText.setVisibility(View.GONE);positionText.setVisibility(View.VISIBLE);
            queueLyout.setVisibility(View.GONE);
        }
        nameText.setText(queueElement.getCarOwner().getFullName());
        locationText.setText(queueElement.getUserPositon().getLocationName());
        durationText.setText(queueElement.getDepartureDurationString());
        return item;
    }
}
