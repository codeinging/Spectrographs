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
import org.achartengine.renderer.support.SupportSeriesRender;
import org.achartengine.renderer.support.SupportXAlign;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

/**
 * Created by wangjia on 28/06/14.
 */
public class SupportCubicLineUtils extends BaseSupportUtils {

    public static final int COLOR_START = Color.parseColor("#FFEA00");
    public static final int COLOR_CENTER = Color.parseColor("#FFAB44");
    public static final int COLOR_END = Color.parseColor("#FF6D80");
    private String TAG=SupportCubicLineUtils.class.getSimpleName();

    public SupportCubicLineUtils(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void setRespectiveRender(XYMultipleSeriesRenderer render) {
        render.setPointSize(0f);
        render.setSupportXAlign(SupportXAlign.RIGHT);
    }

    @Override
    protected XYSeriesRenderer getSimpleSeriesRender(int color) {
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setColor(color);
        renderer.setFillPoints(true);   //是否是实心的点
        renderer.setDisplayChartValues(false);  // 设置是否在点上显示数据
        renderer.setLineWidth(7f);    //设置曲线的宽度
        renderer.setPointStrokeWidth(4f);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setChartValuesTextSize(14f);
        return renderer;
    }

    public View initCubicLineGraphView() {
        mXYMultipleSeriesDataSet = new XYMultipleSeriesDataset();

        //扩展的属性
        SupportSeriesRender supportSeriesRender = new SupportSeriesRender();
        //设置曲线的颜色
        supportSeriesRender.setLineColor(Color.parseColor("#851A9F7A"));
        //设置曲线颜色为渐变色，默认的渐变色从上到下，3色渐变
        supportSeriesRender.setShapeLineColor(new int[]{COLOR_START,COLOR_CENTER,COLOR_END});
        supportSeriesRender.setZoomEnabled(true);
        supportSeriesRender.setPanEnabled(true);
        supportSeriesRender.setZoomRate(2);
        supportSeriesRender.setZoomButtonsVisible(true);
        String[] hours = new String[20];

        double[] allDataSets = new double[]{
                5,8,10,11,13,15,10,7,14,18,1131,10, 5,8,10,11,15,10,7,14
        };

        XYSeries sysSeries = new XYSeries("渐变曲线");
        for (int i = 0; i < allDataSets.length; i++) {
            sysSeries.add(i, allDataSets[i]);
            mXYRenderer.addXTextLabel(i, hours[i]);
        }
        mXYRenderer.addSupportRenderer(supportSeriesRender);
        mXYRenderer.setYAxisMax(500);
        mXYMultipleSeriesDataSet.addSeries(sysSeries);
        GraphicalView view= ChartFactory.getSupportCubeLineChartView(mContext, mXYMultipleSeriesDataSet, mXYRenderer, 0.2f);

        view.addZoomListener(new ZoomListener() {
            @Override
            public void zoomApplied(ZoomEvent e) {
                Log.e(TAG, "zoomApplied: " );
            }

            @Override
            public void zoomReset() {
                Log.e(TAG, "zoomReset: " );

            }
        },true,true);
        return view;
    }

}
