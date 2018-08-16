package sheva.singapp.mvp.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blur.andrey.blurtest.BlurActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fotoapparat.Fotoapparat;
import io.fotoapparat.parameter.ScaleType;
import io.fotoapparat.parameter.selector.AspectRatioSelectors;
import io.fotoapparat.parameter.selector.LensPositionSelectors;
import io.fotoapparat.parameter.selector.SizeSelectors;
import sheva.singapp.App;
import sheva.singapp.R;
import sheva.singapp.mvp.presenter.activities.MenuActivityPresenter;
import sheva.singapp.mvp.ui.adapters.PagerMenuPicsAdapter;
import sheva.singapp.mvp.ui.fragments.LoginFragment;
import sheva.singapp.mvp.ui.interfaces.IMenuActivityView;

public class MenuActivity extends AppCompatActivity implements IMenuActivityView, LoginFragment.OnFragmentInteractionListener{
    @BindView(R.id.rlMenuItemProgressReport)
    RelativeLayout rlProgressReport;
    @BindView(R.id.rlMenuItemSettings)
    RelativeLayout rlSettings;
    @BindView(R.id.rlMenuItemSingSongs)
    RelativeLayout rlSingSongs;
    ///
    /*@BindView(R.id.rlMenuItemTrainingProgram)
    RelativeLayout rlTrainingProgram;*/
    @BindView(R.id.rlMenuItemWarmUp)
    RelativeLayout rlWarmUp;
    @BindView(R.id.vpMenuPics)
    ViewPager vpPics;
    private PagerMenuPicsAdapter adapter;
    private Handler handler;
    private boolean isScrolling = true;
    private DialogFragment fragment;
    private final MenuActivityPresenter presenter = new MenuActivityPresenter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        App.get().getAppComponent().inject(this);
        ButterKnife.bind(this);
        presenter.attachView(this);
        adapter = new PagerMenuPicsAdapter(this);
        vpPics.setAdapter(adapter);
        vpPics.setOffscreenPageLimit(5);
        handler = new Handler();
        startInfiniteScroll();
        setUiFlags();
        rlSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.showAlertInfo();
            }
        });
        rlWarmUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, WarmUpActivity.class);
                startActivity(intent);
            }
        });
        rlSingSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, SingSongActivity.class);
                startActivity(intent);
            }
        });
        rlProgressReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, ProgressReportActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUiFlags();
    }

    private void startInfiniteScroll() {
        if(isScrolling) {
            vpPics.setCurrentItem(0);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vpPics.setCurrentItem(1);
                }
            }, 2000);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vpPics.setCurrentItem(2);
                }
            }, 4000);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vpPics.setCurrentItem(3);
                }
            }, 6000);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vpPics.setCurrentItem(4);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startInfiniteScroll();
                        }
                    }, 2000);
                }
            }, 8000);
        } else {
            isScrolling = true;
        }
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

    private void stopInfiniteScroll() {
        isScrolling = false;
    }

    @Override
    public void loginUser(String username, String password) {
        presenter.loginUser(username, password);
    }

    @Override
    public void showAlertInfo() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setPositiveButton("Log in", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.showLoginFragment();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setMessage("Please, ask your teacher to give you an account info.")
                .create();
        dialog.show();
        setUiFlags();
    }

    @Override
    public void showLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        fragment = LoginFragment.newInstance("");
        fragment.show(ft, "dialog");
    }

    @Override
    public void setUsername(String username) {
        Toast.makeText(this, "Hello, " + username, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessfullyLogged(String username) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment).commit();
        Toast.makeText(this, "Hello, " + username, Toast.LENGTH_SHORT).show();
        setUiFlags();
    }

    @Override
    public void onLoginError() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Failed to login. Incorrect username or password")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onLogoutSuccess() {
        Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLogoutError() {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Failed to logout. You have to sign in first!")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onLogIn(String username, String password) {
        presenter.loginUser(username, password);
    }

    @Override
    public void closeFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.remove(fragment).commit();
    }

    public void qqqonclick(View v) {
        startActivity(new Intent(MenuActivity.this, BlurActivity.class));
    }
}
