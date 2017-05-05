package com.example.nouno.easydep_repairservice.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.nouno.easydep_repairservice.Activities.MainActivity;
import com.example.nouno.easydep_repairservice.Data.Comment;
import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.Data.Statistic;
import com.example.nouno.easydep_repairservice.ListAdapters.CommentAdapter;
import com.example.nouno.easydep_repairservice.QueryUtils;
import com.example.nouno.easydep_repairservice.R;
import com.example.nouno.easydep_repairservice.Utils;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticsFragment extends Fragment {
    MainActivity mainActivity;
    ArrayList<Comment> comments;
    Statistic statistic;
    ListView listView;
    ProgressBar progressBar;
    RepairService repairService;
    View root;
    public StatisticsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = (MainActivity)getActivity();
        //mainActivity.getSupportActionBar().show();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        root = view.findViewById(R.id.root_layout);
        root.setVisibility(View.GONE);
        listView = (ListView)view.findViewById(R.id.list);
        repairService = Utils.getLoggedRepairService(getActivity().getApplicationContext());
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        getData();
        return view;
    }



    @Override
    public void onStop() {


        super.onStop();
    }

    private void displayData (View view)
    {

        TextView numberOfInterventionsThisWeekText = (TextView)view.findViewById(R.id.intervention_in_week_text);
        TextView averageInterventionsInWeekText = (TextView)view.findViewById(R.id.average_interventions_per_week_number);
        TextView totalInterventionsText = (TextView)view.findViewById(R.id.total_interventions_number);
        numberOfInterventionsThisWeekText.setText(statistic.getNumberOfInterventionsThisWeek()+"");
        averageInterventionsInWeekText.setText(statistic.getAverageInterventionsInWeek()+"");
        totalInterventionsText.setText(statistic.getTotalInterventions()+"");
        if (comments.size()==0)
        {
            View commentsLayout = view.findViewById(R.id.comments_layout);
            commentsLayout.setVisibility(View.GONE);
            TextView userCommentsText = (TextView)view.findViewById(R.id.user_comments_text);
            userCommentsText.setText(R.string.no_comments);
        }
        else
        {
            TextView ratingText = (TextView)view.findViewById(R.id.rating_text);
            ratingText.setText(Comment.getRating(comments)+"");
            TextView ratingNumber = (TextView)view.findViewById(R.id.rating_number);
            ratingNumber.setText(comments.size()+"");
        }

    }

    private void getData()
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("action","get_comments_for_repair_service");
        map.put("repairServiceId",repairService.getId()+"");
        LinkedHashMap<String,String> map1 = new LinkedHashMap<>();
        map1.put("action","get_statistics");
        map1.put("repair_service_id",repairService.getId()+"");
        GetDataTask getDataTask = new GetDataTask();
        getDataTask.execute(map,map1);
    }

    private void populateListView ()
    {
        CommentAdapter commentAdapter = new CommentAdapter(getActivity(),comments);
        listView.setAdapter(commentAdapter);
        justifyListViewHeightBasedOnChildren(listView);
    }

    private class GetDataTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            root.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.GET_COMMENTS_URL,params[0]);
                comments = Comment.parseJson(response);
                response = QueryUtils.makeHttpPostRequest(QueryUtils.REQUESTS_URL,params[1]);
                statistic = Statistic.parseJson(response);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            displayData(getView());
            populateListView();
            progressBar.setVisibility(View.GONE);
            root.setVisibility(View.VISIBLE);
        }
    }

    public void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

}
