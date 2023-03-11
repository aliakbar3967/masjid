package com.app.masjid;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NamazFragment extends Fragment {
    private EditText mPrayerNameEditText;
    private EditText mPrayerTimeEditText;
    private TextView mPrayerListTextView;
    private String[] prayers;

    private SharedPreferences mSharedPreferences;
    private int mSelectedPrayerIndex = -1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_namaz, container, false);

        mPrayerNameEditText = view.findViewById(R.id.prayer_name_edittext);
        mPrayerTimeEditText = view.findViewById(R.id.prayer_time_edittext);
        mPrayerListTextView = view.findViewById(R.id.prayer_list_textview);

        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        String savedPrayerList = mSharedPreferences.getString("prayer_list", "");
        mPrayerListTextView.setText(savedPrayerList);

        Button resetButton = view.findViewById(R.id.resetButton);

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear prayer list
                prayers = new String[]{};
                mPrayerListTextView.setText("");
                Toast.makeText(getContext(), "Prayers Cleared", Toast.LENGTH_SHORT).show();

                // Update SharedPreferences with empty prayer list
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("prayer_list", "");
                editor.apply();
            }
        });

        Button addPrayerButton = view.findViewById(R.id.add_prayer_button);
        addPrayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prayerName = mPrayerNameEditText.getText().toString();
                String prayerTime = mPrayerTimeEditText.getText().toString();

                if (!prayerName.isEmpty() && !prayerTime.isEmpty()) {
                    // Check the number of prayers in the list
                    String currentPrayerList = mPrayerListTextView.getText().toString();
                    int prayerCount = currentPrayerList.split("\n").length;
                    if (prayerCount >= 5) {
                        Toast.makeText(getContext(), "Cannot add more than 5 prayers", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Add the new prayer
                    Toast.makeText(getContext(), "Namaz Added", Toast.LENGTH_SHORT).show();
                    String newPrayer = prayerName + "  -  " + prayerTime + "\n";
                    String updatedPrayerList = currentPrayerList + newPrayer;
                    mPrayerListTextView.setText(updatedPrayerList);

                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString("prayer_list", updatedPrayerList);
                    editor.apply();

                    // Schedule silent mode and restore mode
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                    try {
                        Date prayerDate = dateFormat.parse(prayerTime);
                        long silentTime = prayerDate.getTime() - 2 * 60 * 1000;
                        long restoreTime = prayerDate.getTime() + 15 * 60 * 1000;

                        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                        Intent silentIntent = new Intent(getContext(), SilentReceiver.class);
                        silentIntent.setAction("SILENT_MODE");
                        PendingIntent silentPendingIntent = PendingIntent.getBroadcast(getContext(), 0, silentIntent, 0);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, silentTime, silentPendingIntent);

                        Intent restoreIntent = new Intent(getContext(), SilentReceiver.class);
                        restoreIntent.setAction("RESTORE_MODE");
                        PendingIntent restorePendingIntent = PendingIntent.getBroadcast(getContext(), 0, restoreIntent, 0);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, restoreTime, restorePendingIntent);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Please Enter Namaz Name & time", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }



    public class SilentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (intent.getAction().equals("SILENT_MODE")) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else if (intent.getAction().equals("RESTORE_MODE")) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }


    }

}

