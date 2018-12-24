package com.mydiary.note;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.mydiary.App;
import com.mydiary.model.AppDatabase;
import com.mydiary.model.Note;
import com.mydiary.model.NoteDao;
import com.mydiary.utils.Converters;
import com.mydiary.utils.FileUtils;
import com.mydiary.utils.FormatDate;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


@InjectViewState
public class NoteFragmentPresenter extends MvpPresenter<INoteFragment> {
    private static final String TAG = NoteFragmentPresenter.class.getSimpleName();


    private AppDatabase db = App.getInstance().getDatabase();
    private NoteDao noteDao = db.noteDao();
    private Disposable mDisposable;
    private Note mNote;
    private String mCameraPhotoFileName;
    private List<Uri> mUriList;

    public void onPause() {
        if (mNote != null) {
            Observable.create(emitter -> noteDao.update(mNote))
                    .subscribeOn(Schedulers.io())
                    .subscribe();
        }
    }

    public long getDate() {
        return mNote.getDate();

    }

    @Override
    public void onDestroy() {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    public void createNoteCopy() {
        Note note = new Note();
        Observable.create(emitter -> {
            note.copy(mNote);
            noteDao.insert(note);
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public Uri createPhotoUri(Context context) {
        mCameraPhotoFileName = "IMG_" + System.currentTimeMillis() % 1000000 + ".jpg";
        Uri uri = FileUtils.getPhotoFileUri(context, mCameraPhotoFileName, "photo", true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && uri != null) {
            mCameraPhotoFileName = uri.toString().substring(uri.toString().lastIndexOf("/") + 1);
        } else {
            FileUtils.tempFileName = mCameraPhotoFileName;
        }
        return uri;
    }

    public void deleteImage(Uri uri) {
        mUriList.remove(uri);
    }

    public void deleteNote() {
        Completable.create(emitter -> {
            noteDao.delete(mNote);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    public void getNote(String noteId) {
        mDisposable = noteDao.getById(noteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(note -> {
                    mUriList = note.getUriImages();
                    mNote = note;
                    getViewState().loadImage(mUriList);
                    getViewState().setData();
                });
    }

    public Uri getPhotoUri(Context context) {
        Uri takenPhotoUri = FileUtils.getPhotoFileUri(context, mCameraPhotoFileName, "photo");
        return takenPhotoUri;

    }

    public String getTitleNote() {
        return mNote.getTitle();
    }

    public String getTextNote() {
        return mNote.getTextInputField();
    }

    public String modelToJs() {
        return mNote.toJson();
    }

    public String getUriList() {
        return Converters.convertUriToString(mUriList);
    }

    public void onRequestPermissionsResult(@NonNull int[] grantResults) {
        boolean permissionGranted = true;
        for (int result : grantResults) {
            permissionGranted = permissionGranted && result == PackageManager.PERMISSION_GRANTED;
        }
        if (permissionGranted) {
            getViewState().openCamera();
        }
    }

    public void onTitleTextChanged(CharSequence s) {
        if (mNote != null) {
            mNote.setTitle(s.toString());
        }
    }

    public void onEnterNoteTextChanged(CharSequence s) {
        if (mNote != null) {
            mNote.setTextInputField(s.toString());
        }
    }

    public void setDateInNote(Intent data) {
        Date date = (Date) data
                .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
        mNote.setDate(date.getTime());
        updateDate();
    }

    public void updateUriList(Uri uri) {
        mUriList.add(uri);
        mNote.setUriImages(mUriList);
    }

    public void updateDate() {
        Date date = new Date(mNote.getDate());
        String s = FormatDate.updateFormatDate(date);
        getViewState().setDateButton(s);

    }


}
