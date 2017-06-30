package com.example.hellochart.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hellochart.R;
import com.example.hellochart.TimeUtil;
import com.example.hellochart.bean.BloodPressureBean;
import com.lwz.chart.hellocharts.gesture.ZoomType;
import com.lwz.chart.hellocharts.listener.LineChartOnValueSelectListener;
import com.lwz.chart.hellocharts.model.*;
import com.lwz.chart.hellocharts.util.ChartUtils;
import com.lwz.chart.hellocharts.view.LineChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * 时间数据的折线图
 * <p/>
 * 主要是时间数据的处理，x轴时间的显示和y轴数值的显示
 */
public class TimeLineActivity extends Activity {
    private LineChartView chart;        //显示线条的自定义View
    private LineChartData data;          // 折线图封装的数据类
    private int numberOfLines = 1;         //线条的数量
    private int maxNumberOfLines = 4;     //最大的线条数据
//    private int numberOfPoints = 12;     //点的数量

    //    private float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];         //二维数组，线的数量和点的数量
    private List<BloodPressureBean> listBlood = new ArrayList<BloodPressureBean>();//数据

    private boolean hasAxes = true;       //是否有轴，x和y轴
    private boolean hasAxesNames = true;   //是否有轴的名字
    private boolean hasLines = true;       //是否有线（点和点连接的线）
    private boolean hasPoints = true;       //是否有点（每个值的点）
    private ValueShape shape = ValueShape.CIRCLE;    //点显示的形式，圆形，正方向，菱形
    private boolean isFilled = false;                //是否是填充
    private boolean hasLabels = false;               //每个点是否有名字
    private boolean isCubic = false;                 //是否是立方的，线条是直线还是弧线
    private boolean hasLabelForSelected = false;       //每个点是否可以选择（点击效果）
    private boolean pointsHaveDifferentColor;           //线条的颜色变换
    private boolean hasGradientToTransparent = false;      //是否有梯度的透明

    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();   //x轴方向的坐标数据
    private List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();            //y轴方向的坐标数据

    private List<Float> distanceList = new ArrayList<Float>();
    private static final long timepre = 2 * 60 * 60 * 1000;//两个小时为一个时间单位,横坐标好像只能容纳12的长度，超过就不显示

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simpleline_activity);
        String title = getIntent().getStringExtra("title");
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText("" + title);
        initView();
        initData();
        initEvent();

    }


    private void initView() {
        chart = (LineChartView) findViewById(R.id.chart);

    }

    private void initData() {
        // Generate some random values.
        generateValues();   //设置四条线的值数据
        generateData();    //设置数据

        // Disable viewport recalculations, see toggleCubic() method for more info.
        chart.setViewportCalculationEnabled(false);

        chart.setZoomType(ZoomType.HORIZONTAL);//设置线条可以水平方向收缩
        resetViewport();   //设置折线图的显示大小
    }

    private void initEvent() {
        chart.setOnValueTouchListener(new ValueTouchListener());

    }


    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        v.right = 12; //  listBlood.size() - 1//如何解释
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    /**
     * 添加数据,一天的时间和数值数据
     */
    private void generateValues() {
        listBlood.add(new BloodPressureBean("2017-5-1 12:10:5", 120));
        listBlood.add(new BloodPressureBean("2017-5-1 12:50:5", 140));
        listBlood.add(new BloodPressureBean("2017-5-1 13:50:1", 110));
        listBlood.add(new BloodPressureBean("2017-5-1 14:20:3", 80));

        listBlood.add(new BloodPressureBean("2017-5-1 16:10:5", 150));
        listBlood.add(new BloodPressureBean("2017-5-1 17:50:5", 140));
        listBlood.add(new BloodPressureBean("2017-5-1 18:10:1", 120));
        listBlood.add(new BloodPressureBean("2017-5-1 19:20:3", 180));

        listBlood.add(new BloodPressureBean("2017-5-1 21:10:5", 120));
        listBlood.add(new BloodPressureBean("2017-5-1 23:20:5", 60));
        listBlood.add(new BloodPressureBean("2017-5-2 1:10:1", 160));
        listBlood.add(new BloodPressureBean("2017-5-2 3:20:3", 110));
        listBlood.add(new BloodPressureBean("2017-5-2 5:10:3", 190));
        listBlood.add(new BloodPressureBean("2017-5-2 8:10:3", 130));

        //设置x轴坐标 ，显示的是时间1：00，2：00.。。。
        mAxisXValues.clear();
        int mClockNumberLength = 24;//显示的x轴的时间点的总数量
        int startClock = TimeUtil.getTimeInt(listBlood.get(0).getTime(), "H");   //开始的时间的点数，这里使用到一个时间工具类
        for (int i = 0; i < mClockNumberLength; i++) {      //mClockNumberLength

            mAxisXValues.add(new AxisValue(i / 2f).setLabel(startClock + ":00"));
            startClock++;
            if (startClock >= 24) {
                startClock = 0;
            }
        }

        //设置y轴坐标，显示的是数值0，30，60.。。。
        mAxisYValues.clear();
        for (int i = 0; i < 8; i++) {
            int lengthY = 30 * i;
            mAxisYValues.add(new AxisValue(lengthY / 2).setLabel("" + lengthY));
        }


        //获取距离
        for (int i = 0; i < listBlood.size(); i++) {
            long disY0 = TimeUtil.getTimeLong("yyyy-MM-dd HH", listBlood.get(0).getTime());//第一个小时数的距离
            long disY = TimeUtil.getTimeLong("yyyy-MM-dd HH:mm:ss", listBlood.get(i).getTime());
            float apart = (disY - disY0) / (float) timepre;//得到的拒Y轴的距离
            distanceList.add(apart);
        }


    }

    private void generateData() {

        //存放线条对象的集合
        List<Line> lines = new ArrayList<Line>();

        //把数据设置到线条上面去
        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < listBlood.size(); ++j) {
                //PointValue的两个参数值，一个是距离y轴的长度距离，另一个是距离x轴长度距离
                values.add(new PointValue(distanceList.get(j), listBlood.get(j).getValue() / 2f));  //y的值除以2，因为默认在y上显示的是0到100，0到200的数值除以2，就相当于0到100.
            }

            Line line = new Line(values);
            //设置线条的基本属性
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            line.setHasGradientToTransparent(hasGradientToTransparent);
            if (pointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }

        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("时间");
                axisY.setName("血压");
            }

            //对x轴，数据和属性的设置
            axisX.setTextSize(8);//设置字体的大小
            axisX.setHasTiltedLabels(false);//x坐标轴字体是斜的显示还是直的，true表示斜的
            axisX.setTextColor(Color.BLACK);//设置字体颜色
            axisX.setHasLines(true);//x轴的分割线
            axisX.setValues(mAxisXValues); //设置x轴各个坐标点名称

            //对Y轴 ，数据和属性的设置
            axisY.setTextSize(10);
            axisY.setHasTiltedLabels(false);//true表示斜的
            axisY.setTextColor(Color.BLACK);//设置字体颜色
            axisY.setValues(mAxisYValues); //设置x轴各个坐标点名称


            data.setAxisXBottom(axisX);//x轴坐标线的文字，显示在x轴下方
//            data.setAxisXTop();      //显示在x轴上方
            data.setAxisYLeft(axisY);   //显示在y轴的左边

        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);

    }

    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(TimeLineActivity.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {


        }

    }

}
