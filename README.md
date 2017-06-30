#hellochart详细讲解（一）折线图

之前有介绍HelloChart图形绘制框架的使用，还有各种效果图：
http://blog.csdn.net/wenzhi20102321/article/details/73133718

之前没有对各个图形设计做详细介绍。
本文重点hellochart折线图的使用。
##效果：

![1](http://i.imgur.com/J2AolDK.gif)


##总览图：

![2](http://i.imgur.com/cdFAsXN.png)

一共显示5中基本图形，其实通过设置属性可以变成几十种图形样式！
第一种简单折线图，设置很少的属性：
##简单折线图

![3](http://i.imgur.com/VVtesq3.png)


##弧线线条

![4](http://i.imgur.com/GWRL5dp.png)

代码和简单折线的一样，就修改一个属性，就可以然线条变成弧形的



##一天的血压值和时间关系折线图


![5](http://i.imgur.com/oJIFTD1.png)


##一个星期的温度和时间关系折线图

![6](http://i.imgur.com/DHHJaul.png)


##一个星期的不规则温度刻度和时间关系折线图

![7](http://i.imgur.com/ZvWVg6D.png)


#折线图使用讲解

##（一）依赖hellochart，或导入jar包

依赖和jar包都可以上官网找，我的项目中也有jar包（在后面）。

##（二）布局文件

```
//这是我自己打包的jar包，包名和官网不一样，但是类名完全一样的
  <com.lwz.chart.hellocharts.view.LineChartView
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:id="@+id/chart"
            />


```

##（三）代码

//通过简单折线代码来学会使用

```
package com.example.hellochart.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.example.hellochart.R;
import com.lwz.chart.hellocharts.gesture.ZoomType;
import com.lwz.chart.hellocharts.listener.LineChartOnValueSelectListener;
import com.lwz.chart.hellocharts.model.*;
import com.lwz.chart.hellocharts.util.ChartUtils;
import com.lwz.chart.hellocharts.view.LineChartView;

import java.util.ArrayList;
import java.util.List;

/**
 * 简单折线线条的绘制
 */
public class SimpleLineActivity extends Activity {
    private LineChartView chart;        //显示线条的自定义View
    private LineChartData data;          // 折线图封装的数据类
    private int numberOfLines = 3;         //线条的数量
    private int maxNumberOfLines = 4;     //最大的线条数据
    private int numberOfPoints = 12;     //点的数量

    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints]; //二维数组，线的数量和点的数量

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simpleline_activity);
        initView();
        initData();
        initEvent();
    }


    private void initView() {
		//实例化
        chart = (LineChartView) findViewById(R.id.chart);
    }

    private void initData() {
        // Generate some random values.
        generateValues();   //设置四条线的值数据
        generateData();    //设置数据

        // Disable viewport recalculations, see toggleCubic() method for more info.
        chart.setViewportCalculationEnabled(false);

        chart.setZoomType(ZoomType.HORIZONTAL);//设置线条可以水平方向收缩，默认是全方位缩放
        resetViewport();   //设置折线图的显示大小
    }

    private void initEvent() {
        chart.setOnValueTouchListener(new ValueTouchListener());

    }

    /**
     * 图像显示大小
     */
    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(chart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        v.right = numberOfPoints - 1;
        chart.setMaximumViewport(v);
        chart.setCurrentViewport(v);
    }

    /**
     * 设置四条线条的数据
     */
    private void generateValues() {
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) Math.random() * 100f;
            }
        }
    }

    /**
     * 配置数据
     */
    private void generateData() {
		  //存放线条对象的集合
        List<Line> lines = new ArrayList<Line>();
  	 //把数据设置到线条上面去
        for (int i = 0; i < numberOfLines; ++i) {

            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }

            Line line = new Line(values);
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
                axisX.setTextColor(Color.BLACK);//设置x轴字体的颜色
                axisY.setTextColor(Color.BLACK);//设置y轴字体的颜色
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        chart.setLineChartData(data);

    }

    /**
     * 触摸监听类
     */
    private class ValueTouchListener implements LineChartOnValueSelectListener {

        @Override
        public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
            Toast.makeText(SimpleLineActivity.this, "Selected: " + value, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {


        }

    }

}




```

上面有些属性的意思看，上面的注解就差不多知道了。

不过简单线条中没有设置x轴，y轴的标尺的数值。

下面看看一周内温度变化的折线设置x，y轴坐标标尺的代码：



```
private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();   //x轴方向的坐标数据
private List<AxisValue> mAxisYValues = new ArrayList<AxisValue>();  //y轴方向的坐标数据
private List<TemperatureBean> listBlood = new ArrayList<TemperatureBean>();//数据
//TemperatureBean类里面包含一个时间字符串，一个最高温度和一个最低温度
	//设置一周内的时间和数据
   		listBlood.add(new TemperatureBean("2017-5-1", 35, 20));
        listBlood.add(new TemperatureBean("2017-5-2", 41, 28));
        listBlood.add(new TemperatureBean("2017-5-3", 30, 20));
        listBlood.add(new TemperatureBean("2017-5-4", 29, 15));

        listBlood.add(new TemperatureBean("2017-5-5", 25, 10));
        listBlood.add(new TemperatureBean("2017-5-6", 30, 20));
        listBlood.add(new TemperatureBean("2017-5-7", 25, 21));

   //设置x轴坐标 ，显示的是时间5-1,5-2.。。。
        for (int i = 0; i < 7; i++) {     
            //获取年月日中的月日
            String data = listBlood.get(i).getData();
            String[] split = data.split("-");
            data = split[1] + " - " + split[2];
            mAxisXValues.add(new AxisValue(i * (12 / 7)).setLabel(data));
        }

  //设置y轴坐标，显示的是数值0、1、2、3、4、5、6...50。。。
        for (int i = 0; i < 50; i++) {
            mAxisYValues.add(new AxisValue(i * 2).setLabel("" + i));
        }


```


//最后在配置线条数据之后，设置把x，y轴的数据放进去就可以了


```
   if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("时间");//x轴坐标显示的标题
                axisY.setName("温度/C");//y轴坐标显示的标题
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
			//data.setAxisXTop();      //显示在x轴上方
            data.setAxisYLeft(axisY);   //显示在y轴的左边，也可以设置在右边

        }


```

关于x，y轴的比例和线条的比例这个问题，我大概总结了一下，

先看看一周内温度设置线条的数据主要代码：

```
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
           。。。
            lines.add(line);
        }
        data = new LineChartData(lines);
        chart.setLineChartData(data);

    }


```

##比例的探讨，其实很多都是参考默认显示的那个图形！

先说x，y轴的事，都是存放AxisValue对象来显示

new AxisValue(i * (12 / 7)).setLabel("data")//创建AxisValue对象要传入一个数值，也可以设置轴上对应的文字


###x轴详解：

默认是分成12段的，所以你看到简单线条中，线条的点设置12个，刚好显示完全，并且不用设置比例，

如果设置13个点时，若没改变比例1，将会不显示后面一个点！

设计一周内温度时，x坐标显示七个日期，所以每段都是12/7（其实是12/6，1到7实际是分成6段，我故意让右边留一段空隙），每项逐增就可以实现效果。


###y轴详解：

默认是分成100端，可以看到简单线条的显示数值是0到100，中间部分它会智能按比例隐藏，放大后就会显示。

我上面温度显示的刻度是0到50，所以数组乘以2，就变成0到100的效果。

可以看到最后一个例子是Y轴显示的刻度不一样的，其实就是改变AxisValue对象的数值和设置的标签值就可以了


###折线的点数值也是x分成12，y分成100.

PointValue的两个参数值，一个是距离y轴的长度距离，另一个是距离x轴长度距离

pointValue(x1,y1);

pointValue(x2,y2);

...
//这里的x1，y1要计算好比例在放进去！



多说不宜！有些人可能还是需要一些时间去消化！

##一般的设计折线图，要一步一步来，

###要么先显示x，y轴的标尺，按照他们的比例再显示折线图形。

###要么先显示折线图形，在把折线图形的比例用到x，y坐标的标尺中。

###如果两边一起设计，你的思路会很乱，设计效果一直不理想！


也可以先运行下我的程序，我的代码看看效果，再替换下数据。
