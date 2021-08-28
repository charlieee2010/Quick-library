package com.sidduron.easy.Control;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.sidduron.easy.Control.CameraActivity;
import com.sidduron.easy.R;

public class SplashScreen extends AppCompatActivity {
    private final String TAG = "Splash Screen Log";

    private ProgressBar progress;
    GoogleSignInClient mGoogleSignInClient;
    // Set the dimensions of the sign-in button.
    SignInButton signInButton;
    int RC_SIGN_IN = 0;
    public static final String googleExtra = "GOOGLE_ACCOUNT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        progress = (ProgressBar)findViewById(R.id.SplashProgressBar);

        progress.setActivated(true);
        progress.setIndeterminate(true);
        progress.setVisibility(View.GONE);


        //Set google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.sign_in_button:
                        signIn();
                        //Start the progress bar
                        progress.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        progress.setVisibility(View.GONE);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            moveToMain(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode() + " - " + GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));

            //updateUI(null);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.

        progress.setVisibility(View.VISIBLE);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);


        moveToMain(account);
    }


    private  void moveToMain(GoogleSignInAccount account){
        if(account != null) {
            Toast.makeText(this, "Welcome " + account.getDisplayName(), Toast.LENGTH_SHORT).show();
            Intent main = new Intent(this, MainPageActivity2.class);
            main.putExtra(googleExtra, account);
            startActivity(main);
            finish();
        }
        else {
            progress.setVisibility(View.GONE);
        }
    }
}