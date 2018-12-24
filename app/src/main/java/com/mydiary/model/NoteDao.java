package com.mydiary.model;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;


@Dao
public interface NoteDao {
    @Query("SELECT * FROM tableNotes")
    Flowable<List<Note>> getAllStream();

    @Query("SELECT * FROM tableNotes")
    Single<List<Note>> getAll();

    @Query("SELECT * FROM tableNotes WHERE mId = :id")
    Single<Note> getById(String id);

    @Insert
    void insert(Note note);

    @Insert
    void insert(List<Note> note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Delete
    void delete(List<Note> note);
}
