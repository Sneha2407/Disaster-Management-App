package com.kripton.ssdisastermanagement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link News_Report#newInstance} factory method to
 * create an instance of this fragment.
 */
public class News_Report extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public News_Report() {
        // Required empty public constructor
    }

    public static News_Report newInstance(String param1, String param2) {
        News_Report fragment = new News_Report();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    RecyclerView resv;
    List<Model> list = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private ShimmerFrameLayout simer;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news__report, container, false);
        resv = view.findViewById(R.id.newsRecyclerView);
        simer = view.findViewById(R.id.news_shimmer);
        simer.startShimmer();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        resv.setLayoutManager(layoutManager);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getBaseContext());
        String url = "https://newsapi.org/v2/top-headlines?country=in&apiKey=3752112d8e3d414fb9bbe02c36554502";
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", "onResponse: "+response);
                String json = response;
                try {
                    JSONObject object = new JSONObject(json);
                    JSONArray array = object.optJSONArray("articles");
                    String title,desc,image,url;

                    for (int i = 0;i<array.length();i++)
                    {
                        JSONObject object1 = array.getJSONObject(i);
                        title = object1.getString("title");
                        desc = object1.getString("description");
                        image = object1.getString("urlToImage");
                        url = object1.getString("url");
                        list.add(new Model(title,desc,image,url));
                    }
                    NewsAdapter adapter = new NewsAdapter(list,getActivity());
                    simer.stopShimmer();
                    simer.setVisibility(View.GONE);
                    resv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error",error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> header =new HashMap<>();
                header.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36");

                return header;
            }
        };
        queue.add(request);
        return view;
    }
}