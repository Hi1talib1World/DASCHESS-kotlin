package com.denzo.daschess.http;


import retrofit2.Response;
import rx.Subscriber;


public class HttpSubscriber<T> extends Subscriber<Response<T>> {

    private HttpObserver<T> mObserver;

    public HttpSubscriber() {
    }

    public HttpSubscriber(HttpObserver<T> observer) {
        mObserver = observer;
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        if (mObserver != null)
            mObserver.onError(e);
    }

    @Override
    public void onNext(Response<T> r) {
        if (mObserver != null)
            mObserver.onSuccess(new HttpResponse<>(r));
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}