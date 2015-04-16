package net.rizkyzulkarnaen.productgallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class ZoomImage  extends ImageView implements OnTouchListener {


    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;

    private int mode = NONE;

    private PointF mStartPoint = new PointF();
    private PointF mMiddlePoint = new PointF();
    private Point mBitmapMiddlePoint = new Point();

    private float oldDist = 1f;
    private float matrixValues[] = {0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    private float scale;
    private float oldEventX = 0;
    private float oldEventY = 0;
    private float oldStartPointX = 0;
    private float oldStartPointY = 0;
    private int mViewWidth = -1;
    private int mViewHeight = -1;
    private int mBitmapWidth = -1;
    private int mBitmapHeight = -1;
    private boolean mDraggable = false;


    public ZoomImage(Context context) {
        this(context, null, 0);
    }

    public ZoomImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setOnTouchListener(this);
    }

    @Override
    public void onSizeChanged (int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
    }

    public void setBitmap(Bitmap bitmap){
        if(bitmap != null){
            setImageBitmap(bitmap);

            mBitmapWidth = bitmap.getWidth();
            mBitmapHeight = bitmap.getHeight();
            mBitmapMiddlePoint.x = (mViewWidth / 2) - (mBitmapWidth /  2);
            mBitmapMiddlePoint.y = (mViewHeight / 2) - (mBitmapHeight / 2);

            matrix.postTranslate(mBitmapMiddlePoint.x, mBitmapMiddlePoint.y);
            this.setImageMatrix(matrix);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            savedMatrix.set(matrix);
            mStartPoint.set(event.getX(), event.getY());
            mode = DRAG;
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            oldDist = spacing(event);
            if(oldDist > 10f){
                savedMatrix.set(matrix);
                midPoint(mMiddlePoint, event);
                mode = ZOOM;
            }
            break;
        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_POINTER_UP:
            mode = NONE;
            break;
        case MotionEvent.ACTION_MOVE:
            if(mode == DRAG){
                drag(event);
            } else if(mode == ZOOM){
                zoom(event);
            } 
            break;
        }
        invalidate();
        return true;
    }



   public void drag(MotionEvent event){
       matrix.getValues(matrixValues);

       float left = matrixValues[2];
       float top = matrixValues[5];
       float bottom = (top + (matrixValues[0] * mBitmapHeight)) - mViewHeight;
       float right = (left + (matrixValues[0] * mBitmapWidth)) -mViewWidth;

       float eventX = event.getX();
       float eventY = event.getY();
       float spacingX = eventX - mStartPoint.x;
       float spacingY = eventY - mStartPoint.y;
       float newPositionLeft = (left  < 0 ? spacingX : spacingX * -1) + left;
       float newPositionRight = (spacingX) + right;
       float newPositionTop = (top  < 0 ? spacingY : spacingY * -1) + top;
       float newPositionBottom = (spacingY) + bottom;
       boolean x = true;
       boolean y = true;

       if(newPositionRight < 0.0f || newPositionLeft > 0.0f){
           if(newPositionRight < 0.0f && newPositionLeft > 0.0f){
               x = false;
           } else{
               eventX = oldEventX;
               mStartPoint.x = oldStartPointX;
           }
       }
       if(newPositionBottom < 0.0f || newPositionTop > 0.0f){
           if(newPositionBottom < 0.0f && newPositionTop > 0.0f){
               y = false;
           } else{
               eventY = oldEventY;
               mStartPoint.y = oldStartPointY;
           }
       }

       if(mDraggable){
           matrix.set(savedMatrix);
           matrix.postTranslate(x? eventX - mStartPoint.x : 0, y? eventY - mStartPoint.y : 0);
           this.setImageMatrix(matrix);
           if(x)oldEventX = eventX;
           if(y)oldEventY = eventY;
           if(x)oldStartPointX = mStartPoint.x;
           if(y)oldStartPointY = mStartPoint.y;
       }

   }
   
   private void getProperBaseMatrix(Bitmap bitmap, Matrix matrix) {

       float viewWidth = getWidth();
       float viewHeight = getHeight();

       float w = bitmap.getWidth();
       float h = bitmap.getHeight();
       matrix.reset();

       // We limit up-scaling to 2x otherwise the result may look bad if it's
       // a small icon.
       float widthScale = Math.min(viewWidth / w, 2.0f);
       float heightScale = Math.min(viewHeight / h, 2.0f);
       float scale = Math.min(widthScale, heightScale);

       matrix.postScale(scale, scale);

       matrix.postTranslate(
               (viewWidth - w * scale) / 2F,
               (viewHeight - h * scale) / 2F);
   }

   public void zoom(MotionEvent event){
       matrix.getValues(matrixValues);

       float newDist = spacing(event);
       float bitmapWidth = matrixValues[0] * mBitmapWidth;
       float bitmapHeight = matrixValues[0] * mBitmapHeight;
       boolean in = newDist > oldDist;

       if(!in && matrixValues[0] < 1){
           return;
       }
       if(bitmapWidth > mViewWidth|| bitmapHeight > mViewHeight){
           mDraggable = true;
       } else{
           mDraggable = false;
       }

       float midX = (bitmapWidth / 2);
       float midY = (bitmapHeight / 2);
       float midVX = (mViewWidth / 2);
       float midVY = (mViewHeight / 2);

       matrix.set(savedMatrix);
       scale = newDist / oldDist;
       matrix.postScale(scale, scale); 
       float dx = bitmapWidth > mViewWidth ? midVX : midX;
       float dy = bitmapHeight > mViewHeight ? midVY : midY; 
       
       //matrix.postTranslate(dx, dy);
       
       //matrix.postScale(scale, scale, bitmapWidth > mViewWidth ? mMiddlePoint.x : midX, bitmapHeight > mViewHeight ? mMiddlePoint.y : midY); 
       //getProperBaseMatrix(getImageBitmap(),matrix);
       setImageMatrix(matrix);

    }
    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float)Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


}
