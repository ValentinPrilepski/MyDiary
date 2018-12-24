package com.mydiary.note;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.mydiary.R;
import com.mydiary.model.Note;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mydiary.note.NoteFragment.newInstance;


public class NotePagerActivity extends MvpAppCompatActivity implements INotePager {
    private static final String TAG = NotePagerActivity.class.getSimpleName();
    private static final String EXTRA_NOTE_ID =
            "com.mydiary.note_id";

    @BindView(R.id.note_view_pager)
    ViewPager mViewPager;
    @InjectPresenter
    NotePagerPresenter mNotePagerPresenter;
    private String mNoteId;

    public static Intent newIntent(Context packageContext, String noteId) {
        Intent intent = new Intent(packageContext, NotePagerActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_pager);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setElevation(0f);
        }
        if (mNoteId == null) {
            mNoteId = getIntent()
                    .getStringExtra(EXTRA_NOTE_ID);
        }
        mNotePagerPresenter.getNotes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setNotes(List<Note> notes) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Note note = notes.get(position);
                return newInstance(note.getId());

            }

            @Override
            public int getCount() {
                return notes.size();
            }

        });
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(mNoteId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

}
