package com.example.segev.traveler;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.segev.traveler.Model.Model;
import com.example.segev.traveler.Model.ModelFirebase;
import com.example.segev.traveler.Model.Post;
import com.example.segev.traveler.Model.PostAsyncDao;
import com.example.segev.traveler.Model.PostListViewModel;
import com.example.segev.traveler.Model.PostsLinkedList;
import com.example.segev.traveler.Model.ViewPagerAdapter;

import java.util.List;

public class HomeFragment extends Fragment {
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();

    PostListViewModel postsViewModel;

    private Button mSaved_Posts_Button;
    private Button mSearch_Posts_Button;

    private EditText mSearch_Posts_Text;

    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeViews(rootView);

        mPager = rootView.findViewById(R.id.home_viewpager);
        mAdapter = new ViewPagerAdapter((AppCompatActivity)getActivity());

        mPager.setPageTransformer(true,new ZoomOutPageTransformer());

        mSaved_Posts_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onSavedPostsButtonClicked(); }
        });
        mSearch_Posts_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchPostsButtonClicked();
            }
        });

        if (savedInstanceState != null) {
            mPager.setCurrentItem(savedInstanceState.getInt("currentItem", 0));
        }


        return rootView;
    }

    private void onSearchPostsButtonClicked() {
        //turn spinner on

        ModelFirebase.getInstance().getPostsByLocation(mSearch_Posts_Text.getText().toString(), new ModelFirebase.GetAllPostsListener() {
            @Override
            public void onSuccess(List<Post> postsList) {
                Fragment fragment = new SearchFragment();
                Bundle bundle = new Bundle();
                PostsLinkedList<Post> postsLinkedList = new PostsLinkedList();
                for(Post post : postsList){
                    postsLinkedList.add(post);
                }
                bundle.putSerializable("PostsList",postsLinkedList);
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
            }
        });
    }

    private void onSavedPostsButtonClicked() {
        Fragment fragment = new SavedFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
    }

    private void initializeViews(View rootView){
        mSearch_Posts_Button = rootView.findViewById(R.id.homeserach_button);
        mSaved_Posts_Button = rootView.findViewById(R.id.home_saved);
        mSearch_Posts_Text = rootView.findViewById(R.id.home_search_textview);

    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        Log.d(LOG_TAG,"onattach");
        super.onAttach(context);
        postsViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);
        postsViewModel.getData().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                mAdapter.setPosts(posts);
                mPager.setAdapter(mAdapter);
            }
        });
    }


}


