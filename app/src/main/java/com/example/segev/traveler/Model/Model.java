package com.example.segev.traveler.Model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.segev.traveler.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
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


    public void cancelGetAllPosts() {
        modelFirebase.cancelGetAllPosts();
    }



    ////////////////////////////// Post List Data Class ///////////////////////////////////////
    class PostListData extends MutableLiveData<List<Post>> {

        // TODO ADD SPINNER
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
                            // 4. update the live data with the new student list
                            setValue(postsFromFireBase);
                            Log.d(LOG_TAG, "got students from firebase " + postsFromFireBase.size());

                            // 5. update the local DB - need to delete all and insert all
                            PostAsyncDao.deleteAllPosts(new PostAsyncDao.PostAsyncDaoListener<Boolean>() {
                                @Override
                                public void onComplete(Boolean data) {
                                    PostAsyncDao.insertAllPosts(new PostAsyncDao.PostAsyncDaoListener<Boolean>() {
                                        @Override
                                        public void onComplete(Boolean data) {}
                                    }, postsFromFireBase);
                                }
                            },postsFroLocalDB);
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

//    public void getUserByID(int id){
//
//    }



    ////////////////////////////////////////////////////////
    //  HAndle Image Files
    ////////////////////////////////////////////////////////



//    public interface SaveImageListener{
//        void onDone(String url);
//    }
//
////    public void saveImage(Bitmap imageBitmap, SaveImageListener listener) {
////        modelFirebase.saveImage(imageBitmap,listener);
////    }
//
//
//
//    public interface GetImageListener{
//        void onDone(Bitmap imageBitmap);
//    }
//    public void getImage(final String url, final GetImageListener listener ){
//        String localFileName = URLUtil.guessFileName(url, null, null);
//        final Bitmap image = loadImageFromFile(localFileName);
//        if (image == null) {                                      //if image not found - try downloading it from parse
//            modelFirebase.getImage(url, new GetImageListener() {
//                @Override
//                public void onDone(Bitmap imageBitmap) {
//                    if (imageBitmap == null) {
//                        listener.onDone(null);
//                    }else {
//                        //2.  save the image localy
//                        String localFileName = URLUtil.guessFileName(url, null, null);
//                        Log.d("TAG", "save image to cache: " + localFileName);
//                        saveImageToFile(imageBitmap, localFileName);
//                        //3. return the image using the listener
//                        listener.onDone(imageBitmap);
//                    }
//                }
//            });
//        }else {
//            Log.d("TAG","OK reading cache image: " + localFileName);
//            listener.onDone(image);
//        }
//    }
//
//    // Store / Get from local mem
//    private void saveImageToFile(Bitmap imageBitmap, String imageFileName){
//        if (imageBitmap == null) return;
//        try {
//            File dir = Environment.getExternalStoragePublicDirectory(
//                    Environment.DIRECTORY_PICTURES);
//            if (!dir.exists()) {
//                dir.mkdir();
//            }
//            File imageFile = new File(dir,imageFileName);
//            imageFile.createNewFile();
//
//            OutputStream out = new FileOutputStream(imageFile);
//            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.close();
//
//            //addPicureToGallery(imageFile);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Bitmap loadImageFromFile(String imageFileName){
//        Bitmap bitmap = null;
//        try {
//            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//            File imageFile = new File(dir,imageFileName);
//            InputStream inputStream = new FileInputStream(imageFile);
//            bitmap = BitmapFactory.decodeStream(inputStream);
//            Log.d("tag","got image from cache: " + imageFileName);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bitmap;
//    }

}





