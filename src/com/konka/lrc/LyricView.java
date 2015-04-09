package com.konka.lrc;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import com.konka.lrcUtil.DisplayUtil;
import com.konka.showlrc.R;

public class LyricView extends View {
	private final static String TAG = LyricView.class.getSimpleName();
	private Paint paint = new Paint();// 画笔，用于画不是高亮的歌词
	private Paint paintHL = new Paint(); // 画笔，用于画高亮的歌词，即当前唱到这句歌词

	private float mInterval = 0;
	private float mTextSize = 0;// 显示歌词文字的大小值
	private float mHightLightTextSize = 0;// 显示歌词文字的大小值
	private int mTextColor;
	private int mHightLightColor;
	
	private int playIndex = 0;
	private  int flagIndex = 0;
	private boolean isSliding = false;
	private boolean isFirst = false;//判断是否是否为第一次加载

	int mLastY = 0;
	int mCurrentY = 0;
	int currentProgress = 0;

	public ISetLrcProgress iSetProgress;
	private Context context=null;

	public LyricView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context=context;
		init(attrs);
	}

	public LyricView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		init(attrs);
	}

	public LyricView(Context context) {
		super(context);
		this.context=context;
//		init();
	}

	public void init(AttributeSet attrs) {
//		Log.d("Ouyang","##LyricView init()");
		isFirst=true;
	    int dip=DisplayUtil.dip2px(this.context,1);
	    float scaleN=1;
	    float height= context.getResources().getDisplayMetrics().heightPixels;
//	    float height2 =context.getResources().getDisplayMetrics().ydpi;
//	    float density= context.getResources().getDisplayMetrics().density;
        if(height>854)
        {
        	scaleN=(height/854);
//        	scaleN= scaleN*scaleN/density;
        }
        
//        Log.d("Ouyang","分辨率高度："+height);
//        Log.d("Ouyang","屏幕高度："+height2);
//        Log.d("Ouyang","density："+density);
//        Log.d("Ouyang","倍数："+scaleN);
		TypedArray ta = getContext().obtainStyledAttributes(attrs,R.styleable.LyricView);
//		mTextSize=ta.getDimension(R.styleable.LyricView_textSize, 12*dip*scaleN);
//		mHightLightTextSize=ta.getDimension(R.styleable.LyricView_hightLightTextSize, 14*dip*scaleN);
//		mInterval=ta.getDimension(R.styleable.LyricView_textInvernal, 18*dip*scaleN);
		mTextSize=ta.getDimension(R.styleable.LyricView_textSize, 22*scaleN);
		mHightLightTextSize=ta.getDimension(R.styleable.LyricView_hightLightTextSize, 26*scaleN);
		mInterval=ta.getDimension(R.styleable.LyricView_textInvernal, 40*scaleN);
		mTextColor=ta.getColor(R.styleable.LyricView_textColor, getResources().getColor(R.color.white));
		mHightLightColor=ta.getColor(R.styleable.LyricView_highLightColor, getResources().getColor(R.color.yellow));
		
		paint = new Paint();
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(mTextColor);
		paint.setTextSize(mTextSize);
		paint.setAntiAlias(true);
		paint.setDither(true);  
		paint.setAlpha(180);

		paintHL = new Paint();
		paintHL.setTextAlign(Paint.Align.CENTER);

		paintHL.setColor(mHightLightColor);
		paintHL.setTextSize(mHightLightTextSize);
		paintHL.setAntiAlias(true);
		paintHL.setAlpha(255);

	}

	/**
	 * 将歌词的时间传入这里就可以实现歌词滚动了
	 * 
	 * @param currentTimeMillis
	 */
	public void setCurPosition(int currentTimeMillis) {
		if (!ArrayUtils.isEmpty(lyricObjects)) {
			if (playIndex >= lyricObjects.size())
				return;
			if (isSliding == true)
				return;
			// for (int i = playIndex; i < lyricObjects.size(); i++) {
			mCurrentY = 0;
			// currentProgress=currentTimeMillis;
			for (int i = 0; i < lyricObjects.size(); i++) {
				if (currentTimeMillis >= lyricObjects.get(i).getBegintime()) {
					if (i + 1 < lyricObjects.size()) {
						if (currentTimeMillis <= lyricObjects.get(i + 1)
								.getBegintime()) {
							playIndex = i;
							postInvalidate();
							return;
						}
					} else {
						playIndex = i;
						postInvalidate();
						return;
					}
				}
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (!ArrayUtils.isEmpty(lyricObjects)) {

			if (playIndex == lyricObjects.size() - 1 && mCurrentY < 0)
				mCurrentY = 0;
			if (playIndex == 0 && mCurrentY > 0)
				mCurrentY = 0;

			for (int i = 0; i < lyricObjects.size(); i++) {
				float heigth = mInterval * (i - playIndex)
						+ (this.getHeight() >> 1) + mCurrentY;
				if (i == 0 && heigth > (this.getHeight() >> 1))// 歌词超过上界
					playIndex = 0;
				if (i == lyricObjects.size() - 1
						&& heigth < (this.getHeight() >> 1))// 歌词超过下界
					playIndex = lyricObjects.size() - 1;
				if (Math.abs(mInterval * (i - playIndex) + mCurrentY) < (mInterval/2))// 表示到达中间
				{
					canvas.drawText(lyricObjects.get(i).getLrc(),
							this.getWidth() >> 1, mInterval * (i - playIndex)
									+ (this.getHeight() >> 1) + mCurrentY,
							paintHL);
					flagIndex = i;// 用以记录到达中间的index
//					Log.d("Ouyang", "#flagIndex:" + flagIndex);
				} else {
					canvas.drawText(lyricObjects.get(i).getLrc(),
							this.getWidth() >> 1, mInterval * (i - playIndex) 
									+ (this.getHeight() >> 1) + mCurrentY,
							paint);
				}

				// canvas.drawText(
				// lyricObjects.get(i).getLrc(),
				// this.getWidth() >> 1, //
				// INTERVAL * (i - playIndex) + (this.getHeight() >>
				// 1)+mCurrentY,
				// i == playIndex ? paintHL : paint);
			}
		} else {
			canvas.drawText("没有找到歌词", this.getWidth() >> 1,
					this.getHeight() >> 1, paint);
		}
	}

	public void setLyricObjects(List<LyricObject> lyricObjects) {
		this.lyricObjects = lyricObjects;
		if(isFirst==true)
		playIndex = 0;
		isFirst=false;
		postInvalidate();
	}

	public List<LyricObject> getLyricObjects() {
		return lyricObjects;
	}

	private List<LyricObject> lyricObjects = null;

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (MotionEvent.ACTION_MOVE == event.getAction()) {
			mCurrentY = (int) event.getY() - mLastY;
//			Log.d("Ouyang", "当前playIndex" + playIndex);
			// mLastY = (int) event.getY();
			postInvalidate();
		}

		if (MotionEvent.ACTION_UP == event.getAction()) {
			if (iSetProgress != null&&lyricObjects!=null) {
				isSliding = false;
				int setCurrentProgress = lyricObjects.get(flagIndex)
						.getBegintime()+1;
				iSetProgress.setLrcProgress(setCurrentProgress);
			}
		}

		if (MotionEvent.ACTION_DOWN == event.getAction()) {
//			Log.d("Ouyang", "MotionEvent.ACTION_DOWN");
			playIndex = flagIndex;
			mLastY = (int) event.getY();
			isSliding = true;
		}
		return true;
	}

	public void setLrcInterface(ISetLrcProgress iSetProgress) {
		this.iSetProgress = iSetProgress;
	}
	
	/***
	 * 设置歌词文字大小
	 * 单位：px
	 * @param size
	 */
	public void setTextSize(float size)
	{
		mTextSize = size;
		paint.setTextSize(mTextSize);
	}
	
	/***
	 * 设置高亮歌词文字大小
	 * 单位：px
	 * @param size
	 */
	public void setHightLightTextSize(float size)
	{
		mHightLightTextSize = size;
		paintHL.setTextSize(mHightLightTextSize);
	}
	
	
	/***
	 * 设置字体颜色
	 * @param color
	 */
	public void setTextColor(int color)
	{
		mTextColor=color;
		paint.setColor(mTextColor);
	}
	
	
	/***
	 * 设置高亮字体颜色
	 * @param color
	 */
	public void setHightLightColor(int color)
	{
		mHightLightColor=color;
		paintHL.setColor(mHightLightColor);
	}
	
	/***
	 * 设置歌词之间的间距
	 * @param interval
	 */
	public void setInterval(float interval)
	{
		mInterval = interval;	
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}
	
}
