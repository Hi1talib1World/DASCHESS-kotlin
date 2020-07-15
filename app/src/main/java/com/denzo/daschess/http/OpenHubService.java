package com.denzo.daschess.http;


import androidx.annotation.NonNull;

import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface OpenHubService {

    /**
     * get trending repos, update every day
     * @param since daily, weekly, monthly
     * @param language language
     */
    @Headers("Cache-Control: public, max-age=86400")
    @NonNull
    @GET("trending")
    Observable<Response<ArrayList<Repository>>> getTrendingRepos(
            @Query("since") String since,
            @Query("language") String language
    );

    /**
     * get trending languages, update every day
     */
    @Headers("Cache-Control: public, max-age=86400")
    @NonNull @GET("languages")
    Observable<Response<ArrayList<TrendingLanguage>>> getTrendingLanguages();

}
