package com.kripton.ssdisastermanagement;

public class Contact_model_list {
    String name,phone;
    boolean status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Contact_model_list(String name, String phone, boolean status) {
        this.name = name;
        this.phone = phone;
        this.status = status;
    }
}
