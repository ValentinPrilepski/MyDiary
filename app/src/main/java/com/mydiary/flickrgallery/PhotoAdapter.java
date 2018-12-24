package com.mydiary.flickrgallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mydiary.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoHolder> {
    private List<Uri> mPhotoUris;
    private Context mContext;

    PhotoAdapter(List<Uri> photoUris) {
        mPhotoUris = photoUris;
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        return new PhotoHolder(layoutInflater ,viewGroup);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder photoHolder, int position) {
        Uri uri = mPhotoUris.get(position);
        photoHolder.bindImage(uri);
    }

    @Override
    public int getItemCount() {
        return mPhotoUris.size();
    }

    public void setPhotoUris(List<Uri> uriList) {
        mPhotoUris = uriList;
    }

    public class PhotoHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_image_view)
        ImageView mImageView;

        PhotoHolder(LayoutInflater layoutInflater, ViewGroup viewGroup) {
            super(layoutInflater.inflate(R.layout.gallery_item, viewGroup, false));
            ButterKnife.bind(this, itemView);

        }

        @SuppressLint("CheckResult")
       public void bindImage(Uri uri) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
            requestOptions.centerCrop();
            requestOptions.skipMemoryCache(true);
            Glide
                    .with(mContext)
                    .load(uri)
                    .apply(requestOptions)
                    .into(mImageView);
            mImageView.setOnClickListener(v -> {
                if(mContext instanceof IGalleryMethodCaller){
                    ((IGalleryMethodCaller)mContext).onClickPhoto(uri);
                }


            });
        }
    }
}
