package com.kripton.ssdisastermanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout layout;
    NavigationView navigationView;
     TextView header_username;
     CircleImageView imageView;
     GoogleSignInClient googleSignInClient;
     FirebaseAuth auth;
     public final int HOME_FRAGEMENT = 1;
    public final int QUICK_CONTACT = 2;
    public final int FOOD_PRESERVE = 3;
    public final int EXIT = 4;
    public  static int CURRENT=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Dexter.withContext(getApplicationContext())
                .withPermissions(Manifest.permission.CALL_PHONE,Manifest.permission.READ_CONTACTS,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        Toast.makeText(MainActivity.this, "Now you can easily call to your nearest emergency", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
        layout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_bar_drawer);

        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser =auth.getCurrentUser();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(MainActivity.this,layout,toolbar,R.string.Open,R.string.Close);
        toggle.setDrawerIndicatorEnabled(true);
        layout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        navigationView.setCheckedItem(R.id.nav_home);
        setFragement(new Home_Fragement(),HOME_FRAGEMENT);
        View hview = navigationView.getHeaderView(0);
        header_username = hview.findViewById(R.id.header_username);
        imageView = (CircleImageView) hview.findViewById(R.id.header_pic);
        if(firebaseUser!=null) {
            header_username.setText(firebaseUser.getDisplayName());
            Glide.with(MainActivity.this)
                    .load(firebaseUser.getPhotoUrl())
                    .into(imageView);
        }
    }

    @Override
    protected void onDestroy() {
        CURRENT = 0;
        super.onDestroy();
    }

    public void setFragement(Fragment fragement, int fragement_number)
    {
        if(CURRENT!=fragement_number) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            CURRENT = fragement_number;
            transaction.replace(R.id.content_replace, fragement);
            transaction.commit();
        }
    }
    public void gotoFragement(int fragement)
    {
        if(fragement == HOME_FRAGEMENT)
        {
            setFragement(new Home_Fragement(),HOME_FRAGEMENT);
            navigationView.setCheckedItem(R.id.nav_home);
        }else if(fragement == QUICK_CONTACT)
        {
            setFragement(new Quick_Contact(),QUICK_CONTACT);
            navigationView.setCheckedItem(R.id.dial_up);
        }
        if(fragement == FOOD_PRESERVE)
        {
            setFragement(new FoodPreserve(),FOOD_PRESERVE);
            navigationView.setCheckedItem(R.id.Food);

        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout layout = findViewById(R.id.drawer_layout);
        BottomNavigationView navigationView = findViewById(R.id.bottomNavigationView);

        if(layout.isDrawerOpen(GravityCompat.START))
        {
            layout.closeDrawer(GravityCompat.START);
        }else {
            if(CURRENT!=HOME_FRAGEMENT)
            {
                gotoFragement(HOME_FRAGEMENT);
            }
            else
            {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.nav_home:
            {
                gotoFragement(HOME_FRAGEMENT);
                break;
            }
            case R.id.dial_up:{
                gotoFragement(QUICK_CONTACT);
                break;
            }
            case R.id.Food:
            {
                gotoFragement(FOOD_PRESERVE);
                break;
            }
            case R.id.exit:
            {
                System.exit(0);
               break;
            }
            case R.id.sign_out:
            {
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            auth.signOut();
                            Toast.makeText(MainActivity.this, "Successfully SignOut", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this,Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            finish();
                        }
                    }
                });
                break;
            }
            case R.id.sos_noti:
            {
                Dexter.withContext(this)
                        .withPermissions(Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                SharedPreferences sharedPreferences = getSharedPreferences("SOS_Contact", Context.MODE_PRIVATE);
                                Set<String> get = sharedPreferences.getStringSet("SOS_File",null);
                                if(get!=null) {

                                    final double[] lat = {0};
                                    final double[] lon = {0};
                                    FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(MainActivity.this);
                                    if((ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED )&&
                                            ( ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED )) {
                                        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Location> task) {
                                                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                                progressDialog.setMessage("Sending Message please wait");
                                                progressDialog.show();
                                                Location location = task.getResult();
                                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                                try {
                                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                                    lat[0] = addresses.get(0).getLatitude();
                                                    lon[0] = addresses.get(0).getLongitude();
                                                    String add = String.valueOf(addresses.get(0).getAddressLine(0));
                                                    String message ="I am in distress ! My location is http://maps.google.com/maps?q=" + lat[0] + "," + lon[0]+ " This is my Current Location "+ add +" Please Help Me ASAP!!!";
                                                    if(message.length()>140)
                                                    {
                                                        message = "Distress Map loc is http://maps.google.com/maps?q=" + lat[0] + "," + lon[0]+" My Address is "+add+" Please Help ASAP";
                                                    }
                                                    if(message.length()>140)
                                                    {
                                                        message = "Urgent Sitution http://maps.google.com/maps?q=" + lat[0] + "," + lon[0]+" My Address is "+add+ " Please Help";
                                                    }
                                                    for (String s : get) {
                                                        SmsManager smsManager = SmsManager.getDefault();
                                                        smsManager.sendTextMessage(s, null,message , null, null);
                                                    }
                                                    Toast.makeText(MainActivity.this, "Sms send to all", Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                                    }
                                }else
                                {
                                    Snackbar.make(navigationView,"Add Contacts to your sos directory",Snackbar.LENGTH_LONG)
                                            .setAction("ADD", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    gotoFragement(QUICK_CONTACT);
                                                }
                                            })
                                    .setActionTextColor(Color.parseColor("#de34eb"))
                                    .show();
                                }

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();

                break;
            }
            case R.id.call_police:{
                Intent police = new Intent(Intent.ACTION_DIAL);
                police.setData(Uri.parse("tel:100"));
                if(police.resolveActivity(getPackageManager())!=null)
                {                startActivity(police);}
                break;
            }//disaster_helpline
            case R.id.disaster_helpline:{
                Intent police = new Intent(Intent.ACTION_DIAL);
                police.setData(Uri.parse("tel:108"));
                if(police.resolveActivity(getPackageManager())!=null)
                {                startActivity(police);}
                break;
            }
            case R.id.women_safty:{
                Intent police = new Intent(Intent.ACTION_DIAL);
                police.setData(Uri.parse("tel:1091"));
                if(police.resolveActivity(getPackageManager())!=null)
                {                startActivity(police);}
                break;
            }
            case R.id.fire_place:{
                Intent police = new Intent(Intent.ACTION_DIAL);
                police.setData(Uri.parse("tel:101"));
                if(police.resolveActivity(getPackageManager())!=null)
                {                startActivity(police);}
                break;
            }
            case R.id.call_ambu:{
                Intent police = new Intent(Intent.ACTION_DIAL);
                police.setData(Uri.parse("tel:102"));
                if(police.resolveActivity(getPackageManager())!=null)
                {                startActivity(police);}
                break;
            }
        }
        DrawerLayout layout = findViewById(R.id.drawer_layout);
        layout.closeDrawer(GravityCompat.START);
        return true;
    }
}