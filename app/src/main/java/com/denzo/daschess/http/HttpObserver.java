package com.denzo.daschess.http;

public interface HttpObserver<T> {
    /**
     * Error
     * @param error
     */
    void onError(Throwable error);

    /**
     * success
     * @param response
     */
    void onSuccess(HttpResponse<T> response);
}
