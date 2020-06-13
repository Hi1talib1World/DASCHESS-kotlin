package com.denzo.daschess.ui;

public interface IBasePagerContract {
    interface View extends IBaseContract.View {
        boolean isPagerFragment();
        boolean isFragmentShowed();
    }

    interface Presenter<V extends IBasePagerContract.View>
            extends IBaseContract.Presenter<V> {
        void prepareLoadData();
    }
}
