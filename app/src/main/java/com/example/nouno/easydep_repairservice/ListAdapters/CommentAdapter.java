package com.example.nouno.easydep_repairservice.ListAdapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.nouno.easydep_repairservice.Data.Comment;
import com.example.nouno.easydep_repairservice.OnButtonClickListener;
import com.example.nouno.easydep_repairservice.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by nouno on 18/04/2017.
 */

public class CommentAdapter extends ArrayAdapter<Comment> {
    private OnButtonClickListener<Comment> onButtonClickListener;
    public CommentAdapter(Context context, ArrayList<Comment> list) {
        super(context,0,list);
    }

    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View item = convertView;
        if (item ==null)
        {
            item = LayoutInflater.from(getContext()).inflate(R.layout.comment_list_item ,parent, false);
        }
        TextView userNameText = (TextView)item.findViewById(R.id.nameText);
        TextView commentText = (TextView)item.findViewById(R.id.comment_text);
        RatingBar ratingBar = (RatingBar)item.findViewById(R.id.ratingbar);
        TextView dateText = (TextView)item.findViewById(R.id.date_text);
        TextView signalDeleteText = (TextView)item.findViewById(R.id.signal_delete);
        Comment userComment = getItem(position);
        userNameText.setText(userComment.getCarOwner().getFullName());
        commentText.setText(userComment.getComment());
        ratingBar.setRating(userComment.getRating());
        DateFormat dateFormat = new SimpleDateFormat();
        String date = dateFormat.format(userComment.getDate());
        dateText.setText(date);

        signalDeleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickListener.OnClick(getItem(position));
            }
        });
        return item;
    }
}
