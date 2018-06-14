package com.example.segev.traveler;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.segev.traveler.Model.Model;
import com.example.segev.traveler.Model.Post;
import com.example.segev.traveler.Model.PostAdapter;
import com.example.segev.traveler.Model.PostAsyncDao;
import com.example.segev.traveler.Model.PostListViewModel;
import com.example.segev.traveler.Model.PostsLinkedList;

import java.util.LinkedList;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class SearchFragment extends Fragment implements PostAdapter.ItemClickListener{
    public static final String LOG_TAG = ExperiencesFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private PostAdapter mAdapter;

    LinkedList<Post> postsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        postsList = (PostsLinkedList<Post>) getArguments().getSerializable("PostsList");


        View rootView = inflater.inflate(R.layout.fragment_experiences, container, false);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = rootView.findViewById(R.id.recyclerViewPosts);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new PostAdapter(getActivity(), this);
        mAdapter.setTasks(postsList);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity().getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);


        return rootView;
    }

    // Gets the post from the local DB and opening a PostDetailsFragment with it.
    @Override
    public void onItemClickListener(int itemId) { // check if a spinner isn needed

        Model.getInstance().getPostById(itemId, new SavedFragment.onGotPostById() {
            @Override
            public void onComplete(Post post) {
                Fragment fragment = new PostDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Post",post);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            }
        });
    }

//    @Override
//    public void onDestroy() {
//        Log.d(LOG_TAG,"ondestory");
//        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//        super.onDestroy();
//    }
}


