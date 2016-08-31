package com.yyt.blue.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by yangyoutao on 2016/8/27.
 */
public class DeviceListViewAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater;
    private List<BluetoothDevice> mlist;

    public DeviceListViewAdapter(Context context, List<BluetoothDevice> list) {
        mLayoutInflater = LayoutInflater.from(context);
        this.mlist = list;
    }

    public void setListData(List<BluetoothDevice> list) {
        Log.i("ViewHolder", "setListData" + list.size());
        this.mlist = list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.mlist.size();
    }

    @Override
    public BluetoothDevice getItem(int i) {
        return this.mlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder mViewHolder = null;
        if (view == null) {
            mViewHolder = new ViewHolder();
            view = mLayoutInflater.inflate(R.layout.listitem_layout, null);
            mViewHolder.mShow = (TextView) view.findViewById(R.id.show);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        mViewHolder.mShow.setText(getItem(i).getName() + "\n" + getItem(i).getAddress());
        return view;
    }

    private static class ViewHolder {
        TextView mShow;
    }
}
