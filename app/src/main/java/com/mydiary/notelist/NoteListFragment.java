package com.mydiary.notelist;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mydiary.R;
import com.mydiary.model.Note;
import com.mydiary.note.NoteFragment;
import com.mydiary.utils.SelectableModelWrapper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;




public class NoteListFragment extends MvpAppCompatFragment implements INoteList {

    private static final String TAG = NoteListFragment.class.getSimpleName();
    public static final String UPD_MODEL_IN_LIST = "diaryapp.action.UPD_MODEL_IN_LIST";

    private NoteAdapter mAdapter;
    private MenuItem mSubtitleItem;
    private MenuItem mSearchItem;
    private CallbacksListNote mCallbacksListNote;
    private BroadcastReceiver mBroadcastReceiver;
    private ActionMode mActionMode;

    @InjectPresenter
    NoteListPresenter mNoteListPresenter;
    @BindView(R.id.note_recycler_view)
    RecyclerView mNoteRecyclerView;
    @BindView(R.id.new_note)
    FloatingActionButton mFabNewNote;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacksListNote = (CallbacksListNote) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String json = intent.getStringExtra(NoteFragment.SEND_MODEL_BR);
                mNoteListPresenter.updateItem(json);

            }
        };

        IntentFilter intentFilter = new IntentFilter(UPD_MODEL_IN_LIST);
        if (getContext() != null) {
            getContext().registerReceiver(mBroadcastReceiver, intentFilter);
        }
        mNoteListPresenter.loadData();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = mNoteListPresenter.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager
                (getActivity()));
        mFabNewNote.setOnClickListener(v -> {
            mNoteListPresenter.loadData();
            mNoteListPresenter.createNewNote();
        });
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mNoteListPresenter.onSaveInstanceState(outState);
    }

    @Override
    public void toSelectionMode(boolean isSelectionMode) {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        if (isSelectionMode) {
            mActionMode = getActivity().startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.m_action_mode_note_list, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.copy_selectable_notes:
                            copySelected();
                            return true;
                        case R.id.delete_selectable_notes:
                            deleteSelected();
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    mAdapter.cancelSelectionMode();
                }
            });
        }
    }

    private void deleteSelected() {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog);
        AlertDialog.Builder a_builder = new AlertDialog.Builder(contextThemeWrapper);
        a_builder.setMessage(getString(R.string.delete_selected))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel())
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    mNoteListPresenter.deleteSelected();
                    mActionMode.finish();
                    Toast toast = Toast.makeText(getContext(),
                            getString(R.string.deleted), Toast.LENGTH_SHORT);
                    toast.show();

                });
        AlertDialog alertDialog = a_builder.create();
        alertDialog.show();
    }

    private void copySelected() {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog);
        AlertDialog.Builder a_builder = new AlertDialog.Builder(contextThemeWrapper);
        a_builder.setMessage(getString(R.string.copy_selected))
                .setCancelable(false)
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel())
                .setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                    mNoteListPresenter.copySelected();
                    mActionMode.finish();
                    Toast toast = Toast.makeText(getContext(),
                            getString(R.string.copy_complete), Toast.LENGTH_SHORT);
                    toast.show();

                });
        AlertDialog alertDialog = a_builder.create();
        alertDialog.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacksListNote = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null) {
            getContext().unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.m_fragment_note_list, menu);
        mSubtitleItem = menu.findItem(R.id.show_count_notes);
        mSearchItem = menu.findItem(R.id.search);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_count_notes:
                mNoteListPresenter.inverseSubtitle();
                updateActionBar();
                return true;
            case R.id.search:
                SearchView searchView = (SearchView) mSearchItem.getActionView();
                searchView.setFocusable(false);
                searchView.setQueryHint(getString(R.string.search));
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        mNoteListPresenter.onClickSearch(s);
                        return false;
                    }

                });

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void updateUI(List<SelectableModelWrapper<Note>> notes) {
        if (mAdapter == null) {
            createAdapter(notes);
        } else {
            mAdapter.setNotes(notes);
            mAdapter.notifyDataSetChanged();
        }
        updateActionBar();
    }

    @Override
    public void notesDeleted(List<Note> noteToDelete) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        for (Note note : noteToDelete) {
            Fragment fragment;
            if ((fragment = fragmentManager.findFragmentByTag(note.getId())) != null) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        }
    }

    @Override
    public void updateItem(SelectableModelWrapper<Note> note, List<SelectableModelWrapper<Note>> notes) {
        if (mAdapter == null) {
            createAdapter(notes);
        }
        mAdapter.updateItem(note);
    }

    private void createAdapter(List<SelectableModelWrapper<Note>> notes) {
        mAdapter = new NoteAdapter(notes);
        mAdapter.setHasStableIds(true);
        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }
        });
        mNoteRecyclerView.setAdapter(mAdapter);
        mNoteRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public void showNote(String id) {
        mCallbacksListNote.onNoteSelected(id);
    }

    public void updateActionBar() {
        int count = 0;
        if (mAdapter != null) {
            count = mAdapter.getItemCount();
        }
        String subtitle = getResources()
                .getQuantityString(R.plurals.subtitle_plural, count, count);
        if (!mNoteListPresenter.isSubtitleVisible()) {
            subtitle = null;
        }
        if (mSubtitleItem != null) {
            mSubtitleItem.setIcon(mNoteListPresenter.isSubtitleVisible() ? R.drawable.ic_hide_count_notes : R.drawable.ic_show_count_notes);
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null) {
            activity.getSupportActionBar().setSubtitle(subtitle);
        }
    }
}

