package com.example.hellochart.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.example.hellochart.R;

/**
 * hellochart的jar包使用
 */
public class MyActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

    }

    /**
     * 跳转到简单折线图页面
     *
     * @param view
     */
    public void simpleLine(View view) {
        startActivity(new Intent(this, SimpleLineActivity.class).putExtra("title", "简单线条图"));
    }

    /**
     * 跳转到弧形线条图页面 ,其实只是在折线图基础上修改一个属性isCubic就可以了
     *
     * @param view
     */
    public void simpleLine2(View view) {
        startActivity(new Intent(this, SimpleLineActivity.class).putExtra("isCubic", true).putExtra("title", "弧形线条图"));
    }


    /**
     * 跳转到时间折线图形
     *
     * @param view
     */
    public void timeLine(View view) {
        startActivity(new Intent(this, TimeLineActivity.class).putExtra("title", "血压·一天时间线条图"));
    }


    /**
     * 跳转到一周的温度折线图形
     *
     * @param view
     */
    public void weekLine(View view) {
        startActivity(new Intent(this, WeekLineActivity.class).putExtra("title", "温度·一周时间线条图"));
    }

    /**
     * 跳转到一周的温度折线图形
     *
     * @param view
     */
    public void weekLine2(View view) {
        startActivity(new Intent(this, WeekLine2Activity.class).putExtra("title", "不均匀温度·一周时间线条图"));
    }


}
