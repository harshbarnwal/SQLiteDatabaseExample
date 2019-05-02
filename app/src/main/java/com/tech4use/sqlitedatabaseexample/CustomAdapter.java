package com.tech4use.sqlitedatabaseexample;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    Context context;
    // getting the data from sqlite in form of a cursor
    Cursor cursor;

    public CustomAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    // creating the viewholder
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        // getting refrence to child layout's items
        TextView text_firstName, text_lastName;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            text_firstName = itemView.findViewById(R.id.display_firstName);
            text_lastName = itemView.findViewById(R.id.display_lastName);
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // inflating and adding the xml child layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_list, viewGroup, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }
        // storing data from sqlite to string
        String firstName = cursor.getString(cursor.getColumnIndex(DatabaseSQLite.COLUMN_firstNAME));
        String lastName = cursor.getString(cursor.getColumnIndex(DatabaseSQLite.COLUMN_lastNAME));

        // for deleting data on swipe
        long swipe_id = cursor.getLong(cursor.getColumnIndex(DatabaseSQLite.COLUMN_ID));
        customViewHolder.itemView.setTag(swipe_id);

        // setting the data
        customViewHolder.text_firstName.setText(firstName);
        customViewHolder.text_lastName.setText(lastName);
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    // swping cursor to get the new data
    public void swapCursor (Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;

        if (newCursor != null ) {
            notifyDataSetChanged();
        }
    }
}
