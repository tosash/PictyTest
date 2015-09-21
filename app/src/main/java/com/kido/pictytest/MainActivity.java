package com.kido.pictytest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.software.shell.fab.ActionButton;

import static com.software.shell.fab.ActionButton.Type.DEFAULT;

public class MainActivity extends AppCompatActivity {
    public static View view_main;
    private static ActionButton actionButton;
    private static boolean isFAB = true;
    private static boolean bigFAB = false;
    private Toolbar mToolbar;
    private long lastBackPressTime = 0;
    private Toast toast;

    public boolean isFAB() {
        return isFAB;
    }

    public void setIsFAB(boolean isFAB) {
        this.isFAB = isFAB;
    }

    public boolean isBigFAB() {
        return bigFAB;
    }

    public void setBigFAB(boolean bigFAB) {
        this.bigFAB = bigFAB;
    }

    public void updateFAB() {
        actionButton = (ActionButton) findViewById(R.id.action_button);
        actionButton.setShowAnimation(ActionButton.Animations.SCALE_UP);
        actionButton.setHideAnimation(ActionButton.Animations.SCALE_DOWN);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get instance of Vibrator from current Context
                shotClick();
            }
        });
        if (isFAB) {
            actionButton.setButtonColor(Color.rgb(223, 48, 41));
            actionButton.setStrokeColor(Color.WHITE);

            if (!bigFAB) {
                actionButton.setType(DEFAULT);
                actionButton.setStrokeWidth(2.5f);
            } else {
                actionButton.setType(ActionButton.Type.BIG);
                actionButton.setStrokeWidth(3.0f);
            }
            actionButton.setVisibility(View.VISIBLE);
        } else {
            actionButton.setVisibility(View.INVISIBLE);
        }
    }

    public ActionButton getActionButton() {
        return actionButton;
    }

    public void setActionButton(ActionButton actionButton) {
        MainActivity.actionButton = actionButton;
    }

    public Toolbar getmToolbar() {
        return mToolbar;
    }

    public void setmToolbar(Toolbar mToolbar) {
        this.mToolbar = mToolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view_main = (CoordinatorLayout) findViewById(R.id.root_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        updateFAB();


        if (savedInstanceState == null) {

            Fragment fragment = new Fragment();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            fragment = new MainFragment();
            fragmentTransaction.replace(R.id.containerMain, fragment);
            fragmentTransaction.addToBackStack(MainFragment.class.toString());
            fragmentTransaction.commit();
        }
    }

    public void shotClick() {
        String ss = getActiveFragment();
        switch (ss) {
            case "MainFragment":
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(50);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
                Fragment fragment = new CameraFragment();
                fragmentTransaction.replace(R.id.containerMain, fragment, fragment.getClass().getSimpleName());
                fragmentTransaction.addToBackStack(CameraFragment.class.getSimpleName());
                fragmentTransaction.commit();
                break;
            case "CameraFragment":
                CameraFragment fc = (CameraFragment) getFragmentManager().findFragmentByTag(CameraFragment.class.getSimpleName());
                if (fc != null) {
                    fc.takePhoto();
                }
                break;
        }
    }


//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            actionButton.setVisibility(View.VISIBLE);
//            actionButton.playShowAnimation();
//        } else {
//            actionButton.playHideAnimation();
//        }
//    }

    public void clearBackStack() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 1) {
            if (this.lastBackPressTime < System.currentTimeMillis() - 4000) {
                toast = Toast.makeText(this, "Press BACK again to exit", 4000);
                toast.show();
                this.lastBackPressTime = System.currentTimeMillis();
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                super.onBackPressed();
            }
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public String getActiveFragment() {
        String st;
        Fragment f = getFragmentManager().findFragmentById(R.id.containerMain);
        st = f.getClass().getSimpleName();
        return st;
    }
}

