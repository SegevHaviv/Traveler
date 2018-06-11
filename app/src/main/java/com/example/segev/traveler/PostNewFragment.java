package com.example.segev.traveler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.segev.traveler.Model.Model;
import com.example.segev.traveler.Model.Post;
import com.example.segev.traveler.Model.UserModel;

import java.util.Date;

public class PostNewFragment extends Fragment {
    private static final String LOG_TAG = PostNewFragment.class.getSimpleName();

    private EditText mTitle_Field;
    private EditText mDescription_Field;
    private EditText mImages_Field;
    private EditText mLocation_Field;
    private Button mSubmit_Btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_post, container, false);
        initializeViews(rootView);


        mSubmit_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitButtonClicked();
            }
        });
        return rootView;
    }

    private void initializeViews(View rootView) {
        mDescription_Field = rootView.findViewById(R.id.post_new_description);
        mImages_Field = rootView.findViewById(R.id.post_new_images);
        mLocation_Field = rootView.findViewById(R.id.post_new_location);
        mTitle_Field = rootView.findViewById(R.id.post_edit_title);
        mSubmit_Btn = rootView.findViewById(R.id.submit_new_btn);
    }

    private void onSubmitButtonClicked() {
        String userIdWhoPosted = UserModel.getInstance().getCurrentUser().getUid();
        String title = mTitle_Field.getText().toString();
        String image = mImages_Field.getText().toString();
        String location = mLocation_Field.getText().toString();
        String description = mDescription_Field.getText().toString();
        Date currentDate = new Date();

        Post postToInsert = new Post(userIdWhoPosted, currentDate, title, location, description, image);
        Model.getInstance().insertPost(postToInsert);
        getActivity().onBackPressed();
    }
}

