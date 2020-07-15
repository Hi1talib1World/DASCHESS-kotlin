package com.denzo.daschess.inject.component;


import com.denzo.daschess.AppApplication;
import com.denzo.daschess.dao.DaoSession;
import com.denzo.daschess.inject.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    AppApplication getApplication();

    DaoSession getDaoSession();

}
