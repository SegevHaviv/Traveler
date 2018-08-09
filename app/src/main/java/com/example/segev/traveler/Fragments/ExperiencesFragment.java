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

import com.example.segev.traveler.MainScreenActivity;
import com.example.segev.traveler.Model.Model;
import com.example.segev.traveler.Model.Post;
import com.example.segev.traveler.Model.PostAdapter;
import com.example.segev.traveler.Model.PostListViewModel;
import com.example.segev.traveler.R;

import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class ExperiencesFragment extends Fragment implements PostAdapter.ItemClickListener{
    public static final String LOG_TAG = ExperiencesFragment.class.getSimpleName();

    PostListViewModel mPostListViewModel;
    private RecyclerView mRecyclerView;
    private PostAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_experiences, container, false);

        mRecyclerView = rootView.findViewById(R.id.recyclerViewPosts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

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
        mPostListViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);
        mPostListViewModel.getData().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                mAdapter.setPosts(posts);
            }
        });
    }

    private void onAddPostClicked(){
        Fragment fragment = null;

        try {
            fragment = PostNewFragment.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
    }

    @Override
    public void onItemClickListener(int itemId) {
        
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


