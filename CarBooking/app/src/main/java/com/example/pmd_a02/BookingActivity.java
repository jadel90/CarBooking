package com.example.pmd_a02;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class BookingActivity extends AppCompatActivity {

    private static final String TAG = "BookingActivity"; // For logging

    private RecyclerView slotsRecyclerView;
    private BookingSlotAdapter adapter;
    private ArrayList<AppointmentSlot> slots;
    private FloatingActionButton fab;
    private DB_Helper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        initializeViewsAndDBHelper();

        loadAppointments();
        populateSlots();

        Log.d(TAG, "BookingActivity created with " + slots.size() + " slots.");
    }

    private void loadAppointments() {


        slots.clear();

        // Fetch new data
        ArrayList<AppointmentSlot> newSlots = dbHelper.getAllAppointments();

        // Add new data to the list
        slots.addAll(newSlots);


        adapter.notifyDataSetChanged(); // Refresh the RecyclerView adapter
        Log.d(TAG, "Appointments loaded from database");

    }
    @Override
    protected void onResume() {
        super.onResume();
        loadAppointments();
        populateSlots();
    }

    private void initializeViewsAndDBHelper() {
        fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            Log.d(TAG, "Navigating back to MainActivity");
            startActivity(new Intent(BookingActivity.this, MainActivity.class));
        });

        slotsRecyclerView = findViewById(R.id.slotsRecyclerView);
        slots = new ArrayList<>();
        dbHelper = new DB_Helper(this);
        adapter = new BookingSlotAdapter(this, slots);
        slotsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        slotsRecyclerView.setAdapter(adapter);
    }

    private void populateSlots() {
        slots.clear(); // Clear current slots to avoid duplication

        // Fetch new data from the database
        ArrayList<AppointmentSlot> bookedSlots = dbHelper.getAllAppointments();
        slots.addAll(bookedSlots);

        // Load unbooked slots from strings.xml
        String[] freeSlotsArray = getResources().getStringArray(R.array.booking_free_slots);
        for (String slotDetails : freeSlotsArray) {
            String[] details = slotDetails.split(", ");
            if (details.length == 4) {
                AppointmentSlot slot = new AppointmentSlot(details[0], details[1], details[2], details[3]);
                if (!isSlotBooked(slot, bookedSlots)) {
                    slots.add(slot);
                }
            }
        }

        adapter.notifyDataSetChanged(); // Refresh the RecyclerView adapter
    }

    private boolean isSlotBooked(AppointmentSlot slot, ArrayList<AppointmentSlot> bookedSlots) {
        for (AppointmentSlot bookedSlot : bookedSlots) {
            if (bookedSlot.getServiceType().equals(slot.getServiceType()) &&
                    bookedSlot.getDate().equals(slot.getDate()) &&
                    bookedSlot.getTime().equals(slot.getTime())) {
                return true;
            }
        }
        return false;
    }

    public void onSlotBooked(AppointmentSlot slot) {
        slot.setBooked(true);
        dbHelper.updateAppointment(slot);
        Toast.makeText(this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
        populateSlots();
    }


    public void onChangeAppointment(AppointmentSlot oldSlot) {
        showAvailableSlotsDialog(oldSlot);
    }

    private void showAvailableSlotsDialog(AppointmentSlot oldSlot) {
        ArrayList<AppointmentSlot> availableSlots = dbHelper.getAvailableAppointments(); // Fetch available slots

        Log.d(TAG, "Showing available slots dialog with slots count: " + availableSlots.size());
        // Convert available slots to a String array oronChangeAppointment a list of descriptions
        String[] availableSlotsDescriptions = new String[availableSlots.size()];
        for (int i = 0; i < availableSlots.size(); i++) {
            availableSlotsDescriptions[i] = availableSlots.get(i).toString(); // Customize this to show relevant info
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a new slot")
                .setItems(availableSlotsDescriptions, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 'which' is the index of the selected item
                        AppointmentSlot newSlot = availableSlots.get(which);
                        changeAppointmentSlot(oldSlot, newSlot);
                    }
                });
        builder.create().show();
    }


    private void changeAppointmentSlot(AppointmentSlot oldSlot, AppointmentSlot newSlot) {
        // Mark the old slot as available
        dbHelper.updateAppointmentBookingStatus(oldSlot.getId(), false);

        // Book the new slot
        dbHelper.updateAppointmentBookingStatus(newSlot.getId(), true);

        // Refresh UI
        loadAppointments();
        populateSlots();

        Toast.makeText(this, "Appointment changed successfully", Toast.LENGTH_SHORT).show();
    }

}


