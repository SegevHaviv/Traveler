package com.example.segev.traveler;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.segev.traveler.Model.Model;
import com.example.segev.traveler.Model.Post;
import com.example.segev.traveler.Model.UserModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

//TODO CHANGE ALL THE WAY OF INITALIZING FRAGMENTS TO NEWINSTANCE

//Is there a way to go back to a specific fragment?
public class MainScreenActivity extends AppCompatActivity {
    private final static String LOG_TAG = MainScreenActivity.class.getSimpleName();


    //Action Toolbar and it's Drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private Toolbar toolbar;
    //Action Toolbar and it's Drawer

    //Views
    private TextView header_tv;
    private NavigationView nav_view;
    //Views

    final int REQUEST_WRITE_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawerLayout);

        mDrawerLayout.addDrawerListener(mToggle);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        mToggle.syncState();

        nav_view = findViewById(R.id.nav_view);

        setupDrawerContent(nav_view);
        initializeHeaderEmail(nav_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            }
        }

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new HomeFragment();

            transaction.replace(R.id.flContent, fragment, "Home");
            //transaction.addToBackStack("Home");
            transaction.commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }else if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            moveTaskToBack(true);
        }else{
            super.onBackPressed();
        }
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        boolean movingFragment = false;
        menuItem.setChecked(true);
        mDrawerLayout.closeDrawers();

//        //Clearing the stack but the home
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 1; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;

        switch(menuItem.getItemId()) {
            case R.id.nav_home: // TODO Change so it'll come to the same fragment each time.
                movingFragment = true;
                fragmentClass = HomeFragment.class;
                break;

            case R.id.nav_experiences:
                movingFragment = true;
                fragmentClass = ExperiencesFragment.class;
                break;

//            case R.id.nav_equipment:
//                break;
//
//            case R.id.nav_articles:
//                break;
//
//            case R.id.nav_voucher:
//                break;

            case R.id.nav_saved:
                movingFragment = true;
                fragmentClass = SavedFragment.class;
                break;

            case R.id.nav_settings:
                break;

            case R.id.nav_logout:
                menuItem.setChecked(false);
                logoutUser();
                break;
        }

        if(movingFragment){
            setTitle(menuItem.getTitle());
            try {
                 fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
        }
    }

    private void initializeHeaderEmail(NavigationView navigationView){
        View header = navigationView.getHeaderView(0);
        header_tv = header.findViewById(R.id.nav_header_tv);
        header_tv.setText(UserModel.getInstance().getCurrentUser().getEmail());
    }

    private void logoutUser(){
        AlertDialog.Builder builder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
             else
                builder = new AlertDialog.Builder(this);

            builder.setTitle("Sign out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(LOG_TAG,"Logging out from user : " + UserModel.getInstance().getCurrentUser().getEmail());
                            UserModel.getInstance().signOutAccount();
                            Intent switchActivityIntent = new Intent(getApplicationContext(),LoginActivity.class);
                            switchActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(switchActivityIntent);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
    }
}
