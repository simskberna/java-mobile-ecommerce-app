package com.simsekberna.etech_app_v1.ViewHolders;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.simsekberna.etech_app_v1.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView category_name;
    public ImageView category_img;
    public AdapterView.OnItemClickListener itemClickListener;

    public void setItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public CategoryViewHolder(View itemView) {
        super(itemView);
        category_name = (TextView) itemView.findViewById(R.id.categoryName);
        category_img = (ImageView) itemView.findViewById(R.id.categoryImg);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        /**/
    }
}


