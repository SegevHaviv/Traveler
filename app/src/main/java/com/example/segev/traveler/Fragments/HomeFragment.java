package com.example.segev.traveler.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.segev.traveler.Model.CustomViewPager;
import com.example.segev.traveler.Model.Model;
import com.example.segev.traveler.Model.ModelFirebase;
import com.example.segev.traveler.Model.Post;
import com.example.segev.traveler.Model.PostListViewModel;
import com.example.segev.traveler.Model.PostsLinkedList;
import com.example.segev.traveler.Model.SearchQuery;
import com.example.segev.traveler.Model.ViewPagerAdapter;
import com.example.segev.traveler.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String LOG_TAG = HomeFragment.class.getSimpleName();

    PostListViewModel postsViewModel;

    private Button mSaved_Posts_Button;
    private Button mSearch_Posts_Button;

    private EditText mSearch_Posts_Text;

    private ProgressBar mView_Progressbar;

    private ViewPager mPager;
    private ViewPagerAdapter mAdapter;
    int page;


    private ArrayList<TextView> mRecentSearch;


    private Thread timerThread;


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
            public void onClick(View v) { onSearchPostsButtonClicked();
            }
        });
        page = 0;
        mPager.setAdapter(mAdapter);


        initializeRecentSearches(rootView);

        startViewPagerTime(4000);
        return rootView;
    }

    private void initializeRecentSearches(final View rootView) {
        Model.getInstance().getTopThreeSearches(new ModelFirebase.onGotSearchTopFive() {
            @Override
            public void onComplete(List<SearchQuery> search) {
                switch(search.size()){
                    case 1:
                        mRecentSearch.get(0).setText(search.get(0).getQuery());
                        break;

                    case 2:
                        mRecentSearch.get(0).setText(search.get(0).getQuery());
                        mRecentSearch.get(1).setText(search.get(1).getQuery());
                        break;

                    case 3:
                        mRecentSearch.get(0).setText(search.get(0).getQuery());
                        mRecentSearch.get(1).setText(search.get(1).getQuery());
                        mRecentSearch.get(2).setText(search.get(2).getQuery());
                }

                setRecentSearchOnClickListener(mRecentSearch);
            }
        });
    }

    private void setRecentSearchOnClickListener(ArrayList<TextView> recentSearches){
        for(final TextView recent : recentSearches){
            recent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Model.getInstance().getPostByLocation(recent.getText().toString(), new Model.onGetPostByLocation() {
                        @Override
                        public void onDone(List<Post> postsList) {
                            Fragment fragment = null;

                            try {
                                fragment = SearchFragment.class.newInstance();
                            } catch (java.lang.InstantiationException e) {
                                e.printStackTrace();
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
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
            });
        }
    }

    private void onSearchPostsButtonClicked() {
        if(TextUtils.isEmpty(mSearch_Posts_Text.getText().toString())){
            Toast.makeText(getActivity(),"Please enter a query", Toast.LENGTH_LONG).show();
            return;
        }

        updateLatestSearches(mSearch_Posts_Text.getText().toString().toLowerCase());


        Model.getInstance().getPostByLocation(mSearch_Posts_Text.getText().toString(), new Model.onGetPostByLocation() {
            @Override
            public void onDone(List<Post> postsList) {
                Fragment fragment = null;

                try {
                    fragment = SearchFragment.class.newInstance();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
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

    private void updateLatestSearches(final String searchQuery) {
        Model.getInstance().getSearchByQuery(searchQuery, new ModelFirebase.onGotSearchByNameListener() {
            @Override
            public void onComplete(SearchQuery getSearchByQueryResult) {
                if(getSearchByQueryResult != null) { // it is found in the fb
                    getSearchByQueryResult.setSearchesAmount(getSearchByQueryResult.getSearchesAmount() + 1);
                    Model.getInstance().insertSearch(getSearchByQueryResult);
                }else{// first time it appears
                    SearchQuery newQuery = new SearchQuery(searchQuery.toLowerCase(), 0);
                    Model.getInstance().insertSearch(newQuery);
                }
            }
        });
    }

    private void onSavedPostsButtonClicked() {
        Fragment fragment = null;
        try {
            fragment = SavedFragment.class.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
    }

    private void initializeViews(View rootView){
        mSearch_Posts_Button = rootView.findViewById(R.id.homeserach_button);
        mSaved_Posts_Button = rootView.findViewById(R.id.home_saved);
        mSearch_Posts_Text = rootView.findViewById(R.id.home_search_textview);

        mRecentSearch = new ArrayList<>();


        TextView recentSearch1 = rootView.findViewById(R.id.recentSearch1);
        TextView recentSearch2 = rootView.findViewById(R.id.recentSearch2);
        TextView recentSearch3 = rootView.findViewById(R.id.recentSearch3);

        mRecentSearch.add(recentSearch1);
        mRecentSearch.add(recentSearch2);
        mRecentSearch.add(recentSearch3);
    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screenz to the left.
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postsViewModel = ViewModelProviders.of(this).get(PostListViewModel.class);
        postsViewModel.getData().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                    mAdapter.setPosts(posts);
                    mPager.setAdapter(mAdapter);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Home");
        NavigationView view = getActivity().findViewById(R.id.nav_view);
        view.getMenu().getItem(0).setChecked(true);
    }

    private void startViewPagerTime(final int delay){
        Runnable runnable = new Runnable() {
            public void run() {
                if (page == mAdapter.getCount()) {
                    page = 0;
                } else {
                    page = mPager.getCurrentItem()+ 1;
                }
                mPager.setCurrentItem(page, true);
                mPager.postDelayed(this,delay);
            }
        };

        timerThread = new Thread(runnable);
        timerThread.start();
    }

    @Override
    public void onDestroyView() {
        timerThread.interrupt();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        timerThread.interrupt();
        super.onDestroy();
    }
}