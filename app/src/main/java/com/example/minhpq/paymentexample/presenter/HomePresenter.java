package com.example.minhpq.paymentexample.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.adyen.core.PaymentRequest;
import com.adyen.core.interfaces.HttpResponseCallback;
import com.adyen.core.interfaces.PaymentDataCallback;
import com.adyen.core.interfaces.PaymentRequestListener;
import com.adyen.core.models.Payment;
import com.adyen.core.models.PaymentRequestResult;
import com.adyen.core.utils.AsyncHttpClient;
import com.example.minhpq.paymentexample.model.PaymentSetupRequest;
import com.example.minhpq.paymentexample.ui.activity.PaymentResultActivity;
import com.example.minhpq.paymentexample.ulti.ApiKey;
import com.example.minhpq.paymentexample.view.HomeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class HomePresenter {
    private HomeView homeView;
    private Context context;
    private PaymentSetupRequest paymentSetupRequest;

    /**
     *
     * @param homeView
     * @param context
     * @param paymentSetupRequest
     */
    public HomePresenter(HomeView homeView, Context context, PaymentSetupRequest paymentSetupRequest) {
        this.homeView = homeView;
        this.context = context;
        this.paymentSetupRequest = paymentSetupRequest;
    }


    public void getDataPaymentRequest() {
        PaymentRequest paymentRequest = new PaymentRequest(context, new PaymentRequestListener() {
            /**
             *
             * @param paymentRequest
             * @param sdkToken
             * @param callback
             */
            @Override
            public void onPaymentDataRequested(@NonNull final PaymentRequest paymentRequest,
                                               @NonNull final String sdkToken,
                                               @NonNull final PaymentDataCallback callback) {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                headers.put(ApiKey.DEMO_API_KEY, ApiKey.X_DEMO_SEVER_API_KEY);
                AsyncHttpClient.post(ApiKey.URL_DEMO + ApiKey.SETUP,
                        headers, getSetupDataString(sdkToken), new HttpResponseCallback() {
                            @Override
                            public void onSuccess(final byte[] response) {
                                callback.completionWithPaymentData(response);
                                Log.e("Json", getSetupDataString(sdkToken).toString());
                            }

                            @Override
                            public void onFailure(final Throwable e) {
                                paymentRequest.cancel();

                            }
                        });
            }

            /**
             *
             * @param paymentRequest
             * @param result
             */
            @Override
            public void onPaymentResult(@NonNull PaymentRequest paymentRequest, @NonNull PaymentRequestResult result) {
                String resultString;
                if (result.isProcessed() && (
                        result.getPayment().getPaymentStatus() == Payment.PaymentStatus.AUTHORISED
                                || result.getPayment().getPaymentStatus() == Payment.PaymentStatus.RECEIVED)) {
                    resultString = result.getPayment().getPaymentStatus().toString();
                    verifyPayment(result.getPayment());
                    Toast.makeText(context, "Pay Success", Toast.LENGTH_SHORT).show();
                    homeView.showSuccess();
                } else {
                    resultString = result.getError().getMessage();
                    homeView.showError();
                }
                Intent intent = new Intent(context, PaymentResultActivity.class);
                intent.putExtra("Result", resultString);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
        paymentRequest.start();
    }

    /**
     *
     * @param token
     * @return
     */
    private String getSetupDataString(String token) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("merchantAccount", "TestMerchant");
            jsonObject.put("shopperLocale", paymentSetupRequest.getShopperLocale());
            jsonObject.put("token", token);
            jsonObject.put("returnUrl", "example-shopping-app://");
            jsonObject.put("countryCode", paymentSetupRequest.getCountryCode());
            final JSONObject amount = new JSONObject();
            amount.put("value", paymentSetupRequest.getAmount().getValue());
            amount.put("currency", paymentSetupRequest.getAmount().getCurrency());
            jsonObject.put("amount", amount);
            jsonObject.put("channel", "Android");
            jsonObject.put("reference", "Android Checkout SDK Payment: " + System.currentTimeMillis());
            jsonObject.put("shopperReference", "pepe@gmail.com");
        } catch (JSONException jsonException) {
            Log.e("TAG", "Setup failed", jsonException);
        }
        return jsonObject.toString();
    }


    /**
     *
     * @param payment
     */
    private void verifyPayment(final Payment payment) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("payload", payment.getPayload());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to verify payment.", Toast.LENGTH_LONG).show();
            return;
        }
        String verifyString = jsonObject.toString();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put(ApiKey.DEMO_API_KEY, ApiKey.X_DEMO_SEVER_API_KEY);
        AsyncHttpClient.post(ApiKey.URL_DEMO + ApiKey.VERIFY, headers, verifyString, new HttpResponseCallback() {
            String resultString = "";

            @Override
            public void onSuccess(final byte[] response) {
                try {
                    JSONObject jsonVerifyResponse = new JSONObject(new String(response, Charset.forName("UTF-8")));
                    String authResponse = jsonVerifyResponse.getString("authResponse");
                    if (authResponse.equalsIgnoreCase(payment.getPaymentStatus().toString())) {
                        resultString = "Payment is " + payment.getPaymentStatus().toString().toLowerCase() + " and verified. Reference: "
                                + jsonVerifyResponse.getString("merchantReference");
                    } else {
                        resultString = "Failed to verify payment.";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    resultString = "Failed to verify payment.";
                }
                Toast.makeText(context, resultString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(final Throwable e) {
                Toast.makeText(context, resultString, Toast.LENGTH_LONG).show();
            }
        });
    }

}
