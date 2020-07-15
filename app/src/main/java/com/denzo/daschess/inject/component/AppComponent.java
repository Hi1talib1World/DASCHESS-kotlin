package com.denzo.daschess.inject.component;


import com.denzo.daschess.AppApplication;
import com.denzo.daschess.dao.DaoSession;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    /**
     * 获取AppApplication
     * @return
     */
    AppApplication getApplication();

    /**
     * 获取数据库Dao
     * @return
     */
    DaoSession getDaoSession();

}
