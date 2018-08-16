package sheva.singapp.mvp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import sheva.singapp.R;
import sheva.singapp.mvp.model.entities.ExerciseWrapper;
import sheva.singapp.mvp.presenter.activities.SingSongActivityPresenter;
import sheva.singapp.mvp.ui.adapters.PagerWarmUpAdapter;
import sheva.singapp.mvp.ui.fragments.FragmentEachItem;
import sheva.singapp.mvp.ui.interfaces.ISingSongActivityView;


public class SingSongActivity extends AppCompatActivity implements FragmentEachItem.OnSongSelectedListener, ISingSongActivityView{
    @BindView(R.id.tlSingSongTabs)
    TabLayout tabLayout;
    @BindView(R.id.vpSingSong)
    ViewPager vpPager;
    @BindView(R.id.ibSingSongBack)
    ImageButton ibBack;

    private PagerWarmUpAdapter adapter;

    private SingSongActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_song);
        ButterKnife.bind(this);
        adapter = new PagerWarmUpAdapter(getSupportFragmentManager());
        setUiFlags();
        presenter = new SingSongActivityPresenter();
        presenter.attachView(this);
        inflateTabs();
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        presenter.destroyPresenter();
        super.onDestroy();
    }

    private void inflateTabs(){
        tabLayout.addTab(tabLayout.newTab().setText("Popular"));
        tabLayout.addTab(tabLayout.newTab().setText("A-Z"));
        tabLayout.addTab(tabLayout.newTab().setText("Your Songs"));
        tabLayout.addTab(tabLayout.newTab().setText("Song Suggest"));
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
        intent.putExtra(WarmUpActivity.SONG_NAME, entity);
        startActivity(intent);
    }

    @Override
    public void showError(String message, String title) {
        presenter.showError(message, title);
    }

    @Override
    public void onError(String message, String title, String positiveButtonText, String cancelButtonText) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //
                    }
                })
                .show();
    }
}
