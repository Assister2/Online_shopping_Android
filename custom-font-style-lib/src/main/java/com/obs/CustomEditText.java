package com.obs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatEditText;
import android.util.AttributeSet;

/**
 * CustomTextView TextView widget with a typeface done directly using style.
 */
public class CustomEditText extends AppCompatEditText {

    public CustomEditText(Context context, AttributeSet attrs) {

        super(context, attrs);
         {

            FontCustomTextViewHelper.initialize(this, context, attrs);
        }
    }

    @Override
    public void setError(CharSequence error, Drawable icon) {
        setCompoundDrawables(null, null, icon, null);
    }

}
