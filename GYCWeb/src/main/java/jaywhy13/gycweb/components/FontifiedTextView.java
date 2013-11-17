package jaywhy13.gycweb.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import jaywhy13.gycweb.GYCMainActivity;
import jaywhy13.gycweb.R;

/**
 * Created by Jay on 10/28/13.
 */
public class FontifiedTextView extends TextView {

    public FontifiedTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context, attrs);
    }

    protected void init(Context context, AttributeSet attrs){
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FontifiedTextView, 0, 0);
        String defaultFontFamily = context.getString(R.string.default_font);
        String fontFamily = ta.getString(R.styleable.FontifiedTextView_android_fontFamily);
        if(fontFamily == null){
            fontFamily = defaultFontFamily;
        }

        if(fontFamily != null && !isInEditMode()){
            try {
            Typeface typeFace = Typeface.createFromAsset(getContext().getAssets(), fontFamily);
            setTypeface(typeFace);
            } catch(Exception e){
                Log.d(GYCMainActivity.TAG, "Failed to load font: " + fontFamily + ". " + e.getMessage());
            }
        }
        ta.recycle();
    }
}