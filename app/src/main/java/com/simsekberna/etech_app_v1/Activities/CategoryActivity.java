package com.simsekberna.etech_app_v1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simsekberna.etech_app_v1.Adapters.ProductAdapter;
import com.simsekberna.etech_app_v1.Models.Product;
import com.simsekberna.etech_app_v1.R;
import com.simsekberna.etech_app_v1.databinding.ActivityCategoryBinding;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity implements ProductAdapter.onProductClickListener{
    ActivityCategoryBinding binding;
    RecyclerView recyclerView;
    ProductAdapter adapter;
    DatabaseReference reference;
    ArrayList<Product> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = this.getIntent();
        String name = "";
        if (intent != null) {
           name = intent.getStringExtra("Name");
            binding.catName.setText(name);
        }
        /*to display products*/
        recyclerView = findViewById(R.id.catProductList);
        reference = FirebaseDatabase.getInstance().getReference("Product");
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2,LinearLayoutManager.VERTICAL,false));

        list = new ArrayList<>();
        adapter = new ProductAdapter(this,list,this);
        recyclerView.setAdapter(adapter);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        String finalName = name;
        reference.orderByChild("Category").equalTo(finalName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    list.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /*to display products*/


    }

    @Override
    public void onProductClick(int position) {
        Intent intent =  new Intent(this,ProductActivity.class);
        intent.putExtra("Name", list.get(position).getName());
        intent.putExtra("Brand", list.get(position).getBrand());
        intent.putExtra("Description", list.get(position).getDescription());
        intent.putExtra("Price", list.get(position).getPrice());
        intent.putExtra("Oldprice",list.get(position).getOldprice());
        intent.putExtra("Image", list.get(position).getImage());

        startActivity(intent);
    }

}