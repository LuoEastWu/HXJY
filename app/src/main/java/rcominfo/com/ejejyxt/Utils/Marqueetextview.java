package rcominfo.com.ejejyxt.Utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by 王璐阳 on 2017/3/10.
 */
public class Marqueetextview extends TextView {
    public Marqueetextview(Context context) {
        super(context);
    }

    public Marqueetextview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Marqueetextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Marqueetextview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {

    }
}

