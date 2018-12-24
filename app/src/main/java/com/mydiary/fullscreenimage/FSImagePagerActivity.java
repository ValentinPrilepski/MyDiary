package com.mydiary.fullscreenimage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.mydiary.R;
import com.mydiary.utils.Converters;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FSImagePagerActivity extends AppCompatActivity {
    private static final String EXTRA_URI = "extra_uri";
    private static final String EXTRA_URI_LIST = "extra_uri_list";

    @BindView(R.id.vp_full_screen_image)
    ViewPager mViewPager;
    private Uri mUri;


    public static Intent newIntent(Context packageContext, String uri, String uriList) {
        Intent intent = new Intent(packageContext, FSImagePagerActivity.class);
        intent.putExtra(EXTRA_URI, uri);
        intent.putExtra(EXTRA_URI_LIST, uriList);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_screen_image_pager_activity);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mUri = Uri.parse(getIntent().getStringExtra(EXTRA_URI));
        List<Uri> uriList = Converters.convertStringToUri(getIntent().getStringExtra(EXTRA_URI_LIST));
        setUris(uriList);
        Slidr.replace(mViewPager, new SlidrConfig.Builder()
                .position(SlidrPosition.BOTTOM)
                .build());
    }


    private void setUris(List<Uri> uries) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Uri uri = uries.get(position);
                return FSImageFragment.newInstance(uri.toString());
            }

            @Override
            public int getCount() {
                return uries.size();
            }

        });
        for (int i = 0; i < uries.size(); i++) {
            if (uries.get(i).equals(mUri)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}

