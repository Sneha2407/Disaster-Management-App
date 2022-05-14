package com.kripton.ssdisastermanagement;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Contact_Adapter extends RecyclerView.Adapter<Contact_Adapter.ViewHolder> {
   List<Contact_model_list> list;
   Activity activity;

    public Contact_Adapter(List<Contact_model_list> list, Activity activity) {
        this.list = list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public Contact_Adapter.ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_view_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  Contact_Adapter.ViewHolder holder, int position) {
        String name = list.get(position).getName();
        String phone = list.get(position).getPhone();
        boolean status = list.get(position).isStatus();
        holder.setData(name,phone,status);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView name,number;
        public ViewHolder(@NonNull View view) {
            super(view);
            checkBox = view.findViewById(R.id.add_in_list);
            name = view.findViewById(R.id.contact_name);
            number = view.findViewById(R.id.contact_phone);

        }
        public void  setData(String n,String p,boolean status)
        {
            name.setText(n);
            number.setText(p);
            SharedPreferences sharedPreferences = activity.getSharedPreferences("SOS_Contact", Context.MODE_PRIVATE);
            Set<String> get = sharedPreferences.getStringSet("SOS_File",null);
            Set<String> num = new HashSet<>();
            if(get!=null) {
                num.addAll(get);
                if (num.contains(number.getText().toString()))
                    checkBox.setChecked(true);
                else
                    checkBox.setChecked(false);
            }
            else
            {
                checkBox.setChecked(false);
            }
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences preferences = activity.getSharedPreferences("SOS_Contact", Context.MODE_PRIVATE);
                    SharedPreferences.Editor edit = preferences.edit();
                    Set<String> cont = preferences.getStringSet("SOS_File",null);
                    Set<String> num2 = new HashSet<>();
                    if(cont!=null)
                    {
                        num2.addAll(cont);
                    }
                    if(!isChecked) {
                        checkBox.setChecked(false);
                        num2.remove(number.getText().toString());
                    }
                    else {
                        checkBox.setChecked(true);
                        num2.add(number.getText().toString());
                    }
                    edit.putStringSet("SOS_File",num2);
                    edit.apply();
                }
            });
        }
    }
}
