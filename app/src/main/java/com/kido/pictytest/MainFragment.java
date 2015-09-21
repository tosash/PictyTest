package com.kido.pictytest;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import jp.wasabeef.recyclerview.animators.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.adapters.ScaleInAnimationAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private static List<Pict> picts;
    Runnable runUpdate;
    private CustomSwype mSwipeRefreshLayout;
    private String TAG = MainActivity.class.getSimpleName();
    private boolean isTaskRunning = false;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private AlphaInAnimationAdapter alphaAdapter;
    private ScaleInAnimationAdapter scaleAdapter;
    private Context fContext;
    private View rootView;
    private MainActivity mActivity;
    private boolean isManualUpdate;

    {
        runUpdate = new Runnable() {
            public void run() {
                int min = 0;
                int max = picts.size() - 1;
                Random r = new Random();
                int i1 = r.nextInt(max - min + 1) + min;

                Pict pictNew = new Pict(picts.get(i1).getId(), picts.get(i1).getUrl(), picts.get(i1).getFlag(), picts.get(i1).getOs());
                picts.add(0, pictNew);
                recyclerView.getAdapter().notifyItemInserted(0);
                recyclerView.scrollToPosition(0);
                isTaskRunning = false;
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
    }

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fContext = getActivity().getApplicationContext();
        mActivity = (MainActivity) getActivity();

        if (picts == null) {
            picts = new ArrayList<>();
            picts.add(new Pict(1, "http://data14.gallery.ru/albums/gallery/157320-306a6-39750020-m750x740-udc912.jpg", "br", 1));
            picts.add(new Pict(2, "http://data14.gallery.ru/albums/gallery/157320-2112e-39750040-m750x740-u699c1.jpg", "ua", 1));
            picts.add(new Pict(3, "http://data15.gallery.ru/albums/gallery/4819-2d9f3-62127094-m750x740-u1225f.jpg", "usa", 1));
            picts.add(new Pict(4, "http://data14.gallery.ru/albums/gallery/157320-576e9-39750006-m750x740-ucd76e.jpg", "gb", 1));
        }
        recyclerView = (RecyclerView) rootView.findViewById(R.id.cardList_get_photos);
        recyclerView.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(fContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
//        recyclerView.getItemAnimator().setAddDuration(1000);
//        recyclerView.getItemAnimator().setRemoveDuration(1000);
//        recyclerView.getItemAnimator().setMoveDuration(1000);
//        recyclerView.getItemAnimator().setChangeDuration(1000);

        mAdapter = new PictyAdapter(fContext, picts);
        alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
        scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
        scaleAdapter.setFirstOnly(false);
        scaleAdapter.setInterpolator(new OvershootInterpolator());
        recyclerView.setAdapter(scaleAdapter);

        mSwipeRefreshLayout = (CustomSwype) rootView.findViewById(R.id.id_swype_get_photos);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isManualUpdate = true;
                getShotsFromServer();
            }
        });
        mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.YELLOW);
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);



        if (isTaskRunning) {
            mSwipeRefreshLayout.setRefreshing(true);
        }


        if (savedInstanceState == null) {
            mActivity.getmToolbar().setVisibility(View.VISIBLE);
            mActivity.setBigFAB(false);
            mActivity.setIsFAB(true);
            mActivity.updateFAB();
            mActivity.getActionButton().playShowAnimation();
            getShotsFromServer();
        } else {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String s = savedInstanceState.getString("picts");
            Pict[] obj = gson.fromJson(s, Pict[].class);
            if (obj == null) {
                picts = new ArrayList<Pict>();
            } else {
                picts = new ArrayList<Pict>(Arrays.asList(obj));
            }
            mAdapter = new PictyAdapter(fContext, picts);
            alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
            scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
            scaleAdapter.setFirstOnly(false);
            scaleAdapter.setInterpolator(new OvershootInterpolator());
            recyclerView.setAdapter(scaleAdapter);
        }

    }

    public void getShotsFromServer() {
        if (!isManualUpdate) {
            return;
        }
        isManualUpdate = false;
        if (!isTaskRunning) {
            isTaskRunning = true;
            mSwipeRefreshLayout.setRefreshing(true);
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        mActivity.runOnUiThread(runUpdate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        String s = gson.toJson(picts);
        outState.putString("picts", s);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Gson gson = new GsonBuilder()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create();
            String s = savedInstanceState.getString("picts");
            Pict[] obj = gson.fromJson(s, Pict[].class);
            if (obj == null) {
                picts = new ArrayList<Pict>();
            } else {
                picts = new ArrayList<Pict>(Arrays.asList(obj));
            }
            mAdapter = new PictyAdapter(fContext, picts);
            alphaAdapter = new AlphaInAnimationAdapter(mAdapter);
            scaleAdapter = new ScaleInAnimationAdapter(alphaAdapter);
            scaleAdapter.setFirstOnly(false);
            scaleAdapter.setInterpolator(new OvershootInterpolator());
            recyclerView.setAdapter(scaleAdapter);
        }
        super.onViewStateRestored(savedInstanceState);
    }


    @Override
    public void onDetach() {
        if (mSwipeRefreshLayout != null && mSwipeRefreshLayout.isShown()) {
            mSwipeRefreshLayout.setRefreshing(false);
            VolleySingleton.getInstance(fContext).cancelPendingRequests(TAG);
        }
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity.setIsFAB(true);
        mActivity.setBigFAB(false);
        mActivity.updateFAB();
    }
}
