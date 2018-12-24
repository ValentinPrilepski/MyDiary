package com.mydiary.utils;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class SelectableModelWrapper<T> {

    private T mModel;
    private SelectionListener<T> mSelectionListener;

    private boolean mSelectableMode;
    private boolean mSelected;


    public static <T> List<SelectableModelWrapper<T>> wrapModelList(List<T> modelList, SelectionListener<T> selectionListener) {
        return wrapModelList(modelList, false, selectionListener);
    }

    public static <T> List<SelectableModelWrapper<T>> wrapModelList(List<T> modelList, Boolean isSelectableMode, SelectionListener<T> selectionListener) {
        List<SelectableModelWrapper<T>> wrappedModels = new ArrayList<>();
        for (T model : modelList) {
            wrappedModels.add(new SelectableModelWrapper<>(model, isSelectableMode, selectionListener));
        }
        return wrappedModels;
    }

    public static <T> List<T> unwrapModelList(List<SelectableModelWrapper<T>> wrappedList) {
        List<T> models = new ArrayList<>();
        for (SelectableModelWrapper<T> wrappedModel : wrappedList) {
            models.add(wrappedModel.getModel());
        }
        return models;
    }

    public interface SelectionListener<T> {
        void OnSelectionChanged(SelectableModelWrapper<T> selectionModel, boolean isSelected);

        void OnSelectableModeChanged(SelectableModelWrapper<T> selectionModel, boolean isSelectedMode);
    }

    public SelectableModelWrapper(T model, @NonNull SelectionListener<T> selectionListener) {
        this(model, false, selectionListener);
    }

    public SelectableModelWrapper(T model, boolean isSelectableMode, @NonNull SelectionListener<T> selectionListener) {
        this(model);
        this.mSelectionListener = selectionListener;
        this.setSelectableMode(isSelectableMode);
    }

    public SelectableModelWrapper(T model) {
        this.mModel = model;
    }

    public void setSelectionListener(SelectionListener<T> selectionListener) {
        this.mSelectionListener = selectionListener;
    }

    public T getModel() {
        return mModel;
    }

    public SelectionListener<T> getSelectionListener() {
        return this.mSelectionListener;
    }

    public void setSelectableMode(boolean selectableMode) {
        setSelectableMode(selectableMode, false);
    }

    public void setSelectableMode(boolean selectableMode, boolean notify) {
        mSelectableMode = selectableMode;
        if (!mSelectableMode) {
            mSelected = false;
        }
        if (notify) {
            mSelectionListener.OnSelectableModeChanged(this, selectableMode);
        }
    }

    public void setSelected(boolean selected, boolean notify) {
        mSelected = selected;
        if (mSelectionListener != null && notify) {
            mSelectionListener.OnSelectionChanged(this, selected);
        }
    }

    public void setSelected(boolean selected) {
        setSelected(selected, true);
    }

    public boolean switchSelection() {
        mSelected = !mSelected;
        if (mSelectionListener != null) {
            mSelectionListener.OnSelectionChanged(this, mSelected);
        }
        return mSelected;
    }

    public boolean isSelectableMode() {
        return this.mSelectableMode;
    }

    public boolean isSelected() {
        return this.mSelected;
    }

}