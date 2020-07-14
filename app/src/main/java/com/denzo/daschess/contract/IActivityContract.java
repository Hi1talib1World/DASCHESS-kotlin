package com.denzo.daschess.contract;

import android.app.usage.UsageEvents;

import androidx.annotation.NonNull;

import com.denzo.daschess.model.ActivityRedirectionModel;
import com.denzo.daschess.ui.IBaseContract;
import com.denzo.daschess.ui.IBasePagerContract;

import java.util.ArrayList;

public interface IActivityContract {

    interface View extends IBaseContract.View, IBasePagerContract.View, IBaseListContract.View {
        void showEvents(ArrayList<UsageEvents.Event> events);
    }

    interface Presenter extends IBasePagerContract.Presenter<IActivityContract.View>{
        void loadEvents(boolean isReload, int page);
        ArrayList<ActivityRedirectionModel> getRedirectionList(@NonNull UsageEvents.Event event);
    }

}