package com.simsekberna.etech_app_v1.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simsekberna.etech_app_v1.Models.CartModel;
import com.simsekberna.etech_app_v1.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

Context context;
List<CartModel> cartModelList;
FirebaseDatabase database;
FirebaseAuth auth = FirebaseAuth.getInstance();
FirebaseUser currentUser = auth.getCurrentUser();
DatabaseReference reference;

public CartAdapter(Context context, List<CartModel> cartModelList){
    this.context = context;
    this.cartModelList = cartModelList;
}

@NonNull
@Override
public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_cart_item,parent,false));
}

@Override
public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
    database  = FirebaseDatabase.getInstance();
    reference = database.getReference("users").child(currentUser.getUid()).child("cartInfo");


    holder.name.setText(cartModelList.get(position).getProductName());
    holder.price.setText(cartModelList.get(position).getProductPrice()+ " TL");
    holder.quantity.setText(cartModelList.get(position).getTotalQuantity());
    holder.totalPrice.setText(String.valueOf(cartModelList.get(position).getTotalPrice()));
    Glide
            .with(holder.image)
            .load(cartModelList.get(position).getImage())
            .placeholder(R.drawable.loading_spinner)
            .into(holder.image);
    database  = FirebaseDatabase.getInstance();
    reference = database.getReference("users").child(currentUser.getUid()).child("cartInfo");

        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String selected = String.valueOf(cartModelList.get(position).getImage());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        final HashMap<String, Object> dataMap = (HashMap<String, Object>) snapshot.getValue();
                        for (String key : dataMap.keySet()){
                            Object data = dataMap.get(key);
                            try{
                                HashMap<String, Object> test = (HashMap<String, Object>) data;
                                for(Map.Entry<String, Object> entry : test.entrySet()){
                                    if(entry.getValue() == selected){

                                       String id = reference.child(key).getKey().toString();
                                        reference.child(id).removeValue();
                                        cartModelList.remove(cartModelList.get(position));
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, cartModelList.size());
                                        // Initialising Toast
                                        Toast toast = new Toast(context.getApplicationContext());
                                        ImageView view = new ImageView(context.getApplicationContext());
                                        // set image resource to be shown
                                        view.setImageResource(R.drawable.item_deleted_popup);
                                        // setting view and text to toast
                                        toast.setView(view);
                                        // showing toast
                                        toast.show();

                                        break;
                                    }
                                }

                            }catch (ClassCastException cce){
                            }
                        }
                        // do something

                    }else {
                        // when empty data
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    });


}

@Override
public int getItemCount() {
    return cartModelList.size();
}

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView name,price,quantity,totalPrice;
    ImageView image,deleteItem;
    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        deleteItem = itemView.findViewById(R.id.delete);
        image = itemView.findViewById(R.id.cartImg);
        name  = itemView.findViewById(R.id.prodName);
        price = itemView.findViewById(R.id.prodPrice);
        quantity = itemView.findViewById(R.id.prodQuantity);
        totalPrice = itemView.findViewById(R.id.totalPrice);

    }
}

}
