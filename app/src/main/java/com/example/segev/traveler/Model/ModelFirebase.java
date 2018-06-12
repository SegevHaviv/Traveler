package com.example.segev.traveler.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
// MIGHT CAUSE PROBLEM WHEN CHANGED DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(); TO MEMBER
public class ModelFirebase {
    private static final String TABLE_NAME = "posts";
    private ValueEventListener eventListener;

    private static final Object LOCK = new Object();
    private static ModelFirebase instance;

    private ModelFirebase(){}

    public static ModelFirebase getInstance() {
        if(instance == null)
            synchronized (LOCK){
                instance = new ModelFirebase();
            }
            return instance;
    }


    public void insertPost(Post post){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(TABLE_NAME).child(post.getId()).setValue(post);
    }

    public void deletePost(Post post){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(TABLE_NAME).child(post.getId()).removeValue();
    }

    public void cancelGetAllPosts() {
        DatabaseReference DatabaseRef = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME);
        DatabaseRef.removeEventListener(eventListener);
    }

    public void getAllPosts(final GetAllPostsListener listener) {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(TABLE_NAME);

        eventListener = mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> postList = new LinkedList<>();

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    postList.add(post);
                }
                listener.onSuccess(postList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public interface GetAllPostsListener{
        void onSuccess(List<Post> postsList);
    }



/////////////////////////////////   PHOTOS    ///////////////////////////////////////

    
    public void saveImage(Bitmap imageBitmap, final Model.SaveImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        Date d = new Date();
        String name = ""+ d.getTime();
        StorageReference imagesRef = storage.getReference().child("images").child(name);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.onDone(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                listener.onDone(downloadUrl.toString());
            }
        });

    }

    public void getImage(String url, final Model.GetImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpsReference.getBytes(3* ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                Log.d("TAG","get image from firebase success");
                listener.onDone(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                Log.d("TAG",exception.getMessage());
                Log.d("TAG","get image from firebase Failed");
                listener.onDone(null);
            }
        });
    }

}
