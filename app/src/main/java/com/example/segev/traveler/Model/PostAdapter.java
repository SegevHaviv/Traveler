package com.example.segev.traveler.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.segev.traveler.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * This PostAdapter creates and binds ViewHolders, that hold the posts,
 * to a RecyclerView to efficiently display data.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> implements Serializable{
    private static final String LOG_TAG = PostAdapter.class.getSimpleName();

    // Constant for date format
    private static final String DATE_FORMAT = "dd/MM/yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<Post> mPostEntries;
    private Context mContext;
    // Date formatter
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public PostAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the task_layout to a view
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.post_layout, parent, false);

        return new PostViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(final PostViewHolder holder, int position) {
        // Determine the values of the wanted data
        Post postEntry = mPostEntries.get(position);


        String title = postEntry.getTitle();
        String location = postEntry.getLocation();
        String imageURL = postEntry.getImage();

        Model.getInstance().getImage(imageURL, new Model.GetImageListener() {
            @Override
            public void onDone(Bitmap imageBitmap) {
                holder.mPostImageView.setImageBitmap(imageBitmap);
            }
        });

        //Set values
        holder.mPostTitleField.setText(title);
        holder.mPostLocationField.setText(location);

    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (mPostEntries == null) {
            return 0;
        }
        return mPostEntries.size();
    }

    /**
     * When data changes, this method updates the list of postEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTasks(List<Post> postEntries) {
        mPostEntries = postEntries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    public class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView mPostTitleField;
        TextView mPostLocationField;
        ImageView mPostImageView;

        /**
         * Constructor for the PostViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public PostViewHolder(View itemView) {
            super(itemView);

            mPostTitleField = itemView.findViewById(R.id.post_layout_title);
            mPostLocationField = itemView.findViewById(R.id.post_layout_location);
            mPostImageView = itemView.findViewById(R.id.post_layout_imageView);

            itemView.setOnClickListener(this);
        }

        public String getViewHolderPostIdByPos(int pos){
            if(pos > mPostEntries.size())
                return "";

            Post post = mPostEntries.get(pos);

            if(post != null){
                return post.getId();
            }
            return "";
        }

        @Override
        public void onClick(View view) {
            int elementId = Integer.parseInt(mPostEntries.get(getAdapterPosition()).getId());
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}