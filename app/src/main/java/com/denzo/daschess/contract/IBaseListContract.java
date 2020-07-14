package com.denzo.daschess.contract;


public interface IBaseListContract {

    interface View {
        void showLoadError(String errorMsg);

        void setCanLoadMore(boolean canLoadMore);
    }

}