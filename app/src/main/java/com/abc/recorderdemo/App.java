package com.abc.recorderdemo;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.abc.recorderdemo.db.DbHelper;
import com.abc.recorderdemo.db.wb.DaoMaster;
import com.abc.recorderdemo.db.wb.DaoSession;
import com.abcpen.livemeeting.sdk.wbrecord.mo.WeikeMo;
import com.abcpen.livemeeting.sdk.wbrecord.viewutil.ABCRecordSDK;
import com.abcpen.pen.plugin.abcpen.ABCPenSDK;
import com.abcpen.sdk.pen.PenSDK;

import org.abcpen.common.util.util.AToastUtils;
import org.abcpen.common.util.util.AppUtils;
import org.abcpen.common.util.util.Utils;
import org.greenrobot.greendao.database.Database;

import java.util.List;

/**
 * Created by zarkshao on 2017/11/21.
 */

public class App extends MultiDexApplication implements ABCRecordSDK.ABCRecordSDKInitListener {

    static App appCtx;

    private DaoSession daoSession;

    /**
     * 三天失效
     */
    private long DURATION = 3 * 24 * 60 * 60 * 1000L;

    public static Context getAppCtx() {
        return appCtx;
    }

    public static App getInstance() {
        return appCtx;
    }

    public static List<WeikeMo> getAllWeikeMo() {
        return App.getInstance().getDaoSession().getWeikeMoDao().loadAll();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appCtx = this;

        DbHelper dbHelper = new DbHelper(this, "abcpen_wb");
        Database db = dbHelper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
        Utils.init(appCtx);

        /**
         * 验证Api
         */
        //#online
        final String appkey = "97b5f664b5b94c6f90ca993340a9eb54";
        final String appSecret = "6e8f3a945f03494399e873c14e060225";
//        final String appkey = "7d206011b003450b8c452447506ed187";
//        final String appSecret = "f9bdb1f2f0d94f21bb2f984cffccbb49";
        ABCRecordSDK.LOADING_DRAWABLE_RES = R.drawable.common_loading;
        ABCRecordSDK.LOADING_BG_RES = R.color.abc_b9;
        ABCRecordSDK.getInstance().init(this, appkey,
                appSecret, String.valueOf(System.currentTimeMillis() + DURATION)
                , AppUtils.getAppPackageName(), this);

        PenSDK.getInstance().init(this);

        //设置为笔声智能笔 一代
        //
        PenSDK.getInstance().setPenImpl(ABCPenSDK.getInstance());

        //设置为笔声智能笔 二代
        //
//        PenSDK.getInstance().setPenImpl(UGPenSDK.getInstance());


        //Na smart Pen
//        PenSDK.getInstance().setPenImpl(NASDK.getInstance());

        //Bridge Pen
//        PenSDK.getInstance().setPenImpl(BridgePenSDK.getInstance());

        //Equil 笔
//        PenSDK.getInstance().setPenImpl(EquilSDK.getInstance());

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static WeikeMo getWeikeMoById(long uniqueId) {
        return App.getInstance().getDaoSession().getWeikeMoDao().load(uniqueId);
    }

    @Override
    public void initSuccess() {
        AToastUtils.showShort("SDK init successfully");
    }

    @Override
    public void initFailed(String reason) {
        if (!TextUtils.isEmpty(reason)) {
            AToastUtils.showShort(reason);
        }
    }
}
