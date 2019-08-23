package com.homie.ml_kit.TextRecognition;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.homie.ml_kit.ImageLabel.Label;
import com.homie.ml_kit.R;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<String> {

    private final List<String> mLabels_list;
    private final Context          mContext;
    private LayoutInflater   mInflater;
    private int mResource_layout;

    public ListViewAdapter(Context context, int resourceId, List<String> mLabels_list)
    {
        super(context, resourceId, mLabels_list);

        this.mResource_layout =resourceId;
        this.mContext = context;
        this.mLabels_list = mLabels_list;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = mInflater.inflate(mResource_layout, null);
            holder = new ViewHolder();

            holder.text=convertView.findViewById(R.id.item_text_tv);


            holder.text.setText(mLabels_list.get(position));

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }



        return convertView;
    }

    @Override
    public int getCount()
    {
        return mLabels_list.size();
    }

    @Override
    public String getItem(int position)
    {
        return mLabels_list.get(position);
    }

    public class ViewHolder
    {
        TextView    text;

    }
}