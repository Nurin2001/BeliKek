package com.example.belikek;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageLoader {
    public static void imageLoader(Context context, ImageView view, String imageUrl) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_default) // Add a placeholder image
                .error(R.drawable.ic_default) // Add an error image
                .into(view);
    }

    // use if the error image doesnt work properly using the above function
    public static void imageLoader(Context context, ImageView view, String imageUrl, boolean isSidebar) {
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_default) // Add a placeholder image
                .error(R.drawable.ic_cloud) // Add an error image
                .into(view);
    }
}