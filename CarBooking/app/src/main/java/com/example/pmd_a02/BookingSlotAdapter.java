package com.example.pmd_a02;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class BookingSlotAdapter extends RecyclerView.Adapter<BookingSlotAdapter.ViewHolder> {
    private static final String TAG = "BookingSlotAdapter"; // Tag for logging
    private final ArrayList<AppointmentSlot> slots;
    private Context context;
    private DB_Helper dbHelper;
    private OnSlotBookListener slotBookListener;

    // Constructor
    public BookingSlotAdapter(Context context, ArrayList<AppointmentSlot> slots) {
        this.context = context;
        this.slots = slots;
        this.dbHelper = new DB_Helper(context);
        Log.d(TAG, "Adapter initialized with " + slots.size() + " slots");
    }

    // Interface for slot booking listener
    public interface OnSlotBookListener {
        void onSlotBooked(AppointmentSlot slot);
        void onChangeAppointment(AppointmentSlot oldSlot);
    }

    // Set the listener for slot booking
    public void setOnSlotBookListener(OnSlotBookListener listener) {
        this.slotBookListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_book, parent, false);
        Log.d(TAG, "ViewHolder created for viewType: " + viewType);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppointmentSlot slot = slots.get(position);

        // Set the text views with slot details
        holder.serviceText.setText(slot.getServiceType());
        holder.dateText.setText(slot.getDate());
        holder.timeText.setText(slot.getTime());
        holder.durationText.setText(slot.getDuration());

        // Set the button state based on booking status
        holder.bookButton.setText(slot.isBooked() ? "Booked" : "Book");
        holder.bookButton.setEnabled(!slot.isBooked());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "Getting item count: " + slots.size());
        return slots.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceText, dateText, timeText, durationText;
        Button bookButton;
        Button changeButton;

        ViewHolder(View itemView) {
            super(itemView);
            serviceText = itemView.findViewById(R.id.service_text);
            dateText = itemView.findViewById(R.id.date_text);
            timeText = itemView.findViewById(R.id.time_text);
            durationText = itemView.findViewById(R.id.duration_text);
            bookButton = itemView.findViewById(R.id.btn_book);


            bookButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    AppointmentSlot newAppointment = slots.get(position);
                    if (!newAppointment.isBooked()) {
                        // Insert the new appointment into the database
                        long newId = dbHelper.insertAppointment(newAppointment);
                        if (newId != -1) {
                            newAppointment.setId((int) newId); // Set the ID from the database
                            newAppointment.setBooked(true); // Mark as booked
                            dbHelper.updateAppointment(newAppointment); // Update the slot in the database
                            notifyItemChanged(position); // Refresh this item
                            if (slotBookListener != null) {
                                slotBookListener.onSlotBooked(newAppointment); // Notify the listener in BookingActivity
                            }
                            Log.d(TAG, "Slot booked at position: " + position + ", Service: " + newAppointment.getServiceType());
                        } else {
                            // Handle insertion error
                            Log.e(TAG, "Failed to insert new appointment");
                        }
                    } else {
                        Log.d(TAG, "Attempted to book an already booked slot at position: " + position);
                    }
                }
            });



        }
    }
}
