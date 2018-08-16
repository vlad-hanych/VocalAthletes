package sheva.singapp.mvp.ui.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import sheva.singapp.R;
import sheva.singapp.mvp.model.entities.Exercise;
import sheva.singapp.mvp.model.entities.ExerciseWrapper;
import sheva.singapp.mvp.ui.adapters.PagerWarmUpAdapter;
import sheva.singapp.mvp.ui.fragments.FragmentEachItem;

public class WarmUpActivity extends AppCompatActivity implements FragmentEachItem.OnSongSelectedListener {
    @BindView(R.id.ibWarmUpBack)
    ImageButton ibBack;
    @BindView(R.id.tlSingSongTabs)
    TabLayout tabLayout;
    @BindView(R.id.vpWarmUp)
    ViewPager vpPager;

    private PagerWarmUpAdapter adapter;

    public static final String SONG_NAME = "song-name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warm_up);
        ButterKnife.bind(this);
        setUiFlags();
        adapter = new PagerWarmUpAdapter(getSupportFragmentManager());
        inflateTabs();
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void inflateTabs(){
        tabLayout.addTab(tabLayout.newTab().setText("Basic"));
        tabLayout.addTab(tabLayout.newTab().setText("Intermediate"));
        tabLayout.addTab(tabLayout.newTab().setText("Advanced"));
        tabLayout.addTab(tabLayout.newTab().setText("Melodic"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        vpPager.setAdapter(adapter);
        vpPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setUiFlags() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    public void onSongSelected(ExerciseWrapper entity) {
        Intent intent = new Intent(this, SingProcessActivity.class);
        intent.putExtra(SONG_NAME, entity);
        startActivity(intent);
    }

    @Override
    public void showError(String message, String title) {

    }
}
