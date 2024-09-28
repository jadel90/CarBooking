package com.example.pmd_a02;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ViewAppointmentAdapter extends RecyclerView.Adapter<ViewAppointmentAdapter.ViewHolder> {

    private static final String TAG = "ViewAppointmentAdapter"; // Tag for logging
    private final ArrayList<AppointmentSlot> mAppointments;
    private Context context;
    private OnAppointmentListener listener;
    private DB_Helper dbHelper;

    public ViewAppointmentAdapter(Context context, ArrayList<AppointmentSlot> appointments) {
        this.context = context;
        this.mAppointments = appointments;
        this.dbHelper = new DB_Helper(context);
        Log.d(TAG, "Adapter initialized with " + mAppointments.size() + " appointments");
    }

    public void setOnAppointmentListener(OnAppointmentListener listener) {
        this.listener = listener;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_view_appointments, parent, false);
        Log.d(TAG, "ViewHolder created for viewType: " + viewType);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppointmentSlot appointment = mAppointments.get(position);

        holder.serviceText.setText(appointment.getServiceType());
        holder.dateText.setText(appointment.getDate());
        holder.timeText.setText(appointment.getTime());
        holder.durationText.setText(appointment.getDuration());

        if (appointment.isBooked()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green));
            holder.serviceText.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.light_grey));
            holder.serviceText.setTextColor(ContextCompat.getColor(context, R.color.light_pink));
        }

        // Set an OnClickListener on the itemView to show the PopupMenu
        holder.itemView.setOnClickListener(v -> showPopupMenu(holder.itemView, position));

    }

    @Override
    public int getItemCount() {

        Log.d(TAG, "Getting item count: " + mAppointments.size());
        return mAppointments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView serviceText, dateText, timeText, durationText;

        ViewHolder(View itemView) {
            super(itemView);
            serviceText = itemView.findViewById(R.id.service_text);
            dateText = itemView.findViewById(R.id.date_text);
            timeText = itemView.findViewById(R.id.time_text);
            durationText = itemView.findViewById(R.id.duration_text);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onUpdateAppointment(mAppointments.get(position));
                    Log.d(TAG, "Item clicked at position: " + position);
                }
            });
        }
    }

    public interface OnAppointmentListener {
        void onUpdateAppointment(AppointmentSlot appointment);

        void onChangeAppointment(AppointmentSlot appointment); // Add this line
    }


    private void showPopupMenu(View view, int position) {
        // Create and show a PopupMenu when an item is clicked
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.options_menu); // Inflate menu from XML
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menu1) { // Change
                if (listener != null) {
                    Log.d(TAG, "Change listener is not null. Executing change logic.");
                    listener.onChangeAppointment(mAppointments.get(position));
                } else {
                    Log.d(TAG, "Change listener is null.");
                }
            } else if (id == R.id.menu2) { // Cancel
                // Handle cancellation of the appointment
                AppointmentSlot itemToDelete = mAppointments.get(position);
                dbHelper.deleteAppointment(itemToDelete.getId()); // Remove from database
                mAppointments.remove(position); // Remove from the list
                notifyItemRemoved(position); // Notify the adapter to remove this item
            }
            return false;
        });
        popup.show();
        Log.d(TAG, "Showing popup menu for item at position: " + position);
    }


    public void refreshAppointments() {
        ArrayList<AppointmentSlot> updatedAppointments = dbHelper.getAllAppointments();
        this.updateData(updatedAppointments); // Update the current adapter with new data
    }

    public void updateData(ArrayList<AppointmentSlot> newAppointments) {
        this.mAppointments.clear();
        this.mAppointments.addAll(newAppointments);
        notifyDataSetChanged(); // Refresh the RecyclerView
        Log.d(TAG, "Data updated in ViewAppointmentAdapter");
    }


}


