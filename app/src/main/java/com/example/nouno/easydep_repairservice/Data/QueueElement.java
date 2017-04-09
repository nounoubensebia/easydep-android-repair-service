package com.example.nouno.easydep_repairservice.Data;

import com.example.nouno.easydep_repairservice.QueueFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nouno on 09/04/2017.
 */

public class QueueElement extends AssistanceRequest {
    private int position;

    public QueueElement(long id, CarOwner carOwner, Position departure, Path toDeparture, Position destination, Path toDestination, long time, int position) {
        super(id, carOwner, departure, toDeparture, destination, toDestination, time);
        this.position = position;
    }

    public static ArrayList<QueueElement> parseQueueJson(String json) {
        ArrayList<QueueElement> queueElements = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);


            ArrayList<AssistanceRequest> assistanceRequests = parseJson(json);
            for (int i = 0; i < assistanceRequests.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int position = jsonObject.getInt("position");
                AssistanceRequest assistanceRequest = assistanceRequests.get(i);
                QueueElement queueElement = new QueueElement(assistanceRequest.getId(), assistanceRequest.getCarOwner(), assistanceRequest.getDeparture(),
                        assistanceRequest.getToDeparture(), assistanceRequest.getDestination(), assistanceRequest.getToDestination(), assistanceRequest.getTime(), position);
                queueElements.add(queueElement);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return queueElements;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
