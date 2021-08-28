package com.sidduron.easy.Model.Backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.ListActivity;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.sidduron.easy.Model.Entities.Book;
import com.sidduron.easy.Model.Entities.BookAdapter;
import com.sidduron.easy.R;

import static android.os.Build.VERSION_CODES.R;

public class Tools {
    //copy the data from the searching results
    private String URL_ITEMS = "https://www.googleapis.com/demo/v1?fields=kind,items(title,author)\n";
    private static final String TAG_TITLE = "title";
    private static final String TAG_AUTHOR = "author";
    private static final String TAG_EDITION = "edition";
    private static final String TAG_ISBN = "ISBN";
    JSONArray booksDetails = null;
    ArrayList<JSONArray> booksDetailsList = new ArrayList<JSONArray>();

    public static Set<BookAdapter> GetBookAdaptersFromJSONArray(JSONArray itemsArray) throws JSONException {
        Set<BookAdapter> list = new HashSet<>();

        int i = 0;
        String title = null;
        String authors = null;

        // Look for results in the items array, exiting when both the title and author
        // are found or when all items have been checked.
        while (i < itemsArray.length() || (authors == null && title == null)) {
            // Get the current item information.
            JSONObject book = itemsArray.getJSONObject(i);
            JSONObject volumeInfo = book.getJSONObject("volumeInfo");

            // Try to get the author and title from the current item,
            // catch if either field is empty and move on.
            try {
                title = volumeInfo.getString("title");
                authors = volumeInfo.getString("authors");
                String id = book.getString("id");
                list.add(new BookAdapter(id, title));
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Move to the next item.
            i++;
        }

        return list;
    }

    public static Book GetBookFromJSONObject(JSONObject item) throws JSONException {
        //Start to get the fields
        String bookId = item.getString("id");
        JSONObject vol_info = item.getJSONObject("volumeInfo");
        String bookTitle = vol_info.getString("title");
        String bookAuthors = "";
        JSONArray auth = vol_info.getJSONArray("authors");

        for (int i = 0; i < auth.length(); i++) {
            bookAuthors += auth.get(i) + (i < auth.length() - 1 ? ", " : "");
        }

        String bookPhotoUri = "";
        try {
            JSONObject imagesLinks = vol_info.getJSONObject("imageLinks");
            bookPhotoUri = imagesLinks.getString("thumbnail");
        }
        catch (Exception er){

        }

        String bookIsbn = "";
        if(vol_info.has("industryIdentifiers")) {
            JSONArray industryIdentifiers = vol_info.getJSONArray("industryIdentifiers");
            JSONObject ISBN_Object = industryIdentifiers.getJSONObject(0);
            bookIsbn = ISBN_Object.getString("identifier");
        }

        return new Book(bookTitle, bookAuthors, bookPhotoUri, bookIsbn, null);
    }

    public void executeFixture() {
        // Call Async task to get the match fixture
        new GetFixture().execute();
    }


    private class GetFixture extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("LongLogTag")
        @Override
        protected Void doInBackground(Void... arg) {
            /*ServiceHandler serviceClient = new ServiceHandler();
            Log.d("url: ", "> " + URL_ITEMS);
            String json = serviceClient.makeServiceCall(URL_ITEMS, ServiceHandler.GET);
            // print the json response in the log
            Log.d("Get the book's details: ", "> " + json);
            if (json != null) {
                try {
                    Log.d("try", "in the try");
                    JSONObject jsonObj = new JSONObject(json);
                    Log.d("jsonObject", "new json Object");
                    // Getting JSON Array node
                    booksDetails = jsonObj.getJSONArray();
                    Log.d("json aray", "user point array");
                    int len = booksDetails.length();
                    Log.d("len", "get array length");
                    for (int i = 0; i < booksDetails.length(); i++) {
                        JSONObject c = booksDetails.getJSONObject(i);
                        String title = c.getString(TAG_TITLE);
                        Log.d("title", title);
                        String author = c.getString(TAG_AUTHOR);
                        Log.d("author", author);
                        String edition = c.getString(TAG_EDITION);
                        Log.d("edition", edition);
                        String ISBN = c.getString(TAG_ISBN);
                        Log.d("ISBN", ISBN);
                        //  hashmap for single match
                        HashMap<String, String> matchFixture = new HashMap<String, String>();
                        // adding each child node to HashMap key => value
                        //TODO: bad thing
                        booksDetails.put(Integer.parseInt(TAG_TITLE), title);
                        booksDetails.put(Integer.parseInt(TAG_AUTHOR), author);
                        booksDetails.put(Integer.parseInt(TAG_EDITION), edition);
                        booksDetails.put(Integer.parseInt(TAG_ISBN), ISBN);
                        booksDetailsList.add(booksDetails);
                    }
                } catch (JSONException e) {
                    Log.d("catch", "in the catch");
                    e.printStackTrace();
                }
            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            */

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            /*ListAdapter adapter = new SimpleAdapter(
                    Tools.this, booksDetailsList,
                    com.sidduron.easy.R.layout.list_item, new String[]{
                    TAG_TITLE, TAG_AUTHOR, TAG_EDITION, TAG_ISBN}
                    , new int[]{
                    R.id.matchId, R.id.teamA,
                    R.id.teamB}
            );
            setListAdapter(adapter);*/
        }
    }


    public static String BookListToJson(List<Book> list) {
        String res = "{" +
                "\"items\": [";
        for (Book book : list) {
            res += book.toJsonString() + ",";
        }
        res = res.substring(0, res.length() - 1) + "]}";
        return res;
    }

    /*
        public static String (list <> list){

        }
    */
    public static ArrayList<Book> JsonToBookList(String item) throws JSONException {
        ArrayList<Book> list = new ArrayList<>();
        JSONObject json = new JSONObject(item);
        JSONArray items = json.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            list.add(Book.fromJsonObject(items.getJSONObject(i)));
        }
        return list;
    }

    public static boolean saveJsonStringToFile(String json, String filename, Context c){
        try{
            writeToFile(json, filename, c);
        }
        catch (IOException e){
            return false;
        }

        return true;
    }

    public static String getJsonStringFromFile( String filename, Context c){
        try {
            return readFromFile(c, filename);
        } catch (FileNotFoundException e){
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  null;
    }


    public static void writeToFile(String data, String filename,Context context) throws IOException{
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            throw e;
        }
    }

    public static String readFromFile(Context context, String filename) throws IOException {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
            ret = "";
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
            throw e;
        }

        return ret;
    }

}


