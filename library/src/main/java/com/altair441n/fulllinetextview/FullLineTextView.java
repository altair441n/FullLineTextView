package com.altair441n.fulllinetextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;


public class FullLineTextView extends TextView {

    private Map<Integer, CharSequence> lines;
    private int expandableMaxLines;
    private float lastLineRightPadding;
    private boolean expandable;
    private boolean expand;
    private CharSequence lastLine;
    private boolean cacheLastLine;
    private boolean needLineFeed;
    private boolean cacheLineFeed;
    private ExpandableListener expandableListener;
    private OnExpandListener onExpandListener;

    public FullLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public FullLineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    public FullLineTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FullLineTextView, defStyleAttr, defStyleRes);
        try {
            expandableMaxLines = typedArray.getInt(R.styleable.FullLineTextView_expandableMaxLines, 0);
            lastLineRightPadding = typedArray.getDimensionPixelSize(R.styleable.FullLineTextView_lastLineRightPadding, 0);
        } finally {
            typedArray.recycle();
        }
        lines = new ArrayMap<>();
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
        requestLayout();
    }

    public int getExpandableMaxLines() {
        return expandableMaxLines;
    }

    public void setExpandableMaxLines(int expandableMaxLines) {
        this.expandableMaxLines = expandableMaxLines;
    }

    public float getLastLineRightPadding() {
        return lastLineRightPadding;
    }

    public void setLastLineRightPadding(float lastLineRightPadding) {
        this.lastLineRightPadding = lastLineRightPadding;
    }

    public void setExpandableListener(ExpandableListener listener) {
        this.expandableListener = listener;
    }

    public void setOnExpandListener(OnExpandListener onExpandListener) {
        this.onExpandListener = onExpandListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();

        CharSequence text = getText();
        int textHeight = getTextHeight();
        int lineNum = 0;
        int lineStart = 0;
        int lineEnd;
        float lineHeight = paint.getTextSize();
        while (lineStart < text.length()) {
            lineNum++;
            CharSequence line;
            if (lines.get(lineNum) != null) {
                line = lines.get(lineNum);
            } else {
                line = getLine(getMeasuredWidth(), text.subSequence(lineStart, text.length()), paint);
                lines.put(lineNum, line);
            }
            lineEnd = lineStart + line.length();

            if (expandableMaxLines > 0 && lineNum == expandableMaxLines) {
                if (expandable && !expand) {
                    if (!cacheLastLine) {
                        lastLine = getExpandLine((int) (getMeasuredWidth() - lastLineRightPadding), text.subSequence(lineStart, text.length()), paint);
                        cacheLastLine = true;
                    }
                    line = lastLine;
                    canvas.drawText(line, 0, line.length(), 0, lineHeight, paint);
                    break;
                } else {
                    canvas.drawText(line, 0, line.length(), 0, lineHeight, paint);
                }
            } else {
                canvas.drawText(line, 0, line.length(), 0, lineHeight, paint);
            }

            lineStart = lineEnd;
            lineHeight += textHeight;
        }
        if (onExpandListener != null) {
            onExpandListener.onExpand(expand);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureText(widthMeasureSpec, heightMeasureSpec);
    }

    private void measureText(int widthMeasureSpec, int heightMeasureSpec) {
        if (getLayoutParams().width == ViewGroup.LayoutParams.MATCH_PARENT && getLayoutParams().height == ViewGroup.LayoutParams.MATCH_PARENT) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        TextPaint paint = getPaint();
        CharSequence text = getText();
        int textHeight = getTextHeight();
        int lineNum = 0;
        int lineStart = 0;
        int lineEnd;
        float lineHeight = paint.getTextSize();
        while (lineStart < text.length()) {
            lineNum++;
            CharSequence line;
            if (lines.get(lineNum) != null) {
                line = lines.get(lineNum);
            } else {
                line = getLine(getMeasuredWidth(), text.subSequence(lineStart, text.length()), paint);
                lines.put(lineNum, line);
            }
            lineEnd = lineStart + line.length();

            if (lineNum == expandableMaxLines) {
                expandable = lineEnd != text.length();
                if (expandableListener != null) {
                    expandableListener.isExpandable(expandable);
                }
                if (expandable && !expand) {
                    lineHeight += textHeight;
                    break;
                }
            }

            // 如果最后一行减去lastLineRightPadding放不下剩余文字则增加一行
            if (lineEnd == text.length() && lineNum > expandableMaxLines && lastLineRightPadding > 0) {
                if (!cacheLineFeed) {
                    needLineFeed = needLineFeed((int) (getMeasuredWidth() - lastLineRightPadding), text.subSequence(lineStart, text.length()), paint);
                    cacheLineFeed = true;
                }
                if (needLineFeed) {
                    lineHeight += textHeight;
                }
            }

            lineStart = lineEnd;
            lineHeight += textHeight;
        }

        int width;
        int height;
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            width = (lineNum == 1) ? (int) StaticLayout.getDesiredWidth(lines.get(1), paint) : widthMeasureSpec;
        } else {
            width = widthMeasureSpec;
        }
        if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            height = (int) (lineHeight - textHeight + getTextSpacing());
        } else {
            height = heightMeasureSpec;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (lines != null) {
            lines.clear();
        }
        expandable = false;
        expand = false;
        cacheLastLine = false;
        cacheLineFeed = false;
        super.setText(text, type);
    }

    private int getTextHeight() {
        Layout layout = getLayout();
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        int textHeight = (int) Math.ceil(fm.descent - fm.ascent);
        return (int) (textHeight * layout.getSpacingMultiplier() + layout.getSpacingAdd());
    }

    private int getTextSpacing() {
        Layout layout = getLayout();
        Paint.FontMetrics fm = getPaint().getFontMetrics();
        int textHeight = (int) Math.ceil(fm.descent - fm.ascent);
        int textSpacingHeight = (int) (textHeight * layout.getSpacingMultiplier() + layout.getSpacingAdd());
        return (textSpacingHeight - textHeight) / 2;
    }

    /**
     * 获取一行显示的文本
     */
    private CharSequence getLine(int width, CharSequence text, TextPaint paint) {
        CharSequence line = null;
        for (int i = 1; i <= text.length(); i++) {
            line = text.subSequence(0, i);
            float textWidth = StaticLayout.getDesiredWidth(line, paint);
            if (textWidth > width) {
                line = text.subSequence(0, i - 1);
                return line;
            }
        }
        return line;
    }

    /**
     * 获取折叠后最后一行显示的文本
     */
    private CharSequence getExpandLine(int width, CharSequence text, TextPaint paint) {
        CharSequence line = text;
        String ellipsis = "...";
        float ellipsisWidth = StaticLayout.getDesiredWidth(ellipsis, paint);
        for (int i = 1; i <= text.length(); i++) {
            CharSequence cha = line.subSequence(0, i);
            float textWidth = StaticLayout.getDesiredWidth(cha, paint);
            if (textWidth + ellipsisWidth > width) {
                // 需要显示 ...
                line = line.subSequence(0, i - 1) + ellipsis;
                return line;
            }
        }
        return line;
    }

    /**
     * 最后一行是否需要换行
     */
    private boolean needLineFeed(int width, CharSequence text, TextPaint paint) {
        for (int i = 1; i <= text.length(); i++) {
            CharSequence cha = text.subSequence(0, i);
            float textWidth = StaticLayout.getDesiredWidth(cha, paint);
            if (textWidth > width) {
                return true;
            }
        }
        return false;
    }

    public interface ExpandableListener {

        void isExpandable(boolean expandable);
    }

    public interface OnExpandListener {

        void onExpand(boolean expand);
    }
}