package com.example.pmd_a02;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;


// It presents options to book a new appointment and view current appointments.
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // Tag for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button bookAppointmentButton = findViewById(R.id.bookAppointmentButton);
        Button viewAppointmentsButton = findViewById(R.id.viewAppointmentsButton);

        // select a free slot
        bookAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BookingActivity.class);
                startActivity(intent);
                Log.d(TAG, "Navigating to BookingActivity for booking an appointment");
            }
        });

        // Your booked appointments
        viewAppointmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AppointmentsActivity.class);
                startActivity(intent);
                Log.d(TAG, "Navigating to AppointmentsActivity to view booked appointments");
            }
        });

    }
}

