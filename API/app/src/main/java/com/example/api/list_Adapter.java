package com.example.api;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class list_Adapter extends BaseAdapter {

    Context context;
    ArrayList <products> arrayList;
    private OnItemClick mCallback;


    public list_Adapter(Context context, ArrayList<products> arrayList,OnItemClick listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.mCallback = listener;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public  View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView ==  null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.items, parent, false);
        }
        TextView name,price;
        ImageView image;
        ImageButton btn;
        name = convertView.findViewById(R.id.name);
        price = convertView.findViewById(R.id.price);
        image = convertView.findViewById(R.id.image);
        btn = convertView.findViewById(R.id.btn);
        int resID = convertView.getResources().getIdentifier(arrayList.get(position).getImage(), "drawable", context.getPackageName());
        //Log.d("vineel", String.valueOf(resID));
        if(resID != 0) {
            image.setImageResource(resID);
        }else {
            image.setImageResource(R.drawable.noimage);

        }
        name.setText(arrayList.get(position).getName());
        price.setText(arrayList.get(position).getPrice());

        final View finalConvertView = convertView;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(finalConvertView.getContext(),arrayList.get(position).getPrice(),Toast.LENGTH_SHORT).show();
                mCallback.onClick(arrayList.get(position).getPrice());


            }
        });
        return convertView;
    }

    public interface OnItemClick {
        void onClick (String value);
    }
}
