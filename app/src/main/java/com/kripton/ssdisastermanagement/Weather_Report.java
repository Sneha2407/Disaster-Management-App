package com.kripton.ssdisastermanagement;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Weather_Report extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Weather_Report() {
    }
    // TODO: Rename and change types and number of parameters
    public static Weather_Report newInstance(String param1, String param2) {
        Weather_Report fragment = new Weather_Report();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    TextView your_address,location_temp,weather_desc,humidity,atom_pressure,wind_speed,wind_direction;
    ImageView weather_icon;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_weather__report, container, false);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        your_address = view.findViewById(R.id.your_address);
        location_temp = view.findViewById(R.id.location_temp);
        weather_icon = view.findViewById(R.id.weather_icon);
        weather_desc = view.findViewById(R.id.weather_desc);
        wind_speed = view.findViewById(R.id.wind_speed);
        atom_pressure = view.findViewById(R.id.atom_pressure);
        wind_direction = view.findViewById(R.id.wind_direction);
        humidity = view.findViewById(R.id.humidity);
        Dexter.withContext(getActivity().getBaseContext())
                .withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        featchlocation();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }

                }).check();
        return view;
    }
    private void featchlocation() {
        if(ActivityCompat.checkSelfPermission(getActivity().getBaseContext(),Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location!=null)
                    {
                        Geocoder geocoder = new Geocoder(getActivity().getBaseContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            String add = addresses.get(0).getAddressLine(0);
                            Log.d("Address",add);
                            your_address.setText(add);
                            Log.d("latlon",addresses.get(0).getLatitude()+"::"+addresses.get(0).getLongitude());
                            String url ="https://api.openweathermap.org/data/2.5/weather?lat="+addresses.get(0).getLatitude()+"&lon="+addresses.get(0).getLongitude()+"&appid=ef62a9bd58ca12a0a48ef4eae3624705";
                            RequestQueue queue = Volley.newRequestQueue(getActivity().getBaseContext());
                            StringRequest request = new StringRequest(Request.Method.GET, url
                                    , new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    String weather = null;
                                    try {
                                        Log.d("Location","Step 4 "+response);
                                        weather = response;
                                        JSONObject jsonObject = new JSONObject(weather);
                                        Log.d("latlon",weather);
                                        JSONArray w = jsonObject.getJSONArray("weather");
                                        JSONObject sky = w.getJSONObject(0);
                                        JSONObject wh = w.getJSONObject(0);
                                        String desc = wh.getString("description");
                                        String place = jsonObject.getString("name");
                                        JSONObject mai = jsonObject.getJSONObject("main");
                                        String img_url="https://openweathermap.org/img/wn/"+sky.getString("icon")+"@2x.png";
                                        //weather_icon
                                        weather_desc.setText(sky.getString("description"));
                                        Glide.with(getActivity())
                                                .load(img_url)
                                                .placeholder(R.drawable.loading)
                                                .into(weather_icon);
                                        int temp = (int) mai.getDouble("temp");
                                        humidity.setText(mai.getDouble("humidity")+"%");
                                        //atom_pressure,wind_speed
                                        JSONObject wind = jsonObject.getJSONObject("wind");
                                        atom_pressure.setText(mai.getDouble("pressure")+"mb");
                                        wind_speed.setText(wind.getDouble("speed")+"Km/h");
                                        wind_direction.setText(wind.getDouble("deg")+"°");
                                        temp = temp - 273;
                                        location_temp.setText(temp +""+ Html.fromHtml("<sup>°C</sup>"));
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                                    , new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }
                            ){
                                @Override
                                public Map<String,String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("Accept", "application/json");
                                    return params;
                                }
                            };
                            queue.add(request);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}