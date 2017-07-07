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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Casi on 07.07.2017.
 */

public class Adapter extends ArrayAdapter<Book> {

    Adapter(@NonNull Context context, List<Book> books) {
        super(context, 0, books);
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Book currentBook = getItem(position);
        holder.titleView.setText(currentBook.getTitle());
        holder.authorView.setText(currentBook.getAuthor());
        holder.descriptionView.setText(currentBook.getDescription());
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.book_title)
        TextView titleView;
        @BindView(R.id.author_name)
        TextView authorView;
        @BindView(R.id.book_description)
        TextView descriptionView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
