package com.mydiary.notelist;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.mydiary.model.Note;
import com.mydiary.utils.SelectableModelWrapper;

import java.util.List;




public interface INoteList extends MvpView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void updateUI(List<SelectableModelWrapper<Note>> notes);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void toSelectionMode(boolean isSelectionMode);

    @StateStrategyType(SkipStrategy.class)
    void showNote(String id);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void updateItem(SelectableModelWrapper<Note> note, List<SelectableModelWrapper<Note>> notes);

    @StateStrategyType(SkipStrategy.class)
    void notesDeleted(List<Note> noteToDelete);
}
