package com.example.minhpq.paymentexample.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.adyen.core.PaymentRequest;
import com.adyen.core.interfaces.HttpResponseCallback;
import com.adyen.core.interfaces.PaymentDataCallback;
import com.adyen.core.interfaces.PaymentRequestListener;
import com.adyen.core.models.Amount;
import com.adyen.core.models.Payment;
import com.adyen.core.models.PaymentRequestResult;
import com.adyen.core.utils.AmountUtil;
import com.adyen.core.utils.AsyncHttpClient;
import com.example.minhpq.paymentexample.R;
import com.example.minhpq.paymentexample.model.PaymentSetupRequest;
import com.example.minhpq.paymentexample.presenter.HomePresenter;
import com.example.minhpq.paymentexample.ulti.ApiKey;
import com.example.minhpq.paymentexample.view.HomeView;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements HomeView {

    private static final String TAG = MainActivity.class.getSimpleName();


    @BindView(R.id.ed_orderamountentry)
    EditText edOrderAmountEntry;

    @BindView(R.id.ed_ordercurrencyentry)
    EditText edOrderCurrencyEntry;

    @BindView(R.id.ed_merchantreferenceentry)
    EditText edMerchantReferenceEntry;

    @BindView(R.id.ed_countryentry)
    EditText edCountryEntry;

    @BindView(R.id.ed_paymentdeadlineentry)
    EditText edPaymentDeadlineEntry;

    @BindView(R.id.ed_shopperlocaleentry)
    EditText edShopperLocaleEntry;

    @BindView(R.id.ed_shopperipentry)
    EditText edShopperIpEntry;

    @BindView(R.id.ed_merchantaccountentry)
    EditText edMerchantAccountEntry;

    @BindView(R.id.ed_returnurlentry)
    EditText edReturnUrlEntry;

    @BindView(R.id.btn_proceedbutton)
    Button btnProceedbutton;

    private HomePresenter homePresenter;

    private PaymentSetupRequest paymentSetupRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_proceedbutton)
    public void onViewClicked() {
        homePresenter = new HomePresenter(this, MainActivity.this, getPaymentRequest());
        homePresenter.getDataPaymentRequest();
    }

    @Override
    int getLayoutID() {
        return R.layout.activity_main;
    }

    @NonNull
    private PaymentSetupRequest getPaymentRequest() {
        Log.v(TAG, "buildPaymentRequest()");
        paymentSetupRequest = new PaymentSetupRequest();
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
        paymentSetupRequest.setMerchantAccount(edMerchantAccountEntry.getText().toString());
        paymentSetupRequest.setMerchantReference(edMerchantReferenceEntry.getText().toString());
        paymentSetupRequest.setPaymentDeadline(edPaymentDeadlineEntry.getText().toString());
        paymentSetupRequest.setReturnURL(edReturnUrlEntry.getText().toString());
        return paymentSetupRequest;
    }


    @Override
    public void showSuccess() {
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }
}
