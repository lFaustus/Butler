package com.fluxinatedhotmail.butler.common.customviews;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.Button;

public class PlayFontButton extends Button
{
    static Typeface mTypeface;

    public PlayFontButton(Context context)
    {
        super(context);
        init(context);
    }

    public PlayFontButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public PlayFontButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PlayFontButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    void init(Context context)
    {
        if(mTypeface == null)
            mTypeface = Typeface.createFromAsset(context.getAssets(), "Play-Regular.ttf");
        setTypeface(mTypeface);
    }
}