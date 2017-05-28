package com.bong.smartcart;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 배봉현 on 2017-05-13.
 */
public class ListViewAdapter extends BaseAdapter {

    private Context mContext = null;
    private ArrayList<ListItem> mListItem = new ArrayList<ListItem>();

    // ListViewAdapter의 생성자
    public ListViewAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }
    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return mListItem.size() ;
    }
    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return mListItem.get(position) ;
    }
    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }
    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final  Context context = parent.getContext();
        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);
        }
        ListItem item = mListItem.get(position);
        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        //ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
        TextView idTextView = (TextView) convertView.findViewById(R.id.list_id) ;
        TextView nameTextView = (TextView) convertView.findViewById(R.id.list_name) ;
        TextView priceTextView = (TextView) convertView.findViewById(R.id.list_price) ;
        TextView countTextView = (TextView) convertView.findViewById(R.id.list_count) ;
        TextView placeTextView = (TextView) convertView.findViewById(R.id.list_place) ;
        TextView categoryTextView = (TextView) convertView.findViewById(R.id.list_category) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListItem listViewItem = mListItem.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        //iconImageView.setImageDrawable(listViewItem.getIcon());
        idTextView.setText(listViewItem.getId());
        nameTextView.setText(listViewItem.getName());
        priceTextView.setText(listViewItem.getPrice());
        countTextView.setText(listViewItem.getCount());
        placeTextView.setText(listViewItem.getPlace());
        categoryTextView.setText(listViewItem.getCategory());

        return convertView;
    }



    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String id, String name, String price, String count, String place, String category) {
        ListItem item = new ListItem();

        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        item.setCount(count);
        item.setPlace(place);
        item.setCategory(category);

        mListItem.add(item);
    }

}
