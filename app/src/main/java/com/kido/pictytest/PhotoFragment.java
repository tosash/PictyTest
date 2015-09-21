package com.kido.pictytest;


import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment {
    private static String bmp;
    private View rootView;
    private ImageButton btnSave;
    private ImageButton btnCancel;
    private ImageView imgView;
    private LinearLayout bottomPanel;
    private Context fContext;
    private MainActivity mActivity;
    private DonutProgress progress;
    private int curProgress=1;


    public PhotoFragment() {
        // Required empty public constructor
    }

    public static PhotoFragment newInstance(String data) {
        PhotoFragment mFragment = new PhotoFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString("bmp", data);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String data = getArguments().getString("bmp");
            if ((data != null) && (data != "")) {
                bmp = data;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_photo, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fContext = getActivity().getApplicationContext();
        mActivity = (MainActivity) getActivity();
        bottomPanel = (LinearLayout) rootView.findViewById(R.id.bottomPanelPhoto);
        progress =(DonutProgress) rootView.findViewById(R.id.donut_progress);
        progress.setVisibility(View.GONE);
        btnSave = (ImageButton) rootView.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendFilesTask().execute();

            }
        });
        btnCancel = (ImageButton) rootView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        imgView = (ImageView) rootView.findViewById(R.id.imgView);
        try {
            ImageLoader.getInstance().displayImage("file:///mnt/" + bmp, imgView);
        } catch (Exception e) {

        }

        if (savedInstanceState == null) {
            mActivity.setIsFAB(false);
            mActivity.updateFAB();
            startAnimation();
        }
    }
    private class SendFilesTask extends AsyncTask<URL, Integer, Integer> {
        protected Integer doInBackground(URL... urls) {
            int count = 100;
            for (int i = 0; i < count; i=i+7) {
                curProgress = curProgress+7;
                try {
                    Thread.currentThread();
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                publishProgress(curProgress);
                // Escape early if cancel() is called
                if (isCancelled()) break;
            }
            return curProgress;
        }

        protected void onProgressUpdate(Integer... pprogress) {
            progress.setProgress(pprogress[0]);
        }

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }

        protected void onPostExecute(Integer result) {
            progress.setVisibility(View.GONE);
            mActivity.onBackPressed();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mActivity.setIsFAB(false);
        mActivity.updateFAB();
    }

    public void startAnimation() {
        Animation animation = AnimationUtils.loadAnimation(fContext, R.anim.bottom_to_camera);
        Animation animationImg = AnimationUtils.loadAnimation(fContext, R.anim.image_show);
        bottomPanel.startAnimation(animation);
        imgView.startAnimation(animationImg);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }


}
