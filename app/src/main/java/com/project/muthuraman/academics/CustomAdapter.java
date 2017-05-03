package com.project.muthuraman.academics;

/**
 * Created by muthuraman on 20/4/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {
    String [] result;
    String result1[];
    String result3[];
    Context context;
    String [] result2;
    private static LayoutInflater inflater=null;
    public CustomAdapter(HomePageActivity mainActivity, String[] name, String[] course, String[] facnam,String[] slot ) {
        // TODO Auto-generated constructor stub
        result=name;
        result1 = course;
        context=mainActivity;
        result2=facnam;
        result3=slot;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        TextView tv2;
        TextView tv3;
        TextView tv4;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder=new Holder();
        View rowView;
        rowView = inflater.inflate(R.layout.courselist, null);
        holder.tv=(TextView) rowView.findViewById(R.id.textView4);
        holder.tv2=(TextView) rowView.findViewById(R.id.textView5);
        holder.tv3=(TextView) rowView.findViewById(R.id.textView6);
        holder.tv4=(TextView) rowView.findViewById(R.id.textView7);
        holder.tv.setText(result[position]);
        holder.tv2.setText(result1[position]);
        holder.tv3.setText(result2[position]);
        holder.tv4.setText(result3[position]);
        return rowView;
    }

}