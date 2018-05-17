package charco.android.iosdownloadview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import charco.android.iosdownloadview.R;


/**
 * Created 18/5/14 15:05
 * Author:charcolee
 * Version:V1.0
 * ----------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------
 */

public class DownloadView extends View {

    private Paint mBgPaint;
    private Paint mLayerPaint;
    private Paint mArcPaint;
    private Bitmap mBgBitmap;
    private RectF mArcRectF,mSmallRectF ;
    private Path mPath = new Path();
    private int percent,pointSize ,layerRadius;
    private boolean stop,finish;

    private OnDownloadFinishListener mListener;

    public DownloadView(Context context) {
        this(context,null);
    }

    public DownloadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DownloadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        //绘制背景icon的画笔
        mBgPaint = new Paint();
        mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg);

        //外层剪切圆角的矩形
        RectF rect =  new RectF();
        rect.set(0, 0, mBgBitmap.getWidth(), mBgBitmap.getHeight());
        mPath.addRoundRect(rect,30f,30f, Path.Direction.CW);

        //绘制外圈圆环
        mLayerPaint = new Paint();
        mLayerPaint.setAntiAlias(true);
        mLayerPaint.setColor(getResources().getColor(R.color.layer));
        mLayerPaint.setStyle(Paint.Style.STROKE);
        mLayerPaint.setStrokeWidth(mBgBitmap.getWidth() - 100);

        //绘制内圈扇形
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(getResources().getColor(R.color.layer));
        mArcPaint.setStyle(Paint.Style.FILL);
        mArcPaint.setStrokeWidth(10);
        mArcRectF =  new RectF(mBgBitmap.getWidth()/2-45,mBgBitmap.getHeight()/2-45,
                mBgBitmap.getWidth()/2+45,mBgBitmap.getHeight()/2+45);

        //绘制暂停扇形所用到的RectF
        mSmallRectF = new RectF();

        //加载结束动画使用的半径
        layerRadius = mBgBitmap.getWidth()/2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (finish)return;

        //简单裁剪为圆角View
        canvas.clipPath(mPath);
        //画背景icon
        canvas.drawBitmap(mBgBitmap,0,0,mBgPaint);


        if (stop) {//如果暂停
            if (pointSize < 70){//判断是否需要执行最内层扇形变大的动画
                pointSize += 10;
                invalidate();
            }else {
                //圆点动画执行完成则剪切出 缕空暂停 的效果
                canvas.clipRect(getWidth()/2-20,getHeight()/2-10,getWidth()/2-10,getHeight()/2+10,
                Region.Op.DIFFERENCE);

                canvas.clipRect(getWidth()/2+10,getHeight()/2-10,getWidth()/2+20,getHeight()/2+10,
                Region.Op.DIFFERENCE);

            }
            setSmallRectSize(pointSize);
            canvas.drawArc(mSmallRectF,-90,percent*360/100,true,mArcPaint);

        }else if ( pointSize > 0){//如果不是暂停模式，并且最内层扇形大小没有变为0，则执行扇形变小的动画

            pointSize -= 10;//圆点大小每次自减10

            if (pointSize >=  60){//如果圆点大小大于60以上，则还是需要显示 缕空暂停
                canvas.clipRect(getWidth()/2-20,getHeight()/2-10,getWidth()/2-10,getHeight()/2+10,
                        Region.Op.DIFFERENCE);

                canvas.clipRect(getWidth()/2+10,getHeight()/2-10,getWidth()/2+20,getHeight()/2+10,
                        Region.Op.DIFFERENCE);
            }

            setSmallRectSize(pointSize);
            canvas.drawArc(mSmallRectF,-90,percent*360/100,true,mArcPaint);
            invalidate();

        }

        if (percent < 100){//如果进度还没完成，画内部大扇形
            //画内圈扇形
            canvas.drawArc(mArcRectF,-90+percent*360/100,360-percent*360/100,true,mArcPaint);

        }else {//否则执行外圈消失的动画
            if (layerRadius < getWidth()){
                layerRadius += 10;
                invalidate();
            }else {
                finish = true;
                if (mListener!=null)
                    mListener.onDownloadFinish();
            }
        }
        //画外圈圆环
        canvas.drawCircle(getWidth()/2,getHeight()/2,layerRadius,mLayerPaint);

    }

    //设置暂停扇形的矩形大小
    private void setSmallRectSize(int size){
        int centerX = getWidth()/2;
        int centerY = getHeight()/2;
        mSmallRectF.set(centerX-size/2,centerY-size/2,centerX+size/2,centerY+size/2);
    }

    public void setDownloadLietener(OnDownloadFinishListener lietener){
        this.mListener = lietener;
    }

    //设置百分比进度
    public void setPercent(@IntRange(from = 0, to = 100)int percent){
        this.percent = percent;
        if (stop){
            stop = false;
        }
        invalidate();
    }

    //暂停
    public void stop(){
        stop = true;
        invalidate();
    }

    //开始
    public void restart(){
        stop = false;
        invalidate();
    }

    //重置
    public void reset(){
        finish = false;
        stop = false;
        percent = 0;
        pointSize = 0;
        layerRadius = getWidth()/2;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mBgBitmap.getWidth(),mBgBitmap.getHeight());
    }

    public interface OnDownloadFinishListener{
        void onDownloadFinish();
    };

}
