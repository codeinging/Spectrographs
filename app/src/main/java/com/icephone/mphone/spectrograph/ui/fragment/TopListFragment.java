package com.icephone.mphone.spectrograph.ui.fragment;


import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.icephone.mphone.spectrograph.R;
import com.icephone.mphone.spectrograph.ui.base.BaseFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 刘道兴 on 2016/10/29.
 */

public class TopListFragment extends BaseFragment {
    @Bind(R.id.toplist_recycleView)
    RecyclerView toplistRecycleView;
    @Bind(R.id.toplist_swipefresh)
    SwipeRefreshLayout toplistSwipefresh;
    private ToplistAdapter toplistAdapter;
    private ArrayList<String> items;    //recycleView中的数据

    @Override
    public int bindLayout() {
        return R.layout.toplist_layout;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this, view);
        toplistRecycleView.setLayoutManager(new LinearLayoutManager(context));
        toplistAdapter = new ToplistAdapter();
        initData();
        toplistRecycleView.setAdapter(toplistAdapter);
        toplistSwipefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                toplistSwipefresh.setRefreshing(false);
                toplistAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        items = new ArrayList<String>();
    }

    @Override
    public void doBusiness() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    class ToplistAdapter extends RecyclerView.Adapter<ToplistAdapter.ToplistViewHolder> {



        @Override
        public void onBindViewHolder(ToplistViewHolder holder, int position) {
            holder.tv.setText(items.get(position) + 1);
        }

        @Override
        public ToplistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            ToplistViewHolder holder = new ToplistViewHolder(LayoutInflater.from(context).inflate(R.layout.index_recyclerview_item, parent, false));
            return holder;
        }


        @Override
        public int getItemCount() {
            return items.size();
        }

        class ToplistViewHolder extends RecyclerView.ViewHolder {
            TextView tv;

            public ToplistViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.item_tv);
            }
        }
    }
}
