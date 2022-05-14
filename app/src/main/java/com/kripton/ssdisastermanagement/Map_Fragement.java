package com.kripton.ssdisastermanagement;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class Map_Fragement extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public Map_Fragement() {
    }
    public static Map_Fragement newInstance(String param1, String param2) {
        Map_Fragement fragment = new Map_Fragement();
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
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient locationProviderClient;
    double lat=0,lon=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map__fragement, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        locationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());
        String[] place_type = {"police","doctor","fire_station","pharmacy","train_station","hospital","drugstore","dentist","department_store"};
        Dexter.withContext(getActivity().getBaseContext())
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        getCurrentLocation();
//                        getCurrentLocation();
//                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
//                            @Override
//                            public void onMapReady(@NonNull GoogleMap googleMap) {
//                                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                                    @Override
//                                    public void onMapClick(@NonNull LatLng latLng) {
//                                        MarkerOptions markerOptions = new MarkerOptions();
//                                        markerOptions.position(latLng);
//                                        markerOptions.title("My Place");
//                                        googleMap.clear();
//                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
//                                        googleMap.addMarker(markerOptions);
//                                    }
//                                });
//                            }
//                        });
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();
//        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"+
//                "?location="+lat+","+lon+
//                "&radius=5000"+
//                "&type"+place_type[0]+
//                "&sensor=true"+
//                "&key"+getResources().getString(R.string.google_map_key);
//        Log.d("location",url);
//               new PlaceTask().execute(url);
        return view;
    }

    private void getCurrentLocation() {
        if((ActivityCompat.checkSelfPermission(getActivity().getBaseContext(),Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED)&&
                (ActivityCompat.checkSelfPermission(getActivity().getBaseContext(),Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)) {
            Task<Location> task = locationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location!=null)
                    {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(@NonNull GoogleMap googleMap) {
                                map=googleMap;
//                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),17));
                                MarkerOptions marker = new MarkerOptions();
                                marker.title("My Location");
                                LatLng latLng = new LatLng(lat,lon);
                                marker.position(latLng);
                                map.clear();
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
                                map.addMarker(marker);
                            }
                        });
                    }
                }
            });
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = null;
            try {
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            new PhaserTask().execute(s);
        }

        private String downloadUrl(String string) throws IOException {
            URL url = new URL(string);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder builder = new StringBuilder();
            String line = "";
            while ((line=reader.readLine())!=null)
            {
                builder.append(line);
            }
            String data = builder.toString();
            reader.close();
            return data;
        }
    }

    private class PhaserTask extends AsyncTask<String,Integer,List<HashMap<String,String>>>{
        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            JsonPaser jsonPaser = new JsonPaser();
            List<HashMap<String,String>> mapList = null;
            JSONObject object = null;
            try {
                object = new JSONObject(strings[0]);
                mapList = jsonPaser.parseResult(object);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            map.clear();
            for(int i=0;i<hashMaps.size();i++)
            {
                HashMap<String,String> hashMapList = hashMaps.get(i);
                double lat = Double.parseDouble(hashMapList.get("lat"));
                double lon = Double.parseDouble(hashMapList.get("lng"));
                String name = hashMapList.get("name");
                LatLng latLng = new LatLng(lat,lon);
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(name);
                map.addMarker(options);

            }
        }
    }
}