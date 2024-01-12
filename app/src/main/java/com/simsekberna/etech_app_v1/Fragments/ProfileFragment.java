package com.simsekberna.etech_app_v1.Fragments;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.simsekberna.etech_app_v1.Activities.LoginActivity;
import com.simsekberna.etech_app_v1.Activities.MainActivity;
import com.simsekberna.etech_app_v1.Models.User;
import com.simsekberna.etech_app_v1.R;

import java.io.IOException;
import java.io.InputStream;

public class ProfileFragment extends Fragment {

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private static final String ARG_PARAM1 = "param1";
private static final String ARG_PARAM2 = "param2";

// TODO: Rename and change types of parameters
private String mParam1;
private String mParam2;

String displayName = null;
String email = null;
String profileLink = null;
String newPassword = null;
EditText passwTxt;
CircularImageView profile;
EditText displayNameEditText;
EditText emailText;
ProgressBar progressBar;
FloatingActionButton fab;
Uri resultUri;
private static int RESULT_LOAD_IMAGE = 1;
public ProfileFragment() {
    // Required empty public constructor
}

// TODO: Rename and change types and number of parameters
public static ProfileFragment newInstance(String param1, String param2) {
    ProfileFragment fragment = new ProfileFragment();
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


private final ActivityResultLauncher<Intent> startForMediaPickerResult = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            Intent data = result.getData();
            if (data != null && result.getResultCode() == getActivity().RESULT_OK) {
                resultUri = data.getData();
                profile.setImageURI(resultUri);
                profileLink = resultUri.toString();
            }
            else {
                    Toast.makeText(requireActivity(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
                }
            });



@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view =  inflater.inflate(R.layout.fragment_profile, container, false);

    displayNameEditText  = view.findViewById(R.id.username);
    profile = view.findViewById(R.id.profile_image);
    emailText  = view.findViewById(R.id.usermail);
    passwTxt = view.findViewById(R.id.password);
    EditText usernameTxt = view.findViewById(R.id.username);
    progressBar = view.findViewById(R.id.progressBar);
    usernameTxt.setText(this.getArguments().getString("userName"));
    EditText usermailTxt = view.findViewById(R.id.usermail);
    usermailTxt.setText(this.getArguments().getString("email"));
    String img = this.getArguments().getString("profile");
    Glide
            .with(profile)
            .load(img)
            .placeholder(R.drawable.loading_spinner)
            .into(profile);

    fab = view.findViewById(R.id.uploadImage);

    fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImagePicker.Companion.with(getActivity())
                    .crop()
                    .cropSquare()
                    .compress(1024)
                    .maxResultSize(1080,1080)
                    .createIntent(intent -> {
                startForMediaPickerResult.launch(intent);
                return null;
            });
        }
    });


    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    Button btnLogout = view.findViewById(R.id.logout);
    btnLogout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (currentUser != null)
                auth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    });
    displayName = displayNameEditText.getText().toString();

    newPassword = passwTxt.getText().toString();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    ImageView save_email = view.findViewById(R.id.save_email);
    save_email.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            view.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            if(currentUser != null){
                email = emailText.getText().toString();
                user.updateEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    User user = new User(displayName,email,profileLink);
                                    FirebaseDatabase.getInstance().getReference("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("email").setValue(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){ view.setEnabled(true);
                                                        progressBar.setVisibility(View.GONE);
                                                        // Initialising Toast
                                                        Toast toast = new Toast(getActivity().getApplicationContext());
                                                        ImageView view = new ImageView(getActivity().getApplicationContext());
                                                        // set image resource to be shown
                                                        view.setImageResource(R.drawable.profile_update_popup);
                                                        // setting view to toast
                                                        toast.setView(view);
                                                        // showing toast
                                                        toast.show();
                                                    }else{
                                                        view.setEnabled(true);
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_SHORT).show();
                                                    }

                                                }
                                            });

                                }
                            }
                        });
            }
        }
    });

    ImageView save_username = view.findViewById(R.id.save_username);
    save_username.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            displayName = displayNameEditText.getText().toString();
            view.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            if(currentUser != null){
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(displayName)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    User user = new User(displayName,email,profileLink);
                                    FirebaseDatabase.getInstance().getReference("users")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child("username").setValue(displayName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        view.setEnabled(true);
                                                        progressBar.setVisibility(View.GONE);
                                                        // Initialising Toast
                                                        Toast toast = new Toast(getActivity().getApplicationContext());
                                                        ImageView view = new ImageView(getActivity().getApplicationContext());
                                                        // set image resource to be shown
                                                        view.setImageResource(R.drawable.profile_update_popup);
                                                        // setting view to toast
                                                        toast.setView(view);
                                                        // showing toast
                                                        toast.show();
                                                    }else{
                                                        view.setEnabled(true);
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                }
                            }
                        });


            }
        }
    });
    view.findViewById(R.id.progressBar).setVisibility(View.GONE);

    ImageView save_password = view.findViewById(R.id.save_password);
    save_password.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newPassword = passwTxt.getText().toString();
            view.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            if(currentUser != null){
                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    view.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);
                                    // Initialising Toast
                                    Toast toast = new Toast(getActivity().getApplicationContext());
                                    ImageView view = new ImageView(getActivity().getApplicationContext());
                                    // set image resource to be shown
                                    view.setImageResource(R.drawable.profile_update_popup);
                                    // setting view to toast
                                    toast.setView(view);
                                    // showing toast
                                    toast.show();

                                }else{
                                    view.setEnabled(true);
                                    progressBar.setVisibility(View.GONE);

                                    Toast.makeText(getActivity(),"Error occured",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    });
    return view;

}


}