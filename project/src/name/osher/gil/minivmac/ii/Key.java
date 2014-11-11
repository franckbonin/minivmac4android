package name.osher.gil.minivmac.ii;

import name.osher.gil.minivmac.ii.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Key extends Button implements View.OnTouchListener {

	private String mText;
	private int mScanCode;
	private int mDownImage;
	private int mUpImage;
	private int mHoldImage;
	private boolean mIsStickyDown = false;
	
	public Key(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.Key);
		//mText = a.getString(R.styleable.Key_text);
		mScanCode = a.getInteger(R.styleable.Key_scanCode, -1);
		mDownImage = a.getResourceId(R.styleable.Key_downImage, R.drawable.kb_key_down);
		mUpImage = a.getResourceId(R.styleable.Key_upImage, R.drawable.kb_key_up);
		mHoldImage = a.getResourceId(R.styleable.Key_holdImage, -1);
		a.recycle();
		
		//this.setText(mText);
		this.setTextColor(Color.BLACK);
		this.setBackgroundResource(mUpImage);
		this.setOnTouchListener(this);
		this.setPadding(1, 1, 1, 1);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mScanCode == -1) return false;
		
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			this.setBackgroundResource(mDownImage);

			if(isStickyKey()) {
				if (!mIsStickyDown) {
					Core.setKeyDown(mScanCode);
					mIsStickyDown = true;
				} else {
					mIsStickyDown = false;
				}
			} else {
				Core.setKeyDown(mScanCode);
			}
			
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if(mIsStickyDown) {
				if (-1 != mHoldImage)
				{
					this.setBackgroundResource(mHoldImage);
				}
			} else {
				this.setBackgroundResource(mUpImage);
				Core.setKeyUp(mScanCode);
			}
			return true;
		}
		
		return false;
	}
	
	protected boolean isStickyKey() {
		return (mScanCode == 55 || mScanCode == 56 || mScanCode == 58 || mScanCode == 59); // Command, Options, Shift, Control
	}

}
