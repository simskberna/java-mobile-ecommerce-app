package com.simsekberna.etech_app_v1.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simsekberna.etech_app_v1.Activities.LoginActivity;
import com.simsekberna.etech_app_v1.Activities.MainActivity;
import com.simsekberna.etech_app_v1.Adapters.CartAdapter;
import com.simsekberna.etech_app_v1.Models.CartModel;
import com.simsekberna.etech_app_v1.Models.Category;
import com.simsekberna.etech_app_v1.Models.User;
import com.simsekberna.etech_app_v1.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CartFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference reference;
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    List<CartModel> cartModelList;
    Float totalPrice = 0f;
    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root =  inflater.inflate(R.layout.fragment_cart, container, false);
        //init firebase auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        database  = FirebaseDatabase.getInstance();
        recyclerView = root.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        cartModelList = new ArrayList<>();
        cartAdapter = new CartAdapter(getActivity(),cartModelList);
        recyclerView.setAdapter(cartAdapter);
        reference = database.getReference("users").child(currentUser.getUid()).child("cartInfo");

        Button confirmOrder = root.findViewById(R.id.checkout);
        confirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Check();
            }
        });
       updateRec(reference,root);


        return root;
    }
    public void updateRec(DatabaseReference reference,View root){

        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalPrice = 0f;
                cartModelList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key= dataSnapshot.getKey().toString();
                    CartModel cartModel = snapshot.child(key).getValue(CartModel.class);
                    cartModelList.add(cartModel);
                    cartAdapter.notifyDataSetChanged();
                }
                for (int i = 0; i < cartModelList.size(); i++)
                {
                    totalPrice += Float.parseFloat(cartModelList.get(i).getTotalPrice());
                }
                TextView totalPriceOfCart =  root.findViewById(R.id.totalPriceOfCart);
                totalPriceOfCart.setText(String.format("%.02f",totalPrice).toString()+" TL");
                if(totalPrice <= 0f){

                    root.findViewById(R.id.empty_cart).setVisibility(View.VISIBLE);
                    root.findViewById(R.id.checkout).setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void Check(){
        if(totalPrice > 0f){
            ConfirmOrder();
        }else{
            Toast.makeText(getActivity(),"There are no products in your cart!",Toast.LENGTH_LONG).show();
        }
    }
    private void ConfirmOrder(){

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String saveCurrentDate,saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());
        String ID_cart = reference.push().getKey();
        DatabaseReference ordersRef = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(currentUser.getUid()).child(ID_cart);
        HashMap<String,Object> ordersMap = new HashMap<>();
        Float amount = totalPrice;
        ordersMap.put("totalAmount",amount.toString());
        ordersMap.put("userName",currentUser.toString());
        ordersMap.put("date",saveCurrentDate);
        ordersMap.put("time",saveCurrentTime);
        ordersMap.put("state","not shipped");

        ordersRef.updateChildren(ordersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(currentUser.getUid())
                            .child("cartInfo")
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        getView().findViewById(R.id.empty_cart).setVisibility(View.VISIBLE);
                                        getView().findViewById(R.id.checkout).setVisibility(View.INVISIBLE);
                                        TextView totalPriceOfCart =  getView().findViewById(R.id.totalPriceOfCart);
                                        totalPriceOfCart.setText("0.00 TL");
                                        Toast.makeText(getActivity(),"your final order has been placed successfully",Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                }
            }
        });
    }

}