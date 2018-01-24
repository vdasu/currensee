package com.skvrahul.currensee;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by skvrahul on 23/1/18.
 */

public interface APIInterface {
    @Multipart
    @POST("upload")
    Call<Response> uploadImage(@Part MultipartBody.Part image);

}

