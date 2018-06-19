package com.example.segev.traveler.Fragments;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.segev.traveler.R;


public class PrivacyPolicyFragment extends Fragment {

    TextView privacy_policy_text;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        privacy_policy_text = rootView.findViewById(R.id.privacy_policy_textView);
        privacy_policy_text.setMovementMethod(new ScrollingMovementMethod());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Privacy Policy");
        NavigationView view = getActivity().findViewById(R.id.nav_view);
        view.getMenu().getItem(3).setChecked(true); }
}
