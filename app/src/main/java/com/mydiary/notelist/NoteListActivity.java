package com.mydiary.notelist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.mydiary.R;
import com.mydiary.note.NoteFragment;
import com.mydiary.note.NotePagerActivity;




public class NoteListActivity extends AppCompatActivity implements CallbacksListNote {


    @Override
    public void onNoteSelected(String id) {
        if (findViewById(R.id.detail_fragment_container) == null) {
            Intent intent = NotePagerActivity.newIntent(this, id);
            startActivity(intent);
        } else {
            if (getSupportFragmentManager().findFragmentByTag(id) == null) {
                Fragment newDetail = NoteFragment.newInstance(id);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.detail_fragment_container, newDetail, id)
                        .commit();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masterdetail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }
        createFragment();
    }

    private void createFragment() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new NoteListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }


}
