package com.example.segev.traveler;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.segev.traveler.Model.Model;
import com.example.segev.traveler.Model.Post;

import java.util.Date;

//ADD A IF(SAVEDINSTANCE STATE == NULL) CHECK LIKE ELIAV DID
//TODO VALIDATE INPUT

public class PostEditFragment extends Fragment {
    private static final String LOG_TAG = PostEditFragment.class.getSimpleName();

    private EditText mTitle_Field;
    private EditText mDescription_Field;
    private EditText mImages_Field;
    private EditText mLocation_Field;
    private Button mSubmit_Btn;

    private Post mPost;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_post, container, false);
        Bundle addedArguments = getArguments();

        if (addedArguments != null)  // Not supposed to happen, a post to edit should be transferred
            mPost = (Post) addedArguments.getSerializable("Post");
        else {
            mPost = new Post();
            Log.d(LOG_TAG, "addedArguments is null, check it out.");
        }
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
        mTitle_Field = rootView.findViewById(R.id.post_new_title);
        mLocation_Field = rootView.findViewById(R.id.post_edit_location);
        mDescription_Field = rootView.findViewById(R.id.post_edit_description);
        mImages_Field = rootView.findViewById(R.id.post_edit_images);
        mSubmit_Btn = rootView.findViewById(R.id.post_edit_submit_btn);

        mTitle_Field.setText(mPost.getTitle());
        mLocation_Field.setText(mPost.getLocation());
        mDescription_Field.setText(mPost.getDescription());
        mImages_Field.setText(mPost.getImage());
    }

    private void onSubmitButtonClicked() {
        mSubmit_Btn.setEnabled(false);
        String title = mTitle_Field.getText().toString();
        String description = mDescription_Field.getText().toString();
        String location = mLocation_Field.getText().toString();
        String image = mImages_Field.getText().toString();
        Date currentDate = new Date();

        mPost.setTitle(title);
        mPost.setDescription(description);
        mPost.setLocation(location);
        mPost.setImage(image);
        mPost.setUpdatedAt(currentDate);

        Model.getInstance().insertPost(mPost);
        mSubmit_Btn.setEnabled(true);

        getActivity().getSupportFragmentManager().popBackStack();
    }
}