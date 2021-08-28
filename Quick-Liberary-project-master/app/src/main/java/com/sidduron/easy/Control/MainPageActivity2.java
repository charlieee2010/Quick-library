package com.sidduron.easy.Control;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.sidduron.easy.Model.Backend.Tools;
import com.sidduron.easy.Model.Entities.Book;
import com.sidduron.easy.R;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MainPageActivity2 extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private final Context context = this;
    final String myBooksFileName = "my_books.json";

    private BottomAppBar mBottomAppbar;
    private FloatingActionButton addBookFab;

    ListView myBooksListView;
    BooksListAdapter booksAdapter;
    ArrayList<Book> myBooksList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page2);

        final GoogleSignInAccount account = getIntent().getParcelableExtra(SplashScreen.googleExtra);

        mBottomAppbar = (BottomAppBar) findViewById(R.id.bottom_app_bar);
        setSupportActionBar(mBottomAppbar);

        mBottomAppbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetFragment bottomNavDrawerFragment = new BottomSheetFragment(account);

                bottomNavDrawerFragment.show(getSupportFragmentManager(), bottomNavDrawerFragment.getTag());

            }
        });

        addBookFab = (FloatingActionButton) findViewById(R.id.add_book_fab);
        addBookFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(context, CameraOnlyActivity.class), 0);
            }
        });

        myBooksListView = (ListView) findViewById(R.id.main_my_books);
        booksAdapter = new BooksListAdapter(this, myBooksList);
        myBooksListView.setAdapter(booksAdapter);

        try {
            String s = Tools.getJsonStringFromFile(myBooksFileName, context);
            ArrayList<Book> l = Tools.JsonToBookList(s);
            for (Book b : l) {
                booksAdapter.add(b);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //booksAdapter.add(new Book("12", "34", "https://upload.wikimedia.org/wikipedia/en/thumb/7/70/Harry_Potter_and_the_Order_of_the_Phoenix.jpg/220px-Harry_Potter_and_the_Order_of_the_Phoenix.jpg", "", ""));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            //When the data is from the camera
            if (requestCode == 0) {
                Bundle bundle = data.getExtras();

                //Put the image into a Bitmap object
                ArrayList<Book> books = (ArrayList<Book>) bundle.get("books");
                int arrayCount = books.size();
                //Apply to the List view
                HashSet<Book> map = new HashSet<>();
                ArrayList<Book> dup = new ArrayList<>();
                for (Book b : books) {
                    booksAdapter.add(b);
                    if (!map.add(b))
                        dup.add(b);
                }

                for (Book b : dup)
                    booksAdapter.remove(b);

                String js = Tools.BookListToJson(myBooksList);
                boolean res = Tools.saveJsonStringToFile(js, myBooksFileName, context);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bottom_appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item!=null) {
            switch (item.getItemId()) {
                //R.id.app_bar_fav -> toast("Fav menu item is clicked!")
                case R.id.app_bar_search:
                    Toast.makeText(this, "Search menu item is clicked!", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.app_bar_settings:
                    Toast.makeText(this, "Settings item is clicked!", Toast.LENGTH_SHORT).show();
                    break;
                case android.R.id.home:
                    Toast.makeText(this, "Menu item is clicked!", Toast.LENGTH_SHORT).show();
                    break;
                default:

                    Toast.makeText(this, item.getTitle() + "tt", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}