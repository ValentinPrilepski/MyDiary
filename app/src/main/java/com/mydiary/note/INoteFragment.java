package com.mydiary.note;

import android.net.Uri;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

@StateStrategyType(AddToEndSingleStrategy.class)
public interface INoteFragment extends MvpView {
    @StateStrategyType(SkipStrategy.class)
    void loadImage(List<Uri> images);

    @StateStrategyType(SkipStrategy.class)
    void openCamera();

    void setData();

    void setDateButton(String s);

    @StateStrategyType(SkipStrategy.class)
    void sendBroadcastUpdated();

}
