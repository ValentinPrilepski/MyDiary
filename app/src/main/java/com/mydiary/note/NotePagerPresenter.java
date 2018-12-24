package com.mydiary.note;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mydiary.App;
import com.mydiary.model.AppDatabase;
import com.mydiary.model.NoteDao;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


@InjectViewState
public class NotePagerPresenter extends MvpPresenter<INotePager> {

    private static final String TAG = NotePagerPresenter.class.getSimpleName();

    private AppDatabase db = App.getInstance().getDatabase();
    private NoteDao noteDao = db.noteDao();
    private Disposable disposable;

    public void getNotes() {
        disposable = noteDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(notes -> getViewState().setNotes(notes));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
