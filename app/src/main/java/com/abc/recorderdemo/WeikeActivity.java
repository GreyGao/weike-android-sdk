package com.abc.recorderdemo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.abc.recorderdemo.ui.SandboxView;
import com.abc.recorderdemo.ui.Vector2D;
import com.abc.recorderdemo.ui.WeikeWBMenu;
import com.abcpen.livemeeting.sdk.WBImageModel;
import com.abcpen.livemeeting.sdk.wbrecord.mo.WeikeMo;
import com.abcpen.livemeeting.sdk.wbrecord.util.BitmapUtil;
import com.abcpen.livemeeting.sdk.wbrecord.util.Utils;
import com.abcpen.livemeeting.sdk.wbrecord.wb.ABCWeakReferenceHandler;
import com.abcpen.livemeeting.sdk.wbrecord.wb.BaseWeikeActivity;
import com.abcpen.sdk.abcpenutils.ABCFileUtils;
import com.abcpen.sdk.pen.device.BluetoothLeDevice;
import com.abcpen.sdk.utils.PenBleWindow;

import org.abcpen.common.util.util.ALog;
import org.abcpen.common.util.util.AToastUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * author 邵小泽
 */
public class WeikeActivity extends BaseWeikeActivity implements WeikeWBMenu.OnItemClickListener {

    protected ABCWeakReferenceHandler mHandler = new ABCWeakReferenceHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @BindView(R.id.iv_close)
    ImageView ivClose;
    @BindView(R.id.iv_recorder)
    ImageView ivRecorder;
    @BindView(R.id.iv_preview)
    ImageView ivPreview;
    @BindView(R.id.tv_recorder_time)
    TextView tvRecorderTime;
    @BindView(R.id.fm_image)
    FrameLayout fmImage;
    @BindView(R.id.tv_image_cancel)
    TextView tvImageCancel;
    @BindView(R.id.tv_image_save)
    TextView tvImageSave;
    @BindView(R.id.ll_image)
    LinearLayout llImage;
    @BindView(R.id.rl_image_fame)
    RelativeLayout rlImageFame;
    @BindView(R.id.iv_last_page)
    ImageView ivLastPage;
    @BindView(R.id.tv_page)
    TextView tvPage;
    @BindView(R.id.iv_next_page)
    ImageView ivNextPage;
    @BindView(R.id.rl_page)
    LinearLayout rlPage;
    @BindView(R.id.wb_menu_view)
    WeikeWBMenu wbMenuView;
    @BindView(R.id.iv_undo)
    ImageView ivUndo;
    @BindView(R.id.iv_redo)
    ImageView ivRedo;
    @BindView(R.id.ll_base)
    LinearLayout llBase;
    @BindView(R.id.fm_image_bg)
    FrameLayout fmImageBg;
    @BindView(R.id.content)
    RelativeLayout content;
    @BindView(R.id.tv_reverse_level)
    TextView tvReverseLevel;
    @BindView(R.id.tv_tile)
    TextView tvTile;
    @BindView(R.id.tv_vertical_inversion)
    TextView tvVerticalInversion;

    private SandboxView sandboxView;
    private PenBleWindow penBleWindow;
    private boolean isResmueRecord = false;

    public static void startWeikeForResult(Context context, WeikeMo mo) {
        Intent intent = new Intent(context, WeikeActivity.class);
        intent.putExtra(WEIKE_DATA, mo);
        context.startActivity(intent);
    }

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_white_board);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ivPreview.setEnabled(false);
        ivNextPage.setEnabled(false);
        ivLastPage.setEnabled(false);
        wbMenuView.setOnItemClickListener(this);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(whiteboardlistener.getIsEdit()){
                    saveWhiteBoard(false);
                }else {
                    destroySurface();
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });
        setRecordTime("00:00");
        setPageText("1/1");
        ivPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previewVideo();
            }
        });

        ivRecorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCapturing();
            }
        });
        ivRedo.setEnabled(false);
        ivUndo.setEnabled(false);
        ivRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whiteboardlistener != null) {
                    whiteboardlistener.redo();
                }
            }
        });

        ivUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (whiteboardlistener != null) {
                    whiteboardlistener.undo();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (penBleWindow != null) {
            penBleWindow.release();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ALog.e(TAG, "onAttachedToWindow: ");
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ALog.e(TAG, "onDetachedFromWindow: ");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ALog.e(TAG, "onConfigurationChanged: ");
        llBase.requestLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        ALog.e(TAG, "onResume: ");
        if (whiteboardlistener != null && isResmueRecord) {
            if (!whiteboardlistener.isRecording()) {
                ALog.e(TAG, "onResume: " + whiteboardlistener.isRecording());
                if (llBase != null) {
                    llBase.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            whiteboardlistener.startRecord();
                        }
                    }, 1000);
                }
                isResmueRecord = false;
            }
        }
    }


    /**
     * 个别本子可以支持翻页
     *
     * @param notePageNo
     * @return
     */
    @Override
    protected boolean onNotePageNoData(int notePageNo) {
        if (notePageNo == -1) return true;
        if (whiteboardlistener.isLoading()) return false;
        if (whiteboardlistener.getCurrentPage() != notePageNo - 1) {
            whiteboardlistener.changeScreen(notePageNo - 1);
        } else {
            return true;
        }

        return false;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ALog.e(TAG, "onSaveInstanceState: ");
    }

    @Override
    protected void onPause() {
        ALog.e(TAG, "onPause: ");
        super.onPause();
        if (whiteboardlistener != null && whiteboardlistener.isRecording()) {
            ALog.e(TAG, "onPause: " + whiteboardlistener.isRecording());
            isResmueRecord = true;
            whiteboardlistener.stopRecord();
            if (llBase != null) {
                llBase.removeCallbacks(null);
            }
        }
    }

    @Override
    public void loadImage(String uri, String imgPath) {

    }

    @Override
    public void onPageText(int index, int total) {
        totalPage = total;
        tvPage.setText(String.valueOf(index + 1) + "/" + total);
    }


    @Override
    public void onEnabledStateChanged(boolean b) {

    }

    @Override
    public void onFragmentCreated() {
        super.onFragmentCreated();
        wbMenuView.init(this, whiteboardlistener, true);
    }


    @Override
    protected void showGuide() {
    }

    @Override
    public void sendMsg(String s, int i) {
        ALog.d(TAG, "sendMsg:");
    }

    public void onBluetoothClick() {
        changeBlePenState();
    }

    @Override
    public void onNetDiskClick() {
    }

    /**
     * 截图显示回调
     *
     * @param path
     */
    @Override
    protected void showScreenShotPage(String path) {
        //在这里显示图片
    }


    @Override
    protected void onRecorderStart() {
        super.onRecorderStart();
        ivRecorder.setImageResource(R.drawable.ic_wb_stop_recoder);
        tvRecorderTime.setTextColor(getResources().getColor(R.color.abc_c1));
        updatePreviewButton(false);
    }

    @Override
    protected void onRecorderStop() {
        super.onRecorderStop();
        ivRecorder.setImageResource(R.drawable.ic_wb_recorder);
        if (isFileExist() ||  ABCFileUtils.tempVideoFolderFileExists(this)) {
            updatePreviewButton(true);
        } else {
            updatePreviewButton(false);
        }
    }

    @Override
    public void onRecordComplete(String s) {
        super.onRecordComplete(s);
        mergeMp4Success = true;
        if (timeInit > 0) {
            mWeikeMo.duration = timeInit / 1000;
        }
        startMergeWhiteboard(this);
    }

    @Override
    public void onRecordFailed() {
        if (saveLoadingDialog != null) {
            saveLoadingDialog.dismiss();
        }
        mergeMp4Success = false;
        AToastUtils.showShort(R.string.merage_video_fail);
        startMergeWhiteboard(this);
    }


    /**
     * 合并白板回调
     **/
    @Override
    public void onMergeComplete(final String[] result) {
        String wbFileName = result[0];
        String wbPNGFileName = result[1];
        if (TextUtils.isEmpty(wbPNGFileName)) {
            AToastUtils.showShort(R.string.merge_white_board_error);
            return;
        }
        mWeikeMo.local_wb_path = wbFileName;
        mWeikeMo.local_wb_png_path = wbPNGFileName;
        mWeikeMo.totalPage = totalPage;
        if (mergeMp4Success && isFileExist()) {
            if (!isNameEquals()) {
                deleteWeike();
            }
            setPath();
        }
        WeikeMo weikeMo = App.getWeikeMoById(mWeikeMo.id);
        if (weikeMo == null) {
            App.getInstance().getDaoSession().getWeikeMoDao().insert(mWeikeMo);
        } else {
            App.getInstance().getDaoSession().getWeikeMoDao().update(mWeikeMo);
        }
        disMissSaveLoading();
        finish();
    }

    /**
     * 合并白板失败
     */
    @Override
    public void onMergeFailed() {
        cancelDialog();
        if (saveLoadingDialog != null) saveLoadingDialog.dismiss();
        AToastUtils.showShort(R.string.save_whiteboard_error);
    }

    @Override
    protected void onRecorderError() {
        super.onRecorderError();
        // TODO: 2017/7/26 录制失败
    }

    @Override
    public void isCanUndo(boolean b) {
        ivUndo.setEnabled(b);
    }

    @Override
    public void isCanRedo(boolean b) {
        ivRedo.setEnabled(b);
    }


    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    @Override
    protected void updatePreviewButton(boolean enable) {
        ivPreview.setEnabled(enable);
    }

    @Override
    protected void onVideoPreview(String localPath) {

    }

    @OnClick({R.id.iv_last_page, R.id.iv_next_page})
    public void changePageClick(View view) {
        switch (view.getId()) {
            case R.id.iv_last_page:
                if (whiteboardlistener != null)
                    whiteboardlistener.changeScreen(false);
                break;
            case R.id.iv_next_page:
                if (whiteboardlistener != null)
                    whiteboardlistener.changeScreen(true);
                break;
        }

    }

    @Override
    public void onDownLoadSuccess(String uri, String path) {
        super.onDownLoadSuccess(uri, path);
    }

    @Override
    public void onDownLoadProgress(long bytesRead, long contentLength) {
        super.onDownLoadProgress(bytesRead, contentLength);
    }

    @Override
    public void onDownLoadFail() {
        super.onDownLoadFail();
    }


    @OnClick({R.id.tv_image_cancel, R.id.tv_tile, R.id.tv_image_save, R.id.tv_reverse_level, R.id.tv_vertical_inversion})
    public void onImageBarClick(View view) {

        if (fmImage.getChildCount() == 1) {
            SandboxView sandboxView = (SandboxView) fmImage.getChildAt(0);
            sandboxView.setBackgroundResource(R.color.abc_transparent);
            switch (view.getId()) {
                case R.id.tv_image_cancel:
                    fmImage.removeView(sandboxView);
                    sandboxView.reset();
                    wbMenuView.setVisibility(View.VISIBLE);
                    fmImageBg.setVisibility(View.GONE);
                    break;

                case R.id.tv_image_save:
                    fmImage.removeView(sandboxView);
                    addImgToWb(sandboxView);
                    wbMenuView.setVisibility(View.VISIBLE);
                    rlPage.setVisibility(View.VISIBLE);
                    fmImageBg.setVisibility(View.GONE);
                    break;

                case R.id.tv_reverse_level:
                    sandboxView.reverseImg();
                    break;

                case R.id.tv_vertical_inversion:
                    sandboxView.verticalInversion();
                    break;

                case R.id.tv_tile:
                    sandboxView.tileImg();
                    break;

            }
        }
    }

    /**
     * 把图片添加到白板中
     *
     * @param sandboxView
     */
    private void addImgToWb(final SandboxView sandboxView) {
        final WBImageModel wbImageModel = new WBImageModel();
        float px, py, width, height;
        float rotate = sandboxView.getRotate();
        float scale = sandboxView.getScale();
        Vector2D vector2D = sandboxView.getVector2D();
        String path = (String) sandboxView.getTag();
        Point p = sandboxView.getBitmapSize();
        px = vector2D.getX();
        py = vector2D.getY();
        width = p.x;
        height = p.y;
        wbImageModel.x = px;
        wbImageModel.y = py;
        wbImageModel.scale = scale;
        wbImageModel.rotate = rotate;
        wbImageModel.width = width;
        wbImageModel.height = height;
        wbImageModel.local_url = path;
        wbImageModel.isVerticalInversion = sandboxView.isVerticalInversion();
        wbImageModel.isReverseImg = sandboxView.isReverseImg();
        if (whiteboardlistener != null)
            whiteboardlistener.addPhotoImage(wbImageModel);
        sandboxView.reset();
    }


    @Override
    protected void showPrivateView() {
//        int requestedOrientation = getRequestedOrientation();
//        PrivateSelectActivity.startPrivateSelectActivity(this, mWeikeMo, PUBLISH_CODE, requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ? 0 : 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        hideBottomUIMenu();
        if (requestCode == REQUEST_CODE_BLE) {
        }
    }

    @Override
    public void onTakeResultData(@NonNull final String path) {
        hideBottomUIMenu();
        AsyncImageTask imageTask = new AsyncImageTask();
        imageTask.execute(path);
    }

    @Override
    public void onScanResult(final List<BluetoothLeDevice> itemList) {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (penBleWindow != null)
                        penBleWindow.setScanResult(itemList);
                }
            });
        }
    }

    @Override
    public void onScanClear() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (penBleWindow != null)
                        penBleWindow.setScanResult(null);
                }
            });
        }
    }

    @Override
    public void onScanAdd(BluetoothLeDevice bluetoothLeDevice) {

    }

    /**
     * 显示调整图片的界面：Demo
     */
    private final class AsyncImageTask extends AsyncTask<String, Integer, Bitmap> {
        String originalPath;

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                originalPath = params[0];
                Bitmap bitmap = BitmapUtil.getSmallBitmap(originalPath);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public boolean isFistCheckSize = true;

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                whiteboardlistener.resetScale();
                if (sandboxView == null)
                    sandboxView = new SandboxView(WeikeActivity.this);
                sandboxView.setBitmap(result);
                sandboxView.setTag(originalPath);
                if (isFistCheckSize) {
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) fmImage.getLayoutParams();
                    Point size = whiteboardlistener.getSize();
                    layoutParams.width = size.x;
                    layoutParams.height = size.y;
                    fmImage.setLayoutParams(layoutParams);
                    isFistCheckSize = false;
                    rlImageFame.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                    fmImage.removeAllViews();
                    fmImage.addView(sandboxView);
                }
                rlPage.setVisibility(View.GONE);
                wbMenuView.setVisibility(View.GONE);
                fmImageBg.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void setPageText(String page) {
        tvPage.setText(page);
    }

    @Override
    public int getFragmentRes() {
        return R.id.content;
    }

    @Override
    protected void canDoChangePage(boolean isCanDo) {
        ivLastPage.setEnabled(isCanDo);
        ivNextPage.setEnabled(isCanDo);
    }

    @Override
    protected void setRecordTime(String timer) {
        tvRecorderTime.setText(timer);
    }

    @Override
    protected void onPenStateChange(int state) {

        if (penBleWindow != null) penBleWindow.onStateChange(state);

        if (wbMenuView != null) {
            wbMenuView.setBlePenState(state);
        }
    }

    @Override
    protected void showPenBleWindow() {
        if (penBleWindow == null) {
            penBleWindow = new PenBleWindow(this, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            penBleWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {

                }
            });

        }
        penBleWindow.showAtLocation(fmImage, Gravity.CENTER, 0, 0);
    }


    @Override
    public void onYunPanClick() {
        //loadPDF.
    }

    @Override
    public void onAddImageClick() {
        //打开相册
        showPhotoDialog();
    }

    @Override
    public void onScreenShotClick() {
        /**用户点击截图**/
        if (whiteboardlistener != null) {
            isDoScreenShot = true;
            whiteboardlistener.getScreenShot(screenShotFileName, this);
        }
    }


    @Override
    public void onBleClick() {
        changeBlePenState();
    }

    @Override
    public void onAddWBPage() {
        if (whiteboardlistener != null) {
            whiteboardlistener.changeScreen(Utils.getUsePage());
        }
    }

}