package com.example.nouno.easydep_repairservice.ListAdapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.nouno.easydep_repairservice.Data.AssistanceRequest;
import com.example.nouno.easydep_repairservice.R;

import java.util.ArrayList;

/**
 * Created by nouno on 09/04/2017.
 */

public class AssistanceRequestAdapter extends ArrayAdapter<AssistanceRequest> {

    public AssistanceRequestAdapter(Context context, ArrayList<AssistanceRequest> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AssistanceRequest assistanceRequest = getItem(position);
        View item = convertView;
        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.assistance_request_list_item,parent, false);
        }
        TextView nameText = (TextView)item.findViewById(R.id.name_text);
        TextView toDepartureDurationText = (TextView)item.findViewById(R.id.toDepartureDistanceText);
        TextView departureText = (TextView)item.findViewById(R.id.departure_text);
        View destinationLayout = item.findViewById(R.id.destination_layout);
        nameText.setText(assistanceRequest.getCarOwner().getFullName());
        toDepartureDurationText.setText(assistanceRequest.getDepartureDurationString());
        departureText.setText(assistanceRequest.getUserPositon().getLocationName());
        if (assistanceRequest.getDestination()==null)
        {
            destinationLayout.setVisibility(View.GONE);
        }
        else
        {
            destinationLayout.setVisibility(View.VISIBLE);
            TextView pathText = (TextView)item.findViewById(R.id.path_text);
            pathText.setText(assistanceRequest.getDepartureToDestinationString());
            TextView destinationText = (TextView)item.findViewById(R.id.destination_text);
            destinationText.setText(assistanceRequest.getDestination().getLocationName());
        }
        return item;
    }
}
