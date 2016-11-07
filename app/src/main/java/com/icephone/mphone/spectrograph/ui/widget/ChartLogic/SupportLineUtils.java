package com.icephone.mphone.spectrograph.ui.widget.ChartLogic;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.renderer.support.SupportColorLevel;
import org.achartengine.renderer.support.SupportSeriesRender;

import java.util.ArrayList;

/**
 * Created by wangjia on 28/06/14.
 */
public class SupportLineUtils extends  BaseSupportUtils{


    private static final String TAG = SupportLineUtils.class.getSimpleName();
    private final  static int COLOR_UP_TARGET = Color.parseColor("#FF843D");
    private final  static int COLOR_LOW_TARGET = Color.parseColor("#FFC23E");
    private final  static int COLOR_OTHER= Color.parseColor("#8FD85A");

    public XYMultipleSeriesRenderer getXYMultipleSeriesRenderer(){
        return mXYRenderer;
    }
    public XYMultipleSeriesDataset getmXYMultipleSeriesDataSet() {
        return mXYMultipleSeriesDataSet;
    }

    public void setmXYMultipleSeriesDataSet(XYMultipleSeriesDataset mXYMultipleSeriesDataSet) {
        this.mXYMultipleSeriesDataSet = mXYMultipleSeriesDataSet;
    }

    private XYMultipleSeriesDataset mXYMultipleSeriesDataSet;
    public SupportLineUtils(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void setRespectiveRender(XYMultipleSeriesRenderer render) {
        render.setPointSize(0f);
    }

    public View initLineGraphView() {

        final SupportSeriesRender lineSeriesRender = new SupportSeriesRender();
        lineSeriesRender.setClickPointColor(Color.parseColor("#8F77AA"));
        lineSeriesRender.setColorLevelValid(true);
//        lineSeriesRender.set
//        lineSeriesRender.setDisplayValues(true);
        ArrayList<SupportColorLevel> list = new ArrayList<SupportColorLevel>();

        //如果仅仅以target作为颜色分级，可以使用这个用法
//        SupportColorLevel supportColorLevel_a = new SupportColorLevel(0,mXYRenderer.getTargetValue(),COLOR_LOW_TARGET);
//        SupportColorLevel supportColorLevel_b = new SupportColorLevel(mXYRenderer.getTargetValue(),mXYRenderer.getTargetValue()*10,COLOR_UP_TARGET);

//         若有多个颜色等级可以使用这个用法
        SupportColorLevel supportColorLevel_a = new SupportColorLevel(0,12200,COLOR_LOW_TARGET);
        SupportColorLevel supportColorLevel_b = new SupportColorLevel(13000,20000,COLOR_UP_TARGET);
        SupportColorLevel supportColorLevel_c = new SupportColorLevel(20000,100000,Color.RED);


        list.add(supportColorLevel_a);
        list.add(supportColorLevel_b);
        list.add(supportColorLevel_c);
        lineSeriesRender.setColorLevelList(list);


        String[] hours = new String[20];
/*1        double[] allDataSets = new double[]{
                5,8,10,11,13,15,10,7,14,18,13,101, 5,8,10,11,15,10,7,14
        };*/
        XYSeries sysSeries = new XYSeries("");
/*1        for (int i = 0; i < allDataSets.length; i++) {
            sysSeries.add(i, allDataSets[i]);
            mXYRenderer.addXTextLabel(i, ""+i);
        }*/

        mXYRenderer.setXAxisMin(350);
        mXYRenderer.setXAxisMax(750);
       /* mLineUtils.getXYMultipleSeriesRenderer().setXTitle("波长/nm");
        mLineUtils.getXYMultipleSeriesRenderer().setYTitle("相对强度");
        mLineUtils.getXYMultipleSeriesRenderer().setChartTitle("波长-强度曲线");
        mLineUtils.getXYMultipleSeriesRenderer().setChartTitleTextSize(40);*/
        mXYRenderer.setXLabels(20);
        mXYRenderer.setYLabels(40);
        mXYRenderer.setXTitle("波长/nm");
        mXYRenderer.setYTitle("相对强度");
        mXYRenderer.setChartTitle("");
        mXYRenderer.setYAxisMin(5000);
        mXYRenderer.setYAxisMax(100000);

//        if(mXYRenderer.getSeriesRendererCount() == 0){
//
//            Log.e(TAG, "initLineGraphView:111111111111111 " );
//        }
            mXYRenderer.addSupportRenderer(lineSeriesRender);


//1        mXYMultipleSeriesDataSet.addSeries(sysSeries);
        //如果不许要颜色分级功能，则直接用原始的lineChart既可

        Log.d("debug","dataset count"+mXYMultipleSeriesDataSet.getSeriesCount()+"renderercount"+mXYRenderer.getSeriesRendererCount());
        View view =  ChartFactory.getSupportLineChartView(mContext, mXYMultipleSeriesDataSet, mXYRenderer);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " );
                GraphicalView graphicalView = (GraphicalView) v;
//                ((GraphicalView) v).getCurrentPoint(1)
                graphicalView.handPointClickEvent(lineSeriesRender,"SupportLine");
            }
        });
        return  view;
    }
    @Override
    protected XYSeriesRenderer getSimpleSeriesRender(int color) {
/*1        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(color);
        renderer.setFillPoints(true);   //是否是实心的点
        renderer.setDisplayChartValues(false);  // 设置是否在点上显示数据
        renderer.setLineWidth(5f);    //设置曲线的宽度
        renderer.setPointStyle(PointStyle.CIRCLE_POINT);
        renderer.setInnerCircleColor(Color.parseColor("#CC9B61"));*/
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(color);
        renderer.setFillPoints(true);   //是否是实心的点
        renderer.setDisplayChartValues(false);  // 设置是否在点上显示数据
        renderer.setLineWidth(4f);    //设置曲线的宽度
        renderer.setPointStrokeWidth(4f);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setChartValuesTextSize(14f);
        return renderer;

//        renderer.setChartValuesTextSize(14f);
    }


}
