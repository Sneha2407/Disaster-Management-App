package com.kripton.ssdisastermanagement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Set;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Quick_Contact#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Quick_Contact extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Quick_Contact() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Quick_Contact.
     */
    // TODO: Rename and change types and number of parameters
    public static Quick_Contact newInstance(String param1, String param2) {
        Quick_Contact fragment = new Quick_Contact();
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
    Button add_more_contacts;
    ListView contact_list_view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_quick__contact, container, false);
        add_more_contacts = view.findViewById(R.id.add_more_contacts);
        contact_list_view = view.findViewById(R.id.contact_list_view);
        add_more_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),Contacts_list.class);
                getActivity().startActivityForResult(intent,100);
            }
        });
        ArrayList<String> list = new ArrayList<>();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SOS_Contact", Context.MODE_PRIVATE);
        Set<String> get = sharedPreferences.getStringSet("SOS_File",null);
        if(get!=null) {
            for (String s : get) {
                list.add(s);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,list);
            contact_list_view.setAdapter(adapter);
        }
        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ArrayList<String> list = new ArrayList<>();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SOS_Contact", Context.MODE_PRIVATE);
        Set<String> get = sharedPreferences.getStringSet("SOS_File",null);
        if(get!=null) {
            for (String s : get) {
                list.add(s);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,list);
            contact_list_view.setAdapter(adapter);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==100)
        {
            ArrayList<String> list = new ArrayList<>();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SOS_Contact", Context.MODE_PRIVATE);
            Set<String> get = sharedPreferences.getStringSet("SOS_File",null);
            if(get!=null) {
                for (String s : get) {
                    list.add(s);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,list);
                contact_list_view.setAdapter(adapter);
            }
        }
    }
}