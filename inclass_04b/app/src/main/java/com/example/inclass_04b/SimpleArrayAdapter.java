package com.example.inclass_04b;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SimpleArrayAdapter extends ArrayAdapter<Card_details> {
    private final Context context;
    private final ArrayList<Card_details> card_details;

    public SimpleArrayAdapter(@NonNull Context context, ArrayList<Card_details> card_details) {
        super(context, R.layout.card_list_custom, card_details);
        this.context=context;
        this.card_details=card_details;

    }

    @Override
    public int getCount() {
        return card_details.size();
    }
    @Override
    public Card_details getItem(int position){
        return card_details.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView==null){
            convertView=LayoutInflater.from(context).inflate(R.layout.card_list_custom,parent,false);


        }
        TextView t_view=convertView.findViewById(R.id.name);
        TextView last_4=convertView.findViewById(R.id.last4);
        String name=card_details.get(position).name;
        String last4=card_details.get(position).last_4;
        t_view.setText(name);
        last_4.setText(last4);
        return convertView;
    }
}
