package com.icephone.mphone.spectrograph.ui.actiivty;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;

import com.icephone.mphone.spectrograph.R;
import com.icephone.mphone.spectrograph.ui.base.BaseActivity;
import com.icephone.mphone.spectrograph.ui.fragment.HistoryFragment;
import com.icephone.mphone.spectrograph.ui.fragment.IndexFragment;
import com.icephone.mphone.spectrograph.ui.fragment.PersonFragment;
import com.icephone.mphone.spectrograph.ui.fragment.TopListFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 刘道兴 on 2016/10/29.
 */

public class MainActivity extends BaseActivity {
    @Bind(R.id.index_bt)
    ImageButton indexBt;
    @Bind(R.id.history_bt)
    ImageButton historyBt;
    @Bind(R.id.hotlist_bt)
    ImageButton hotListBt;
    @Bind(R.id.person_bt)
    ImageButton personBt;
    @Bind(R.id.scan_bt)
    ImageButton scanBt;
    @Bind(R.id.frame_switch_content)
    FrameLayout frameSwitchContent;
    @Bind(R.id.ll_navigationbar)
    RadioGroup llNavigationbar;
    IndexFragment indexFragment;
    HistoryFragment historyFragment;
    TopListFragment topListFragment;
    PersonFragment personFragment;
    private android.support.v4.app.FragmentManager fragmentManager;

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected String[] getNeededPermissions() {
        return new String[0];
    }

    @Override
    protected void register2AppManager() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        //当Activity内存重启时，使用findFragmentByTag能防止fragment重叠
        if(savedInstanceState == null){
            indexFragment = new IndexFragment();
            historyFragment = new HistoryFragment();
            topListFragment = new TopListFragment();
            personFragment = new PersonFragment();

            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frame_switch_content,indexFragment,indexFragment.getClass().getName())
                                .add(R.id.frame_switch_content,historyFragment,historyFragment.getClass().getName())
                                .add(R.id.frame_switch_content,topListFragment,topListFragment.getClass().getName())
                                .add(R.id.frame_switch_content,personFragment,personFragment.getClass().getName())
                                .hide(historyFragment)
                                .hide(topListFragment)
                                .hide(personFragment)
                                .commit();
        }else {
            indexFragment = (IndexFragment) fragmentManager.findFragmentByTag(IndexFragment.class.getName());
            historyFragment = (HistoryFragment) fragmentManager.findFragmentByTag(HistoryFragment.class.getName());
            topListFragment = (TopListFragment) fragmentManager.findFragmentByTag(TopListFragment.class.getName());
            personFragment = (PersonFragment) fragmentManager.findFragmentByTag(PersonFragment.class.getName());
            fragmentManager.beginTransaction().show(indexFragment)
                                                .hide(historyFragment)
                                                .hide(topListFragment)
                                                .hide(personFragment)
                                                .commit();
        }
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getAllDrawable();
        setImageButtonBackground(R.id.index_bt);
    }

    Drawable index_unpress = null;
    Drawable index_press = null;
    Drawable history_unpress = null;
    Drawable history_press = null;
    Drawable toplist_unpress = null;
    Drawable toplist_press = null;
    Drawable my_unpress = null;
    Drawable my_press = null;


    private void getAllDrawable(){
        index_unpress = getResources().getDrawable(R.drawable.index);
        index_press = getResources().getDrawable(R.drawable.index_press);
        history_unpress = getResources().getDrawable(R.drawable.history);
        history_press = getResources().getDrawable(R.drawable.history_press);
        toplist_unpress = getResources().getDrawable(R.drawable.praise);
        toplist_press = getResources().getDrawable(R.drawable.praise_press);
        my_unpress = getResources().getDrawable(R.drawable.my);
        my_press = getResources().getDrawable(R.drawable.my_press);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
    }

    private void hideAllFragment(){

    }

    private void setImageButtonBackground(int id){
        indexBt.setImageDrawable(index_unpress);
        historyBt.setImageDrawable(history_unpress);
        hotListBt.setImageDrawable(toplist_unpress);
        personBt.setImageDrawable(my_unpress);
        switch (id) {
            case R.id.index_bt:
                indexBt.setImageDrawable(index_press);
                break;
            case R.id.history_bt:
                historyBt.setImageDrawable(history_press);
                break;
            case R.id.hotlist_bt:
                hotListBt.setImageDrawable(toplist_press);
                break;
            case R.id.person_bt:
                personBt.setImageDrawable(my_press);
                break;
        }
    }

    @OnClick({R.id.index_bt, R.id.history_bt, R.id.hotlist_bt, R.id.person_bt, R.id.scan_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.index_bt:
                setImageButtonBackground(R.id.index_bt);
                fragmentManager.beginTransaction()
                        .show(indexFragment)
                        .hide(historyFragment)
                        .hide(topListFragment)
                        .hide(personFragment)
                .commit();
                break;
            case R.id.history_bt:
                setImageButtonBackground(R.id.history_bt);
                fragmentManager.beginTransaction()
                        .show(historyFragment)
                        .hide(indexFragment)
                        .hide(topListFragment)
                        .hide(personFragment)
                        .commit();
                break;
            case R.id.hotlist_bt:
                setImageButtonBackground(R.id.hotlist_bt);
                fragmentManager.beginTransaction()
                        .show(topListFragment)
                        .hide(indexFragment)
                        .hide(historyFragment)
                        .hide(personFragment)
                        .commit();
                break;
            case R.id.person_bt:
                setImageButtonBackground(R.id.person_bt);
                fragmentManager.beginTransaction()
                        .show(personFragment)
                        .hide(indexFragment)
                        .hide(topListFragment)
                        .hide(historyFragment)
                        .commit();
                break;
            case R.id.scan_bt:
                Log.d(TAG,"scan onclick");
                break;
        }
    }
}
