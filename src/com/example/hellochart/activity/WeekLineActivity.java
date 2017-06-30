package com.example.hellochart.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hellochart.R;
import com.example.hellochart.bean.TemperatureBean;
import com.lwz.chart.hellocharts.gesture.ZoomType;
import com.lwz.chart.hellocharts.listener.LineChartOnValueSelectListener;
import com.lwz.chart.hellocharts.model.*;
import com.lwz.chart.hellocharts.util.ChartUtils;
import com.lwz.chart.hellocharts.view.LineChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * 一个星期内的气温数据的折线图
 * <p/>
 * 主要是时间数据的处理，x轴时间的显示和y轴数值的显示
 */
public class WeekLineActivity extends Activity {
    private LineChartView chart;        //显示线条的自定义View
    private LineChartData data;          // 折线图封装的数据类
    private int numberOfLines = 2;         //线条的数量  ，一条最高温度，一条最低温度
    private int maxNumberOfLines = 4;     //最大的线条数据
//    private int numberOfPoints = 12;     //点的数量

    //    private float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];         //二维数组，线的数量和点的数量
    private List<TemperatureBean> listBlood = new ArrayList<TemperatureBean>();//数据

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
        v.right = listBlood.size(); //  listBlood.size() - 1//如何解释
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    /**
     * 添加数据,一天的时间和数值数据
     */
    private void generateValues() {
        listBlood.add(new TemperatureBean("2017-5-1", 35, 20));
        listBlood.add(new TemperatureBean("2017-5-2", 41, 28));
        listBlood.add(new TemperatureBean("2017-5-3", 30, 20));
        listBlood.add(new TemperatureBean("2017-5-4", 29, 15));

        listBlood.add(new TemperatureBean("2017-5-5", 25, 10));
        listBlood.add(new TemperatureBean("2017-5-6", 30, 20));
        listBlood.add(new TemperatureBean("2017-5-7", 25, 21));

        //设置x轴坐标 ，显示的是时间5-1,5-2.。。。
        mAxisXValues.clear();
        for (int i = 0; i < 7; i++) {
            //获取年月日中的月日
            String data = listBlood.get(i).getData();
            String[] split = data.split("-");
            data = split[1] + " - " + split[2];
            mAxisXValues.add(new AxisValue(i * (12 / 7)).setLabel(data));
        }

        //设置y轴坐标，显示的是数值0、1、2、3、4、5、6...50。。。
        mAxisYValues.clear();
        for (int i = 0; i < 50; i++) {
            mAxisYValues.add(new AxisValue(i * 2).setLabel("" + i));
        }


        //获取Y轴距离
        for (int i = 0; i < listBlood.size(); i++) {
            float apart = i;//得到的拒Y轴的距离
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
                //i=0是最高温度的线，i=1,是最低温度的线
                if (i == 0) {
                    values.add(new PointValue(distanceList.get(j), listBlood.get(j).getMaxTemp() * 2f));
                } else if (i == 1) {
                    values.add(new PointValue(distanceList.get(j), listBlood.get(j).getMinTemp() * 2f));
                }

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
                axisY.setName("温度/C");
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
            Toast.makeText(WeekLineActivity.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {


        }

    }

}
