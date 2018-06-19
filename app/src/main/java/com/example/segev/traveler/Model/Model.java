package com.example.segev.traveler.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.URLUtil;


import com.example.segev.traveler.MyApplication;
import com.example.segev.traveler.Fragments.SavedFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Model {
    private static final String LOG_TAG = Model.class.getSimpleName();

    private static Model instance;
    private static final Object LOCK = new Object();

    private ModelFirebase modelFirebase;

    private Model(){
        modelFirebase = ModelFirebase.getInstance();
    }

    public static Model getInstance(){
        if(instance == null) {
            synchronized (LOCK) {
                instance = new Model();
            }
        }
        return instance;
    }


    public interface onGetPostByLocation{
        void onDone(List<Post> postsList);
    }

    public void getPostByLocation(final String location,final onGetPostByLocation listener){
        PostAsyncDao.getPostByLocation(new PostAsyncDao.PostAsyncDaoListener<List<Post>>() {
            @Override
            public void onComplete(List<Post> data) {
                if(data != null) {
                    listener.onDone(data);
                }
            }
        },location);
    }






    ////////////////////////////// Post List Data Class ///////////////////////////////////////
    class PostListData extends MutableLiveData<List<Post>> {

        @Override
        protected void onActive() {
            super.onActive();
            Log.d(LOG_TAG,"ON ACTIVE HAS BEEN CALLED");

            // 1. get the students list from the local DB
            PostAsyncDao.getAllPosts(new PostAsyncDao.PostAsyncDaoListener<List<Post>>() {
                @Override
                public void onComplete(final List<Post> postsFroLocalDB) {
                    // 2. update the live data with the new student list
                    setValue(postsFroLocalDB);
                    Log.d(LOG_TAG, "got students from local DB " + postsFroLocalDB.size());

                    // 3. get the student list from firebase
                    modelFirebase.getAllPosts(new ModelFirebase.GetAllPostsListener() {
                        @Override
                        public void onSuccess(final List<Post> postsFromFireBase) {
                            // 4. update the live data with the new student list)
                            long mostRecentDateFromLocal = getLastUpdatedDate(postsFroLocalDB);
                            long mostRecentDateFromFirebase = getLastUpdatedDate(postsFromFireBase);

                            Log.d(LOG_TAG," local is " +  mostRecentDateFromLocal + " firebase is " + mostRecentDateFromFirebase);

                            if(mostRecentDateFromFirebase != mostRecentDateFromLocal || postsFroLocalDB.size() != postsFromFireBase.size()) {
                                setValue(postsFromFireBase);
                                Log.d(LOG_TAG, "got students from firebase " + postsFromFireBase.size());

                                // 5. update the local DB - need to delete all and insert all
                                PostAsyncDao.deleteAllPosts(new PostAsyncDao.PostAsyncDaoListener<Boolean>() {
                                    @Override
                                    public void onComplete(Boolean data) {
                                        PostAsyncDao.insertAllPosts(new PostAsyncDao.PostAsyncDaoListener<Boolean>() {
                                            @Override
                                            public void onComplete(Boolean data) {
                                            }
                                        }, postsFromFireBase);
                                    }
                                }, postsFroLocalDB);
                            }
                        }
                    });
                }
            });
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            Log.d(LOG_TAG,"INACTIVE");
            cancelGetAllPosts();
        }

        public PostListData() {
            super();
            setValue(new LinkedList<Post>());
        }

        private long getLastUpdatedDate(List<Post> posts){
            if(posts.size() == 0 || posts == null)
                return 0;
            Post post = Collections.max(posts, new Comparator<Post>() {
                @Override
                public int compare(Post o1, Post o2) {
                    return (int)(o1.getUpdatedAt() - o2.getUpdatedAt());
                }
            });
            return post.getUpdatedAt();
        }
    }

    ////////////////////////////// Post List Data Class ///////////////////////////////////////


    PostListData postListData = new PostListData();

    public LiveData<List<Post>> getAllPosts(){
        return postListData;
    }

    public void insertPost(Post post){
        modelFirebase.insertPost(post);
    }

    public void deletePost(final Post post){
        modelFirebase.deletePost(post);
    }

    public Post getPostById(int id, final SavedFragment.onGotPostById listener){
        PostAsyncDao.getPostById(new PostAsyncDao.PostAsyncDaoListener<Post>() {
            @Override
            public void onComplete(Post data) {
                listener.onComplete(data);
            }
        },id);
        return null;
    }

    private void cancelGetAllPosts() {
        modelFirebase.cancelGetAllPosts();
    }



////////////////////// Images //////////////////////////////


    public interface SaveImageListener{
        void onDone(String url);
    }

    public void saveImage(Bitmap imageBitmap, SaveImageListener listener) {
        modelFirebase.saveImage(imageBitmap,listener);
    }


    public interface GetImageListener{
        void onDone(Bitmap imageBitmap);
    }

    public void getImage(final String url, final GetImageListener listener ){
        if(TextUtils.isEmpty(url))
            return;

        String localFileName = URLUtil.guessFileName(url, null, null);
        final Bitmap image = loadImageFromFile(localFileName);
        if (image == null) {                                      //if image not found - try downloading it from parse
            modelFirebase.getImage(url, new GetImageListener() {
                @Override
                public void onDone(Bitmap imageBitmap) {
                    if (imageBitmap == null) {
                        listener.onDone(null);
                    }else {
                        //2.  save the image localy
                        String localFileName = URLUtil.guessFileName(url, null, null);
                        Log.d("TAG", "save image to cache: " + localFileName);
                        saveImageToFile(imageBitmap, localFileName);
                        //3. return the image using the listener
                        listener.onDone(imageBitmap);
                    }
                }
            });
        }else {
            listener.onDone(image);
        }
    }

    // Store / Get from local mem
    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
        if (imageBitmap == null) return;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File imageFile = new File(dir,imageFileName);
            imageFile.createNewFile();

            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();

            addPictureToGallery(imageBitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPictureToGallery(Bitmap imageToSave) {
        Date currDate = new Date();
        String currentDateInString = String.valueOf(currDate.getTime());
        MediaStore.Images.Media.insertImage(MyApplication.context.getContentResolver(), imageToSave, currentDateInString , currentDateInString);
    }

    private Bitmap loadImageFromFile(String imageFileName){
        Bitmap bitmap;
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir,imageFileName);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            bitmap = null;
        }
        return bitmap;
    }


    //////////////////////// Searches ///////////////////////////
    public void insertSearch(final SearchQuery query){
        SearchQueryAsyncDao.insertSearchQuery(new SearchQueryAsyncDao.SearchQueryAsyncDaoListener<Boolean>() {
            @Override
            public void onComplete(Boolean data) {
                modelFirebase.insertSearch(query);
            }
        },query);
    }

    public void getTopThreeSearches(final ModelFirebase.onGotSearchTopFive listener){
        SearchQueryAsyncDao.getTopThreeQueries(new SearchQueryAsyncDao.SearchQueryAsyncDaoListener<List<SearchQuery>>() {
            @Override
            public void onComplete(List<SearchQuery> data) {
                listener.onComplete(data);
                modelFirebase.getTopThreeSearches(listener);
            }
        });
    }

    public void getSearchByQuery(final String query,final ModelFirebase.onGotSearchByNameListener listener){
        SearchQueryAsyncDao.getSearchQueryByQuery(new SearchQueryAsyncDao.SearchQueryAsyncDaoListener<SearchQuery>() {
            @Override
            public void onComplete(SearchQuery data) {
                listener.onComplete(data);
                modelFirebase.getSearchByQuery(query,listener);
            }
        },query);
    }
}