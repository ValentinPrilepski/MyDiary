package com.mydiary.notelist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mydiary.App;
import com.mydiary.R;
import com.mydiary.model.AppDatabase;
import com.mydiary.model.Note;
import com.mydiary.model.NoteDao;
import com.mydiary.utils.SelectableModelWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;



@InjectViewState
public class NoteListPresenter extends MvpPresenter<INoteList> {
    private static final String TAG = NoteListPresenter.class.getSimpleName();
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private Disposable mDisposable;
    private AppDatabase db = App.getInstance().getDatabase();
    private NoteDao noteDao = db.noteDao();
    private boolean mSubtitleVisible;
    private List<SelectableModelWrapper<Note>> mNotes;
    private Map<String, SelectableModelWrapper<Note>> mMapNotes;
    private List<String> mKeys;

    public List<SelectableModelWrapper<Note>> getValues(List<String> keys) {
        List<SelectableModelWrapper<Note>> notes = new ArrayList<>();
        for (String key : keys) {
            SelectableModelWrapper<Note> note = mMapNotes.get(key);
            notes.add(note);
        }
        return notes;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_list, container,
                false);
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean
                    (SAVED_SUBTITLE_VISIBLE);
        }
        return view;
    }

    @Override
    public void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    public void createNewNote() {
        Note note = new Note();
        Observable.create(emitter -> {
            noteDao.insert(note);
            getViewState().showNote(note.getId());
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void loadData(boolean forced) {
        if (mNotes != null && !forced) {
            return;
        }
        mDisposable = noteDao.getAllStream()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> {
                    mNotes = SelectableModelWrapper.wrapModelList(notes, new SelectableModelWrapper.SelectionListener<Note>() {
                        @Override
                        public void OnSelectionChanged(SelectableModelWrapper<Note> selectionModel, boolean isSelected) {

                        }

                        @Override
                        public void OnSelectableModeChanged(SelectableModelWrapper<Note> selectionModel, boolean isSelectedMode) {
                            for (SelectableModelWrapper<Note> note : mNotes) {
                                note.setSelectableMode(isSelectedMode);
                            }
                            getViewState().toSelectionMode(isSelectedMode);
                        }
                    });
                    getViewState().updateUI(mNotes);
                    mMapNotes = new HashMap<>();
                    mKeys = new ArrayList<>();
                    for (SelectableModelWrapper<Note> note : mNotes) {
                        if (note.getModel().getTitle() != null) {
                            String s = note.getModel().getTitle().toUpperCase();
                            mKeys.add(s);
                            mMapNotes.put(s, note);
                        }
                    }
                });
    }

    public void loadData() {
        loadData(false);
    }

    public void onClickSearch(String s) {
        List<String> keys = new ArrayList<>();
        for (String key : mKeys) {
            if (key.contains(s.toUpperCase())) {
                keys.add(key);
            }
            getViewState().updateUI(getValues(keys));
        }
    }


    public boolean isSubtitleVisible() {
        return mSubtitleVisible;
    }


    public void inverseSubtitle() {
        mSubtitleVisible = !mSubtitleVisible;
    }


    public void updateItem(String json) {
        Note updatedNote = Note.parseJson(json);
        for (SelectableModelWrapper<Note> note : mNotes) {
            Note model = note.getModel();
            if (model.getId().equals(updatedNote.getId())) {
                model.copy(updatedNote);
                getViewState().updateItem(note, mNotes);
                break;
            }
        }
    }

    @SuppressLint("CheckResult")
    public void deleteSelected() {
        List<Note> noteToDelete = new ArrayList<>();
        for (SelectableModelWrapper<Note> note : mNotes) {
            if (note.isSelected()) {
                noteToDelete.add(note.getModel());
            }
        }
        Completable.create(emitter -> {
            noteDao.delete(noteToDelete);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    getViewState().toSelectionMode(false);
                    getViewState().notesDeleted(noteToDelete);
                });
    }
    @SuppressLint("CheckResult")
    public void copySelected() {
        List<Note> noteToCopy = new ArrayList<>();
        for (SelectableModelWrapper<Note> note : mNotes) {
            if (note.isSelected()) {
                Note newNote = new Note();
                newNote.copy(note.getModel());
                noteToCopy.add(newNote);
            }
        }
        Completable.create(emitter -> {
            noteDao.insert(noteToCopy);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    getViewState().toSelectionMode(false);
                });
    }
}

