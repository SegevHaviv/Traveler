package com.example.segev.traveler.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.segev.traveler.Model.ModelFirebase;
import com.example.segev.traveler.Model.UserModel;
import com.example.segev.traveler.R;

public class UserProfileFragment extends Fragment {
    private TextView mUsername;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        mUsername = rootView.findViewById(R.id.user_profile_name);


        mUsername.setText(UserModel.getInstance().getCurrentUser().getEmail()+ "");


        return rootView;
    }


}
