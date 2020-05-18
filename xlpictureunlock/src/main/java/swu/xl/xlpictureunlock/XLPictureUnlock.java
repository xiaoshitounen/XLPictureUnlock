package swu.xl.xlpictureunlock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class XLPictureUnlock extends RelativeLayout {

    //日志
    private static final String TAG = "XLPictureUnlock";

    //正常状态的图片资源
    private int normal_image_id = R.drawable.normal;
    //选中状态的图片资源
    private int select_image_id = R.drawable.selected;

    //点的大小
    private int dot_size = 45;

    //线条的颜色
    private int line_color = Color.parseColor("#99CCFF");
    //线条的宽度
    private int line_width = 10;

    //选中的点是否能够再次选择
    private boolean can_select_again = false;

    //路径的起点
    private Point start_point;
    //路径的终点
    private Point end_point;
    //画笔
    private Paint paint;

    //存储绘图的点
    private List<ImageView> dots;
    //存储点亮的点
    private List<ImageView> light_dots;
    //存储绘制的路径
    private List<Path> paths;

    //回调密码的监听者
    private CallBackPasswordListener listener;

    /**
     * 构造方法：Java代码初始化
     * @param context
     */
    public XLPictureUnlock(Context context) {
        super(context);

        //初始化操作
        init();

    }

    /**
     * 构造方法：Xml代码初始化
     * @param context
     * @param attrs
     */
    public XLPictureUnlock(Context context, AttributeSet attrs) {
        super(context, attrs);

        //解析属性
        if (attrs != null){
            //1.获取所有属性值的集合
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.XLPictureUnlock);

            //2.解析单个属性的值
            normal_image_id = typedArray.getResourceId(R.styleable.XLPictureUnlock_normal_image_id,normal_image_id);
            select_image_id = typedArray.getResourceId(R.styleable.XLPictureUnlock_select_image_id,select_image_id);
            dot_size = PxUtil.dpToPx(typedArray.getInteger(R.styleable.XLPictureUnlock_dot_size,dot_size),getContext());
            line_color = typedArray.getColor(R.styleable.XLPictureUnlock_line_color,line_color);
            line_width = PxUtil.dpToPx(typedArray.getInteger(R.styleable.XLPictureUnlock_line_width,line_width),getContext());
            can_select_again = typedArray.getBoolean(R.styleable.XLPictureUnlock_can_select_again,can_select_again);

            //3.释放资源
            typedArray.recycle();
        }

        //初始化操作
        init();
    }

    /**
     * 初始化操作
     */
    private void init() {
        //初始化集合
        dots = new ArrayList<>();
        light_dots = new ArrayList<>();
        paths = new ArrayList<>();

        //画笔初始化
        paint = new Paint();
        paint.setColor(line_color);
        paint.setStrokeWidth(line_width);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStyle(Paint.Style.STROKE);//一定要设置，不然绘制Path没效果
        paint.setAntiAlias(true);

        //关闭硬件加速
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    /**
     * 加入点视图
     * @param layout
     */
    public void addDotView(final RelativeLayout layout){
        this.post(new Runnable() {
            @Override
            public void run() {
                //获取视图的宽高
                int width = getWidth();
                int height = getHeight();

                //获取间距
                int hor_padding = (width - dot_size * 3) / 4;
                int ver_padding = (height - dot_size * 3) / 4;

                //依次创建视图
                for (int i = 0; i < 9; i++) {
                    //列数
                    int col = i % 3;
                    //行数
                    int row = i / 3;

                    //创建视图
                    @SuppressLint("DrawAllocation") ImageView dot = new ImageView(getContext());
                    //设置图片
                    dot.setImageResource(normal_image_id);
                    //设置图片填充方式
                    dot.setScaleType(ImageView.ScaleType.FIT_XY);
                    //设置位置
                    LayoutParams layoutParams = new LayoutParams(
                            dot_size,
                            dot_size
                    );
                    layoutParams.leftMargin = hor_padding + (hor_padding + dot_size) * col;
                    layoutParams.topMargin = ver_padding + (ver_padding + dot_size) * row;
                    //将视图加入布局
                    layout.addView(dot,layoutParams);
                    //设置布局的id
                    dot.setId(i+1);
                    //加入集合存储
                    dots.add(dot);
                }
            }
        });
    }

    //重绘过程
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        //判断有没有路径可以画
        if (paths.size() > 0){
            for (Path path : paths) {
                canvas.drawPath(path,paint);
            }
        }

        //判断有没有终点
        if (start_point != end_point && start_point != null && end_point != null){
            canvas.drawLine(
                    start_point.x,start_point.y,
                    end_point.x,end_point.y,paint
            );
        }
    }


    //触摸过程
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取触摸点坐标
        float x = event.getX();
        float y = event.getY();

        //存储获取的点
        ImageView dot;

        //触摸过程
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //获取触摸的点
                dot = isDotsContainPoint(x, y);

                //对选中的点，改变图片，设置为路径的起点
                if (dot != null){
                    dot.setImageResource(select_image_id);
                    light_dots.add(dot);

                    start_point = new Point(
                            (int)(dot.getX()+dot.getPivotX()),
                            (int)(dot.getY()+dot.getPivotY())
                    );
                }

                break;
            case MotionEvent.ACTION_MOVE:
                //获取触摸的点
                dot = isDotsContainPoint(x, y);

                //如果触摸到了点
                if (dot != null) {
                    //判断这个点有没有被点亮 或者 能不能重复点击
                    if (!isDotChangeLight(dot) || can_select_again) {

                        //1.点亮点
                        if (!isDotChangeLight(dot)){
                            dot.setImageResource(select_image_id);
                            light_dots.add(dot);
                        }else {
                            //防止在同一个点内一直移动，导致多次加入
                            if (light_dots.get(light_dots.size()-1).getId() != dot.getId()){
                                light_dots.add(dot);
                            }
                        }

                        //判断是不是第一个点
                        if (start_point != null){
                            //2.在两点之间绘制线条

                            //2.1确认路径
                            Path path = new Path();
                            path.moveTo(start_point.x,start_point.y);
                            path.lineTo(
                                    (dot.getX()+dot.getPivotX()),
                                    (dot.getY()+dot.getPivotY())
                            );
                            paths.add(path);
                            //2.3刷新
                            invalidate();

                            //2.2确认新的起点
                            start_point = new Point(
                                    (int)(dot.getX()+dot.getPivotX()),
                                    (int)(dot.getY()+dot.getPivotY())
                            );
                            end_point = start_point;

                            //2.3刷新
                            invalidate();
                        }else {
                            //2.设置为起始点
                            start_point = new Point(
                                    (int)(dot.getX()+dot.getPivotX()),
                                    (int)(dot.getY()+dot.getPivotY())
                            );
                        }

                    }
                }else {
                    //1.绘制线条
                    end_point = new Point((int)x,(int)y);

                    //2.刷新
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_UP:
                end_point = start_point;
                //刷新
                invalidate();

                //所有选中的点回复正常样式
                for (ImageView light_dot : light_dots) {
                    light_dot.setImageResource(normal_image_id);
                }

                //回调密码
                if (listener != null){
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < light_dots.size(); i++) {
                        stringBuilder.append(light_dots.get(i).getId());
                    }

                    if (!stringBuilder.toString().equals("")){
                        listener.picturePassword(stringBuilder.toString());
                    }
                }

                //清空点亮的点
                light_dots.clear();
                //清空paths
                paths.clear();
                //起始点，终点，清空
                start_point = null;
                end_point = null;

               break;
        }

        return true;
    }

    /**
     * 回调接口
     */
    public interface CallBackPasswordListener {
        void picturePassword(String password);
    }

    /**
     * 判断一个点是否已经被点亮
     * @param dot
     * @return
     */
    private boolean isDotChangeLight(ImageView dot){
        for (ImageView light_dot : light_dots) {
            if (light_dot.getId() == dot.getId()){
                return true;
            }
        }

        return false;
    }

    /**
     * 判断触摸点是否在一个点中
     * @param x
     * @param y
     * @return
     */
    private ImageView isDotsContainPoint(float x, float y){
        //循环判断是否触摸点是否在哪一个点的范围内
        for (ImageView dot : dots) {
            //获取当前点的范围
            RectF rectF = new RectF(dot.getLeft(), dot.getTop(), dot.getRight(), dot.getBottom());
            //判断坐标是否在该点内
            if (rectF.contains(x,y)){
                return dot;
            }
        }

        return null;
    }

    //setter/getter方法
    public int getNormal_image_id() {
        return normal_image_id;
    }

    public void setNormal_image_id(int normal_image_id) {
        this.normal_image_id = normal_image_id;
    }

    public int getSelect_image_id() {
        return select_image_id;
    }

    public void setSelect_image_id(int select_image_id) {
        this.select_image_id = select_image_id;
    }

    public int getDot_size() {
        return dot_size;
    }

    public void setDot_size(int dot_size) {
        this.dot_size = dot_size;
    }

    public int getLine_color() {
        return line_color;
    }

    public void setLine_color(int line_color) {
        this.line_color = line_color;
    }

    public int getLine_width() {
        return line_width;
    }

    public void setLine_width(int line_width) {
        this.line_width = line_width;
    }

    public boolean isCan_select_again() {
        return can_select_again;
    }

    public void setCan_select_again(boolean can_select_again) {
        this.can_select_again = can_select_again;
    }

    public CallBackPasswordListener getCallBackPasswordListener() {
        return listener;
    }

    public void setCallBackPasswordListener(CallBackPasswordListener listener) {
        this.listener = listener;
    }
}
