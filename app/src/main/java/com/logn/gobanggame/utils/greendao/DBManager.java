package com.logn.gobanggame.utils.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.logn.gobanggame.utils.DefaultValue;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by long on 2017/6/19.
 */

public class DBManager {


    private final static String dbName = DefaultValue.DB_NAME;
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    private DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 插入一条记录
     *
     * @param bean
     */
    public void insertChessArrayBean(ChessArrayBean bean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChessArrayBeanDao beanDao = daoSession.getChessArrayBeanDao();
        beanDao.insert(bean);
    }


    /**
     * 删除一条记录
     *
     * @param bean
     */
    public void deleteChessArray(ChessArrayBean bean) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChessArrayBeanDao beanDao = daoSession.getChessArrayBeanDao();
        beanDao.delete(bean);
    }


    /**
     * 根据主键删除一条记录
     *
     * @param key
     */
    public void deleteChessArrayByKey(long key) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChessArrayBeanDao beanDao = daoSession.getChessArrayBeanDao();
        beanDao.deleteByKey(key);
    }


    /**
     * 查询全部数据
     *
     * @return
     */
    public List<ChessArrayBean> queryChessArrayList() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        ChessArrayBeanDao userDao = daoSession.getChessArrayBeanDao();
        QueryBuilder<ChessArrayBean> qb = userDao.queryBuilder();
        List<ChessArrayBean> list = qb.list();
        return list;
    }


    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }

    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }
}
