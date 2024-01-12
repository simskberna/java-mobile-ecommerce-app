package com.simsekberna.etech_app_v1.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.simsekberna.etech_app_v1.Models.Category;
import com.simsekberna.etech_app_v1.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    Context context;
    ArrayList<Category> catList;
    private onCategoryListener cOnCategoryListener;
    public CategoryAdapter(Context context, ArrayList<Category> catList, onCategoryListener onCategoryListener) {
        this.context = context;
        this.catList = catList;
        this.cOnCategoryListener = onCategoryListener;
    }

    @NonNull
    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.category,parent,false);
        return new CategoryAdapter.CategoryViewHolder(v,cOnCategoryListener);
    }


    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.CategoryViewHolder holder, int position) {
        Category category = catList.get(position);
        holder.catName.setText(category.getName());
        Glide
                .with(holder.catImg)
                .load(category.getImage())
                .placeholder(R.drawable.loading_spinner)
                .into(holder.catImg);
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView catName;
        ImageView catImg;
        onCategoryListener onCategoryListener;

        public CategoryViewHolder(@NonNull View itemView, onCategoryListener onCategoryListener) {
            super(itemView);
            catImg = itemView.findViewById(R.id.categoryImg);
            catName = itemView.findViewById(R.id.categoryName);
            this.onCategoryListener = onCategoryListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onCategoryListener.onCategoryClick(getAdapterPosition());
        }
    }
    public interface onCategoryListener{
        void onCategoryClick(int position);
    }
}