package com.example.minhpq.paymentexample.ui.activity;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adyen.core.PaymentRequest;
import com.adyen.core.interfaces.HttpResponseCallback;
import com.adyen.core.interfaces.PaymentDataCallback;
import com.adyen.core.interfaces.PaymentDetailsCallback;
import com.adyen.core.interfaces.PaymentMethodCallback;
import com.adyen.core.interfaces.PaymentRequestDetailsListener;
import com.adyen.core.interfaces.PaymentRequestListener;
import com.adyen.core.interfaces.UriCallback;
import com.adyen.core.models.Amount;
import com.adyen.core.models.Payment;
import com.adyen.core.models.PaymentMethod;
import com.adyen.core.models.PaymentRequestResult;
import com.adyen.core.models.paymentdetails.CVCOnlyPaymentDetails;
import com.adyen.core.models.paymentdetails.CreditCardPaymentDetails;
import com.adyen.core.models.paymentdetails.InputDetail;
import com.adyen.core.models.paymentdetails.InputDetailsUtil;
import com.adyen.core.models.paymentdetails.IssuerSelectionPaymentDetails;
import com.adyen.core.utils.AmountUtil;
import com.adyen.core.utils.AsyncHttpClient;
import com.adyen.ui.adapters.IssuerListAdapter;
import com.adyen.ui.fragments.CreditCardFragment;
import com.adyen.ui.fragments.PaymentMethodSelectionFragment;
import com.example.minhpq.paymentexample.R;
import com.example.minhpq.paymentexample.model.PaymentSetupRequest;
import com.example.minhpq.paymentexample.ui.fragment.CreditsCardFragment;
import com.example.minhpq.paymentexample.ulti.ApiKey;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnProceedbutton;
    private EditText edOrderAmountEntry, edOrderCurrencyEntry,
            edMerchantReferenceEntry, edCountryEntry, edPaymentDeadlineEntry,
            edShopperLocaleEntry, edShopperIpEntry, edMerchantAccountEntry, edReturnUrlEntry;
    private PaymentRequestDetailsListener paymentRequestDetailsListener;
    private PaymentMethodCallback paymentMethodCallback;
    private UriCallback uriCallback;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnProceedbutton=(Button)findViewById(R.id.btn_proceedbutton);
        edOrderAmountEntry = (EditText) findViewById(R.id.ed_orderamountentry);
        edOrderCurrencyEntry = (EditText) findViewById(R.id.ed_ordercurrencyentry);
        edMerchantReferenceEntry = (EditText) findViewById(R.id.ed_merchantreferenceentry);
        edCountryEntry = (EditText) findViewById(R.id.ed_countryentry);
        edPaymentDeadlineEntry = (EditText) findViewById(R.id.ed_paymentdeadlineentry);
        edShopperLocaleEntry = (EditText) findViewById(R.id.ed_shopperlocaleentry);
        edShopperIpEntry = (EditText) findViewById(R.id.ed_shopperipentry);
        edMerchantAccountEntry = (EditText) findViewById(R.id.ed_merchantaccountentry);
        edReturnUrlEntry = (EditText) findViewById(R.id.ed_returnurlentry);
        btnProceedbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentRequest paymentRequest = new PaymentRequest(MainActivity.this, new PaymentRequestListener() {
                    /**
                     *
                     * @param paymentRequest
                     * @param sdkToken
                     * @param callback
                     */
                    @Override
                    public void onPaymentDataRequested(@NonNull final PaymentRequest paymentRequest,
                                                       @NonNull String sdkToken,
                                                       @NonNull final PaymentDataCallback callback) {
                        final Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json; charset=UTF-8");
                        headers.put(ApiKey.X_DEMO_SEVER_API_KEY, ApiKey.CHECKOUT_API_KEY);
                        AsyncHttpClient.post(ApiKey.URL_DEMO + ApiKey.SETUP,
                                headers,getSetupDataString(sdkToken), new HttpResponseCallback() {
                                    @Override
                                    public void onSuccess(final byte[] response) {
                                        callback.completionWithPaymentData(response);
                                        Log.e("response", response.toString());

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
                        Fragment fragment;
                        if (result.isProcessed() && (
                                result.getPayment().getPaymentStatus() == Payment.PaymentStatus.AUTHORISED
                                        || result.getPayment().getPaymentStatus() == Payment.PaymentStatus.RECEIVED)) {
                            resultString = result.getPayment().getPaymentStatus().toString();
                            verifyPayment(result.getPayment());


                        } else {
                            resultString = result.getError().getMessage();
                        }
//                        final Intent intent = new Intent(getApplicationContext(), PaymentResultActivity.class);
//                        intent.putExtra("Result", resultString);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);

                    }
                });
                paymentRequest.start();
            }
        });
        paymentRequestDetailsListener=new PaymentRequestDetailsListener() {
            @Override
            public void onPaymentMethodSelectionRequired(@NonNull PaymentRequest paymentRequest,
                                                         @NonNull List<PaymentMethod> preferredPaymentMethods,
                                                         @NonNull List<PaymentMethod> availablePaymentMethods,
                                                         @NonNull PaymentMethodCallback callback) {

                paymentMethodCallback = callback;
                preferredPaymentMethods.clear();
                preferredPaymentMethods.addAll(preferredPaymentMethods);
                availablePaymentMethods.clear();
                availablePaymentMethods.addAll(availablePaymentMethods);
                final PaymentMethodSelectionFragment paymentMethodSelectionFragment
                        = new PaymentMethodSelectionFragment();
                getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                        paymentMethodSelectionFragment).addToBackStack(null).commitAllowingStateLoss();
            }

            @Override
            public void onRedirectRequired(@NonNull PaymentRequest paymentRequest,
                                           @NonNull String redirectUrl,
                                           @NonNull UriCallback returnUriCallback) {
                Log.d(TAG, "paymentRequestDetailsListener.onRedirectRequired(): " + redirectUrl);
                uriCallback = returnUriCallback;
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(MainActivity.this, Uri.parse(redirectUrl));
            }

            @Override
            public void onPaymentDetailsRequired(@NonNull PaymentRequest paymentRequest,
                                                 @NonNull final Collection<InputDetail> inputDetails,
                                                 @NonNull final PaymentDetailsCallback callback) {
                Log.d(TAG, "paymentRequestDetailsListener.onPaymentDetailsRequired()");
                final String paymentMethodType = paymentRequest.getPaymentMethod().getType();

                if (PaymentMethod.Type.CARD.equals(paymentMethodType)) {

                    final CreditsCardFragment creditsCardFragment = new CreditsCardFragment();
                    final Bundle bundle = new Bundle();
                    final boolean isOneClick = InputDetailsUtil.containsKey(inputDetails, "cardDetails.cvc");
                    if (isOneClick) {
                        bundle.putBoolean("oneClick", true);
                    }
                    creditsCardFragment.setCreditCardInfoListener(new CreditsCardFragment.CreditCardInfoListener() {
                        @Override
                        public void onCreditCardInfoProvided(String creditCardInfo) {
                            if (isOneClick) {
                                CVCOnlyPaymentDetails cvcOnlyPaymentDetails = new CVCOnlyPaymentDetails(inputDetails);
                                cvcOnlyPaymentDetails.fillCvc(creditCardInfo);
                                callback.completionWithPaymentDetails(cvcOnlyPaymentDetails);

                            } else {
                                CreditCardPaymentDetails creditCardPaymentDetails = new CreditCardPaymentDetails(inputDetails);
                                creditCardPaymentDetails.fillCardToken(creditCardInfo);
                                callback.completionWithPaymentDetails(creditCardPaymentDetails);
                            }
                        }
                    });
                    bundle.putString("public_key", paymentRequest.getPublicKey());
                    bundle.putString("generation_time", paymentRequest.getGenerationTime());
                    creditsCardFragment.setArguments(bundle);

                    getSupportFragmentManager().beginTransaction().replace(android.R.id.content,
                            creditsCardFragment).addToBackStack(null).commitAllowingStateLoss();

                } else if (PaymentMethod.Type.IDEAL.equals(paymentMethodType)) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    final List<InputDetail.Item> issuers = InputDetailsUtil.getInputDetail(inputDetails, "idealIssuer").getItems();
                    final IssuerListAdapter issuerListAdapter = new IssuerListAdapter(MainActivity.this, issuers);
                    alertDialog.setSingleChoiceItems(issuerListAdapter, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(@NonNull final DialogInterface dialogInterface, final int i) {
                            IssuerSelectionPaymentDetails issuerSelectionPaymentDetails = new IssuerSelectionPaymentDetails(inputDetails);
                            issuerSelectionPaymentDetails.fillIssuer(issuers.get(i));
                            dialogInterface.dismiss();
                            callback.completionWithPaymentDetails(issuerSelectionPaymentDetails);
                        }
                    });
                    alertDialog.show();

                } else {
                    String message = "UI for " + paymentMethodType + " has not been implemented.";
                    Log.w(TAG, message);
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    paymentRequest.cancel();
                }
            }
        };
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
            Toast.makeText(this, "Failed to verify payment.", Toast.LENGTH_LONG).show();
            return;
        }
        String verifyString = jsonObject.toString();

        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        headers.put(ApiKey.X_DEMO_SEVER_API_KEY, ApiKey.CHECKOUT_API_KEY);

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
                Toast.makeText(MainActivity.this, resultString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(final Throwable e) {
                Toast.makeText(MainActivity.this, resultString, Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    int getLayoutID() {
        return R.layout.activity_main;
    }
    @NonNull
    private PaymentSetupRequest getPaymentRequest(){
        Log.v(TAG, "buildPaymentRequest()");
        PaymentSetupRequest paymentSetupRequest = new PaymentSetupRequest();
        String amountValueString = edOrderAmountEntry.getText().toString();
        String amountCurrencyString = edOrderCurrencyEntry.getText().toString();
        try {
            paymentSetupRequest.setAmount(new Amount(AmountUtil.parseMajorAmount(amountCurrencyString, amountValueString),
                    amountCurrencyString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        paymentSetupRequest.setCountryCode(edCountryEntry.getText().toString());
        paymentSetupRequest.setShopperLocale(edShopperLocaleEntry.getText().toString());
        paymentSetupRequest.setShopperIP(edShopperIpEntry.getText().toString());
        paymentSetupRequest.setMerchantAccount(edMerchantAccountEntry.getText()
                .toString());
        paymentSetupRequest.setMerchantReference(edMerchantReferenceEntry.getText()
                .toString());
        paymentSetupRequest.setPaymentDeadline(edPaymentDeadlineEntry.getText()
                .toString());
        paymentSetupRequest.setReturnURL(edReturnUrlEntry.getText().toString());

        return paymentSetupRequest;
    }

    /**
     *
     * @param token
     * @return
     */
    private String getSetupDataString( String token) {
         JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("merchantAccount", "TestMerchant");
            jsonObject.put("shopperLocale",  getPaymentRequest().getShopperLocale());
            jsonObject.put("token", token);
            jsonObject.put("returnUrl", "example-shopping-app://");
            jsonObject.put("countryCode", getPaymentRequest().getCountryCode());
            final JSONObject amount = new JSONObject();
            amount.put("value",  getPaymentRequest().getAmount().getValue());
            amount.put("currency",  getPaymentRequest().getAmount().getCurrency());
            jsonObject.put("amount", amount);
            jsonObject.put("channel", "Android");
            jsonObject.put("reference", "Android Checkout SDK Payment: " + System.currentTimeMillis());
            jsonObject.put("shopperReference", "example-customer@exampleprovider");
        } catch ( JSONException jsonException) {
            Log.e(TAG, "Setup failed", jsonException);
        }
        return jsonObject.toString();
    }
}
