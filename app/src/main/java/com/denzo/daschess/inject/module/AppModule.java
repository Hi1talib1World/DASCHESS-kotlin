package com.denzo.daschess.inject.module;


import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;

import com.denzo.daschess.AppApplication;
import com.denzo.daschess.AppConfig;
import com.denzo.daschess.dao.DBOpenHelper;
import com.denzo.daschess.dao.DaoSession;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private AppApplication application;

    public AppModule(AppApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public AppApplication provideApplication() {
        return application;
    }

    @NonNull
    @Provides
    @Singleton
    public DaoSession provideDaoSession() {
        DBOpenHelper helper = new DBOpenHelper(application, AppConfig.DB_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }


}