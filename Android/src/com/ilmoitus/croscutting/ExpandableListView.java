package com.ilmoitus.croscutting;
 
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;
import android.content.Context;
 
public class ExpandableListView extends ListView
{
 
boolean expanded = false;
 
public ExpandableListView(Context context)
    {
        super(context);
    }
 
    public ExpandableListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
 
    public ExpandableListView(Context context, AttributeSet attrs,
            int defStyle)
    {
        super(context, attrs, defStyle);
    }
 
    public boolean isExpanded()
    {
        return expanded;
    }
 
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (isExpanded())
        {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        }
        else
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
 
    public void setExpanded(boolean expanded)
    {
        this.expanded = expanded;
    }
}
