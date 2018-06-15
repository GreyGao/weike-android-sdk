package com.abc.recorderdemo.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

/**
 * Created by dear33 on 2016/9/9.
 */
public class WeikeButtonData implements Cloneable {
    private static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;

    private boolean isMainButton = false;//main button is the button you see when buttons are all collapsed
    private boolean iconButton;//true if the button use icon resource,else string resource

    private boolean isselect = false;
    private boolean isPress = false;

    private String[] texts;//String array that you want to show at button center,texts[i] will be shown at the ith row
    private Drawable icon;//icon drawable that will be shown at button center
    private float iconPaddingDp;//the padding of the icon drawable in button
    private int backgroundColor = DEFAULT_BACKGROUND_COLOR;//the background color of the button

    @Override
    protected Object clone() throws CloneNotSupportedException {
        WeikeButtonData weikeButtonData = (WeikeButtonData) super.clone();
        weikeButtonData.setIsIconButton(this.iconButton);
        weikeButtonData.setBackgroundColor(this.backgroundColor);
        weikeButtonData.setIsMainButton(this.isMainButton);
        weikeButtonData.setIcon(this.icon);
        weikeButtonData.setIconPaddingDp(this.iconPaddingDp);
        weikeButtonData.setTexts(this.texts);
        return weikeButtonData;
    }

    public static WeikeButtonData buildTextButton(String... text) {
        WeikeButtonData weikeButtonData = new WeikeButtonData(false);
        weikeButtonData.iconButton = false;
        weikeButtonData.setText(text);
        return weikeButtonData;
    }

    public static WeikeButtonData buildIconButton(Context context, int iconResId, float iconPaddingDp) {
        WeikeButtonData weikeButtonData = new WeikeButtonData(true);
        weikeButtonData.iconButton = true;
        weikeButtonData.iconPaddingDp = iconPaddingDp;
        weikeButtonData.setIconResId(context, iconResId);
        return weikeButtonData;
    }

    private WeikeButtonData(boolean iconButton) {
        this.iconButton = iconButton;
    }

    public void setIsMainButton(boolean isMainButton) {
        this.isMainButton = isMainButton;
    }

    public boolean isMainButton() {
        return isMainButton;
    }

    public void setIsIconButton(boolean isIconButton) {
        iconButton = isIconButton;
    }

    public String[] getTexts() {
        return texts;
    }

    public void setTexts(String[] texts) {
        this.texts = texts;
    }

    public void setText(String... text) {
        this.texts = new String[text.length];
        for (int i = 0, length = text.length; i < length; i++) {
            this.texts[i] = text[i];
        }
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Drawable getIcon() {
        return this.icon;
    }

    public void setIconResId(Context context, int iconResId) {
        this.icon = context.getResources().getDrawable(iconResId);
    }

    public boolean isIconButton() {
        return iconButton;
    }

    public float getIconPaddingDp() {
        return iconPaddingDp;
    }

    public void setIconPaddingDp(float padding) {
        this.iconPaddingDp = padding;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBackgroundColorId(Context context, int backgroundColorId) {
        this.backgroundColor = context.getResources().getColor(backgroundColorId);
    }

    public void setButtonIconState(int state) {
        if (icon != null)
            icon.setState(new int[]{state});

    }

    public void setSelect(boolean isSelect) {
        this.isselect = isSelect;
        if (icon == null) return;
        if (isselect) {
            if (isPress) {
                icon.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_selected});
            } else {
                icon.setState(new int[]{android.R.attr.state_selected});
            }
        } else {
            if (isPress) {
                icon.setState(new int[]{android.R.attr.state_pressed});
            } else {
                icon.setState(null);
            }
        }
    }

    public void setPress(boolean isPress) {
        this.isPress = isPress;
        if (icon == null) return;
        if (isPress) {
            if (isselect) {
                icon.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_selected});
            } else {
                icon.setState(new int[]{android.R.attr.state_pressed});
            }
        } else {
            if (isselect) {
                icon.setState(new int[]{android.R.attr.state_selected});
            } else {
                icon.setState(null);
            }
        }
    }

    public void setButtonIconState(int[] state) {
        if (icon != null)
            icon.setState(state);
    }

    public void cleanButtonIconState() {
        if (icon != null)
            icon.setState(null);
    }

    public int[] getButtonIconState() {
        return icon.getState();
    }
}
