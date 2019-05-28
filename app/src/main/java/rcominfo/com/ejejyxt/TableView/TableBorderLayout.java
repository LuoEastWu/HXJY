package table.border.layout.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;

public class TableBorderLayout extends TableLayout {

    private Context mContext;

    private BaseAdapter mAdapter;// ????????

    private int column = 0;// ????

    public TableBorderLayout(Context context, AttributeSet attrs) {
	super(context, attrs);
	this.mContext = context;
	setStretchAllColumns(true);
    }

    private void drawLayout() {
	removeAllViews();
	int realcount = mAdapter.getCount();
	int count = 0;
	if (realcount < column) {
	    count = column;
	} else if (realcount % column != 0) {
	    count = realcount + column - (realcount % column);
	} else {
	    count = realcount;
	}
	TableRow tableRow = null;//?????TableRow
	for (int i = 0; i < count; i++) {
	    final int index = i;
	    View view = null;
	    if (index >= realcount) {
		view = mAdapter.getView((realcount - 1), null, null);
		view.setVisibility(View.INVISIBLE);
	    } else {
		view = mAdapter.getView(index, null, null);
	    }
	    if (index % column == 0) {// ????
		tableRow = new TableRow(mContext);
	    }
	    if (tableRow != null) {//???????Item
		tableRow.addView(view);
	    }
	    if (index % column == 0) {// ????
		addView(tableRow, new TableLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	    }
	}
    }

    public void setAdapter(BaseAdapter baseAdapter, int column) {
	if (baseAdapter == null || baseAdapter.getCount() == 0) {
	    return;
	}
	this.mAdapter = baseAdapter;
	this.column = column;
	drawLayout();
    }

    public void setAdapter(BaseAdapter baseAdapter) {
	setAdapter(baseAdapter, 4);
    }
}
