package com.mydiary.flickrgallery;

import android.net.Uri;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mydiary.App;
import com.mydiary.flickrgallery.retrofit.Photo;
import com.mydiary.flickrgallery.retrofit.ResultAPI;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


@InjectViewState
public class FlickrGalleryPresenter extends MvpPresenter<IFlickrGallery> {
    private static final String TAG = FlickrGalleryPresenter.class.getSimpleName();
    private static final String API_KEY = "007c3ac214db27d451b1c99db83aedc5";

    private List<Photo> mPhotos;

    public void getResultAPI() {
        App.getApi().getAllImage(
                "flickr.photos.getRecent",
                API_KEY,
                "json",
                1,
                "url_s")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<ResultAPI>() {
                    @Override
                    public void onSuccess(ResultAPI resultAPI) {
                        mPhotos = resultAPI.getPhotos().getPhoto();
                        getPhotoUri(mPhotos);

                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError", e);
                    }
                });

    }

    private void getPhotoUri(List<Photo> photos) {
        List<Uri> uriListFlickr = new ArrayList<>();
        for (Photo photo : photos) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("https")
                    .authority("farm" + photo.getFarm() + ".staticflickr.com")
                    .appendPath(photo.getServer())
                    .appendPath(photo.getId() + "_" + photo.getSecret() + ".jpg");
            uriListFlickr.add(builder.build());
        }
        getViewState().updateUI(uriListFlickr);
    }




}
