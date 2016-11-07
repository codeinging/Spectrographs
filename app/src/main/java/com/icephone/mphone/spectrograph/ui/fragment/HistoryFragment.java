package com.icephone.mphone.spectrograph.ui.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.icephone.mphone.spectrograph.R;
import com.icephone.mphone.spectrograph.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 刘道兴 on 2016/10/29.
 */

public class HistoryFragment extends BaseFragment {
    @Bind(R.id.historyFragment_viewpager)
    ViewPager historyFragmentViewpager;
    @Bind(R.id.history_viewpager_tablayout)
    TabLayout historyViewpagerTablayout;
    private LinearLayout viewpager_scan;
    private LinearLayout viewpager_search;
    private LinearLayout viewpager_mall;
    private List<LinearLayout> linearLayoutList;    //储存viewpager中的三个页面
    private List<String> tabTitles;

    @Override
    public int bindLayout() {
        return R.layout.history_layout;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this, view);
        initViewPager();
    }

    private void initViewPager() {
        linearLayoutList = new ArrayList<LinearLayout>();
        tabTitles = new ArrayList<String>();
        viewpager_scan = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.viewpager_scan_layout, null);
        viewpager_search = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.viewpager_search_layout, null);
        viewpager_mall = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.viewpager_mall_layout, null);
        linearLayoutList.add(viewpager_scan);
        linearLayoutList.add(viewpager_search);
        linearLayoutList.add(viewpager_mall);

        tabTitles.add("扫描");
        tabTitles.add("搜索");
        tabTitles.add("商城");

        historyViewpagerTablayout.addTab(historyViewpagerTablayout.newTab().setText(tabTitles.get(0)));
        historyViewpagerTablayout.addTab(historyViewpagerTablayout.newTab().setText(tabTitles.get(1)));
        historyViewpagerTablayout.addTab(historyViewpagerTablayout.newTab().setText(tabTitles.get(2)));
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return linearLayoutList.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(linearLayoutList.get(position));
                return linearLayoutList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(linearLayoutList.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return tabTitles.get(position);
            }
        };
        historyFragmentViewpager.setAdapter(pagerAdapter);
        historyViewpagerTablayout.setupWithViewPager(historyFragmentViewpager);
    }

    @Override
    public void doBusiness() {

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
