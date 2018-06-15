package com.abc.recorderdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.abcpen.livemeeting.sdk.wbrecord.mo.UploadEvent;
import com.abcpen.livemeeting.sdk.wbrecord.mo.WeikeMo;
import com.abcpen.livemeeting.sdk.wbrecord.service.UploadWeiKeService;

import org.abcpen.common.util.util.AToastUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shaoxiaoze on 2018/2/6.
 */

public class VideoManageActivity extends Activity {

    private List<WeikeMo> uploadVideos = new ArrayList<>();
    private Map<Long, String> urlMap = new HashMap();
    private ListView listView;
    private VideoListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_manager);
        uploadVideos = App.getAllWeikeMo();

        listView = (ListView) findViewById(R.id.list_view);
        adapter = new VideoListAdapter();
        listView.setAdapter(adapter);
        EventBus.getDefault().register(this);
    }

    class VideoListAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
        private ViewHolder holder;

        public VideoListAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return uploadVideos.size();
        }

        @Override
        public Object getItem(int i) {
            return uploadVideos.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, final ViewGroup viewGroup) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.video_push_status, null);
                holder = new ViewHolder();
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.status = (TextView) convertView.findViewById(R.id.status);
                holder.url = (TextView) convertView.findViewById(R.id.url);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final long id = uploadVideos.get(i).getId();
            holder.name.setText(String.valueOf(id));
            holder.status.setText(uploadVideos.get(i).pushStatus == WeikeMo.PUSH_SUCCESS ? "SUCCESS" : "FAILED");
            holder.url.setText(TextUtils.isEmpty(urlMap.get(id))?"url=":"url=" + urlMap.get(id));
            holder.status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listView!=null){
                        View v = listView.getChildAt(i);
                        TextView statusTv = (TextView) v.findViewById(R.id.status);
                        statusTv.setText("UPLOADING");
                        WeikeMo weikeMo = App.getWeikeMoById(uploadVideos.get(i).getId());
                        if (weikeMo == null) {
                            AToastUtils.showShort("微课不存在");
                            statusTv.setText("FAILED");
                        } else {
                            UploadWeiKeService.startUploadService(VideoManageActivity.this, weikeMo, R.mipmap.ic_launcher, "正在后台上传微课");
                        }
                    }

                }
            });
            return convertView;
        }

        class ViewHolder {

            public TextView name;
            public TextView status;
            public TextView url;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUploadResult(UploadEvent event) {
        /** 这里最好不要进行过多的数据库操作，需要优化**
         * demo仅仅展示功能
         */
        WeikeMo mo = App.getWeikeMoById(event.weikeId);
        if (mo != null) {
            mo.pushStatus = event.status;
            if (mo.pushStatus == WeikeMo.PUSH_SUCCESS) {
                urlMap.put(event.weikeId, event.url);
            }
            App.getInstance().getDaoSession().getWeikeMoDao().update(mo);
        }
        uploadVideos = App.getAllWeikeMo();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
