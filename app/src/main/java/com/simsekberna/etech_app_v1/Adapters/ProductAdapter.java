package com.simsekberna.etech_app_v1.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simsekberna.etech_app_v1.Models.Product;
import com.simsekberna.etech_app_v1.R;

import java.util.ArrayList;
import java.util.Objects;

public class    ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    Context context;
    ArrayList<Product> list;
    private onProductClickListener pOnClickListener;
    public ProductAdapter(Context context, ArrayList<Product> list,onProductClickListener onProductClickListener) {
        this.context = context;
        this.list = list;
        this.pOnClickListener = onProductClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.product,parent,false);
        return new ProductViewHolder(v,pOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = list.get(position);
        holder.prodName.setText(product.getName());
        String disc = product.getDiscount();

        if(!Objects.equals(disc, "0")){
            holder.prodDisc.setText(product.getDiscount()+" %");
            holder.oldPrice.setText(product.getOldprice()+" TL");
            holder.oldPrice.setPaintFlags(holder.oldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }else{
            holder.discountBadge.setVisibility(View.INVISIBLE);
            holder.prodDisc.setVisibility(View.INVISIBLE);
            holder.oldPrice.setVisibility(View.INVISIBLE);
            holder.discountArrow.setVisibility(View.INVISIBLE);
        }
        holder.prodCat = (product.getCategory());
        holder.prodDesc = (product.getDescription());
        holder.prodBrand = (product.getBrand());
        holder.prodPrice.setText(product.getPrice()+" TL");
        Glide
                .with(holder.prodImg)
                .load(product.getImage())
                .placeholder(R.drawable.loading_spinner)
                .into(holder.prodImg);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView prodName, prodPrice,prodDisc,discountBadge,oldPrice;
        String prodDesc,prodBrand,prodCat;
        ImageView prodImg,discountArrow;
        onProductClickListener productClickListener;
        public ProductViewHolder(@NonNull View itemView,onProductClickListener productClickListener) {
            super(itemView);
            this.productClickListener = productClickListener;
            discountBadge = itemView.findViewById(R.id.discountBadge);
            prodImg = itemView.findViewById(R.id.productImg);
            prodName = itemView.findViewById(R.id.productName);
            prodDisc = itemView.findViewById(R.id.productDisc);
            prodPrice = itemView.findViewById(R.id.productPrice);
            oldPrice = itemView.findViewById(R.id.productOldprice);
            discountArrow = itemView.findViewById(R.id.discountArrow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            productClickListener.onProductClick(getAdapterPosition());
        }
    }
    public interface onProductClickListener{
        void onProductClick(int position);

    }

}
