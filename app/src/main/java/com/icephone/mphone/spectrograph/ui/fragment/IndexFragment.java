package com.icephone.mphone.spectrograph.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.icephone.mphone.spectrograph.R;
import com.icephone.mphone.spectrograph.ui.adapter.ImageHolderView;
import com.icephone.mphone.spectrograph.ui.base.BaseFragment;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 刘道兴 on 2016/10/29.
 */

public class IndexFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @Bind(R.id.convenientBanner)
    ConvenientBanner convenientBanner;
    @Bind(R.id.swipe_fresh)
    SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<Integer> localImages = new ArrayList<Integer>();  //保存图片
    private IndexAdapter indexAdapter;
    private ArrayList<String> items;    //recycleView中的数据

    @Override
    public int bindLayout() {
        return R.layout.index_layout;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this, view);
        initBanner();
        indexAdapter = new IndexAdapter();
        initData();
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_fresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                for (int i = 0; i < 8; i++) {
                    items.set(i, items.get(i) + 1);
                }
                swipeRefreshLayout.setRefreshing(false);
                indexAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        items = new ArrayList<String>();
        for (int i = 0; i < 8; i++) {
            items.add(i, "" + i);
        }
    }

    @Override
    public void doBusiness() {

    }

    private void initBanner() {
        loadImageDatas();
        convenientBanner.setPages(
                new CBViewHolderCreator() {
                    @Override
                    public ImageHolderView createHolder() {
                        return new ImageHolderView();
                    }
                }
                , localImages)
                .setPageIndicator(new int[]{R.drawable.ic_page_indicator, R.drawable.ic_page_indicator_focused});
    }

    private void loadImageDatas() {
        for (int position = 0; position < 7; position++)
            localImages.add(getResId("ic_test_" + position, R.drawable.class));
    }

    private int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onResume() {
        super.onResume();
        convenientBanner.startTurning(5000);
    }

    @Override
    public void onPause() {
        super.onPause();
        convenientBanner.stopTurning();
    }

    @Override
    public void onPageSelected(int position) {
        Toast.makeText(this.context, "监听到翻到第" + position + "了", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.IndexViewHolder> {

        @Override
        public IndexViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            IndexViewHolder holder = new IndexViewHolder(LayoutInflater.from(context).inflate(R.layout.index_recyclerview_item, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(IndexViewHolder holder, int position) {
            holder.tv.setText(items.get(position) + 1);
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class IndexViewHolder extends RecyclerView.ViewHolder {
            TextView tv;

            public IndexViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.item_tv);
            }
        }
    }


}
