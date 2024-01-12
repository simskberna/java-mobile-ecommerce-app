package com.simsekberna.etech_app_v1.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simsekberna.etech_app_v1.Adapters.CategoryAdapter;
import com.simsekberna.etech_app_v1.Adapters.ProductAdapter;
import com.simsekberna.etech_app_v1.Fragments.CartFragment;
import com.simsekberna.etech_app_v1.Fragments.DefaultFragment;
import com.simsekberna.etech_app_v1.Fragments.OrdersFragment;
import com.simsekberna.etech_app_v1.Fragments.ProfileFragment;
import com.simsekberna.etech_app_v1.Models.Category;
import com.simsekberna.etech_app_v1.Models.Product;
import com.simsekberna.etech_app_v1.Models.User;
import com.simsekberna.etech_app_v1.R;
import com.simsekberna.etech_app_v1.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ProductAdapter.onProductClickListener, CategoryAdapter.onCategoryListener {
    RecyclerView recyclerView;
    ProductAdapter adapter;
    public static ImageView imageView;
    ArrayList<Product> list;
    CategoryAdapter categoryAdapter;
    ArrayList<Category> catList;

    TextView textHello;
    FirebaseDatabase database;
    DatabaseReference reference;
    ActivityMainBinding binding;
    NavigationView nav;
    DrawerLayout drawerLayout;
    Toolbar toolbar;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawerLayout = findViewById(R.id.drawable_layout);
        nav = findViewById(R.id.navView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.menu_drawer_open,R.string.menu_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        //init firebase auth
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

       //textHello = findViewById(R.id.hello);
        final String[] userName = {""};
        final String[] email = {""};
        final String[] profilePhoto = {""};
        database  = FirebaseDatabase.getInstance();
        try{
            reference = database.getReference("users").child(currentUser.getUid());
        }
        catch(Exception e){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
        }
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null){
                    userName[0] =  user.username;
                    email[0] =  user.email;
                    profilePhoto[0] = user.profile;;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        nav.bringToFront();
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.homeOption:
                        showMainActivity();
                        break;
                    case R.id.account:

                        Bundle bundle = new Bundle();
                        bundle.putString("userName",userName[0]);
                        bundle.putString("profile",profilePhoto[0]);
                        bundle.putString("email",email[0]);
                        ProfileFragment profileFragment = new ProfileFragment();
                        profileFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.navHostFragment,
                                profileFragment).commit();
                        break;
                    case R.id.basket:
                        getSupportFragmentManager().beginTransaction().replace(R.id.navHostFragment,
                                new CartFragment()).commit();
                        break;
                    case R.id.orders:
                        getSupportFragmentManager().beginTransaction().replace(R.id.navHostFragment,
                                new OrdersFragment()).commit();
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });



        if(currentUser == null){
            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        /*to display categories*/
        recyclerView = findViewById(R.id.category_list);
        reference = FirebaseDatabase.getInstance().getReference("Category");
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        catList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this,catList,this);
        recyclerView.setAdapter(categoryAdapter);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Category category = dataSnapshot.getValue(Category.class);

                    catList.add(category);
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        /* to display categories*/



        /*to display products*/
        recyclerView = findViewById(R.id.cat_product_list);
        reference = FirebaseDatabase.getInstance().getReference("PopularProducts");
        recyclerView.setHasFixedSize(true);
        recyclerView.setClickable(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        list = new ArrayList<>();
        adapter = new ProductAdapter(this,list,this);
        recyclerView.setAdapter(adapter);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);
        reference.addValueEventListener(new ValueEventListener() {
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
    private void showMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
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

    @Override
    public void onCategoryClick(int position) {
        Intent intent =  new Intent(this,CategoryActivity.class);
        intent.putExtra("Name",catList.get(position).getName());

        startActivity(intent);
    }

     


}