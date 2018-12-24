package com.mydiary.flickrgallery.retrofit;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIServer {
    @GET("/services/rest/")
    Single<ResultAPI> getAllImage(@Query("method") String method,
                                  @Query("api_key") String apiKey,
                                  @Query("format") String format,
                                  @Query("nojsoncallback") int nojsoncallback,
                                  @Query("extras") String extras);
}
