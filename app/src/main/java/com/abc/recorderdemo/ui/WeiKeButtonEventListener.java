package com.abc.recorderdemo.ui;

/**
 * Created by dear33 on 2016/9/11.
 */
public interface WeiKeButtonEventListener {
    /**
     * @param index button index, count from startAngle to endAngle, value is 1 to expandButtonCount
     */
    void onButtonClicked(WeikeButtonData view, int index);

    void onExpand(WeiKeExpandableButton view);

    void onCollapse(WeiKeExpandableButton view);

    void onParentClicked(WeiKeExpandableButton view);

}
