package com.kripton.ssdisastermanagement;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FoodPreserve extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FoodPreserve() {
        // Required empty public constructor
    }
    public static FoodPreserve newInstance(String param1, String param2) {
        FoodPreserve fragment = new FoodPreserve();
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
    FirebaseAuth auth;
    Button submit;
    EditText reason,desc;
    Double lat,lon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_preserve, container, false);
        submit = view.findViewById(R.id.submit);
        reason = view.findViewById(R.id.sos_reason);
        desc = view.findViewById(R.id.describe);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = desc.getText().toString();
                String context = reason.getText().toString();
                if(!message.isEmpty()) {
                    if (!context.isEmpty()) {
                        auth = FirebaseAuth.getInstance();
                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());

                        if ((ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                                (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                @Override
                                public void onComplete(@NonNull Task<Location> task) {
                                    try {
                                        Location location = task.getResult();
                                        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                        if (location != null) {
                                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                            lat = addresses.get(0).getLatitude();
                                            lon = addresses.get(0).getLongitude();
                                            String add = " http://maps.google.com/maps?q=" + lat + "," + lon;
                                            Log.d("Address", addresses.get(0).getAddressLine(0));
                                            Log.d("Address", addresses.get(0).getAddressLine(0));
                                            Log.d("Address", addresses.get(0).getPostalCode());
                                            if (firebaseUser != null) {
                                                String url = "https://bookbin.tech/problem.php";
                                                Log.d("Address", firebaseUser.getDisplayName());
                                                Log.d("Address", firebaseUser.getEmail());
                                                RequestQueue queue = Volley.newRequestQueue(getContext());
                                                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        if (response.equals("200")) {
                                                            Toast.makeText(getContext(), "Thanks for your information", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getContext(), "Error while uploading", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                    }
                                                }) {
                                                    @Nullable
                                                    @Override
                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                        Map<String, String> map = new HashMap<>();
                                                        map.put("email", firebaseUser.getEmail());
                                                        map.put("name", firebaseUser.getDisplayName());
                                                        map.put("desc", message);
                                                        map.put("place", addresses.get(0).getAddressLine(0));
                                                        map.put("problem", context);
                                                        map.put("map_location", add);
                                                        map.put("pincode", addresses.get(0).getPostalCode());
                                                        map.put("submit", "post");
                                                        return map;
                                                    }
                                                };
                                                queue.add(request);

                                            }
                                        } else {
                                            Log.d("Address", "no location");
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Log.e("Exception", e.getMessage());
                                    }
                                }
                            });
                        } else {
                            Log.d("Address", "No permission");
                        }
                    } else {
                        Toast.makeText(getContext(), "Enter your problem", Toast.LENGTH_SHORT).show();
                    }
                }
                    else
                    {
                        Toast.makeText(getContext(), "Enter your problem description", Toast.LENGTH_SHORT).show();

                    }






            }
        });
        return view;
    }
}