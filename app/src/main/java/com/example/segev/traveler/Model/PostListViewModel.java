package com.example.segev.traveler.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;


import java.util.List;

public class PostListViewModel extends ViewModel {
    private LiveData<List<Post>> data;

    public LiveData<List<Post>> getData(){
        data = Model.getInstance().getAllPosts();
        return data;
    }
}
