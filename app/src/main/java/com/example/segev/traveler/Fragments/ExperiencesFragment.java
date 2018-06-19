package com.example.segev.traveler.Fragments;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.segev.traveler.Model.Model;
import com.example.segev.traveler.Model.Post;
import com.example.segev.traveler.Model.PostAdapter;
import com.example.segev.traveler.Model.PostListViewModel;
import com.example.segev.traveler.R;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class ExperiencesFragment extends Fragment implements PostAdapter.ItemClickListener{
    public static final String LOG_TAG = ExperiencesFragment.class.getSimpleName();

    PostListViewModel postListViewModel;
    private RecyclerView mRecyclerView;
    private PostAdapter mAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_experiences, container, false);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = rootView.findViewById(R.id.recyclerViewPosts);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new PostAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity().getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        FloatingActionButton addPostButton = rootView.findViewById(R.id.addPostFloatingButton);
        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddPostClicked();
            }
        });


        return rootView;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);
        postListViewModel.getData().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                mAdapter.setPosts(posts);
            }
        });
    }

    //    public static ExperiencesFragment newInstance(PostListViewModel postListViewModel,RecyclerView mRecyclerView,PostAdapter mAdapter) {
//        ExperiencesFragment fragment = new ExperiencesFragment();
//        Bundle args = new Bundle();
//        args.putSerializable(ARG_PARAM1, postListViewModel);
//        args.putSerializable(ARG_PARAM3, mAdapter);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            postListViewModel = (PostListViewModel) getArguments().getSerializable(ARG_PARAM1);
//            mAdapter = (PostAdapter)getArguments().getSerializable(ARG_PARAM3);
//        }
//
//        postListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);
//        postListViewModel.getData().observe(this, new Observer<List<Post>>() {
//            @Override
//            public void onChanged(@Nullable List<Post> posts) {
//                mAdapter.setTasks(posts);
//            }
//        });
//    }



    private void onAddPostClicked(){
        Fragment fragment = new PostNewFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
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
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Trips Experiences");
        NavigationView view = getActivity().findViewById(R.id.nav_view);
        view.getMenu().getItem(1).setChecked(true);

    }
}


