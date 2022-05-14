package com.kripton.ssdisastermanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Filter;

public class Contacts_list extends AppCompatActivity {
    RecyclerView contact_list;
    Contact_Adapter adapter;
    Button done_to_here;
    List<Contact_model_list> list = new ArrayList<Contact_model_list>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);
        contact_list = findViewById(R.id.contact_recycle);
        done_to_here = findViewById(R.id.done_to_here);
        done_to_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("MESSAGE","done");
                setResult(100,intent);
                finish();//finishing activity
            }
        });
        checkPermission();
    }

    private void checkPermission() {
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED)
        {
            find_contact();
        }else
        {
            Dexter.withContext(getApplicationContext())
                    .withPermission(Manifest.permission.READ_CONTACTS)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                            find_contact();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            permissionDeniedResponse.getRequestedPermission();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        }
    }

    private void find_contact() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String sort = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+ " ASC";
        Cursor cursor = getContentResolver().query(uri,null,null,null, sort);
        if(cursor.getCount()>0)
        {
            while (cursor.moveToNext())
            {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Uri phoneuri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ "=?";
                Cursor phoneCursor = getContentResolver().query(phoneuri,null,selection,new String[]{id},null);
                if(phoneCursor.moveToNext())
                {
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    ));
                    SharedPreferences sharedPreferences = getSharedPreferences("SOS_Contact", Context.MODE_PRIVATE);
                    Set<String> get = sharedPreferences.getStringSet("SOS_File",null);
                    Set<String> num = new HashSet<>();
                    Contact_model_list model_list;
                    if(get!=null) {
                        num.addAll(get);
                        if (num.contains(number)) {
                            Log.d("check_name","True"+number);
                            model_list = new Contact_model_list(name, number, true);
                            Log.d("check_name","1False"+number);
                        }
                        else {
                            model_list = new Contact_model_list(name, number, false);
                            Log.d("check_name","2False"+number);
                        }
                    }
                    else
                    {
                        model_list = new Contact_model_list(name,number,false);
                    }
                    list.add(model_list);
                    phoneCursor.close();

                }

            }
            cursor.close();
        }
        contact_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Contact_Adapter(list,this);
        contact_list.setAdapter(adapter);
    }
}