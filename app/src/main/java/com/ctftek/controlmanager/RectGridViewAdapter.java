package com.ctftek.controlmanager;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctftek.controlmanager.model.AdapterModel;

/**
 * Created by renrui on 2017/6/2 0002.
 */

public class RectGridViewAdapter extends BaseAdapter {

    private static final String TAG = "RectGridViewAdapter";

    private Context mContext;
    private LayoutInflater inflater;
    private AdapterModel model;

    public RectGridViewAdapter(Context ctx, AdapterModel m) {
        mContext = ctx;
        inflater = LayoutInflater.from(mContext);
        model = m;
    }

    public void setModel(AdapterModel model) {
        this.model = model;
    }

    @Override
    public int getCount() {
        return model.getColumn() * model.getRow();
    }

    @Override
    public Object getItem(int position) {
        return model.getClicked()[position][position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.rect_view, null);
            viewHolder.screenView = (LinearLayout) convertView.findViewById(R.id.rect_content);
            viewHolder.countText = (TextView) convertView.findViewById(R.id.count_content);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.screenView.setLayoutParams(new FrameLayout.LayoutParams
                (model.getGridViewWidth() / model.getColumn(), model.getGridViewHeight() / model.getRow()));

        int i = position / model.getColumn();
        int j = position % model.getColumn();
//        Log.d(TAG, "getView position: " + position + ", clicked: " + model.getClicked().length);
        if (model.getClicked()[i][j]) {
            viewHolder.screenView.setBackgroundColor(Color.RED);
        } else {
            viewHolder.screenView.setBackgroundColor(Color.GRAY);
        }

//            viewHolder.countText.setText("0x" + (i + 1) + "" + (j + 1));
        viewHolder.countText.setText(String.valueOf(position + 1));

        return convertView;
    }

    public class ViewHolder {
        public LinearLayout screenView;
        public TextView countText;
    }
}
