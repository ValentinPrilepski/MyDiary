package com.mydiary.flickrgallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mydiary.R;
import com.mydiary.note.NoteFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FlickrGalleryActivity extends MvpAppCompatActivity implements IFlickrGallery, IGalleryMethodCaller {
    private static final String TAG = FlickrGalleryActivity.class.getSimpleName();
    public static final String SEND_FLICKR_URI = "flickr_image_uri";
    public static final String NOTE_ID = "this_note_id";

    String mNoteId;

    @BindView(R.id.flickr_recycler_view)
    RecyclerView mFlickrRecyclerView;
    @InjectPresenter
    FlickrGalleryPresenter mFlickrGalleryPresenter;
    PhotoAdapter mPhotoAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_gallery);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        mNoteId = getIntent().getStringExtra(NoteFragment.EXT_NOTE_ID);
        mFlickrGalleryPresenter.getResultAPI();

    }


    @Override
    public void updateUI(List<Uri> uriList) {
        if (mPhotoAdapter == null) {
            mPhotoAdapter = new PhotoAdapter(uriList);
            mFlickrRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            mFlickrRecyclerView.setAdapter(mPhotoAdapter);
        } else {
            mPhotoAdapter.setPhotoUris(uriList);
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClickPhoto(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(NoteFragment.ADD_PH_FR_FLICK );
        intent.putExtra(SEND_FLICKR_URI, uri.toString());
        intent.putExtra(NOTE_ID, mNoteId);
        sendBroadcast(intent);
        finish();
    }
}
