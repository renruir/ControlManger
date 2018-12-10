package com.ctftek.controlmanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by renrui on 2017/10/25/0025.
 */

public class SpinnerArrayAdapter extends BaseAdapter {

    public static final String TAG = "SpinnerArrayAdapter";
    private List<ReservePlan> mPlan;
    private Context mContext;
    private LayoutInflater inflater;

    public SpinnerArrayAdapter(Context context, List<ReservePlan> data) {
        mPlan = data;
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }

    public void setPlanData(List<ReservePlan> data){
        this.mPlan = data;
    }


    @Override
    public int getCount() {
        return mPlan.size();
    }

    @Override
    public Object getItem(int position) {
        return mPlan.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView: " +888888);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.spinner_item, null);
            viewHolder.index = (TextView) convertView.findViewById(R.id.plan_item_index);
            viewHolder.name = (TextView) convertView.findViewById(R.id.plan_item_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.index.setText("预案" + mPlan.get(position).getIndex() + "：");
        viewHolder.name.setText(mPlan.get(position).getName());
        return convertView;
    }

    class ViewHolder {
        TextView index;
        TextView name;
    }
}
