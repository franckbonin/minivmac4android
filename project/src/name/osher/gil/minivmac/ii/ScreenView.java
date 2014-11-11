package name.osher.gil.minivmac.ii;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import java.util.Timer;
import java.util.TimerTask;

public class ScreenView extends View {
	private Bitmap screenBits;
	private int screenWidth, screenHeight, screenDepth;
	private Paint screenPaint;
	private Rect srcRect, dstRect;
	private boolean scaled, scroll;
	
	private void init() {
		screenWidth = Core.screenWidth();
		screenHeight = Core.screenHeight();
		screenDepth = Core.screenDepth();
		screenBits = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
		screenPaint = new Paint();
		setScaled(true);
	}
	
	public ScreenView(Context context) {
		super(context);
		init();
	}

	public ScreenView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScreenView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(dstRect.width(), dstRect.height());
	}
	
	Rect lastupdaterect = new Rect();
	public void updateScreen(int[] update) {
		if (update.length < 4) return;
		int width = update[3]-update[1];
		int height = update[2]-update[0];
		screenBits.setPixels(update, 4, width, update[1], update[0], width, height);
		int[] viewCoordsLeftTop = toViewCoord(update[1]-2, update[0]-2);
		int[] viewCoordsRightBottom = toViewCoord(update[3]+2, update[2]+2);
		this.invalidate(viewCoordsLeftTop[0], viewCoordsLeftTop[1],
				        viewCoordsRightBottom[0], viewCoordsRightBottom[1]);
		lastupdaterect = new Rect(viewCoordsLeftTop[0], viewCoordsLeftTop[1],
				        viewCoordsRightBottom[0], viewCoordsRightBottom[1]);
		//invalidate();
	}
	
	protected void onDraw (Canvas canvas) {
		canvas.drawBitmap(screenBits, srcRect, dstRect, screenPaint);
		/*Paint p = new Paint();
		p.setStyle(Paint.Style.STROKE);
		canvas.drawRect(lastupdaterect, p);*/
	}
	private Point mDelta = new Point();
	private Point mFirstTouch = new Point();
	private boolean buttondown = false;
	private boolean leftbuttondown = false;
	public boolean onTouchEvent (MotionEvent event) {
		int[] curMove;
		curMove = toMacCoord((int)event.getX(), (int)event.getY());
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//Core.setMousePos(curMove[0], curMove[1]);
			//Core.setMouseButton(true);
			mFirstTouch.x = curMove[0];
			mFirstTouch.y = curMove[1];
			mDelta.x = curMove[0] - Core.getMouseX();
			mDelta.y = curMove[1] - Core.getMouseY();
			buttondown = false;
			leftbuttondown = false;
			Log.d("vMac", "ACTION_DOWN");
			//Log.d("vMac", event.toString());
			break;
		case MotionEvent.ACTION_MOVE:
			Core.setMousePos(curMove[0] - mDelta.x, curMove[1] - mDelta.y);
			if (!buttondown && event.getPointerCount() > 1)
			{
				buttondown = true;
				if (!leftbuttondown && event.getPointerCount() > 2)
				{
					leftbuttondown = true;
					Core.setKeyDown(0x3B); // Ctrl
				}
				Core.setMouseButton(true);
			}
			else if (buttondown)
			{
				if (leftbuttondown && event.getPointerCount() < 3)
				{
					leftbuttondown = false;
					Core.setKeyUp(0x3B); // Ctrl
				}
				if (event.getPointerCount() == 1)
				{
					buttondown = false;
					Core.setMouseButton(false);
				}
			}
			Log.d("vMac", "ACTION_MOVE");
			//Log.d("vMac", event.toString());
			break;
		case MotionEvent.ACTION_CANCEL:
			if (buttondown)
			{
				Core.setMouseButton(false);
				buttondown = false;
			}
			if (leftbuttondown)
			{
				Core.setKeyUp(0x3B); // Ctrl
				leftbuttondown = false;
			}
			Log.d("vMac", "ACTION_CANCEL");
			//Log.d("vMac", event.toString());
			break;
		case MotionEvent.ACTION_UP:
			Core.setMousePos(curMove[0] - mDelta.x, curMove[1] - mDelta.y);
			//Core.setMouseButton(false);
			Log.d("vMac", "check sim click : "+ (event.getEventTime()-event.getDownTime()) + " " + mFirstTouch.x + " " + curMove[0] + " " + mFirstTouch.y + " " + curMove[1]);
			if (!buttondown && event.getEventTime()-event.getDownTime() < 200 && 
					mFirstTouch.x == curMove[0] && mFirstTouch.y == curMove[1])
			{
				Log.d("vMac", "click sim");
				// try to simulate click
				Core.setMouseButton(true);
				Timer mTimer = new Timer();
			    mTimer.schedule(new TimerTask() {
				      @Override
				      public void run() {
				    	  Log.d("vMac", "click release sim");
				    	  Core.setMouseButton(false);
				      }
				    }, 100);
			}
			else
			{
				if (buttondown)
				{
					buttondown = false;
					Core.setMouseButton(false);
				}
				if (leftbuttondown)
				{
					Core.setKeyUp(0x3B); // Ctrl
					leftbuttondown = false;
				}
		    }
			Log.d("vMac", "ACTION_UP");
			//Log.d("vMac", event.toString());
			break;
		}
		return true;
	}

	private int[] toMacCoord(int x, int y) {
		int[] coords = new int[2];
		if (scaled) {
			coords[0] = (x * srcRect.width()) / dstRect.width() + srcRect.left;
			coords[1] = (y * srcRect.height()) / dstRect.height() + srcRect.top;
		} else {
			coords[0] = x + srcRect.left;
			coords[1] = y + srcRect.top;
		}
		return coords;
	}
	

	private int[] toViewCoord(int x, int y) {
		int[] coords = new int[2];
		if (scaled) {
			/*double dstRect_right = dstRect.width();
			double srcRect_right = srcRect.width();
			double srcRect_left = srcRect.left;
			double _x = x;
			double coord0 = ((_x - srcRect_left) * dstRect_right) / srcRect_right ;*/
			coords[0] = ((x - srcRect.left) * dstRect.width()) / srcRect.width();
			/*double dstRect_bottom = dstRect.height();
			double srcRect_bottom = srcRect.height();
			double srcRect_top = srcRect.top;
			double _y = y;
			double coord1 = ((_y - srcRect_top) * dstRect_bottom) / srcRect_bottom ;*/
			coords[1] = ((y - srcRect.top) * dstRect.height()) / srcRect.height();
		} else {
			coords[0] = x - srcRect.left;
			coords[1] = y - srcRect.top;
		}
		return coords;
	}

	public void setScaled(boolean scaled) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		
		int realHeight = (int) Math.ceil(dm.heightPixels * (dm.densityDpi / dm.ydpi));
		int realWidth = (int) Math.ceil(dm.widthPixels * (dm.densityDpi / dm.xdpi));

		/*Log.d("setScaled real Height = ", String.valueOf(realHeight));
		Log.d("setScaled real Width = ", String.valueOf(realWidth));
		Log.d("setScaled real densityDpi = ", String.valueOf(dm.densityDpi));
		Log.d("setScaled real xdpi = ", String.valueOf(dm.xdpi));
		Log.d("setScaled real ydpi = ", String.valueOf(dm.ydpi))*/
		
		if (dm.heightPixels > dm.widthPixels) {
			realHeight = dm.widthPixels;
			realWidth = dm.heightPixels;
		}
		
		Boolean perfectScale = false;
		if (((screenWidth * 2) <= realWidth) && ((screenHeight * 2) <= realHeight)) {
			perfectScale = true;
		} else if (scaled) {
			double aspectRatio = (double)screenWidth / screenHeight;
			realWidth = (int) (realHeight * aspectRatio);
		}
		
		this.scaled = scaled;
		screenPaint.setFilterBitmap(scaled);
		if (scaled) {
			if (perfectScale) {
				srcRect = new Rect(0, 0, realWidth/2, realHeight/2);
			} else {
				srcRect = new Rect(0, 0, screenWidth, screenHeight);
			}
			dstRect = new Rect(0, 0, realWidth, realHeight);
		} else {
			srcRect = new Rect(0, 0, realWidth, realHeight);
			dstRect = new Rect(0, 0, realWidth, realHeight);
		}
		invalidate();
	}

	/*public void setScaled(boolean scaled) {
		this.scaled = scaled;
		screenPaint.setFilterBitmap(scaled);
		
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		
		int hostScreenWidth = dm.widthPixels;
		int hostScreenHeight = dm.heightPixels;
		
		
		double perfectWidthFactor = Math.floor((double)hostScreenWidth / (double)targetScreenWidth);
		double perfectHeightFactor = Math.floor((double)hostScreenHeight / (double)targetScreenHeight);
		double scaleFactor = Math.min(perfectWidthFactor, perfectHeightFactor);
		if (scaleFactor < 1.0) scaleFactor = 1.0;
	
		if (scaled) {
			scaleFactor = Math.min( (double)hostScreenWidth/(double)targetScreenWidth, (double)hostScreenHeight/(double)targetScreenHeight);
		}
		
		int surfaceHeight = (int)(targetScreenHeight * scaleFactor);
		int surfaceWidth = (int)(targetScreenWidth * scaleFactor);
		
		int left = (hostScreenWidth - surfaceWidth)/2;
		int top = (hostScreenHeight - surfaceHeight)/2;
		if (left < 0) left = 0;
		if (top < 0) top = 0;
		dstRect = new Rect(left, top, left + surfaceWidth, top + surfaceHeight);
		srcRect = new Rect(0, 0, targetScreenWidth, targetScreenHeight);
		
		invalidate();
	}*/
	
	public boolean isScaled() {
		return scaled;
	}
	
	public void setScroll(boolean scroll) {
		this.scroll = scroll;
	}
	
	public boolean isScroll() {
		return scroll;
	}
	

	public void scrollScreen(int keyCode, int increment) {
		int top,left;
		if (scaled) return;
		top = srcRect.top;
		left = srcRect.left;
		switch(keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			left += increment;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			left -= increment;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			top -= increment;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			top += increment;
			break;
		}
		if (top < 0) top = 0;
		if (left < 0) left = 0;
		if (top + srcRect.height() > screenHeight) top = screenHeight - srcRect.height();
		if (left + srcRect.width() > screenWidth) left = screenWidth - srcRect.width();
		srcRect.offsetTo(left,top);
		invalidate();
	}

    public void scrollScreen(Point dt) {
		int top,left;
		//if (scaled) return;
		top = srcRect.top - dt.y;
		left = srcRect.left - dt.x;

		if (top >= srcRect.height()*0.5) top = (int) (srcRect.height()*0.5);
		if (top <= srcRect.height()*-0.5) top = (int) (srcRect.height()*-0.5);
		if (left >= srcRect.width()*0.5) left = (int) (srcRect.width()*0.5);
		if (left <= srcRect.width()*-0.5) left = (int) (srcRect.width()*-0.5);	
	
		srcRect.offsetTo(left,top);
		Point realdt = new Point(left,top);
		Log.d("screen realdt", realdt.toString());
		invalidate();
	}

	/*public void scrollScreen(int keyCode, int increment) {
		int top,left;
		if (!scroll) return;
		if (scaled) return;
		top = dstRect.top;
		left = dstRect.left;
		switch(keyCode) {
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			left += increment;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			left -= increment;
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			top -= increment;
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			top += increment;
			break;
		}
		
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		
		int hostScreenWidth = dm.widthPixels;
		int hostScreenHeight = dm.heightPixels;
		
		if (hostScreenHeight < targetScreenHeight) {
			if (top > 0) top = 0;
			if (top < (hostScreenHeight - dstRect.height()))
				top = hostScreenHeight - dstRect.height();
		}
		else
		{
			if (top < 0) top = 0;
			if (top + dstRect.height() > hostScreenHeight) top = hostScreenHeight - dstRect.height();
		}
		
		if (hostScreenWidth < targetScreenWidth) {
			if (left >0) left = 0;
			if (left < (hostScreenWidth - dstRect.width())) 
				left = hostScreenWidth - dstRect.width();
		}
		else
		{
			if (left < 0) left = 0;
			if (left + dstRect.width() > hostScreenWidth) left = hostScreenWidth - dstRect.width();
		}
		
		dstRect.offsetTo(left,top);
		invalidate();
	}*/
}
