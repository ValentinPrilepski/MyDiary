package com.mydiary.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mydiary.utils.Converters;
import com.mydiary.utils.GsonHelper;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity(tableName = "tableNotes")
public class Note {
    @NonNull
    @PrimaryKey
    private String mId;
    private String mTitle;
    private long mDate;
    private String mTextInputField;
    @TypeConverters({Converter.class})
    private List<Uri> mUriImages;

    @Ignore
    public Note() {
        this(UUID.randomUUID().toString());
    }

    public Note(String id) {
        mId = id;
        mDate = new Date().getTime();
    }

    @NonNull
    public String getId() {
        return mId;
    }

    public void setId(@NonNull String id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public long getDate() {
        return mDate;
    }

    public void setDate(long date) {
        mDate = date;
    }

    public String getTextInputField() {
        return mTextInputField;
    }

    public void setTextInputField(String inputField) {
        mTextInputField = inputField;
    }

    public List<Uri> getUriImages() {
        return mUriImages;
    }

    public void setUriImages(List<Uri> uriImages) {
        mUriImages = uriImages;
    }

    public void copy(Note updatedNote) {
        mTitle = updatedNote.mTitle;
        mDate = updatedNote.mDate;
        mTextInputField = updatedNote.mTextInputField;
        mUriImages = updatedNote.mUriImages;
    }

    public static Note parseJson(String json) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new GsonHelper.UriDeserializer())
                .create();
        return gson.fromJson(json, Note.class);
    }

    public String toJson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Uri.class, new GsonHelper.UriSerializer())
                .create();
        return gson.toJson(this);
    }

    public static class Converter {

        @TypeConverter
        public static String uriToString(List<Uri> uriList) {
            return Converters.convertUriToString(uriList);
        }

        @TypeConverter
        public static List<Uri> stringToUri(String stringUri) {
            return Converters.convertStringToUri(stringUri);
        }

    }
}

