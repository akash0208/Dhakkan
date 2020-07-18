package com.shopby.dhakkan.activity;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

import com.shopby.dhakkan.R;

/**
 * Created by Nasir on 7/18/17.
 */

public class StripeActivity extends AppCompatActivity {

    private Activity mActivity;

    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initVariables();
        initView();

        initListener();
    }

    private void initVariables() {
        mActivity = StripeActivity.this;
    }

    private void initView() {
        setContentView(R.layout.activity_gateway_stripe);
//        cardInputWidget = findViewById(R.id.card_input_widget);
        btnSubmit = findViewById(R.id.btnSubmit);
    }

    private void initListener() {
        btnSubmit.setOnClickListener(v -> initFunctionality());
    }

    private void initFunctionality() {


//        Stripe stripe = new Stripe(mContext);
//        Card card = cardInputWidget.getCard();
//        assert card != null;
//        if (card.validateCard()){
//            stripe.createToken(cardInputWidget.getCard(), getString(R.string.stripe_publish_key), new TokenCallback() {
//                public void onSuccess(Token token) {
//
//                    Intent returnIntent = new Intent();
//                    returnIntent.putExtra("stripe_token", token.getId());
//                    setResult(Activity.RESULT_OK, returnIntent);
//                    finish();
//
//                    DialogUtils.dismissProgressDialog(progressDialog);
//                }
//
//                public void onError(Exception error) {
//
//                    DialogUtils.dismissProgressDialog(progressDialog);
//
//                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_LONG).show();
//
//                    Intent returnIntent = new Intent();
//                    setResult(Activity.RESULT_CANCELED, returnIntent);
//                    finish();
//
//                }
//            });
//        }
    }
}
