package com.example.belikek;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

import java.util.List;

public interface ToyyibPayAPI {
    @GET("getBankFPX")
    Call<List<Bank>> getBankList();

    @FormUrlEncoded
    @POST("createBill")
    Call<ResponseBody> createBillRaw(
            @Field("userSecretKey") String userSecretKey,
            @Field("categoryCode") String categoryCode,
            @Field("billName") String billName,
            @Field("billDescription") String billDescription,
            @Field("billPriceSetting") String billPriceSetting,
            @Field("billPayorInfo") String billPayorInfo,
            @Field("billAmount") String billAmount,
//            @Field("billReturnUrl") String billReturnUrl,
//            @Field("billCallbackUrl") String billCallbackUrl,
            @Field("billExternalReferenceNo") String billExternalReferenceNo,
            @Field("billTo") String billTo,
            @Field("billEmail") String billEmail,
            @Field("billPhone") String billPhone,
            @Field("billSplitPayment") String billSplitPayment,
            @Field("billSplitPaymentArgs") String billSplitPaymentArgs,
            @Field("billPaymentChannel") String billPaymentChannel
    );
}
