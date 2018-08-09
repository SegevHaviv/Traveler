package com.example.segev.traveler.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.segev.traveler.R;

public class ContactUsFragment extends Fragment {
    private static final String LOG_TAG = ContactUsFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_contact_us, container, false);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        Activity currentActivity = getActivity();

        currentActivity.setTitle("Contact Us");
        NavigationView view = currentActivity.findViewById(R.id.nav_view);
        view.getMenu().getItem(4).setChecked(true);
    }
}
