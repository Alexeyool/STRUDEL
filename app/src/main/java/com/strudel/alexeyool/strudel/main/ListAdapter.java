package com.strudel.alexeyool.strudel.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.strudel.alexeyool.strudel.R;

import java.util.ArrayList;

/**
 * Created by Alexeyool on 7/27/2017.
 */

public class ListAdapter extends BaseAdapter {
    static  final  String TITLE = "@STRUDEL ";

    Context mContext;
    ArrayList<Cover> coversList;
    ViewHolder holder;

    ListAdapter(Context _mContext, ArrayList<Cover> _coversList){
        mContext = _mContext;
        coversList = _coversList;
    }

    @Override
    public int getCount() {
        return coversList.size();
    }

    @Override
    public Object getItem(int position) {
        return coversList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = inflater.inflate(R.layout.list_cell, parent, false);

            holder.imageView = (ImageView) convertView.findViewById(R.id.LC_imageView);
            holder.textView = (TextView) convertView.findViewById(R.id.LC_textView);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView.setImageBitmap(coversList.get(position).getImage());
        int orientation = mContext.getResources().getConfiguration().orientation;
        if(orientation == mContext.getResources().getConfiguration().ORIENTATION_PORTRAIT) {
            int width = parent.getResources().getDisplayMetrics().widthPixels;
            int height = (int) ((double)(coversList.get(position).getImage().getHeight()) * (double)width / (double)(coversList.get(position).getImage().getWidth()));
            holder.imageView.getLayoutParams().height = height;
        }
        else{
            int height = parent.getResources().getDisplayMetrics().heightPixels;
            int width = (int) ((double)(coversList.get(position).getImage().getWidth()) * (double)height / (double)(coversList.get(position).getImage().getHeight()));
            holder.imageView.getLayoutParams().width = width;
//            holder.imageView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
//            holder.imageView.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        holder.textView.setText(setTitle(position));

        return convertView;
    }



    private String setTitle(int _position) {
        return TITLE + coversList.get(_position).getData();
    }

}

class ViewHolder {
    ImageView imageView;
    TextView textView;
}