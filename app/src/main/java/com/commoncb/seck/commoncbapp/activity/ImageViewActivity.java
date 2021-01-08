package com.commoncb.seck.commoncbapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.commoncb.seck.commoncbapp.DB.DbHelper;
import com.commoncb.seck.commoncbapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.commoncb.seck.commoncbapp.activity.NewCbActivity.LOCATION;
import static com.commoncb.seck.commoncbapp.activity.NewCbActivity.mNew_meters;

/**
 * Created by limbo on 2018/6/19.
 */

public class ImageViewActivity extends BaseActivity {
    @BindView(R.id.iv_pic)
    ImageView iv_pic;
    int id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        id = bundle.getInt("id");
        iv_pic.setImageBitmap(DbHelper.getPicByKH1(mNew_meters.get(LOCATION).getKH(),id));

    }


}
