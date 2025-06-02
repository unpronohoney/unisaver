package com.unisaver.unisaver;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TableItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint;
    private final int dividerSize;

    public TableItemDecoration(int color, int dividerSize) {
        paint = new Paint();
        paint.setColor(color); // Çizgi rengi
        paint.setStrokeWidth(dividerSize); // Çizgi kalınlığı
        this.dividerSize = dividerSize;
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int columnCount = 3; // Eğer GridLayoutManager kullanıyorsan sütun sayısını buraya gir

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            // Yatay çizgileri çiz
            float left = child.getLeft();
            float right = child.getRight();
            float top = child.getBottom() + params.bottomMargin;
            float bottom = top + dividerSize;
            canvas.drawRect(left, top, right, bottom, paint);

            // Dikey çizgileri çiz
            if ((i + 1) % columnCount != 0) { // Son sütunun sağına çizgi ekleme
                left = child.getRight() + params.rightMargin;
                right = left + dividerSize;
                top = child.getTop();
                bottom = child.getBottom();
                canvas.drawRect(left, top, right, bottom, paint);
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.right = dividerSize;
        outRect.bottom = dividerSize;
    }
}
