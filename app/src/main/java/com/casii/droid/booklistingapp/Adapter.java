package com.casii.droid.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Casi on 07.07.2017.
 */

public class Adapter extends ArrayAdapter<Book> {

    Adapter(@NonNull Context context, List<Book> books) {
        super(context, 0, books);
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book currentBook = getItem(position);
        TextView titleView = (TextView) listItemView.findViewById(R.id.book_title);
        TextView authorView = (TextView) listItemView.findViewById(R.id.author_name);
        TextView descriptionView = (TextView) listItemView.findViewById(R.id.book_description);
        titleView.setText(currentBook.getTitle());
        authorView.setText(currentBook.getAuthor());
        descriptionView.setText(currentBook.getDescription());
        return listItemView;
    }
}
