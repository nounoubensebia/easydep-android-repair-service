package com.example.nouno.easydep_repairservice.Data;

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
                QueueElement queueElement = new QueueElement(assistanceRequest.getId(), assistanceRequest.getCarOwner(), assistanceRequest.getUserPositon(),
                        assistanceRequest.getToDeparture(), assistanceRequest.getDestination(), assistanceRequest.getToDestination(), assistanceRequest.getTime(), position);
                queueElements.add(queueElement);
                if (position>0)
                    queueElement.setFlag(AssistanceRequest.FLAG_IN_QUEUE);
                else
                    queueElement.setFlag(AssistanceRequest.FLAG_INTERVENTION);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sortByPosition(queueElements);
        return queueElements;
    }

    public static void sortByPosition (ArrayList<QueueElement> queueElements)
    {
        for (int i=0;i<queueElements.size();i++)
        {

            for (int j=i+1;j<queueElements.size();j++)
            {

                if (queueElements.get(i).position>queueElements.get(j).position)
                {
                    QueueElement queueElement1 = queueElements.get(i);
                    QueueElement queueElement2 = queueElements.get(j);
                    queueElements.set(i,queueElement2);
                    queueElements.set(j,queueElement1);
                }
            }
        }
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
