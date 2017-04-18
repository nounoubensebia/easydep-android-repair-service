package com.example.nouno.easydep_repairservice.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nouno on 18/04/2017.
 */

public class Comment {
    private CarOwner carOwner;
    private RepairService repairService;
    private float rating;
    private String comment;
    private Date date;
    private long id;

    public Comment() {
    }

    public Comment(CarOwner carOwner, int rating, String comment, Date date, long id) {
        this.carOwner = carOwner;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.id = id;
    }


    public static ArrayList<Comment> parseJson (String json)
    {
        ArrayList<Comment> comments = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i=0;i<jsonArray.length();i++)
            {
                Comment comment = new Comment();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                comment.setId(jsonObject.getLong("id"));
                JSONObject carOwnerJson = jsonObject.getJSONObject("car_owner");
                CarOwner carOwner = new CarOwner(carOwnerJson.getLong("id"),carOwnerJson.getString("first_name"),carOwnerJson.getString("last_name"));
                comment.setCarOwner(carOwner);
                comment.setComment(jsonObject.getString("comment_text"));
                long time = jsonObject.getLong("time");
                Date date = new Date(time*1000);
                comment.setDate(date);
                comment.setRating((float)jsonObject.getDouble("rating"));
                comments.add(comment);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sortByTime(comments);
        return comments;
    }



    public CarOwner getCarOwner() {
        return carOwner;
    }

    public void setCarOwner(CarOwner carOwner) {
        this.carOwner = carOwner;
    }

    public RepairService getRepairService() {
        return repairService;
    }

    public void setRepairService(RepairService repairService) {
        this.repairService = repairService;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static void sortByTime(ArrayList<Comment> comments)
    {
        for (int i=0;i<comments.size();i++)
        {

            for (int j=i+1;j<comments.size();j++)
            {

                if (comments.get(i).date.getTime()<comments.get(j).date.getTime())
                {
                    Comment comment1 = comments.get(i);
                    Comment comment2 = comments.get(j);
                    comments.set(i,comment2);
                    comments.set(j,comment1);
                }
            }
        }

    }

    public static float getRating (ArrayList<Comment>comments)
    {
        float avg = 0;
        for (int i=0;i<comments.size();i++)
        {
            avg+=comments.get(i).getRating();
        }
        return avg/comments.size();
    }
}
