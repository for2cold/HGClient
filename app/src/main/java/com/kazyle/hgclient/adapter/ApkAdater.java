package com.kazyle.hgclient.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kazyle.hgclient.R;
import com.kazyle.hgclient.entity.Apk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

/**
 * Created by Kazyle on 2016/10/16.
 */
public class ApkAdater extends BaseAdapter {

    private Context context;
    private List<Apk> apkList;
    private LayoutInflater inflater;
    public Map<Integer, Boolean> state = new HashMap<Integer, Boolean>();

    public ApkAdater(Context context, List<Apk> apkList) {
        this.context = context;
        this.apkList = apkList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return apkList.size();
    }

    @Override
    public Object getItem(int position) {
        return apkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder =  holder = new Holder();
        convertView = inflater.inflate(R.layout.apk_item, null);
        holder.name = (TextView) convertView.findViewById(R.id.apkName);
        holder.checkBox = (CheckBox) convertView.findViewById(R.id.apkChecked);

        convertView.setTag(holder);
        Apk apk = apkList.get(position);
        holder.name.setText(apk.getName());
        holder.checkBox.setTag(apk.getPath());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    state.put(position, isChecked);
                } else {
                    state.remove(position);
                }
            }
        });
        holder.checkBox.setChecked((state.get(position) == null ? false : true));
        return convertView;
    }

    protected class Holder{
        TextView name;
        CheckBox checkBox;
    }
}
