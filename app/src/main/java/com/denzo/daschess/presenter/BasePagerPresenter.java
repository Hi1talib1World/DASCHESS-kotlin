package com.denzo.daschess.presenter;


import com.denzo.daschess.dao.DaoSession;
import com.denzo.daschess.ui.IBasePagerContract;

public abstract class BasePagerPresenter<V extends IBasePagerContract.View> extends BasePresenter<V>
        implements IBasePagerContract.Presenter<V>{

    private boolean isLoaded = false;

    public BasePagerPresenter(DaoSession daoSession) {
        super(daoSession);
    }

    @Override
    public void onViewInitialized() {
        super.onViewInitialized();
        prepareLoadData();
    }

    @Override
    public void prepareLoadData() {
        if(mView == null) {
            return;
        }
        if(mView.isPagerFragment() && (!isViewInitialized() || !mView.isFragmentShowed())){
            return;
        }
        if(isLoaded) return;
        isLoaded = true;
        loadData();
    }

    public void setLoaded(boolean loaded) {
        isLoaded = loaded;
    }

    protected abstract void loadData();

}
