package com.simsekberna.etech_app_v1.ViewHolders;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.simsekberna.etech_app_v1.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView product_name;
    public TextView product_price;
    public TextView product_oldPrice;
    public String product_desc;
    public String product_category;
    public TextView product_discount;
    public ImageView product_image;
    public String product_brand;

    public AdapterView.OnItemClickListener itemClickListener;

    public void setItemClickListener(AdapterView.OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public ProductViewHolder(View itemView) {
        super(itemView);
        product_oldPrice = (TextView) itemView.findViewById(R.id.productOldprice);
        product_name = (TextView) itemView.findViewById(R.id.productName);
        product_image = (ImageView) itemView.findViewById(R.id.productImg);
        product_discount = (TextView) itemView.findViewById(R.id.productDisc);
        product_price = (TextView) itemView.findViewById(R.id.productPrice);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
     /**/
    }
}


