package com.mydiary.flickrgallery;

import android.net.Uri;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;


public interface IFlickrGallery extends MvpView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void updateUI(List<Uri> uriList);

}
