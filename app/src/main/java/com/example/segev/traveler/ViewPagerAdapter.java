package com.example.segev.traveler;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.segev.traveler.Model.Model;
import com.example.segev.traveler.Model.Post;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    AppCompatActivity activity;
   List<Post> postsToPresent;
   LayoutInflater inflater;

    public ViewPagerAdapter(AppCompatActivity activity, List<Post> postsToPresent) {
        this.activity = activity;
        this.postsToPresent = postsToPresent;
    }

    @Override
    public int getCount() {
        return postsToPresent.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        inflater = (LayoutInflater)activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.post_layout,container,false);



        DisplayMetrics dis = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
        int height = dis.heightPixels;
        int width = dis.widthPixels;

        final ImageView image = itemView.findViewById(R.id.post_layout_imageView);
        final TextView title = itemView.findViewById(R.id.post_layout_title);
        final TextView location = itemView.findViewById(R.id.post_layout_location);

        image.setMaxHeight(height);
        image.setMaxWidth(width);

        title.setText(postsToPresent.get(position).getTitle());
        location.setText(postsToPresent.get(position).getLocation());

        Model.getInstance().getImage(postsToPresent.get(position).getImage(), new Model.GetImageListener() {
            @Override
            public void onDone(Bitmap imageBitmap) {
                image.setImageBitmap(imageBitmap);
            }
        });

        container.addView(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PostDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("Post",postsToPresent.get(position));
                fragment.setArguments(bundle);
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.flContent, fragment).commit();
            }
        });


        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }


}
