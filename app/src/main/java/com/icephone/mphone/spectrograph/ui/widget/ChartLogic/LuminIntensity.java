package com.icephone.mphone.spectrograph.ui.widget.ChartLogic;

import com.icephone.mphone.spectrograph.model.RGB;

public class LuminIntensity {
	static volatile int  count = 0;
	boolean flag;
	int width;
	static volatile double []result = new double[640];
	static volatile double []oldResult = new double[640];
	public double []waveLength;// ����
	
	int height;
	
	double a = 390.6;
	double b = 0.515319;
	double c = -0.000019;
	
	public int maxOf3(int red, int green, int blue) {
		int maxOf2 = red > green ? red : green;
		return maxOf2 > blue ? maxOf2 : blue;
	}
	
	public double[] getAveRGB(RGB rgbs[][]) {
		
		double []tmpresult = new double[width];
		int sum;
		for (int i = 0; i < width; i++)
		{
			sum = 0;
			for (int j = 0; j < height; j++)
			{
				sum += maxOf3(rgbs[j][i].red, rgbs[j][i].green, rgbs[j][i].blue);
			}
			tmpresult[i] = sum;
		}
		return tmpresult;
	}
	

	public double[] getP(RGB [][]rgbs) {
		count ++;
	/*	Logger.e("count" + count);
		Logger.e("rgbs.r" + rgbs[400][400].red);
		Logger.e("rgbs.g" + rgbs[400][400].green);
		Logger.e("rgbs.b" + rgbs[400][400].blue);*/

		double []currentResult = getAveRGB(rgbs);
		if (!flag) {
			for (int i = 0; i < width; i++) {
				oldResult[i] = currentResult[i];
				flag = true;
			}
		}

		if (count % 5 == 0) {
			count=0;
			for (int i = 0; i < width; i++) {
				oldResult[i] = result[i];
//				Logger.e("beforeold" + oldResult[100]);
				result[i] = 0;
				oldResult[i] += currentResult[i];
				oldResult[i] /= 5.0;
//				Logger.e("oldResult" + oldResult[100]);
			}
		} else {
			for (int i = 0; i < width; i++) {
//				Logger.e("beforewhatsthis" + result[i]);

				result[i] += currentResult[i];
//				Logger.e("whatsthis" + result[i] + "current" +currentResult[i]);
			}
		}
//		Log.d("123aas", "result[300]"+oldResult[300] +"  "+ count+"  "+currentResult[300]);

		return this.oldResult;
	}
	
	void calWaveLength() {
		this.waveLength = new double[width];
		for( int i = 0; i < width; i++) {
			waveLength[i] = a + b * i + c * i * i;
		}
	}
	
	public LuminIntensity(int width, int height) {
		flag =false;
		this.width = width;
		this.height = height;
		this.oldResult = new double[width];
		this.calWaveLength();
	}
}

