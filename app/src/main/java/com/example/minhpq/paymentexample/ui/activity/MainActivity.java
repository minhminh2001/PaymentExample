package com.example.minhpq.paymentexample.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.adyen.core.PaymentRequest;
import com.adyen.core.interfaces.HttpResponseCallback;
import com.adyen.core.interfaces.PaymentDataCallback;
import com.adyen.core.interfaces.PaymentRequestListener;
import com.adyen.core.models.Payment;
import com.adyen.core.models.PaymentRequestResult;
import com.adyen.core.utils.AsyncHttpClient;
import com.example.minhpq.paymentexample.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private Button btnProceedbutton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnProceedbutton=(Button)findViewById(R.id.btn_proceedbutton);
        btnProceedbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               PaymentRequest paymentRequest = new PaymentRequest(MainActivity.this, new PaymentRequestListener() {
                    @Override
                    public void onPaymentDataRequested(@NonNull final PaymentRequest paymentRequest,
                                                       @NonNull String sdkToken,
                                                       @NonNull final PaymentDataCallback callback) {
                        final Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=UTF-8");

                        headers.put("0101358667EE5CD5932B441CFA2481816B3EB3C5984B84580387074C6045D2359A8D694D8AE4593D91B6734C0F7EAD0E63F9E" +
                                "843E566BFC910C15D5B0DBEE47CDCB5588C48224C6007", "AQEnhmfuXNWTK0Qc+iSBgWs+s8WYS4RYA4eN8auBXM5pW1Wk29x5R/EvEMFdWw2+5HzctViMSCJMYAc=-2yC" +
                                "dFUS0245kS9K0k5BP9VvHEfc4BNSHc5D+5vTimDQ=-zV8XmCeAp5MKJf2W");

                        final JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("token", sdkToken);
                            jsonObject.put("returnUrl", "app://checkout");
                            jsonObject.put("countryCode", "NL");
                            jsonObject.put("shopperLocale", "nl_NL");
                            final JSONObject amount = new JSONObject();
                            amount.put("value", 100);
                            amount.put("currency", "EUR");
                            jsonObject.put("amount", amount);
                            jsonObject.put("shopperReference", "example.merchant@adyen.com");
                            jsonObject.put("channel", "android");
                            jsonObject.put("reference", "test-payment");
                        } catch (final JSONException jsonException) {
                            Log.e("Unexpected error", "Setup failed");
                        }
                        AsyncHttpClient.post("https://checkoutshopper-test.adyen.com/checkoutshopper/demoserver/setup",
                                headers, jsonObject.toString(), new HttpResponseCallback() {
                                    @Override
                                    public void onSuccess(final byte[] response) {
                                        callback.completionWithPaymentData(response);
                                        Log.e("response",response.toString());
                                    }
                                    @Override
                                    public void onFailure(final Throwable e) {
                                        paymentRequest.cancel();
                                    }
                                });
                    }

                    @Override
                    public void onPaymentResult(@NonNull PaymentRequest paymentRequest, @NonNull PaymentRequestResult result) {
                        if (result.isProcessed() && (
                                result.getPayment().getPaymentStatus() == Payment.PaymentStatus.AUTHORISED
                                        || result.getPayment().getPaymentStatus() == Payment.PaymentStatus.RECEIVED)) {
                            Intent intent  = new Intent(MainActivity.this, PaymentResultActivity.class);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Intent intent  = new Intent(MainActivity.this, PaymentResultActivity.class);
                            Toast.makeText(MainActivity.this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                paymentRequest.start();
            }
        });
    }


    @Override
    int getLayoutID() {
        return R.layout.activity_main;
    }

}
