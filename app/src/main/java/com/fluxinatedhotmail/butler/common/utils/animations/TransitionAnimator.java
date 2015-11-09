package com.fluxinatedhotmail.butler.common.utils.animations;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;

import java.util.ArrayList;

/**
 * Created by Fluxi on 9/4/2015.
 */
public class TransitionAnimator
{
    View mView;
    ObjectAnimator mObjectAnimator;
    private static final  int DEFAULT_ANIMATION_DURATION = 400;
    private static final int DEFAULT_ALPHA = 1;
    private static final int DEFAULT_DELAY = 1;
    private int mAnimationDuration = DEFAULT_ANIMATION_DURATION;
    private int mDelay = DEFAULT_DELAY;
    private Interpolator mInterpolator;
    private ArrayList<PropertyValuesHolder> mPropertyValuesHolder = new ArrayList<>();
    private Animator.AnimatorListener mAnimationListener;

    public TransitionAnimator()
    {

    }

    public TransitionAnimator setViewToBeAnimated(View v)
    {
        this.mView = v;
        return this;
    }

    public TransitionAnimator setScaleX(int value)
    {
        this.setProperties(View.SCALE_X,value);
        return this;
    }

    public TransitionAnimator setScaleY(int value)
    {
        this.setProperties(View.SCALE_Y,value);
        return this;
    }

    public TransitionAnimator setTranslationX(int value)
    {
        this.setProperties(View.TRANSLATION_X,value);
        return this;
    }

    public TransitionAnimator setTranslationY(int value)
    {
        this.setProperties(View.TRANSLATION_Y,value);
        return this;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TransitionAnimator setTranslationZ(int value)
    {
        this.setProperties(View.TRANSLATION_Z,value);
        return this;
    }

    public TransitionAnimator setRotationX(int value)
    {
        this.setProperties(View.ROTATION_X,value);
        return this;
    }

    public TransitionAnimator setRotationY(int value)
    {
        this.setProperties(View.ROTATION_Y,value);
        return this;
    }


    public TransitionAnimator setAlpha(int value)
    {
        this.setProperties(View.ALPHA, value);
        return this;
    }

    private void setProperties(Property<View,Float> objectProperty,int value)
    {
        this.mPropertyValuesHolder.add(PropertyValuesHolder.ofFloat(objectProperty, value));
    }

    public TransitionAnimator setStartDelay(int delay)
    {
        this.mDelay = delay;
        return this;
    }

    public TransitionAnimator setInterpolator(Interpolator interpolator)
    {
        this.mInterpolator = interpolator;
        return this;
    }


    public TransitionAnimator setAnimationDuration(int duration)
    {
        this.mAnimationDuration = duration;
        return this;
    }

    public TransitionAnimator setAnimationListener(Animator.AnimatorListener animatorListener)
    {
        this.mAnimationListener = animatorListener;
        return this;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public TransitionAnimator setAnimationPauseListener(Animator.AnimatorPauseListener animatorPauseListener)
    {
        this.mObjectAnimator.addPauseListener(animatorPauseListener);
        return this;
    }

    public TransitionAnimator setAnimationUpdateListener(ValueAnimator.AnimatorUpdateListener animatorUpdateListener)
    {
        this.mObjectAnimator.addUpdateListener(animatorUpdateListener);
        return this;
    }



    public TransitionAnimator animate()
    {
        this.mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(this.mView,  this.mPropertyValuesHolder.toArray(new PropertyValuesHolder[mPropertyValuesHolder.size()]));
        this.mObjectAnimator.setInterpolator(this.mInterpolator);
        this.mObjectAnimator.setDuration(this.mAnimationDuration);
        this.mObjectAnimator.setStartDelay(this.mDelay);
        if(this.mAnimationListener != null)
            this.mObjectAnimator.addListener(this.mAnimationListener);
        this.mObjectAnimator.start();
        return this;
    }


}
