package com.commoncb.seck.commoncbapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.commoncb.seck.commoncbapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by limbo on 2018/6/21.
 */

public class FunctionActivity extends BaseActivity {
    @BindView(R.id.btn_cb)
    Button btn_cb;

    @BindView(R.id.btn_irdaCb)
    Button btn_irdaCb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        final String XH = bundle.getString("XH");
        btn_cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FunctionActivity.this,NewCbActivity.class);
                i.putExtra("XH", XH);
                startActivity(i);

            }
        });

        btn_irdaCb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FunctionActivity.this,IrDACbActivity.class);
                i.putExtra("XH", XH);
                startActivity(i);
            }
        });

    }


}
