package com.example.minhpq.paymentexample.ui.activity;

import com.adyen.core.PaymentRequest;
import com.adyen.core.interfaces.HttpResponseCallback;
import com.adyen.core.interfaces.PaymentDataCallback;
import com.adyen.core.interfaces.PaymentDetailsCallback;
import com.adyen.core.interfaces.PaymentMethodCallback;
import com.adyen.core.interfaces.PaymentRequestDetailsListener;
import com.adyen.core.interfaces.PaymentRequestListener;
import com.adyen.core.interfaces.UriCallback;
import com.adyen.core.models.Payment;
import com.adyen.core.models.PaymentMethod;
import com.adyen.core.models.PaymentRequestResult;
import com.adyen.core.models.paymentdetails.CVCOnlyPaymentDetails;
import com.adyen.core.models.paymentdetails.CreditCardPaymentDetails;
import com.adyen.core.models.paymentdetails.InputDetail;
import com.adyen.core.models.paymentdetails.InputDetailsUtil;
import com.adyen.core.models.paymentdetails.IssuerSelectionPaymentDetails;
import com.adyen.core.utils.AsyncHttpClient;
import com.adyen.ui.adapters.IssuerListAdapter;
import com.example.minhpq.paymentexample.R;
import com.example.minhpq.paymentexample.model.PaymentSetupRequest;
import com.example.minhpq.paymentexample.ui.fragment.CreditsCardFragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    PaymentSetupRequest paymentSetupRequest;

    private static final String TAG = MainActivity.class.getSimpleName();

    private String merchantServerUrl = "https://checkoutshopper-test.adyen.com/checkoutshopper/demoserver/";

    // Add the api secret key for your server here; you can retrieve this key from customer area.
    private String merchantApiSecretKey = "AQEqhmfuXNWTK0Qc+iSBgWs" +
            "+s8WYS4RYA4cbDTXQdryi1WqPyt94j6JPxAsLEMFdWw2+5HzctViMSCJMYAc=-4QP+f7b2n0re" +
            "+zPiv2nAra0ca3kwPAvT4W0kda672n8=-PW23W3Szz7XrGdnB\n";

    // Add the header key for merchant server api secret key here; e.g. "x-demo-server-api-key"
    private String merchantApiHeaderKeyForApiSecretKey = "01013B8667EE5CD5932B441CFA2481816B3EB3C5984B845803871B0D351BD27DB88A2B7A8DC8700" +
            "4E54E2B63325B90239E991A5DC6455FC0F3334C26B76A10C15D5B0DBEE47CDCB5588C48224C6007";

    private static final String SETUP = "setup";

    private static final String VERIFY = "verify";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaymentRequest paymentRequest = new PaymentRequest(this, paymentRequestListener);

    }

    private final PaymentRequestListener paymentRequestListener = new PaymentRequestListener() {
        @Override
        public void onPaymentDataRequested(@NonNull final com.adyen.core.PaymentRequest paymentRequest,
                                           @NonNull String sdkToken,
                                           @NonNull final PaymentDataCallback callback) {
            Log.d(TAG, "paymentRequestListener.onPaymentDataRequested()");
            final Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json; charset=UTF-8");
            headers.put(merchantApiHeaderKeyForApiSecretKey, merchantApiSecretKey);
            getSetupDataString("8215259353522870");
            AsyncHttpClient.post(merchantServerUrl + SETUP, headers, getSetupDataString(sdkToken), new HttpResponseCallback() {
                @Override
                public void onSuccess(final byte[] response) {
                    callback.completionWithPaymentData(response);
                }

                @Override
                public void onFailure(final Throwable e) {
                    Log.e(TAG, "HTTP Response problem: ", e);
                    paymentRequest.cancel();
                }
            });
        }

        @Override
        public void onPaymentResult(@NonNull PaymentRequest paymentRequest, @NonNull PaymentRequestResult result) {
            Log.d(TAG, "paymentRequestListener.onPaymentResult()");
            if (result.isProcessed() && (
                    result.getPayment().getPaymentStatus() == Payment.PaymentStatus.AUTHORISED
                            || result.getPayment().getPaymentStatus() == Payment.PaymentStatus.RECEIVED)) {
                Intent intent = new Intent(this, SuccessActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, PaymentResultActivity.class);
                startActivity(intent);
                finish();
            }

        }
    };

    @Override
    int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    void getBindView() {
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_proceedbutton)
    public void onViewClicked() {
    }

    private String getSetupDataString(final String token) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("merchantAccount", "TestMerchant"); // Not required when communicating with merchant server
            jsonObject.put("shopperLocale", paymentSetupRequest.getShopperLocale());
            jsonObject.put("token", token);
            jsonObject.put("returnUrl", "example-shopping-app://");
            jsonObject.put("countryCode", paymentSetupRequest.getCountryCode());
            final JSONObject amount = new JSONObject();
            amount.put("value", paymentSetupRequest.getAmount().getValue());
            amount.put("currency", paymentSetupRequest.getAmount().getCurrency());
            jsonObject.put("amount", amount);
            jsonObject.put("channel", "Android");
            jsonObject.put("reference", "Android Checkout SDK PaymentSetupRequest: " + System.currentTimeMillis());
            jsonObject.put("shopperReference", "example-customer@exampleprovider");
        } catch (final JSONException jsonException) {
            Log.e(TAG, "Setup failed", jsonException);
        }
        return jsonObject.toString();
    }

    public void verifyPayment(final Payment payment) {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("payload", payment.getPayload());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to verify payment.", Toast.LENGTH_LONG).show();
            return;
        }
        String verifyString = jsonObject.toString();

        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put(merchantApiHeaderKeyForApiSecretKey, merchantApiSecretKey);

        AsyncHttpClient.post(merchantServerUrl + VERIFY, headers, verifyString, new HttpResponseCallback() {
            String resultString = "";

            @Override
            public void onSuccess(final byte[] response) {
                try {
                    JSONObject jsonVerifyResponse = new JSONObject(new String(response, Charset.forName("UTF-8")));
                    String authResponse = jsonVerifyResponse.getString("authResponse");
                    if (authResponse.equalsIgnoreCase(payment.getPaymentStatus().toString())) {
                        resultString = "PaymentSetupRequest is " + payment.getPaymentStatus().toString().toLowerCase(Locale.getDefault()) + " and verified.";
                    } else {
                        resultString = "Failed to verify payment.";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    resultString = "Failed to verify payment.";
                }
                Toast.makeText(MainActivity.this, resultString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(final Throwable e) {
                Toast.makeText(MainActivity.this, resultString, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }
}
