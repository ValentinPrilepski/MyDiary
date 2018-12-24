package com.mydiary.fullscreenimage;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mydiary.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FSImageFragment extends Fragment {
    private static final String ARG_URI = "arg_uri";

    @BindView(R.id.full_image)
    ImageView mImageView;

    private Uri mUri;

    public static FSImageFragment newInstance(String uri) {
        Bundle args = new Bundle();
        args.putString(ARG_URI, uri);
        FSImageFragment fragment = new FSImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUri = Uri.parse(getArguments().getString(ARG_URI));
        }

    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.full_screen_image_fragment, container, false);
        ButterKnife.bind(this, view);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.fitCenter();
        requestOptions.skipMemoryCache(true);
        Glide
                .with(this)
                .load(mUri)
                .apply(requestOptions)
                .into(mImageView);
        return view;
    }
}
