package com.abc.recorderdemo.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.abc.recorderdemo.R;
import com.abcpen.livemeeting.sdk.wbrecord.sketch.Constants;
import com.abcpen.livemeeting.sdk.wbrecord.sketch.WhiteboardListener;
import com.abcpen.livemeeting.sdk.wbrecord.wb.BaseWeikeActivity;
import com.abcpen.sdk.pen.PenSDK;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaoze on 2017/10/20.
 */

public class WeikeWBMenu extends LinearLayout {

    private BaseWeikeActivity mBaseWhiteboardActivity;
    private WhiteboardListener mWhiteBoardListener;
    private int mColorIndex = 0;
    private int mWidthIndex = 0;
    private boolean isHost;

    private WeiKeExpandableButton alPen;
    private WeiKeExpandableButton alSize;
    private WeiKeExpandableButton alColor;
    private WeiKeExpandableButton alDel;
    private WeiKeExpandableButton alAdd;
    private WeiKeExpandableButton alEraser;

    private WeiKeExpandableButton mLastExpandableButton = null;

    private List<WeiKeExpandableButton> itemButtons;

    // 选择了哪种笔
    private int mPenType = Constants.INK_PEN;

    private OnItemClickListener mListener;

    private List<WeikeButtonData> penWeikeButtonDatas,
            sizeWeikeButtonDatas,
            colorWeikeButtonDatas, alAddWeikeButtonDatas;

    public WeikeWBMenu(Context context) {
        this(context, null);
    }

    public WeikeWBMenu(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public WeikeWBMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    public void setBlePenState(int blePenState) {
        if (alAddWeikeButtonDatas != null) {
            WeikeButtonData weikeButtonData = alAddWeikeButtonDatas.get(alAddWeikeButtonDatas.size() - 1);
            if (blePenState == PenSDK.STATE_CONNECTED) {
                weikeButtonData.setSelect(true);
            } else {
                weikeButtonData.setSelect(false);
            }
        }
    }

    public interface OnItemClickListener {

        void onYunPanClick();

        void onAddImageClick();

        void onScreenShotClick();

        void onBleClick();

        void onAddWBPage();
    }

    public void init(BaseWeikeActivity baseWhiteBoardActivity, WhiteboardListener listener, boolean isHost) {
        this.mBaseWhiteboardActivity = baseWhiteBoardActivity;
        this.mWhiteBoardListener = listener;
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
        inflate(getContext(), R.layout.view_wb_menu, this);
        this.isHost = isHost;
        initView();
    }

    private void initView() {

        initPen();
        initSize();
        initColor();
        initEraser();
        initAdd();
        initDel();

        itemButtons = new ArrayList<>(5);
        itemButtons.add(alPen);
        itemButtons.add(alSize);
        itemButtons.add(alColor);
        itemButtons.add(alDel);
        itemButtons.add(alAdd);
        mLastExpandableButton = alPen;
        alPen.getMainButton().setSelect(true);
    }

    private void initDel() {
        alDel = (WeiKeExpandableButton) findViewById(R.id.al_del);

        final List<WeikeButtonData> alDelWeikeButtonDatas = new ArrayList<>();
        int[] alDelDrawable;
        if (isHost) {
            alDelDrawable = new int[]{R.drawable.wb_del_bg, R.drawable.wb_del_item_clean};
        } else {
            alDelDrawable = new int[]{R.drawable.wb_del_bg, R.drawable.wb_del_item_clean};
        }
        for (int i = 0; i < alDelDrawable.length; i++) {
            WeikeButtonData weikeButtonData = WeikeButtonData.buildIconButton(getContext(), alDelDrawable[i], 0);
            alDelWeikeButtonDatas.add(weikeButtonData);
        }
        alDel.setWeikeButtonDatas(alDelWeikeButtonDatas);
        alDel.setButtonEventListener(new WeiKeButtonEventListener() {
            @Override
            public void onButtonClicked(WeikeButtonData weikeButtonData, int index) {
                switch (index) {
                    case 0:
                        showDelDialog();
                        break;
                }
            }

            @Override
            public void onExpand(WeiKeExpandableButton view) {
                resetAllViews(view);
                view.setItemSelected(view.getMainButton(), true);
            }

            @Override
            public void onCollapse(WeiKeExpandableButton view) {
                view.setItemSelected(view.getMainButton(), false);
            }

            @Override
            public void onParentClicked(WeiKeExpandableButton view) {

            }
        });
    }

    private void initAdd() {
        alAdd = (WeiKeExpandableButton) findViewById(R.id.al_add);
        if (isHost) {
            alAddWeikeButtonDatas = new ArrayList<>();
            int[] alAddDrawable = {R.drawable.wb_add_bg,
                    R.drawable.wb_img_bg,
                    R.drawable.abc_record_yunpan,
                    R.drawable.wb_screen_shot_bg,
                    R.drawable.abc_record_wb_ble_normal,
                    R.drawable.abc_record_wb_add_default
            };
            for (int i = 0; i < alAddDrawable.length; i++) {
                WeikeButtonData weikeButtonData = WeikeButtonData.buildIconButton(getContext(), alAddDrawable[i], 0);
                alAddWeikeButtonDatas.add(weikeButtonData);
            }
            alAdd.setWeikeButtonDatas(alAddWeikeButtonDatas);
            alAdd.setButtonEventListener(new WeiKeButtonEventListener() {
                @Override
                public void onButtonClicked(WeikeButtonData weikeButtonData, int index) {
                    switch (index) {
                        case 0:
                            mListener.onAddImageClick();
                            break;
                        case 1:
                            mListener.onYunPanClick();
                            break;
                        case 2:
                            mListener.onScreenShotClick();
                            break;
                        case 3:
                            mListener.onBleClick();
                            break;
                        case 4:
                            mListener.onAddWBPage();
                            break;
                        default:
                            break;
                    }

                }

                @Override
                public void onExpand(WeiKeExpandableButton view) {
                    resetAllViews(view);
                    view.setItemSelected(view.getMainButton(), true);
                }

                @Override
                public void onCollapse(WeiKeExpandableButton view) {
                    view.setItemSelected(view.getMainButton(), false);
                }

                @Override
                public void onParentClicked(WeiKeExpandableButton view) {
                }
            });
        } else {
            alAdd.setVisibility(GONE);
        }
    }

    private void initEraser() {
        alEraser = (WeiKeExpandableButton) findViewById(R.id.al_eraser);
        ArrayList eraserData = new ArrayList<>();
        int[] eraserDrawable = {R.drawable.wb_eraser_bg};
        for (int i = 0; i < eraserDrawable.length; i++) {
            WeikeButtonData weikeButtonData = WeikeButtonData.buildIconButton(getContext(), eraserDrawable[i], 0);
            eraserData.add(weikeButtonData);
        }
        alEraser.setIsCanExpand(false);
        alEraser.setWeikeButtonDatas(eraserData);
        alEraser.setButtonEventListener(new WeiKeButtonEventListener() {
            @Override
            public void onButtonClicked(WeikeButtonData weikeButtonData, int index) {

            }

            @Override
            public void onExpand(WeiKeExpandableButton view) {
                changeSelect(view);
            }

            @Override
            public void onCollapse(WeiKeExpandableButton view) {

            }

            @Override
            public void onParentClicked(WeiKeExpandableButton view) {
                mWhiteBoardListener.setDrawMode(Constants.FreehandMode);
                mWhiteBoardListener.setToolType(Constants.POS_CLEAR,
                        mWidthIndex, mPenType);
                changeSelect(view);
            }
        });
    }

    private void initColor() {
        alColor = (WeiKeExpandableButton) findViewById(R.id.al_color);
        colorWeikeButtonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.wb_color_bg,
                R.drawable.wb_color_item_black,
                R.drawable.wb_color_item_blue,
                R.drawable.wb_color_item_red,
                R.drawable.wb_color_item_yellow,
                R.drawable.wb_color_item_green
        };
        for (int i = 0; i < drawable.length; i++) {
            WeikeButtonData weikeButtonData = WeikeButtonData.buildIconButton(getContext(), drawable[i], 0);
            if (i == 1) {
                weikeButtonData.setSelect(true);
            }
            colorWeikeButtonDatas.add(weikeButtonData);
        }
        alColor.setWeikeButtonDatas(colorWeikeButtonDatas);
        alColor.setButtonEventListener(new WeiKeButtonEventListener() {
            @Override
            public void onButtonClicked(WeikeButtonData weikeButtonData, int index) {
                switch (index) {
                    case 0:
                        mColorIndex = Constants.POS_BLACK;
                        break;
                    case 1:
                        mColorIndex = Constants.POS_BLUE;
                        break;
                    case 2:
                        mColorIndex = Constants.POS_RED;
                        break;
                    case 3:
                        mColorIndex = Constants.POS_YELLOW;
                        break;
                    case 4:
                        mColorIndex = Constants.POS_GREEN;
                        break;
                }
                for (WeikeButtonData btn : colorWeikeButtonDatas) {
                    if (btn != weikeButtonData && !btn.isMainButton()) {
                        btn.setSelect(false);
                    } else
                        btn.setSelect(true);
                }
                mWhiteBoardListener.setToolType(mColorIndex, mWidthIndex, mPenType);
            }

            @Override
            public void onExpand(WeiKeExpandableButton view) {
                changeSelect(view);
            }

            @Override
            public void onCollapse(WeiKeExpandableButton view) {

            }

            @Override
            public void onParentClicked(WeiKeExpandableButton view) {

            }
        });
    }

    private void initSize() {

        alSize = (WeiKeExpandableButton) findViewById(R.id.al_size);
        sizeWeikeButtonDatas = new ArrayList<>();
        int[] sizeDrawable = {R.drawable.wb_size_bg, R.drawable.wb_size_item_big, R.drawable.wb_size_item_mid, R.drawable.wb_size_item_small,};
        for (int i = 0; i < sizeDrawable.length; i++) {
            WeikeButtonData weikeButtonData = WeikeButtonData.buildIconButton(getContext(), sizeDrawable[i], 0);
            if (i == 3) {
                weikeButtonData.setSelect(true);
            }
            sizeWeikeButtonDatas.add(weikeButtonData);
        }
        alSize.setWeikeButtonDatas(sizeWeikeButtonDatas);
        alSize.setButtonEventListener(new WeiKeButtonEventListener() {
            @Override
            public void onButtonClicked(WeikeButtonData weikeButtonData, int index) {
                switch (index) {
                    case 0:
                        mWidthIndex = Constants.POS_THICK;
                        break;
                    case 1:
                        mWidthIndex = Constants.POS_MID;
                        break;
                    case 2:
                        mWidthIndex = Constants.POS_THIN;
                        break;
                }
                for (WeikeButtonData btn : sizeWeikeButtonDatas) {
                    if (btn != weikeButtonData && !btn.isMainButton())
                        btn.setSelect(false);
                    else
                        btn.setSelect(true);
                }
                mWhiteBoardListener.setDrawMode(Constants.FreehandMode);
                mWhiteBoardListener.setToolType(mColorIndex, mWidthIndex, mPenType);
            }

            @Override
            public void onExpand(WeiKeExpandableButton view) {
                changeSelect(view);
            }

            @Override
            public void onCollapse(WeiKeExpandableButton view) {

            }

            @Override
            public void onParentClicked(WeiKeExpandableButton view) {

            }
        });
    }

    private void initPen() {
        alPen = (WeiKeExpandableButton) findViewById(R.id.al_pen);
        penWeikeButtonDatas = new ArrayList<>();
        int[] penDrawable = {R.drawable.wb_pen_bg, R.drawable.wb_default_pen_bg, R.drawable.wb_round_pen_bg, R.drawable.wb_mark_pen_bg};
        for (int i = 0; i < penDrawable.length; i++) {
            WeikeButtonData weikeButtonData = WeikeButtonData.buildIconButton(getContext(), penDrawable[i], 0);
            penWeikeButtonDatas.add(weikeButtonData);
            if (i == 1) {
                weikeButtonData.setSelect(true);
            }
        }
        alPen.setIsCanExpand(true);
        alPen.setWeikeButtonDatas(penWeikeButtonDatas);
        alPen.setButtonEventListener(new WeiKeButtonEventListener() {
            @Override
            public void onButtonClicked(WeikeButtonData weikeButtonData, int index) {
                switch (index) {
                    case 0:
                        mPenType = Constants.INK_PEN;
                        break;
                    case 1:
                        mPenType = Constants.ROUND_PEN;
                        break;
                    case 2:
                        mPenType = Constants.MARKER_PEN;
                        break;

                }
                mWhiteBoardListener.setToolType(mColorIndex, mWidthIndex, mPenType);
                for (WeikeButtonData btn : penWeikeButtonDatas) {
                    if (btn != weikeButtonData && !btn.isMainButton())
                        btn.setSelect(false);
                    else
                        btn.setSelect(true);
                }
            }

            @Override
            public void onExpand(WeiKeExpandableButton view) {
                changeSelect(view);
                mWhiteBoardListener.setDrawMode(Constants.FreehandMode);
                mWhiteBoardListener.setToolType(mColorIndex, mWidthIndex, mPenType);
            }

            @Override
            public void onCollapse(WeiKeExpandableButton view) {

            }

            @Override
            public void onParentClicked(WeiKeExpandableButton view) {

            }
        });
    }


    private void resetAllViews(View view) {
        for (WeiKeExpandableButton item : itemButtons) {
            if (item != view) {
                item.collapse();
            }
        }
    }

    private void showDelDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.abc_dialog_hint)
                .setMessage(R.string.abc_clean_wb_msg)
                .setPositiveButton(R.string.abc_del_str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mWhiteBoardListener.clearCurrentScreen();
                    }
                })
                .setNegativeButton(R.string.abc_cancel_str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    /**
     * 重置确认
     */
    private void showResetPageDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.abc_reset_hint)
                .setMessage(R.string.abc_reset_wb_msg)
                .setPositiveButton(R.string.abc_confirm_str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mBaseWhiteboardActivity != null) {
                            mBaseWhiteboardActivity.doReset();
                        }
                    }
                })
                .setNegativeButton(R.string.abc_cancel_str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }


    public void changeSelect(WeiKeExpandableButton view) {
        resetAllViews(view);
        if (mLastExpandableButton != null && mLastExpandableButton != view) {
            mLastExpandableButton.setItemSelected(mLastExpandableButton.getMainButton(), false);
        }
        mLastExpandableButton = view;
        mLastExpandableButton.setItemSelected(mLastExpandableButton.getMainButton(), true);
    }
}
