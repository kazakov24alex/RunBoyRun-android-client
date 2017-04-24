package twoAK.runboyrun.adapters;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;


public class SquareImageButtonView extends ImageButton {

    public SquareImageButtonView(Context context) {
        super(context);
    }

    public SquareImageButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageButtonView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);

    }
}
