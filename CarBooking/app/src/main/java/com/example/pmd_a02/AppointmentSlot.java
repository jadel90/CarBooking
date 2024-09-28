package com.example.pmd_a02;


import android.util.Log;

// AppointmentSlot model class
public class AppointmentSlot {


    private static final String TAG = "AppointmentSlot"; // Tag for logging
    private int id;
    private String serviceType;

    private String date;
    private String time;
    private String duration;

    private boolean isBooked; // Field to track if the slot is booked


    public AppointmentSlot(String serviceType, String date, String time, String duration) {
        this.serviceType = serviceType;
        this.date = date;
        this.time = time;
        this.duration = duration;
        this.isBooked = false; // Initially, the slot is not booked
        Log.d(TAG, "AppointmentSlot created: " + this.toString());
    }

    public AppointmentSlot() {
        Log.d(TAG, "Empty AppointmentSlot created");
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    //  override toString for easy logging or debugging
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", service=" + serviceType +
                ", date=" + date +
                ", time='" + time + '\'' +
                ", duration=" + duration +
                ", isBooked=" + isBooked +
                '}';
    }



    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {

        isBooked = booked;
        Log.d(TAG, "Set booked status to " + booked + " for appointment ID: " + id);
    }


}