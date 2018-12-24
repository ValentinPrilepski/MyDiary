package com.mydiary.note;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mydiary.R;
import com.mydiary.flickrgallery.FlickrGalleryActivity;
import com.mydiary.fullscreenimage.FSImagePagerActivity;
import com.mydiary.notelist.NoteListFragment;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mydiary.utils.Converters.convertDpToPixels;

public class NoteFragment extends MvpAppCompatFragment implements INoteFragment {

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int GALLERY_REQUEST = 1;
    private static final String DIALOG_DATE = "DialogDate";
    private static final String ARG_NOTE_ID = "note_id";
    private static final String TAG = NoteFragment.class.getSimpleName();
    public static final String SEND_MODEL_BR = "model";
    public static final String ADD_PH_FR_FLICK = "com.mydiary.action.ADD_PH_FR_FLICK";
    public static final String EXT_NOTE_ID = "ext_note_id";

    private String mNoteId;
    private BroadcastReceiver mBroadcastReceiver;

    @InjectPresenter
    NoteFragmentPresenter mNoteFragmentPresenter;
    @BindView(R.id.images_container)
    LinearLayout mContainer;
    @BindView(R.id.et_note_title)
    EditText mTitleField;
    @BindView(R.id.et_field_enter_note)
    EditText mEnterField;
    @BindView(R.id.btn_note_date)
    Button mDateButton;
    @BindView(R.id.btn_add_cont)
    ImageButton mAddContentButton;
    @BindView(R.id.btn_actions)
    ImageButton mActions;


    public static NoteFragment newInstance(String noteId) {
        Bundle args = new Bundle();
        args.putString(ARG_NOTE_ID, noteId);
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNoteId = getArguments().getString(ARG_NOTE_ID);
            mNoteFragmentPresenter.getNote(mNoteId);
        }
        setHasOptionsMenu(true);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Uri uri = Uri.parse(intent.getStringExtra(FlickrGalleryActivity.SEND_FLICKR_URI));
                String id = intent.getStringExtra(FlickrGalleryActivity.NOTE_ID);
                if (mNoteId.equals(id)) {
                    mNoteFragmentPresenter.updateUriList(uri);
                    addImage(uri);
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(ADD_PH_FR_FLICK);
        if (getContext() != null) {
            getContext().registerReceiver(mBroadcastReceiver, intentFilter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mNoteFragmentPresenter.onPause();

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_note, container, false);
        ButterKnife.bind(this, v);
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mNoteFragmentPresenter.onTitleTextChanged(s);
                sendBroadcastUpdated();

            }
        });
        mEnterField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mNoteFragmentPresenter.onEnterNoteTextChanged(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateButton.setOnClickListener(v12 -> onClickDateButton());
        mAddContentButton.setOnClickListener(this::showPopupMenuAddContent);
        mActions.setOnClickListener(this::showPopupMenuActions);
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null) {
            getContext().unregisterReceiver(mBroadcastReceiver);
        }
    }

    private void loadFlickr() {
        Intent intent = new Intent(getActivity(), FlickrGalleryActivity.class);
        intent.putExtra(EXT_NOTE_ID, mNoteId);
        getContext().startActivity(intent);

    }
    @SuppressLint("RestrictedApi")
    private void showPopupMenuAddContent(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.m_add_content);
        popupMenu
                .setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.open_cam:
                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                                    && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                openCamera();
                            } else {
                                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                            return true;
                        case R.id.add_image:
                            addImageFromGallery();
                            return true;
                        case R.id.load_flickr:
                            loadFlickr();
                        default:
                            return false;
                    }
                });
        MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popupMenu.getMenu(), v);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();

    }

    @SuppressLint("RestrictedApi")
    private void showPopupMenuActions(View v) {
        PopupMenu popupMenu = new PopupMenu(getContext(), v);
        popupMenu.inflate(R.menu.m_actions);
        popupMenu
                .setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.share_note:
                            shareNote();
                            return true;
                        case R.id.delete_note:
                            deleteNote();
                            return true;
                        case R.id.copy_note:
                            mNoteFragmentPresenter.createNoteCopy();
                            Toast toast = Toast.makeText(getContext(),
                                    getString(R.string.copy_complete), Toast.LENGTH_SHORT);
                            toast.show();
                            return true;

                        default:
                            return false;
                    }
                });
        MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) popupMenu.getMenu(), v);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.m_fragment_note, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_DATE) {
            mNoteFragmentPresenter.setDateInNote(data);
        }

        if (requestCode == REQUEST_CODE_CAMERA) {
            addImage(mNoteFragmentPresenter.getPhotoUri(getContext()));
        }
        if (requestCode == GALLERY_REQUEST) {
            if (data.getData() != null) {
                Uri uri = data.getData();
                mNoteFragmentPresenter.updateUriList(uri);
                addImage(uri);
            } else if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mNoteFragmentPresenter.updateUriList(uri);
                    addImage(uri);
                }
            }
        }
    }


    @SuppressLint("CheckResult")
    private void addImage(Uri uri) {
        ImageView imageView = createImageView();
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();
        requestOptions.skipMemoryCache(true);
        Glide
                .with(this)
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
        mContainer.addView(imageView);
        imageView.setOnClickListener(v -> {
            Intent intent = FSImagePagerActivity.newIntent(getContext(), uri.toString(), mNoteFragmentPresenter.getUriList());
            intent.setData(uri);
            getContext().startActivity(intent);

        });
        imageView.setOnLongClickListener(v -> {
            AlertDialog.Builder a_builder = new AlertDialog.Builder(getContext());
            a_builder.setMessage(getString(R.string.delete_image))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel())
                    .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                        mContainer.removeView(imageView);
                        mNoteFragmentPresenter.deleteImage(uri);
                    });
            AlertDialog alertDialog = a_builder.create();
            alertDialog.show();
            return true;
        });
    }

    private void addImageFromGallery() {
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    private ImageView createImageView() {
        ImageView imageView = new ImageView(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(convertDpToPixels(200), convertDpToPixels(200));
        params.setMargins(0, 0, convertDpToPixels(1), 0);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    private void deleteNote() {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog);
        AlertDialog.Builder a_builder = new AlertDialog.Builder(contextThemeWrapper);
        a_builder.setMessage(getString(R.string.delete_note))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel())
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    mNoteFragmentPresenter.deleteNote();
                    if (getActivity().findViewById(R.id.detail_fragment_container) == null) {
                        getActivity().finish();
                    } else {
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().remove(this).commit();
                    }
                    Toast toast = Toast.makeText(getContext(),
                            getString(R.string.deleted), Toast.LENGTH_SHORT);
                    toast.show();
                });

        AlertDialog alertDialog = a_builder.create();
        alertDialog.show();
    }


    @Override
    public void loadImage(List<Uri> images) {
        if (images != null && images.size() != 0) {
            for (Uri uri : images) {
                addImage(uri);
            }
        }
    }


    private void onClickDateButton() {
        DatePickerFragment dialog = DatePickerFragment
                .newInstance(new Date(mNoteFragmentPresenter.getDate()));
        FragmentManager manager = getFragmentManager();
        if (manager != null) {
            dialog.show(manager, DIALOG_DATE);
        }
        dialog.setTargetFragment(this, REQUEST_DATE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mNoteFragmentPresenter.onRequestPermissionsResult(grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
            Uri uri = mNoteFragmentPresenter.createPhotoUri(getContext());
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA);
        }
    }

    private void shareNote() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, mNoteFragmentPresenter.getTextNote());
        intent.putExtra(Intent.EXTRA_SUBJECT, mNoteFragmentPresenter.getTitleNote());
        intent = Intent.createChooser(intent, getString(R.string.send_note));
        startActivity(intent);
    }

    @Override
    public void sendBroadcastUpdated() {
        Intent intent = new Intent();
        intent.setAction(NoteListFragment.UPD_MODEL_IN_LIST);
        intent.putExtra(SEND_MODEL_BR, mNoteFragmentPresenter.modelToJs());
        if (getContext() != null) {
            getContext().sendBroadcast(intent);
        }
    }

    @Override
    public void setData() {
        mTitleField.setText(mNoteFragmentPresenter.getTitleNote());
        mEnterField.setText(mNoteFragmentPresenter.getTextNote());
        mNoteFragmentPresenter.updateDate();
    }

    @Override
    public void setDateButton(String s) {
        mDateButton.setText(s);
        sendBroadcastUpdated();
    }


}

