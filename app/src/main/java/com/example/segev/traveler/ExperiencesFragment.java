package com.example.segev.traveler;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.segev.traveler.Model.Post;
import com.example.segev.traveler.Model.PostAdapter;
import com.example.segev.traveler.Model.PostAsyncDao;
import com.example.segev.traveler.Model.PostListViewModel;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class ExperiencesFragment extends Fragment implements PostAdapter.ItemClickListener{
    public static final String LOG_TAG = ExperiencesFragment.class.getSimpleName();

    PostListViewModel postListViewModel;

    private RecyclerView mRecyclerView;
    private PostAdapter mAdapter;

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
    public void onAttach(Context context) {
        super.onAttach(context);
        postListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);
        postListViewModel.getData().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                mAdapter.setTasks(posts);
            }
        });
    }


    private void onAddPostClicked(){
        Fragment fragment = new PostNewFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
    }


    // Gets the post from the local DB and opening a PostDetailsFragment with it.
    @Override
    public void onItemClickListener(int itemId) {
        //create spinner

        // it must be in the localdb since it's in the list
        PostAsyncDao.getPostById(new PostAsyncDao.PostAsyncDaoListener<Post>() {
            @Override
            public void onComplete(Post data) {
                //REMOVE SPINNER
                Fragment fragment = new PostDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Post",data);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
            }
        },itemId);
    }
}


