package com.sidduron.easy.Model.Entities;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.jar.Attributes;

public class Book implements Parcelable {

    private String NameBook;
    private String IsbnBook;
    private String AuthorBook;
    private String UriBook;
    private String CategoryBook;

    public Book (String Name ,String Author,String Uri,String Isbn,String Category)
    {
        NameBook=Name;
        AuthorBook=Author == null ? "" :Author;
        UriBook=Uri == null ? "" :Uri.replace("http:", "https:");
        IsbnBook=Isbn == null ? "" : Isbn;
        CategoryBook=Category == null ? "" : Category;
    }

    protected Book(Parcel in) {
        String[] data = new String[5];
        in.readStringArray(data);
        NameBook = data[0];
        AuthorBook = data[1];
        IsbnBook = data[2];
        UriBook = data[3];
        CategoryBook = data[4];
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getNameBook()
    {
        return NameBook;
    }

    public void setNameBook(String nameBook) {
        NameBook =nameBook;
    }

    public String getIsbnBook() {
        return IsbnBook;
    }

    public void setIsbnBook(String isbnBook) {
        IsbnBook = isbnBook;
    }

    public String getAuthorBook() {
        return AuthorBook;
    }

    public void setAuthorBook(String authorBook) {
        AuthorBook = authorBook;
    }

    public String getUriBook() {
        return UriBook;
    }

    public void setUriBook(String UriBook) {
        this.UriBook = UriBook;
    }

    public String getCategoryBook() {
        return CategoryBook;
    }

    public void setCategoryBook(String categoryBook) {
        CategoryBook = categoryBook;
    }

    public String toJsonString() {
        return "{\"title\": \"" + getNameBook() + "\"" +
                ",\"author\": \"" + getAuthorBook() + "\"" +
                ",\"isbn\": \"" + getIsbnBook() + "\""
                + ",\"category\": \"" + getCategoryBook() + "\"" +
                ",\"uri\": \"" + getUriBook() + "\"}" ;
    }
    
    public static Book fromJsonObject(JSONObject jsonObject)throws JSONException
    { 
       
        String title=jsonObject.getString("title");
        String author=jsonObject.getString("author");
        String isbn=jsonObject.getString("isbn");
        String Category=jsonObject.getString("category");
        String Uri=jsonObject.getString("uri");
        Uri = Uri.replace("http:","https:");
        return new Book(title,author,Uri,isbn,Category) ;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.getNameBook(),
                this.getAuthorBook(),
                this.getIsbnBook(),
                this.getUriBook(),
                this.getCategoryBook()});
    }


}


