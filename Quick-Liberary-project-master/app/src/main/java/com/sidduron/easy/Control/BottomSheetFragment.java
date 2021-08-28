package com.sidduron.easy.Control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;
import com.sidduron.easy.R;

/**
 * Class for the Main menu (bottom app bar menu)
 *
 * This class responsible on the main page menu - to be response on the menu creation and clicking responses
 */

public class BottomSheetFragment extends BottomSheetDialogFragment {

    Context context;

    NavigationView navigationView;

    //User data fields
    TextView accountName, accountEmail;
    ImageView profileImage;

    //Connected google account
    private GoogleSignInAccount account;

    //CTOR
    public BottomSheetFragment(GoogleSignInAccount acc){
        account = acc;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet, container, false);
        context = getActivity();// inflater.getContext();//view.getContext();

        accountName = (TextView) view.findViewById(R.id.full_name);
        accountEmail = (TextView) view.findViewById(R.id.email);
        profileImage = (ImageView) view.findViewById(R.id.profile_image);

        navigationView = (NavigationView) view.findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item != null){
                    switch (item.getItemId()){
                        case R.id.navigation_logout:
                            Toast.makeText(view.getContext(), "Logout is clicked!", Toast.LENGTH_SHORT).show();
                            signOut();
                            break;
                        case R.id.navigation_exit:
                            Toast.makeText(view.getContext(), "Exit is clicked!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }

                return false;
            }
        });

        return  view;
    }

    /**
     * Sign out menu item's
     */
    private void signOut() {
        GoogleSignInClient mGoogleSignInClient;
        //Set google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getView().getContext(), gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener((Activity) context, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //
                        startActivity(new Intent(context, SplashScreen.class));
                        account = null;
                        ((Activity) context).finish();
                    }
                });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Apply account data to the UI field
        setAccountUI();
    }

    private void setAccountUI(){
        if(account != null) {
            accountName.setText(account.getDisplayName());
            accountEmail.setText(account.getEmail());
            Uri accountPhotoUri = account.getPhotoUrl();

            //Apply profile image to the ImageView item from the web URI
            Glide.with(getView().getContext()).load(String.valueOf(accountPhotoUri)).into(profileImage);
        }
    }

}
