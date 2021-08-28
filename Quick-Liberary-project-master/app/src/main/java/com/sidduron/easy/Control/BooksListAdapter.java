package com.sidduron.easy.Control;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sidduron.easy.Model.Entities.Book;
import com.sidduron.easy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BooksListAdapter extends ArrayAdapter<Book> {


    public BooksListAdapter(Context context, ArrayList<Book> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Book book = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        // Lookup view for data population
        TextView bookName = (TextView) convertView.findViewById(R.id.bookprofileTitle);
        TextView bookAuthor = (TextView) convertView.findViewById(R.id.bookprofileAuthor);
        TextView bookIsbn = (TextView) convertView.findViewById(R.id.bookprofileISBN);
        ImageView bookImage = (ImageView) convertView.findViewById(R.id.bookProfileImage);

        // Populate the data into the template view using the data object
        bookName.setText(book.getNameBook());
        bookAuthor.setText(book.getAuthorBook());
        bookIsbn.setText(book.getIsbnBook());

        if(book.getUriBook() != null && !book.getUriBook().isEmpty()) {
            Uri accountPhotoUri = Uri.parse(book.getUriBook());
            /*Picasso.with(convertView.getContext()).load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQBxTEtIzTykWwckXBAk9Z0GKePebU8A452-tR51ZgGnqXm6pqf")
                .placeholder(R.drawable.books)
               .into(bookImage);*/

            //Apply profile image to the ImageView item from the web URI
           Glide.with(convertView.getContext()).load(String.valueOf(accountPhotoUri)).placeholder(R.drawable.books).dontAnimate().into(bookImage);
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
