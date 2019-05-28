package rcominfo.com.ejejyxt.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rcominfo.ejejyxt.R;

import java.util.ArrayList;

import rcominfo.com.ejejyxt.Bean.ShopBean;

/**
 * Created by 王璐阳 on 2017/3/7.
 */
public class SimpleAda2 extends BaseAdapter {

    Context context; ArrayList<ShopBean.tbim> items;
    private ViewHolder holder;

    public SimpleAda2(Context context, ArrayList<ShopBean.tbim> items) {
        this.context = context;
        this.items =items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view ==null){
            view = LayoutInflater.from(context).inflate(R.layout.spinner_express,null);
            holder = new ViewHolder();
            holder.tv = (TextView)view.findViewById(R.id.tv_express);
            view.setTag(holder);

        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.tv.setText(items.get(i).Name);
        return view;
    }

    class ViewHolder{
        TextView tv;
    }
}
