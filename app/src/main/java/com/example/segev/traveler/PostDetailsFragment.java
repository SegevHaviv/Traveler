package com.example.segev.traveler;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.segev.traveler.Model.Model;
import com.example.segev.traveler.Model.Post;
import com.example.segev.traveler.Model.UserModel;

public class PostDetailsFragment extends Fragment {
    private static final String LOG_TAG = PostDetailsFragment.class.getSimpleName();

    private Post mPost;

    private TextView mTitle_Field;
    private TextView mLocation_Field;
    private TextView mDescription_Field;
    private TextView mImages_Field;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_post_details, container, false);

        mPost = (Post)getArguments().getSerializable("Post");

        if(mPost.getUserWhoPostedID().equals(UserModel.getInstance().getCurrentUser().getUid())) // Means he's the owner of the post.
            setHasOptionsMenu(true);

        initializeViews(rootView);

        return rootView;
    }

    private void initializeViews(View rootView){
        mTitle_Field = rootView.findViewById(R.id.post_details_title);
        mLocation_Field = rootView.findViewById(R.id.post_details_location);
        mDescription_Field = rootView.findViewById(R.id.post_details_description);
        mImages_Field = rootView.findViewById(R.id.post_details_images);

        mTitle_Field.setText(mPost.getTitle());
        mLocation_Field.setText(mPost.getLocation());
        mDescription_Field.setText(mPost.getDescription());
        mImages_Field.setText(mPost.getImage());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.post_options, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int ItemThatWasSelected = item.getItemId();

        switch(ItemThatWasSelected){
            case R.id.post_options_delete:
                Toast.makeText(getContext(),"OPTIONS DELETE CLICKED",Toast.LENGTH_LONG).show();
                onDeleteButtonClicked();
                break;
            case R.id.post_options_edit:
                Toast.makeText(getContext(),"OPTIONS EDIT CLICKED",Toast.LENGTH_LONG).show();
                onEditButtonClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDeleteButtonClicked(){ // TODO spinner?
        Model.getInstance().deletePost(mPost);
        getActivity().onBackPressed();
    }

    private void onEditButtonClicked(){
        Fragment fragment = new PostEditFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Post",mPost);
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
    }
}
