package com.simsekberna.etech_app_v1.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simsekberna.etech_app_v1.Adapters.CartAdapter;
import com.simsekberna.etech_app_v1.Adapters.OrderAdapter;
import com.simsekberna.etech_app_v1.Models.CartModel;
import com.simsekberna.etech_app_v1.Models.OrderModel;
import com.simsekberna.etech_app_v1.R;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    FirebaseDatabase database;
    DatabaseReference reference;
    RecyclerView recyclerView;
    OrderAdapter orderAdapter;
    List<OrderModel> orderModelList;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrdersFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static OrdersFragment newInstance(String param1, String param2) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_orders, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        database  = FirebaseDatabase.getInstance();
        recyclerView = root.findViewById(R.id.ordersRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderModelList = new ArrayList<>();
        orderAdapter = new OrderAdapter(getActivity(),orderModelList);
        recyclerView.setAdapter(orderAdapter);
        reference = database.getReference("Orders").child(currentUser.getUid());


        updateRec(reference,root);
        return root;
    }
    public void updateRec(DatabaseReference reference,View root){
        reference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    String key= dataSnapshot.getKey().toString();
                    OrderModel orderModel = snapshot.child(key).getValue(OrderModel.class);
                    orderModelList.add(orderModel);
                    orderAdapter.notifyDataSetChanged();
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}