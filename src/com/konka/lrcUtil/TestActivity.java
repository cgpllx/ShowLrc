package com.konka.lrcUtil;

import java.util.ArrayList;

import com.konka.showlrc.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TestActivity extends Activity implements OnClickListener {

	private Button parserBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		parserBtn = (Button) findViewById(R.id.parserbtn);
		parserBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.parserbtn) {
			Log.d("Ouyang", "点击了解析按钮");
			new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					String title = "后会无期";
					String singer = "邓紫棋";
					ArrayList<String> urlList=LrcUtil. searchLrcUrls(title,singer);
					if(urlList!=null&&urlList.size()>0)
						LrcUtil.saveLrc(urlList.get(0),title);
				}
			}).start();
		}
	}
}
