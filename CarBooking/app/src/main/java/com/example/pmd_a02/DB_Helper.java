package com.example.pmd_a02;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

public class DB_Helper extends SQLiteOpenHelper {
    private static final String TAG = "DB_Helper"; // Log tag
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "appointmentdb";
    private static final String TABLE_Appointments = "appointmentdetails";
    private static final String KEY_ID = "id";
    private static final String KEY_SERVICE = "service";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_DURATION = "duration";

    public DB_Helper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_Appointments + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                KEY_SERVICE + " TEXT," +
                KEY_DATE + " TEXT," +
                KEY_TIME + " TEXT," +
                KEY_DURATION + " TEXT," +
                "isBooked INTEGER DEFAULT 0)";
        db.execSQL(CREATE_TABLE);
        Log.d(TAG, "Database and table created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Appointments);
        onCreate(db);
        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
    }

    public long insertAppointment(AppointmentSlot appointment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SERVICE, appointment.getServiceType());
        values.put(KEY_DATE, appointment.getDate());
        values.put(KEY_TIME, appointment.getTime());
        values.put(KEY_DURATION, appointment.getDuration());
        values.put("isBooked", appointment.isBooked() ? 1 : 0);

        long result = db.insert(TABLE_Appointments, null, values);
        if (result == -1) {
            Log.e(TAG, "Failed to insert appointment");
        } else {
            Log.d(TAG, "Appointment inserted successfully: " + appointment);
        }
        db.close();
        return result;
    }



    public ArrayList<AppointmentSlot> getBookedAppointments() {
        ArrayList<AppointmentSlot> bookedAppointments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_Appointments + " WHERE isBooked = 1";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            AppointmentSlot appointment = new AppointmentSlot();
            int idIndex = cursor.getColumnIndex(KEY_ID);
            int serviceIndex = cursor.getColumnIndex(KEY_SERVICE);
            int dateIndex = cursor.getColumnIndex(KEY_DATE);
            int timeIndex = cursor.getColumnIndex(KEY_TIME);
            int durationIndex = cursor.getColumnIndex(KEY_DURATION);
            int isBookedIndex = cursor.getColumnIndex("isBooked");

            if (idIndex != -1) appointment.setId(cursor.getInt(idIndex));
            if (serviceIndex != -1) appointment.setServiceType(cursor.getString(serviceIndex));
            if (dateIndex != -1) appointment.setDate(cursor.getString(dateIndex));
            if (timeIndex != -1) appointment.setTime(cursor.getString(timeIndex));
            if (durationIndex != -1) appointment.setDuration(cursor.getString(durationIndex));
            if (isBookedIndex != -1) appointment.setBooked(cursor.getInt(isBookedIndex) == 1);

            bookedAppointments.add(appointment);
        }
        cursor.close();
        db.close();
        return bookedAppointments;
    }

    public ArrayList<AppointmentSlot> getAllAppointments() {
        ArrayList<AppointmentSlot> appointmentList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT DISTINCT * FROM " + TABLE_Appointments;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            AppointmentSlot appointment = new AppointmentSlot();
            int idIndex = cursor.getColumnIndex(KEY_ID);
            int serviceIndex = cursor.getColumnIndex(KEY_SERVICE);
            int dateIndex = cursor.getColumnIndex(KEY_DATE);
            int timeIndex = cursor.getColumnIndex(KEY_TIME);
            int durationIndex = cursor.getColumnIndex(KEY_DURATION);
            int isBookedIndex = cursor.getColumnIndex("isBooked");

            if (idIndex != -1) appointment.setId(cursor.getInt(idIndex));
            if (serviceIndex != -1) appointment.setServiceType(cursor.getString(serviceIndex));
            if (dateIndex != -1) appointment.setDate(cursor.getString(dateIndex));
            if (timeIndex != -1) appointment.setTime(cursor.getString(timeIndex));
            if (durationIndex != -1) appointment.setDuration(cursor.getString(durationIndex));
            if (isBookedIndex != -1) appointment.setBooked(cursor.getInt(isBookedIndex) == 1);

            appointmentList.add(appointment);
            Log.d(TAG, "Fetched appointment: " + appointment);
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Total appointments fetched: " + appointmentList.size());
        return appointmentList;
    }

    public int updateAppointment(AppointmentSlot appointment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SERVICE, appointment.getServiceType());
        values.put(KEY_DATE, appointment.getDate());
        values.put(KEY_TIME, appointment.getTime());
        values.put(KEY_DURATION, appointment.getDuration());
        values.put("isBooked", appointment.isBooked() ? 1 : 0);

        int count = db.update(TABLE_Appointments, values, KEY_ID + " = ?",
                new String[]{String.valueOf(appointment.getId())});
        if (count > 0) {
            Log.d(TAG, "Appointment updated successfully: " + appointment);
        } else {
            Log.e(TAG, "Failed to update appointment: " + appointment);
        }
        db.close();
        return count;
    }

    public void deleteAppointment(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Log the ID that is being requested for deletion
        Log.d(TAG, "Requesting to delete appointment with ID: " + id);

        int count = db.delete(TABLE_Appointments, KEY_ID + " = ?", new String[]{String.valueOf(id)});

        if (count > 0) {
            Log.d(TAG, "Appointment deleted successfully: ID = " + id);
        } else {
            Log.e(TAG, "Failed to delete appointment: ID = " + id);
        }
        db.close();
    }



    // new code: change feature
    public ArrayList<AppointmentSlot> getAvailableAppointments() {
        ArrayList<AppointmentSlot> availableSlots = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_Appointments + " WHERE isBooked = 0";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                AppointmentSlot slot = new AppointmentSlot();
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int serviceIndex = cursor.getColumnIndex(KEY_SERVICE);
                int dateIndex = cursor.getColumnIndex(KEY_DATE);
                int timeIndex = cursor.getColumnIndex(KEY_TIME);
                int durationIndex = cursor.getColumnIndex(KEY_DURATION);
                int isBookedIndex = cursor.getColumnIndex("isBooked");

                if (idIndex != -1) slot.setId(cursor.getInt(idIndex));
                if (serviceIndex != -1) slot.setServiceType(cursor.getString(serviceIndex));
                if (dateIndex != -1) slot.setDate(cursor.getString(dateIndex));
                if (timeIndex != -1) slot.setTime(cursor.getString(timeIndex));
                if (durationIndex != -1) slot.setDuration(cursor.getString(durationIndex));
                if (isBookedIndex != -1) slot.setBooked(cursor.getInt(isBookedIndex) == 1);

                availableSlots.add(slot);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        Log.d(TAG, "Available slots fetched: " + availableSlots.size());
        return availableSlots;
    }



    public int updateAppointmentBookingStatus(int id, boolean isBooked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("isBooked", isBooked ? 1 : 0); // Assuming 'isBooked' is the column name in your table

        // Update the row in the database
        int count = db.update(TABLE_Appointments, values, KEY_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return count; // Returns the number of rows affected
    }


}



















//package com.example.pmd_a02;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import android.database.Cursor;
//import android.util.Log;
//
//import java.util.ArrayList;
//
//public class DB_Helper extends SQLiteOpenHelper {
//
//    private static final String TAG = "DB_Helper"; // Log tag
//    private static final int DB_VERSION = 1;
//    private static final String DB_NAME = "appointmentdb";
//    private static final String TABLE_Appointments = "appointmentdetails";
//    private static final String KEY_ID = "id";
//    private static final String KEY_SERVICE = "service";
//    private static final String KEY_DATE = "date";
//    private static final String KEY_TIME = "time";
//    private static final String KEY_DURATION = "duration";
//
//    public DB_Helper(Context context) {
//        super(context, DB_NAME, null, DB_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        String CREATE_TABLE = "CREATE TABLE " + TABLE_Appointments + "(" +
//                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
//                KEY_SERVICE + " TEXT," +
//                KEY_DATE + " TEXT," +
//                KEY_TIME + " TEXT," +
//                KEY_DURATION + " TEXT," +
//                "isBooked INTEGER DEFAULT 0)";
//        db.execSQL(CREATE_TABLE);
//        Log.d(TAG, "Database and table created successfully");
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_Appointments);
//        onCreate(db);
//        Log.d(TAG, "Database upgraded from version " + oldVersion + " to " + newVersion);
//    }
//
//    public long insertAppointment(AppointmentSlot appointment) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_SERVICE, appointment.getServiceType());
//        values.put(KEY_DATE, appointment.getDate());
//        values.put(KEY_TIME, appointment.getTime());
//        values.put(KEY_DURATION, appointment.getDuration());
//        values.put("isBooked", appointment.isBooked() ? 1 : 0);
//
//        long result = db.insert(TABLE_Appointments, null, values);
//        if (result == -1) {
//            Log.e(TAG, "Failed to insert appointment");
//        } else {
//            Log.d(TAG, "Appointment inserted successfully: " + appointment);
//        }
//        db.close();
//        return result;
//    }
//
//    public ArrayList<AppointmentSlot> getAllAppointments() {
//        ArrayList<AppointmentSlot> appointmentList = new ArrayList<>();
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT * FROM " + TABLE_Appointments;
//        Cursor cursor = db.rawQuery(query, null);
//
//        while (cursor.moveToNext()) {
//            AppointmentSlot appointment = new AppointmentSlot();
//            int idIndex = cursor.getColumnIndex(KEY_ID);
//            int serviceIndex = cursor.getColumnIndex(KEY_SERVICE);
//            int dateIndex = cursor.getColumnIndex(KEY_DATE);
//            int timeIndex = cursor.getColumnIndex(KEY_TIME);
//            int durationIndex = cursor.getColumnIndex(KEY_DURATION);
//            int isBookedIndex = cursor.getColumnIndex("isBooked");
//
//            if (idIndex != -1) appointment.setId(cursor.getInt(idIndex));
//            if (serviceIndex != -1) appointment.setServiceType(cursor.getString(serviceIndex));
//            if (dateIndex != -1) appointment.setDate(cursor.getString(dateIndex));
//            if (timeIndex != -1) appointment.setTime(cursor.getString(timeIndex));
//            if (durationIndex != -1) appointment.setDuration(cursor.getString(durationIndex));
//            if (isBookedIndex != -1) appointment.setBooked(cursor.getInt(isBookedIndex) == 1);
//
//            appointmentList.add(appointment);
//            Log.d(TAG, "Fetched appointment: " + appointment);
//        }
//        cursor.close();
//        db.close();
//        Log.d(TAG, "Total appointments fetched: " + appointmentList.size());
//        return appointmentList;
//    }
//
//    public int updateAppointment(AppointmentSlot appointment) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(KEY_SERVICE, appointment.getServiceType());
//        values.put(KEY_DATE, appointment.getDate());
//        values.put(KEY_TIME, appointment.getTime());
//        values.put(KEY_DURATION, appointment.getDuration());
//        values.put("isBooked", appointment.isBooked() ? 1 : 0);
//
//        int count = db.update(TABLE_Appointments, values, KEY_ID + " = ?",
//                new String[]{String.valueOf(appointment.getId())});
//        if (count > 0) {
//            Log.d(TAG, "Appointment updated successfully: " + appointment);
//        } else {
//            Log.e(TAG, "Failed to update appointment: " + appointment);
//        }
//        db.close();
//        return count;
//    }
//
//    public void deleteAppointment(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        int count = db.delete(TABLE_Appointments, KEY_ID + " = ?", new String[]{String.valueOf(id)});
//        if (count > 0) {
//            Log.d(TAG, "Appointment deleted successfully: ID = " + id);
//        } else {
//            Log.e(TAG, "Failed to delete appointment: ID = " + id);
//        }
//        db.close();
//    }
//}
