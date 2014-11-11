package name.osher.gil.minivmac.ii;

import name.osher.gil.minivmac.ii.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class Keyboard extends LinearLayout {

	private Button mToggleButton;
	private Button mHideButton;
	private Boolean mIsAlt;
	
	class Keyboard_closer implements View.OnClickListener {
		private Keyboard mKeyboardLayout;
		public Keyboard_closer(Keyboard parent)
		{
			mKeyboardLayout = parent;
		}
		
		@Override
		public void onClick(View v)
		{
			View kbd = findViewById(R.id.keyboard);
			if (kbd.getVisibility() == View.VISIBLE) {
				kbd.setVisibility(View.INVISIBLE);
			} else {
				kbd.setVisibility(View.VISIBLE);
			}
		}
	}
	Keyboard_closer mCloser;
	
	
	class Keyboard_toggler implements View.OnClickListener {
		private Keyboard mKeyboardLayout;
		public Keyboard_toggler(Keyboard parent)
		{
			mKeyboardLayout = parent;
		}
		
		@Override
		public void onClick(View v)
		{
			Core.clearAllKey();
			mKeyboardLayout.removeAllViews();
			
			if (mKeyboardLayout.mIsAlt) {
				LayoutInflater.from(mKeyboardLayout.getContext()).inflate(R.layout.keyboard, mKeyboardLayout, true);
				mKeyboardLayout.mIsAlt = false;
			} else {
				LayoutInflater.from(mKeyboardLayout.getContext()).inflate(R.layout.keyboard_alt, mKeyboardLayout, true);
				mKeyboardLayout.mIsAlt = true;
			}
			mKeyboardLayout.mToggleButton = (Button) findViewById(R.id.btnToggle);
			mKeyboardLayout.mToggleButton.setOnClickListener(mKeyboardLayout.mToggler); //this
			
			mHideButton = (Button) findViewById(R.id.btnHide);
			mHideButton.setOnClickListener( mKeyboardLayout.mCloser );
		}
	}
	Keyboard_toggler mToggler;
	
	public Keyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        setWeightSum(1.0f);
        
        mIsAlt = false;
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true);
        
        mToggler = new Keyboard_toggler(this);
		mToggleButton = (Button) findViewById(R.id.btnToggle);
		mToggleButton.setOnClickListener(mToggler);
		
        mCloser = new Keyboard_closer(this);
		mHideButton = (Button) findViewById(R.id.btnHide);
		mHideButton.setOnClickListener(mCloser);
        
	}
}
