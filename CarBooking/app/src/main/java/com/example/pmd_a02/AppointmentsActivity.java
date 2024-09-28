package com.example.pmd_a02;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * AppointmentsActivity displays a list of all booked appointments.
 * It retrieves appointment data from the database and presents it using a RecyclerView.
 */
public class AppointmentsActivity extends AppCompatActivity {

    private static final String TAG = "AppointmentsActivity"; // For logging
    private RecyclerView appointmentsRecyclerView;
    private ViewAppointmentAdapter adapter;
    private ArrayList<AppointmentSlot> appointments;
    private FloatingActionButton fab;
    private DB_Helper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        // Initialize database helper
        dbHelper = new DB_Helper(this);

        // Initialize RecyclerView
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);
        if (appointmentsRecyclerView == null) {
            Log.e("AppointmentsActivity", "RecyclerView is null");
            return; // Exit if RecyclerView is not found
        }

        // Initialize the appointments list
        appointments = new ArrayList<>();

        appointments = dbHelper.getAllAppointments();


        // Fetch all booked appointments from the database
        appointments.addAll(dbHelper.getAllAppointments());

        // Set up the adapter for the RecyclerView
        adapter = new ViewAppointmentAdapter(this, appointments);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appointmentsRecyclerView.setAdapter(adapter);

        // Set up FloatingActionButton
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> startActivity(new Intent(AppointmentsActivity.this, MainActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAppointments();
        Log.d("AppointmentsActivity", "Activity resumed, updating appointments");

        refreshAppointments();
    }

    // Method to update the data in the adapter
    public void updateData(ArrayList<AppointmentSlot> newAppointments) {
        appointments.clear(); // Clear existing data
        appointments.addAll(newAppointments); // Add new data
        adapter.notifyDataSetChanged(); // Notify the adapter of data change
        Log.d(TAG, "Data updated in ViewAppointmentAdapter");
    }
    private void refreshAppointments() {
        // Fetch updated appointments from the database
        ArrayList<AppointmentSlot> updatedAppointments = dbHelper.getAllAppointments();
        Log.d("AppointmentsActivity", "Refreshing appointments, total: " + updatedAppointments.size());

        // Update the adapter's data
        appointments.clear();
        appointments.addAll(dbHelper.getAllAppointments());
        appointments.addAll(updatedAppointments);

        // Update the adapter's data
        adapter.updateData(updatedAppointments); // This method will clear and add new data to the adapter

        adapter.notifyDataSetChanged();
    }



}
