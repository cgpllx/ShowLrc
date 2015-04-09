package com.konka.lrc;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.konka.lrcUtil.LrcUtil;
import com.konka.showlrc.R;

public class LrcActivity extends Activity implements ISetLrcProgress {
	private String TAG = "LrcActivity";
	private LyricView lrcView;
	private List<LyricObject> lrcList = null;
	int mPosX = 0;
	int mPosY = 0;
	int mCurrentPosX = 0;
	int mCurrentPosY = 0;
	int currentProgress = 1000;

	@SuppressLint("ResourceAsColor")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.d(TAG, "------onCreate------");
		setContentView(R.layout.activity_main);
		lrcView = (LyricView) findViewById(R.id.lrcview);
		lrcView.setLrcInterface(this);
		lrcView.setBackgroundResource(R.drawable.dzq);
		lrcView.setTextColor(R.color.black);
		LrcUtil.showLrc(lrcView,"海阔天空","");
		// String path = "/mnt/sdcard/konka/thumb/" + "a.lrc";
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void setLrcProgress(int progress) {
		// TODO Auto-generated method stub
		Log.d("Ouyang", "Activity中获取:" + progress);
	}
}
