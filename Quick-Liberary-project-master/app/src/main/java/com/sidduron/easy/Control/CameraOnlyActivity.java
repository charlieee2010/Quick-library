package com.sidduron.easy.Control;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.sidduron.easy.Model.Backend.Tools;
import com.sidduron.easy.Model.Entities.Book;
import com.sidduron.easy.Model.Entities.BookAdapter;
import com.sidduron.easy.Model.Entities.FetchBook;
import com.sidduron.easy.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CameraOnlyActivity extends AppCompatActivity {

    private final int CaptureImageCode = 101;
    private Context context = this;

    ImageView imageResult;
    TextView textResult;
    TextView mTitleText, mAuthorText;
    ListView booksListView;
    Button addButton;
    ProgressBar progressBar;

    private  ArrayList<AsyncTask> openTaskCount = new ArrayList<>();
    public void registerTask(AsyncTask t){
        if(t != null)
            openTaskCount.add(t);
    }

    public void removeTask(AsyncTask t){
        if(t != null)
            openTaskCount.remove(t);
    }

    public int getOpenTaskCount(){
        return openTaskCount.size();
    }

    Set<Book> selectedBooks = new HashSet<>();

    ArrayAdapter<Book> booksAdapter;
    final List<Book> booksList = new ArrayList<Book>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_only);
        imageResult = (ImageView) findViewById(R.id.image_result);
        textResult = (TextView) findViewById(R.id.cam_only_text_result);

        mTitleText = (TextView) findViewById(R.id.bookTitle);
        mAuthorText = (TextView) findViewById(R.id.bookAuthor);

        progressBar = (ProgressBar) findViewById(R.id.addBooksProgress);

        addButton = (Button) findViewById(R.id.addBooksButton);
        addButton.setEnabled(false);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    ArrayList<Book> list = new ArrayList<>();
                    //Save the books list to json file
                    for (Iterator<Book> it = selectedBooks.iterator(); it.hasNext(); ) {
                        list.add(it.next());
                    }
                    String jsonResult = Tools.BookListToJson(list);
                    //Tools.saveJsonStringToFile(jsonResult);

                    Intent resultIntent = new Intent();

                    //return the added books to the main activity
                    resultIntent.putExtra("books", list);
                    setResult(Activity.RESULT_OK, resultIntent);

                    //Hide the progress bar
                    progressBar.setVisibility(View.GONE);

                    //Go back to the main page
                    finish();
                } catch (Exception ec) {

                }
            }
        });

        setBooksListView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
            }
        }
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, 101);

        //booksAdapter.add(new Book("Moshe", "J.k. Rowling", "https://books.google.com/books?id=zyTCAlFPjgYC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api", "3Iyrt", ""));
    }

    private void setBooksListView() {
        //Set the var.
        booksListView = (ListView) findViewById(R.id.resultBooksListView);

        booksAdapter = new ArrayAdapter<Book>(this, R.layout.list_item, booksList) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = View.inflate(CameraOnlyActivity.this,
                            R.layout.list_item,
                            null);
                }


                TextView titleText = (TextView) convertView.findViewById(R.id.bookprofileTitle);
                TextView authorText = (TextView) convertView.findViewById(R.id.bookprofileAuthor);
                TextView isbnText = (TextView) convertView.findViewById(R.id.bookprofileISBN);
                ImageView imageView = (ImageView) convertView.findViewById(R.id.bookProfileImage);
                //CheckBox ck = (CheckBox) convertView.findViewById(R.id.checkBox);

                Book book = booksList.get(position);

                titleText.setText(book.getNameBook());
                authorText.setText(book.getAuthorBook());
                isbnText.setText(book.getIsbnBook());

                //Apply profile image to the ImageView item from the web URI
                Glide.with(convertView.getContext()).load(book.getUriBook().replace("http:", "https:")).into(imageView);

                return convertView;
            }
        };



        /*booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Book selected = booksList.get(position);
                if (selected != null) {


                }
            }
        });*/


        booksListView.setItemsCanFocus(false);
        booksListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        booksListView.setAdapter(booksAdapter);

        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SparseBooleanArray checked = booksListView.getCheckedItemPositions();
                //ArrayList<String> selectedItems = new ArrayList<String>();
                for (int it = 0; it < checked.size(); it++) {
                    // Item position in adapter
                    int position = checked.keyAt(it);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(it)) {
                        selectedBooks.add(booksAdapter.getItem(position));
                    }
                }
                Toast.makeText(context, "selected: " + String.valueOf(booksListView.getCheckedItemCount()), Toast.LENGTH_SHORT).show();
                addButton.setText("Add(" + String.valueOf(booksListView.getCheckedItemCount() + ")"));
                addButton.setEnabled(booksListView.getCheckedItemCount() > 0 ? true:false);

            }
        });
    }



    /**
     * Function that handling the camera photo capturing and sending the photo to the OCR Api for analizing.
     *
     * @param requestCode the type of the result we are excepting for
     * @param resultCode
     * @param data        received data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //When the data is from the camera
        if (requestCode == CaptureImageCode) {
            Bundle bundle = data.getExtras();

            //Put the image into a Bitmap object
            Bitmap bitmap = (Bitmap) bundle.get("data");
            //Apply to the image view
            imageResult.setImageBitmap(bitmap);


            //Send the image to OCR api
            FirebaseVisionImage firebaseImage = FirebaseVisionImage.fromBitmap(bitmap);
            FirebaseVision firebaseVision = FirebaseVision.getInstance();

            //Init. the text recognizer
            FirebaseVisionTextRecognizer textRecognizer = firebaseVision.getOnDeviceTextRecognizer();
            //set the operation in a task
            Task<FirebaseVisionText> firebaseTextTask = textRecognizer.processImage(firebaseImage);
            //add task's listeners
            firebaseTextTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            firebaseTextTask.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    String res = firebaseVisionText.getText();
                    textResult.setText(res);
                    //TODO: send the text to Google Books API

                    for (FirebaseVisionText.TextBlock t : firebaseVisionText.getTextBlocks())
                        searchBooks(t.getText());
                }
            });


        }

    }

    public void searchBooks(String queryString) {
        // Get the search string from the input field.
        //String queryString = mBookInput.getText().toString();

        // Hide the keyboard when the button is pushed.
        /*InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);*/

        // Check the status of the network connection.
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If the network is active and the search field is not empty, start a FetchBook AsyncTask.
        if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
            new FetchBook(this, mTitleText, mAuthorText, booksAdapter, progressBar).execute(queryString);
        }
        // Otherwise update the TextView to tell the user there is no connection or no search term.
        else {
            if (queryString.length() == 0) {
                mAuthorText.setText("");
                mTitleText.setText(R.string.no_search_term);
            } else {
                mAuthorText.setText("");
                mTitleText.setText(R.string.no_network);
            }
        }
    }
}