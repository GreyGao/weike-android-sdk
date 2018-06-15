package com.abc.recorderdemo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.abcpen.livemeeting.sdk.wbrecord.mo.WeikeMo;
import com.abcpen.livemeeting.sdk.wbrecord.viewutil.ABCRecordSDK;
import com.abcpen.livemeeting.sdk.wbrecord.viewutil.WBBoolean;
import com.abcpen.sdk.abcpenutils.ABCBaseFileUtils;
import com.abcpen.sdk.abcpenutils.FileCacheUtil;
import com.abcpen.sdk.utils.PaperType;

import org.abcpen.common.util.util.AToastUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zarkshao on 2017/5/16.
 */

public class SimpleActivity extends Activity {

    private static final int REQUEST_SDCARD = 100;
    @BindView(R.id.progressbar)
    ProgressBar progressbar;
    @BindView(R.id.et_room_id)
    EditText etRoomId;
    @BindView(R.id.enter_wb)
    Button btnStartWB;
    @BindView(R.id.check_permission)
    Button btnCheckPermission;
    @BindView(R.id.test_write_file)
    Button btnWriteFile;

    public static final String TAG = "SimpleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_simple);
        ButterKnife.bind(this);

        btnCheckPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(SimpleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(SimpleActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_SDCARD);
                }//REQUEST_EXTERNAL_STRONGE是自定义个的一个对应码，用来验证请求是否通过
                else {
                    AToastUtils.showShort("有读写权限");
                }
            }
        });

        btnWriteFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeSD();
            }
        });
    }

    FileOutputStream outputStream;

    private void writeSD() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

        } else {
            AToastUtils.showShort("SD卡不存在");
        }
        String directory = getSaveDirectory(this, "tempFolder", true);
        if (directory == null) {
            AToastUtils.showShort("writeSD空文件夹");
            return;
        }

        File newfile = new File(directory, "1.txt");
        try {
            outputStream = new FileOutputStream(newfile);
            outputStream.write("123".getBytes());
            AToastUtils.showShort("写文件成功");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getSaveDirectory(Context context, String folderName, boolean isNew) {
        if (context != null && !TextUtils.isEmpty(context.getPackageName())) {
            File rootFile = FileCacheUtil.getExternalCacheDirectory(context, folderName);

            if (rootFile != null && rootFile.exists()) {
                String fileName = rootFile.getAbsolutePath();
                Log.e(TAG, "保存文件为fileName=" + fileName);

                if (isNew) {
                    ABCBaseFileUtils.deleteDir(fileName);
                }
                return !rootFile.exists() && !rootFile.mkdirs() ? null : fileName;
            } else {
                Log.e(TAG, "保存文件为null");
                return null;
            }
        } else {
            Log.e(TAG, "context 或者package 为null");

            return null;
        }
    }

    public void onGenId(View view) {
        etRoomId.setText(String.valueOf(System.currentTimeMillis() / 1000));
    }

    public void onEnterWB(View view) {
        if (!WBBoolean.getAccess()) {
            AToastUtils.showShort("无法通过验证，请联系笔声技术服务");
            return;
        }
        final long UniqueId = Long.valueOf(etRoomId.getText().toString().trim());
        WeikeMo weikeMo = App.getWeikeMoById(UniqueId);
        if (weikeMo == null) {
            //数据库中不存在
            weikeMo = new WeikeMo();
            weikeMo.paper_type = PaperType.LANDSCAPE_16_9.value();
            weikeMo.id = UniqueId;
        }
        WeikeActivity.startWeikeForResult(this, weikeMo);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //根据请求是否通过的返回码进行判断，然后进一步运行程序
        if (grantResults.length > 0 && requestCode == REQUEST_SDCARD && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            AToastUtils.showShort("同意读写权限");
        }
    }

    public void onUploadVideo(View view) {
        Intent intent = new Intent(this, VideoManageActivity.class);
        startActivity(intent);
    }

    public void onUploadImage(View view) {
        final long UniqueId = Long.valueOf(etRoomId.getText().toString().trim());
        WeikeMo weikeMo = App.getWeikeMoById(UniqueId);
        if (weikeMo == null) {
            AToastUtils.showShort("微课不存在");
        }
        progressbar.setVisibility(View.VISIBLE);
        ABCRecordSDK.getInstance().uploadWeikeImage(weikeMo, new ABCRecordSDK.UploadResultListener() {
            @Override
            public void onUploadResultSuccess(final long id, String url) {
                if (url != null)
                    AToastUtils.showShort(url);
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onUploadResultFailed(final long id, String result) {
                progressbar.setVisibility(View.GONE);
            }
        });
    }

}
