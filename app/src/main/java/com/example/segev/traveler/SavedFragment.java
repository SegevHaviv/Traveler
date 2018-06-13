package com.example.segev.traveler;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.segev.traveler.Model.Model;
import com.example.segev.traveler.Model.Post;
import com.example.segev.traveler.Model.PostAdapter;
import com.example.segev.traveler.Model.PostAsyncDao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

public class SavedFragment extends Fragment implements PostAdapter.ItemClickListener{
    private static final String LOG_TAG = SavedFragment.class.getSimpleName();

    RecyclerView mRecyclerView;
    PostAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_saved, container, false);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = rootView.findViewById(R.id.recyclerViewSavedPosts);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new PostAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(getActivity().getApplicationContext(), VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                PostAdapter.PostViewHolder post = (PostAdapter.PostViewHolder)viewHolder;

                String id = post.getViewHolderPostIdByPos(viewHolder.getAdapterPosition());

                if(!TextUtils.isEmpty(id))
                    removeFromSharedPreferences(id);

                onStart(); // to restart the list
            }
        }).attachToRecyclerView(mRecyclerView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final ArrayList<Post> postsToPresent = new ArrayList<>();
        final String[] savedPosts = getSavedPosts();

        if(savedPosts != null) {
            for (final String postId : savedPosts) {
                Model.getInstance().getPostById(Integer.parseInt(postId), new onGotPostById() {
                    @Override
                    public void onComplete(Post post) {
                        Log.d(LOG_TAG, getSavedPosts().length + "");
                        if (post != null) {
                            postsToPresent.add(post);
                            mAdapter.setTasks(postsToPresent);
                        } else {
                            // needs to be removed from the sharedpreferences
                            removeFromSharedPreferences(postId);
                        }
                    }
                });
            }
        }
    }

    private void removeFromSharedPreferences(String idToRemove){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("savedPosts", Context.MODE_PRIVATE);
        String postsSaved = sharedPreferences.getString("saved","");

        ArrayList<String> postsToKeep = new ArrayList<>(Arrays.asList(postsSaved.split(","))); // arraylist of all saved
        postsToKeep.remove(idToRemove);

        String posts = TextUtils.join(",",postsToKeep);

        SharedPreferences.Editor sharedPreferencesEditor = getActivity().getSharedPreferences("savedPosts", Context.MODE_PRIVATE).edit();
        sharedPreferencesEditor.putString("saved",posts);
        sharedPreferencesEditor.apply();
    }

    private String[] getSavedPosts(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("savedPosts", Context.MODE_PRIVATE);
        String postsSaved = sharedPreferences.getString("saved","");

        if(postsSaved.isEmpty())
            return null;

        return postsSaved.split(",");
    }

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
                fragmentManager.beginTransaction().addToBackStack("Saved").replace(R.id.flContent, fragment,"Saved").commit();
            }
        },itemId);
    }

    public interface onGotPostById{
        void onComplete(Post post);
    }
}
