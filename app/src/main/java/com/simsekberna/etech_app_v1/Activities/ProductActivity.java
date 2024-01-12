package com.simsekberna.etech_app_v1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simsekberna.etech_app_v1.Models.User;
import com.simsekberna.etech_app_v1.R;
import com.simsekberna.etech_app_v1.databinding.ActivityProductBinding;

import java.util.HashMap;
import java.util.Objects;

public class ProductActivity extends AppCompatActivity {

    ActivityProductBinding binding;
    Button addCartBtn;
    ImageView plusBtn;
    FirebaseDatabase database;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database  = FirebaseDatabase.getInstance();
        reference = database.getReference("users").child(currentUser.getUid()).child("cartInfo");
        Intent intent = this.getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("Name");
            String price = intent.getStringExtra("Price");
            String oldprice = intent.getStringExtra("Oldprice");
            String desc = intent.getStringExtra("Description");
            String brand = intent.getStringExtra("Brand");
            String image = intent.getStringExtra("Image");

            if(!Objects.isNull(oldprice)){
                binding.prodOldPrice.setText(oldprice+" TL");
                binding.prodOldPrice.setPaintFlags(binding.prodOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }else{
                binding.prodOldPrice.setVisibility(View.INVISIBLE);
            }

            binding.pName.setText(name);
            binding.pBrand.setText(brand);
            binding.pPrice.setText(price+" TL");
            binding.pDesc.setText(desc);
            Glide
                    .with(binding.pImg)
                    .load(image)
                    .placeholder(R.drawable.loading_spinner)
                    .into(binding.pImg);

            final String[] userName = {""};
            final String[] email = {""};

            plusBtn = findViewById(R.id.plusImg);
                plusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText qtyText =  findViewById(R.id.qty);
                        String newQuantity = qtyText.getText().toString();
                        Integer i = Integer.parseInt(newQuantity) + 1;
                        qtyText.setText(i.toString());
                    }
                });
            addCartBtn = findViewById(R.id.addCartBtn);
                    addCartBtn.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            String ID_cart = reference.push().getKey();
                            HashMap<String, String> parameters = new HashMap<>();
                            EditText qtyText =  findViewById(R.id.qty);
                            String quantity = qtyText.getText().toString();
                            parameters.put("image",image);
                            parameters.put("productName", name);
                            parameters.put("productPrice", price);
                            parameters.put("totalQuantity", quantity);
                            Float totalPrice = Float.parseFloat(price)* Float.parseFloat(quantity);
                            parameters.put("totalPrice", totalPrice.toString());
                            reference.child(ID_cart).setValue(parameters);
                            // Initialising Toast
                            Toast toast = new Toast(getApplicationContext());
                            ImageView view = new ImageView(getApplicationContext());
                            // set image resource to be shown
                            view.setImageResource(R.drawable.added_cart_popup);
                            // setting view and text to toast
                            toast.setView(view);
                            // showing toast
                            toast.show();

                            /*Toast.makeText(getApplicationContext(), "Added to Cart", Toast.LENGTH_SHORT).show();*/
                        }

                    });
        }


    }

}