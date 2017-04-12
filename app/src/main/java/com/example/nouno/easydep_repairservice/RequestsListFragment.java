package com.example.nouno.easydep_repairservice;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.nouno.easydep_repairservice.Data.AssistanceRequest;
import com.example.nouno.easydep_repairservice.Data.CarOwner;
import com.example.nouno.easydep_repairservice.Data.Path;
import com.example.nouno.easydep_repairservice.Data.Position;
import com.example.nouno.easydep_repairservice.Data.RepairService;
import com.example.nouno.easydep_repairservice.ListAdapters.AssistanceRequestAdapter;
import com.example.nouno.easydep_repairservice.exceptions.ConnectionProblemException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsListFragment extends Fragment {
    private ArrayList<AssistanceRequest> assistanceRequests;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private RepairService repairService;
    private ListView listView;
    public RequestsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_requests_list, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refrech_layout);
        getRepairService();
        listView = (ListView)view.findViewById(R.id.list);
        getAssistanceRequests();
        swipeRefreshLayout.setColorSchemeResources(R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAssistanceRequests();
            }
        });

        return view;
    }

    private void getRepairService ()
    {
        repairService = new RepairService(7,"Bensebia","Noureddine");
    }

    private void getAssistanceRequests ()
    {
        LinkedHashMap<String,String> map = new LinkedHashMap<>();
        map.put("action",QueryUtils.GET_REQUESTS);
        map.put("repair_service_id",repairService.getId()+"");
        GetAssistanceRequestsTask getAssistanceRequestsTask = new GetAssistanceRequestsTask();
        getAssistanceRequestsTask.execute(map);
    }


    private void populateAssistanceRequestsList (View view, final ArrayList<AssistanceRequest> assistanceRequests)
    {
        ListView listView = (ListView) view.findViewById(R.id.list);
        AssistanceRequestAdapter assistanceRequestAdapter = new AssistanceRequestAdapter(getContext(),assistanceRequests);
        listView.setAdapter(assistanceRequestAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AssistanceRequest assistanceRequest = assistanceRequests.get(position);
                startAssistanceRequestInfoActivity(assistanceRequest);
            }
        });
        listView.setDividerHeight(0);
    }

    private void startAssistanceRequestInfoActivity(AssistanceRequest assistanceRequest)
    {
        Intent i = new Intent(getContext(),AssistanceRequestInfoActivity.class);
        i.putExtra("assistanceRequest",assistanceRequest.toJson());
        startActivity(i);
    }

    private class GetAssistanceRequestsTask extends AsyncTask<Map<String,String>,Void,String>
    {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            listView.setVisibility(View.GONE);
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

            swipeRefreshLayout.setRefreshing(false);
            assistanceRequests = AssistanceRequest.parseJson(s);
            populateAssistanceRequestsList(view,assistanceRequests);
            listView.setVisibility(View.VISIBLE);
        }
    }


}
