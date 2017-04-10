package com.example.nouno.easydep_repairservice;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import com.example.nouno.easydep_repairservice.Data.QueueElement;
import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.ListAdapters.QueueElementAdapter;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class QueueFragment extends Fragment {


    private ArrayList<QueueElement> queueElements;
    private View view;
    private ListView listView;
    private RepairService repairService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View fab;
    public QueueFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_queue, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swhipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);
        listView = (ListView)view.findViewById(R.id.list);
        fab = view.findViewById(R.id.fab);
        getRepairService();
        getQueueElements();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getQueueElements();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreateRequestActivity();
            }
        });
        return view;
    }

    private void startCreateRequestActivity()
    {
        Intent i = new Intent(getContext(),CreateAssistanceRequestActivity.class);
        startActivity(i);
    }

    private void getRepairService ()
    {
        repairService = new RepairService(3,"Bensebia","Noureddine");
    }

    private void getQueueElements ()
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("repair_service_id",repairService.getId()+"");
        map.put("action",QueryUtils.GET_QUEUE_ELEMENTS);
        GetQueueElementsTask getQueueElementsTask = new GetQueueElementsTask();
        getQueueElementsTask.execute(map);
    }

    private void populateQueueElementList (View view,ArrayList<QueueElement> queueElements)
    {
        //QueueElement queueElement = queueElements.get(0);
        //queueElement.setPosition(0);
        //queueElement = queueElements.get(1);
        //queueElement.setPosition(1);
        ListView listView = (ListView)view.findViewById(R.id.list);
        QueueElementAdapter queueElementAdapter = new QueueElementAdapter(getContext(),queueElements);
        listView.setAdapter(queueElementAdapter);
        listView.setDividerHeight(0);
    }

    public void enter(final View view) {
        view.setVisibility(View.VISIBLE);
        final Animation fabEnter = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_enter);
        fabEnter.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fabEnter);

    }


    public void exit(final View view) {
        final Animation fabExit = AnimationUtils.loadAnimation(view.getContext(), R.anim.scale_exit);
        fabExit.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(fabExit);
    }





    private class GetQueueElementsTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            exit(fab);
            listView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected String doInBackground(Map<String, String>... params) {
            String response = null;
            try {
                response = QueryUtils.makeHttpPostRequest(QueryUtils.SEND_REQUEST_URL,params[0]);
            } catch (ConnectionProblemException e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            enter(fab);
            swipeRefreshLayout.setRefreshing(false);
            queueElements = QueueElement.parseQueueJson(s);
            populateQueueElementList(getView(),queueElements);
            listView.setVisibility(View.VISIBLE);
        }
    }


}
