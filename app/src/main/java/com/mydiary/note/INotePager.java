package com.mydiary.note;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.mydiary.model.Note;

import java.util.List;



public interface INotePager extends MvpView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setNotes(List<Note> notes);
}
