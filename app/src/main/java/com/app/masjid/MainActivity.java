package com.app.masjid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add a click listener to the FAB button to show the add namaz dialog




        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_nav_namaz:
                        switchFragment(new NamazFragment());
                        return true;
                    case R.id.bottom_nav_quran:
                        switchFragment(new QuranFragment());
                        return true;
                    case R.id.bottom_nav_qibla:
                        switchFragment(new QiblaFragment());
                        return true;
                    case R.id.bottom_nav_tariqa:
                        switchFragment(new TariqaFragment());
                        return true;
                    case R.id.bottom_nav_tasbeeh:
                        switchFragment(new TasbeehFragment());
                        return true;

                    default:
                        return false;
                }
            }
        });
    }



    private void showAddNamazDialog() {
        // Create an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Namaz");
        builder.setMessage("Enter namaz name and time");

        // Set the custom layout for the dialog
        View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.namazdialog, null);
        builder.setView(dialogView);

        // Get the EditText views from the dialog layout
        EditText etNamazName = dialogView.findViewById(R.id.edit_text_namaz_name);
        EditText etNamazTime = dialogView.findViewById(R.id.edit_text_namaz_time);

        // Add the positive button to the dialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String namazName = etNamazName.getText().toString();
                String namazTime = etNamazTime.getText().toString();

                // Save the namaz name and time in the app
                saveNamaz(namazName, namazTime);
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null); // Add to back stack to handle back button
        fragmentTransaction.commit();

    }

    private void saveNamaz(String namazName, String namazTime) {
        // TODO: Implement the code to save the namaz name and time in the app
    }
}