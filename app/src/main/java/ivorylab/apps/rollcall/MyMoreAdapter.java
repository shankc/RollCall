package ivorylab.apps.rollcall;

/**
 * Created by mayuukhvarshney on 23/05/16.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyMoreAdapter extends BaseAdapter {
    ArrayList<String> ListItems;
    Context mContext;
    public MyMoreAdapter(Context context,ArrayList<String> items){
        this.mContext=context;
        this.ListItems=items;


    }
    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public String getItem(int position) {
        return ListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        String Menu_Item=getItem(position);
        ViewHolder holder;
        if(convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.more_list_row, parent, false);
            holder = new ViewHolder();
            holder.MenuImage = (ImageView) convertView.findViewById(R.id.menu_image);
            holder.MenuText = (TextView) convertView.findViewById(R.id.menu_text);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.MenuText.setText(ListItems.get(position));
        if(position==0) {
            holder.MenuImage.setImageResource(R.drawable.leave_form);

        }
        else if(position== 1) {
            holder.MenuImage.setImageResource(R.drawable.ic_calweb);

        }
        else if(position== 2) {
            holder.MenuImage.setImageResource(R.drawable.ic_daily_inout);

        }
        else if(position== 3) {
            holder.MenuImage.setImageResource(R.drawable.inbox);

        }
        else {
            holder.MenuImage.setImageResource(R.drawable.outbox);

        }
        return convertView;
    }
    static class ViewHolder{
        TextView MenuText;
        ImageView MenuImage;

    }
}
