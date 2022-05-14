package com.kripton.ssdisastermanagement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonPaser {
    private HashMap<String,String> paserJsonObject(JSONObject object){
        HashMap<String,String> datalist = new HashMap<>();
        try {
            String name = object.getString("name");
            String lat = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lat");
            String lon = object.getJSONObject("geometry")
                    .getJSONObject("location").getString("lng");
            datalist.put("name",name);
            datalist.put("lat",lat);
            datalist.put("lng",lon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return datalist;
    }
    private List<HashMap<String,String>> paserJsonArray(JSONArray jsonArray)
    {
        List<HashMap<String,String>> datalist= new ArrayList<>();
        for (int i = 0;i<jsonArray.length();i++)
        {
            try {
                HashMap<String,String> data = paserJsonObject((JSONObject) jsonArray.get(i));
                datalist.add(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            {

            }
        }
        return datalist;
    }
    public List<HashMap<String,String>> parseResult(JSONObject object)
    {
        JSONArray jsonArray = null;
        try {
            jsonArray = object.getJSONArray("results");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  paserJsonArray(jsonArray);
    }
}
