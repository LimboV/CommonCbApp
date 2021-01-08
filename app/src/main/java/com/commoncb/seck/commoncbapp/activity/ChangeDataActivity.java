package com.commoncb.seck.commoncbapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.commoncb.seck.commoncbapp.R;
import com.commoncb.seck.commoncbapp.utils.HzyUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by limbo on 2018/6/22.
 */
public class ChangeDataActivity extends BaseActivity {
    /**
     * 键盘
     */
    @BindView(R.id.btn_showInput)
    Button btn_showInput;
    @BindView(R.id.btn_0)
    Button btn_0;
    @BindView(R.id.btn_1)
    Button btn_1;
    @BindView(R.id.btn_2)
    Button btn_2;
    @BindView(R.id.btn_3)
    Button btn_3;
    @BindView(R.id.btn_4)
    Button btn_4;
    @BindView(R.id.btn_5)
    Button btn_5;
    @BindView(R.id.btn_6)
    Button btn_6;
    @BindView(R.id.btn_7)
    Button btn_7;
    @BindView(R.id.btn_8)
    Button btn_8;
    @BindView(R.id.btn_9)
    Button btn_9;
    @BindView(R.id.btn_delete)
    Button btn_delete;

    @BindView(R.id.btn_save)
    Button btn_save;
    @BindView(R.id.et_data)
    EditText et_data;

    boolean AJFlag = true;//按键是否显示 false - 不显示

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changedata);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        String data = bundle.getString("data");
        et_data.setText(data);

        String h = et_data.getText().toString().trim();
        if (h.equals("0") ) {
            et_data.setText("");
        }


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                String h = et_data.getText().toString().trim();
                if (!HzyUtils.isEmpty(h)) {
                    h = Integer.parseInt(h) + "";
                } else {
                    h = "0";
                }
                bundle.putString("result", h);
                resultIntent.putExtras(bundle);
                ChangeDataActivity.this.setResult(RESULT_OK, resultIntent);
                ChangeDataActivity.this.finish();
            }
        });

        /**
         * 手动按键
         */
        btn_showInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AJFlag = !AJFlag;
                if (AJFlag) {
                    btn_showInput.setText("隐藏按键");
                    btn_0.setVisibility(View.VISIBLE);
                    btn_1.setVisibility(View.VISIBLE);
                    btn_2.setVisibility(View.VISIBLE);
                    btn_3.setVisibility(View.VISIBLE);
                    btn_4.setVisibility(View.VISIBLE);
                    btn_5.setVisibility(View.VISIBLE);
                    btn_6.setVisibility(View.VISIBLE);
                    btn_7.setVisibility(View.VISIBLE);
                    btn_8.setVisibility(View.VISIBLE);
                    btn_9.setVisibility(View.VISIBLE);
                    btn_delete.setVisibility(View.VISIBLE);
                } else {
                    btn_showInput.setText("显示按键");
                    btn_0.setVisibility(View.GONE);
                    btn_1.setVisibility(View.GONE);
                    btn_2.setVisibility(View.GONE);
                    btn_3.setVisibility(View.GONE);
                    btn_4.setVisibility(View.GONE);
                    btn_5.setVisibility(View.GONE);
                    btn_6.setVisibility(View.GONE);
                    btn_7.setVisibility(View.GONE);
                    btn_8.setVisibility(View.GONE);
                    btn_9.setVisibility(View.GONE);
                    btn_delete.setVisibility(View.GONE);
                }

            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = et_data.getText().toString().trim();
                if (x.length() != 0) {
                    et_data.setText(x.substring(0, x.length() - 1));
                }
            }
        });
        btn_delete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                et_data.setText("");

                return false;
            }
        });
        btn_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.append("0");
            }
        });
        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.append("1");
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.append("2");
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.append("3");
            }
        });
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.append("4");
            }
        });
        btn_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.append("5");
            }
        });
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.append("6");
            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.append("7");
            }
        });
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.append("8");
            }
        });
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_data.append("9");
            }
        });
    }
}
