package com.abc.recorderdemo.db;

import android.content.Context;

import com.abc.recorderdemo.db.wb.DaoMaster;
import com.abc.recorderdemo.db.wb.WeikeMoDao;

import org.abcpen.common.util.util.ALog;
import org.greenrobot.greendao.database.Database;

/**
 * Created by Woong on 2017/8/25.
 */

public class DbHelper extends DaoMaster.OpenHelper{
    public DbHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);

        ALog.i("version", oldVersion + "---先前和更新之后的版本---" + newVersion);
        if (oldVersion < newVersion) {
            ALog.i("version", oldVersion + "---先前和更新之后的版本---" + newVersion);
            MigrationHelper.getInstance().migrate(db, WeikeMoDao.class);
            //更改过的实体类(新增的不用加)   更新UserDao文件 可以添加多个  XXDao.class 文件
//             MigrationHelper.getInstance().migrate(db, UserDao.class,XXDao.class);
        }
    }
}
