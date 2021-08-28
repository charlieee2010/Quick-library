package com.sidduron.easy.Model.Entities;

import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.sidduron.easy.Control.CameraOnlyActivity;
import com.sidduron.easy.Model.Backend.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class FindBooksResult  extends AsyncTask<String,Void, ArrayList<Book>>{
    private Set<BookAdapter> list;
    private ArrayAdapter<Book> mAdapter;
    private ArrayList<Book> books = new ArrayList<>();
    private ProgressBar mProgress;
    CameraOnlyActivity cam;

    public FindBooksResult(CameraOnlyActivity c, Set<BookAdapter> adapters, ArrayAdapter<Book> listAdapter, ProgressBar p){
        list = adapters;
        mAdapter = listAdapter;
        mProgress = p;
        cam = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mProgress != null) {
            mProgress.setVisibility(View.VISIBLE);
            mProgress.setIndeterminate(true);
        }

        if (cam != null)
            cam.registerTask(this);
    }

    @Override
    protected ArrayList<Book> doInBackground(String... strings) {
        final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes";

        for (Iterator<BookAdapter> it = list.iterator(); it.hasNext(); ) {
            // Base URI for the Books API.
            // Build up your query URI, limiting results to 10 items and printed books.
            Uri builtBookURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendPath(it.next().getId())
                    //.appendQueryParameter(MAX_RESULTS, "10")
                    //.appendQueryParameter(PRINT_TYPE, "books")
                    //.appendQueryParameter(PROJECTION, "full")
                    .build();

            URL bookRequestURL = null;
            try {
                bookRequestURL = new URL(builtBookURI.toString());


            // Open the network connection.
            HttpURLConnection bookUrlConnection = (HttpURLConnection) bookRequestURL.openConnection();
            bookUrlConnection.setRequestMethod("GET");
            bookUrlConnection.connect();

            // Get the InputStream.
            InputStream bookInputStream = bookUrlConnection.getInputStream();
            String result = getResponseString(bookInputStream);

            JSONObject js = new JSONObject(result);
            Book b = Tools.GetBookFromJSONObject(js);
            books.add(b);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return books;
    }


    private String getResponseString( InputStream inputStream) throws IOException {

        // Read the response string into a StringBuilder.
        StringBuilder builder = new StringBuilder();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
            // but it does make debugging a *lot* easier if you print out the completed buffer for debugging.
            builder.append(line + "\n");
        }

        if (builder.length() == 0) {
            // Stream was empty.  No point in parsing.
            // return null;
            return null;
        }

        return builder.toString();
    }

    @Override
    protected void onPostExecute(ArrayList<Book> booksList) {
        if (cam != null) {
            //Remove this task from the list
            cam.removeTask(this);
            if (true || cam.getOpenTaskCount() == 0 && mProgress != null) {
                mProgress.setVisibility(View.GONE);
                mProgress.setIndeterminate(true);
            }
        }
        mAdapter.addAll(booksList);

        if(mProgress != null){
            mProgress.setVisibility(View.VISIBLE);
            mProgress.setIndeterminate(true);
        }
    }
}
