package kr.co.openit.bpdiary.customview;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.StyleableRes;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import kr.co.openit.bpdiary.R;

/**
 * Created by Riccardo on 31/08/16.
 */
@SuppressWarnings("ResourceType")
public abstract class CustomAbstractSwitch extends RelativeLayout
                                           implements Checkable, View.OnClickListener, View.OnLayoutChangeListener {

    protected String TAG = getClass().getSimpleName();

    protected static final String BUNDLE_KEY_SUPER_DATA = "bundle_key_super_data";

    protected static final String BUNDLE_KEY_ENABLED = "bundle_key_enabled";

    protected static final String BUNDLE_KEY_FORCE_ASPECT_RATIO = "bundle_key_force_aspect_ratio";

    protected static final String BUNDLE_KEY_DESIGN = "bundle_key_design";

    protected static final float ALPHA_DISABLED = 0.6f;

    protected static final float ALPHA_ENABLED = 1.0f;

    public static final int DESIGN_ANDROID = 0;

    /**
     * If force aspect ratio or keep the given proportion
     */
    protected boolean mForceAspectRatio;

    /**
     * If the view is enabled
     */
    protected boolean mIsEnabled;

    /**
     * The Toggle view, the only moving part of the switch
     */
    protected SquareImageView mImgToggle;

    /**
     * The background image of the switch
     */
    protected ImageView mImgBkg;

    /**
     * The switch design
     */

    protected int mSwitchDesign;

    /**
     * The switch container Layout
     */
    protected RelativeLayout mContainerLayout;

    protected static LayoutTransition sLayoutTransition;

    protected static final int ANIMATION_DURATION = 150;

    public CustomAbstractSwitch(Context context) {
        this(context, null);
    }

    public CustomAbstractSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("WrongConstant")
    public CustomAbstractSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                                                                          getTypedArrayResource(),
                                                                          defStyleAttr,
                                                                          0);

        // Check the switch style
        mSwitchDesign = typedArray.getInt(R.styleable.customSwitch_switchDesign, DESIGN_ANDROID);

        setupLayout();

        try {
            setupSwitchCustomAttributes(typedArray);
        } finally {
            typedArray.recycle();
        }

        // Used to calculate margins and padding after layout changes
        addOnLayoutChangeListener(this);

        // Set the OnClickListener
        setOnClickListener(this);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_KEY_SUPER_DATA, super.onSaveInstanceState());
        bundle.putBoolean(BUNDLE_KEY_ENABLED, mIsEnabled);
        bundle.putBoolean(BUNDLE_KEY_FORCE_ASPECT_RATIO, mForceAspectRatio);
        bundle.putInt(BUNDLE_KEY_DESIGN, mSwitchDesign);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle prevState = (Bundle)state;

        super.onRestoreInstanceState(prevState.getParcelable(BUNDLE_KEY_SUPER_DATA));

        mIsEnabled = prevState.getBoolean(BUNDLE_KEY_ENABLED, true);
        mForceAspectRatio = prevState.getBoolean(BUNDLE_KEY_FORCE_ASPECT_RATIO, true);
        mSwitchDesign = prevState.getInt(BUNDLE_KEY_DESIGN, DESIGN_ANDROID);
    }

    // Setters
    public void setEnabled(boolean enabled) {
        if (mIsEnabled != enabled) {
            mIsEnabled = enabled;
            setupSwitchAppearance();
        }
    }

    public void setForceAspectRatio(boolean forceAspectRatio) {
        if (forceAspectRatio != mForceAspectRatio) {
            mForceAspectRatio = forceAspectRatio;
            setupSwitchAppearance();
        }
    }

    public void setSwitchDesign(int switchDesign) {
        if (switchDesign != mSwitchDesign) {
            mSwitchDesign = switchDesign;
            setupLayout();
            setupSwitchAppearance();
        }

        // Used to calculate margins and padding after layout changes
        addOnLayoutChangeListener(this);
    }

    // Getters
    public boolean isForceAspectRatio() {
        return mForceAspectRatio;
    }

    public boolean isEnabled() {
        return mIsEnabled;
    }

    public int getSwitchDesign() {
        return mSwitchDesign;
    }

    // Get all the current views
    protected void setupLayout() {
        // Inflate the stock switch view
        removeAllViews();

        // Create the layout transition if not already created
        if (sLayoutTransition == null) {
            sLayoutTransition = new LayoutTransition();
            sLayoutTransition.setDuration(ANIMATION_DURATION);
            sLayoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            sLayoutTransition.setInterpolator(LayoutTransition.CHANGING, new FastOutLinearInInterpolator());
        }

        ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_switch,
                                                                                                 this,
                                                                                                 true);

        // Get the sub-viewsmToggleCheckedDrawable
        mImgToggle = (SquareImageView)findViewById(R.id.rm_switch_view_toggle);
        mImgBkg = (ImageView)findViewById(R.id.rm_switch_view_bkg);
        mContainerLayout = (RelativeLayout)findViewById(R.id.rm_switch_view_container);

        // Activate AnimateLayoutChanges in both the container and the root layout
        setLayoutTransition(sLayoutTransition);
        mContainerLayout.setLayoutTransition(sLayoutTransition);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // If set to wrap content, apply standard dimensions
        if (widthMode != MeasureSpec.EXACTLY) {
            int standardWith = getSwitchStandardWidth();

            // If unspecified or wrap_content where there's more space than the standard,
            // set the standard dimensions
            if ((widthMode == MeasureSpec.UNSPECIFIED)
                || (widthMode == MeasureSpec.AT_MOST && standardWith < MeasureSpec.getSize(widthMeasureSpec)))
                widthMeasureSpec = MeasureSpec.makeMeasureSpec(standardWith, MeasureSpec.EXACTLY);
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            int standardHeight = getSwitchStandardHeight();

            // If unspecified or wrap_content where there's more space than the standard,
            // set the standard dimensions
            if ((heightMode == MeasureSpec.UNSPECIFIED)
                || (heightMode == MeasureSpec.AT_MOST && standardHeight < MeasureSpec.getSize(heightMeasureSpec)))
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(standardHeight, MeasureSpec.EXACTLY);
        }

        // Fix the dimension depending on the aspect ratio forced or not
        if (mForceAspectRatio) {

            // Set the height depending on the width
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                                                            (int)(MeasureSpec.getSize(widthMeasureSpec)
                                                                  / getSwitchAspectRatio()),
                                                            MeasureSpec.getMode(heightMeasureSpec));
        } else {

            // Check that the width is greater than the height, if not, resize and make a square
            if (MeasureSpec.getSize(widthMeasureSpec) < MeasureSpec.getSize(heightMeasureSpec))
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),
                                                                MeasureSpec.getMode(heightMeasureSpec));
        }

        setBkgMargins(heightMeasureSpec, widthMeasureSpec);

        setToggleMargins(heightMeasureSpec);

        //        setToggleImagePadding();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void setBkgMargins(int heightMeasureSpec, int widthMeasureSpec) {
        // If slim design add some margin to the background line, else remove them
        int calculatedBackgroundSideMargin = 0;
        int calculatedBackgroundTopBottomMargin = 0;
        calculatedBackgroundSideMargin = MeasureSpec.getSize(widthMeasureSpec) / 6;
        calculatedBackgroundTopBottomMargin = MeasureSpec.getSize(heightMeasureSpec) / 6;
        ((LayoutParams)mImgBkg.getLayoutParams()).setMargins(calculatedBackgroundSideMargin,
                                                             calculatedBackgroundTopBottomMargin,
                                                             calculatedBackgroundSideMargin,
                                                             calculatedBackgroundTopBottomMargin);
    }

    private void setToggleMargins(int heightMeasureSpec) {
        // Set the margin after all measures have been done
        int calculatedToggleMargin;
        calculatedToggleMargin = 0;
        ((LayoutParams)mImgToggle.getLayoutParams()).setMargins(calculatedToggleMargin,
                                                                calculatedToggleMargin,
                                                                calculatedToggleMargin,
                                                                calculatedToggleMargin);
    }

    private void setToggleImagePadding() {
        // Set the padding of the image
        int padding;
        padding = mImgToggle.getHeight() / 5;

        mImgToggle.setPadding(padding, padding, padding, padding);
    }

    // Layout rules management
    protected void removeRules(LayoutParams toggleParams, int[] rules) {
        for (int rule : rules) {
            removeRule(toggleParams, rule);
        }
    }

    protected void removeRule(LayoutParams toggleParams, int rule) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            // RelativeLayout.LayoutParams.removeRule require api >= 17
            toggleParams.removeRule(rule);

        } else {

            // If API < 17 manually set the previously active rule with anchor 0 to remove it
            toggleParams.addRule(rule, 0);
        }
    }

    protected void setSwitchAlpha() {
        setAlpha(mIsEnabled ? ALPHA_ENABLED : ALPHA_DISABLED);
    }

    protected void setupSwitchAppearance() {
        // Create the background drawables
        Drawable bkgDrawable = getSwitchCurrentBkgDrawable();

        // Create the toggle background drawables
        Drawable toggleBkgDrawable = getSwitchCurrentToggleBkgDrawable();

        // Set the background drawable
        if (mImgBkg.getDrawable() != null) {
            // Create the transition for the background
            TransitionDrawable bkgTransitionDrawable =
                                                     new TransitionDrawable(new Drawable[] {
                                                                                            // If it was a transition drawable, take the last one of it's drawables
                                                                                            mImgBkg.getDrawable() instanceof TransitionDrawable ? ((TransitionDrawable)mImgBkg.getDrawable()).getDrawable(1)
                                                                                                                                                : mImgBkg.getDrawable(),
                                                                                            bkgDrawable});
            bkgTransitionDrawable.setCrossFadeEnabled(true);
            // Set the transitionDrawable and start the animation
            mImgBkg.setImageDrawable(bkgTransitionDrawable);
            bkgTransitionDrawable.startTransition(ANIMATION_DURATION);
        } else {
            // No previous background image, just set the new one
            mImgBkg.setImageDrawable(bkgDrawable);
        }
        mImgToggle.setImageDrawable(toggleBkgDrawable);

        // Set the toggle background
        //        if (mImgToggle.getBackground() != null) {
        //            // Create the transition for the background of the toggle
        //            TransitionDrawable toggleBkgTransitionDrawable =
        //                                                           new TransitionDrawable(new Drawable[] {
        //                                                                                                  // If it was a transition drawable, take the last one of it's drawables
        //                                                                                                  mImgToggle.getBackground() instanceof TransitionDrawable ? ((TransitionDrawable)mImgToggle.getBackground()).getDrawable(1)
        //                                                                                                                                                           : mImgToggle.getBackground(),
        //                                                                                                  toggleBkgDrawable});
        //            toggleBkgTransitionDrawable.setCrossFadeEnabled(true);
        //            // Set the transitionDrawable and start the animation
        //            mImgToggle.setBackground(toggleBkgTransitionDrawable);
        //            toggleBkgTransitionDrawable.startTransition(ANIMATION_DURATION);
        //        } else {
        //            // No previous background image, just set the new one
        //            mImgToggle.setImageDrawable(toggleBkgDrawable);
        //        }

        setSwitchAlpha();
    }

    protected void setSwitchInit() {
        // Create the background drawables
        Drawable bkgDrawable = getSwitchCurrentBkgDrawable();

        // Create the toggle background drawables
        Drawable toggleBkgDrawable = getSwitchCurrentToggleBkgDrawable();

        // Set the background drawable

        mImgBkg.setImageDrawable(bkgDrawable);

        mImgToggle.setImageDrawable(toggleBkgDrawable);

        // Set the toggle background
        //        if (mImgToggle.getBackground() != null) {
        //            // Create the transition for the background of the toggle
        //            TransitionDrawable toggleBkgTransitionDrawable =
        //                                                           new TransitionDrawable(new Drawable[] {
        //                                                                                                  // If it was a transition drawable, take the last one of it's drawables
        //                                                                                                  mImgToggle.getBackground() instanceof TransitionDrawable ? ((TransitionDrawable)mImgToggle.getBackground()).getDrawable(1)
        //                                                                                                                                                           : mImgToggle.getBackground(),
        //                                                                                                  toggleBkgDrawable});
        //            toggleBkgTransitionDrawable.setCrossFadeEnabled(true);
        //            // Set the transitionDrawable and start the animation
        //            mImgToggle.setBackground(toggleBkgTransitionDrawable);
        //            toggleBkgTransitionDrawable.startTransition(ANIMATION_DURATION);
        //        } else {
        //            // No previous background image, just set the new one
        //            mImgToggle.setImageDrawable(toggleBkgDrawable);
        //        }

        setSwitchAlpha();
        changeToggleGravity();
    }

    @Override
    public void toggle() {
    }

    @Override
    public void setChecked(boolean b) {
    }

    @Override
    public boolean isChecked() {
        return false;
    }

    // OnClick action
    @Override
    public void onClick(View view) {
        if (mIsEnabled)
            toggle();
    }

    public abstract float getSwitchAspectRatio();

    public abstract int getSwitchStandardWidth();

    public abstract int getSwitchStandardHeight();

    public abstract Drawable getSwitchCurrentToggleDrawable();

    public abstract Drawable getSwitchCurrentToggleBkgDrawable();

    public abstract Drawable getSwitchCurrentBkgDrawable();

    @StyleableRes
    public abstract int[] getTypedArrayResource();

    protected abstract void changeToggleGravity();

    protected abstract void setupSwitchCustomAttributes(TypedArray a);

    @Override
    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
        // Use this listener just when changing the switch layout
        removeOnLayoutChangeListener(this);

        // Change to the new margins and padding
        measure(getMeasuredWidth(), getMeasuredHeight());
    }
}
