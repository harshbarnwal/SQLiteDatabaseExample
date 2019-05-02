package com.tech4use.sqlitedatabaseexample;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // creating instance of the DatabaseSQLite class
    DatabaseSQLite databaseSQLite;
    EditText edtxt_username, edtxt_password;
    String s_delete_id, s_oldUsername, s_newUsername;

    // for recyclerview sqlite database
    RecyclerView recyclerView;
    CustomAdapter adapter;
    SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initiating DatabaseSQLite class
        databaseSQLite = new DatabaseSQLite(this);
        mDatabase = databaseSQLite.getWritableDatabase();

        edtxt_username = findViewById(R.id.edtxt_name);
        edtxt_password = findViewById(R.id.edtxt_password);

        // for recyclerview with sqlite database
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomAdapter(this, getAllDatas());
        recyclerView.setAdapter(adapter);

        // for swipe deleting data
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                removeItem((long) viewHolder.itemView.getTag());
            }
        }).attachToRecyclerView(recyclerView);

    }

    public void saveClick(View view) {
        String username = edtxt_username.getText().toString();
        String password = edtxt_password.getText().toString();
        if (username.equals("") || password.equals("")) {
            Toast.makeText(this, "Please fill all the field", Toast.LENGTH_SHORT).show();
        }
        else {
            // passing data to the DatabaseSQLite class to save it
            long result = databaseSQLite.InsertData(username, password);
            adapter.swapCursor(getAllDatas());
            if (result < 0)
                Toast.makeText(this, "Can't Insert the data kindly Try again", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, "Inserted data successfully", Toast.LENGTH_SHORT).show();
            edtxt_username.setText("");
            edtxt_password.setText("");
        }
    }

  /*  public void loadAllClick(View view) {
        // getting all data from the database
        Cursor cursor = databaseSQLite.getAllData();
        if ((cursor.getCount() == 0)) {
            // showing error
            showData("Error", "Database is empty");
        }
        StringBuffer buffer = new StringBuffer();
        // checking all avaialable data
        while (cursor.moveToNext()) {
            buffer.append("Id : "+cursor.getString(0)+"\n");
            buffer.append("Username : "+cursor.getString(1)+"\n");
            buffer.append("Password : "+cursor.getString(2)+"\n\n");
        }
        // showing all the data
        showData("Data", buffer.toString());
    }*/

    public Cursor getAllDatas () {

        return mDatabase.query(DatabaseSQLite.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
                );
        /*// getting all data from the database
        Cursor cursor = databaseSQLite.getAllData();
        if ((cursor.getCount() == 0)) {
            // showing error
            showData("Error", "Database is empty");
        }
        StringBuffer buffer = new StringBuffer();
        // checking all avaialable data
        while (cursor.moveToNext()) {
            buffer.append("Id : "+cursor.getString(0)+"\n");
            buffer.append("Username : "+cursor.getString(1)+"\n");
            buffer.append("Password : "+cursor.getString(2)+"\n\n");
        }
        // returing the cursor
        return cursor;*/
    }

    public void showData (String title, String Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    private void removeItem (long id) {
        mDatabase.delete(DatabaseSQLite.TABLE_NAME,
                DatabaseSQLite.COLUMN_ID + "="+id, null);
        adapter.swapCursor(getAllDatas());
    }

    public void deleteClick(View view) {
        // opening the custom input dialog to get delete id
        showDeleteDialog();
    }

    // creating custom dialog to take the input
    public void showDeleteDialog () {
        // get dialog xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.delete_input_dialog, null);
        // building the dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Enter the Id");

        // getting the text
        final EditText edtxt_delete_id = (EditText) promptView.findViewById(R.id.edtxt_delete_id);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        s_delete_id = edtxt_delete_id.getText().toString();
                        if (s_delete_id.equals("")) {
                            Toast.makeText(MainActivity.this, "Please enter the number first",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            int count = databaseSQLite.DeleteData(s_delete_id);
                            Toast.makeText(getApplicationContext(), "Successfully deleted " + s_delete_id + ", deleted rows = " + count
                                    , Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void updateClick(View view) {
        // creating the custom update dialog
        showUpdateDialog();
    }

    public void showUpdateDialog() {
        // get dialog xml view
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.update_input_dialog,null);

        // building the dialog
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        // setting the custom view
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle("Enter old and the new Username");
        // getting the text
        final EditText edtxt_oldUsername = (EditText) view.findViewById(R.id.edtxt_oldUsername);
        final EditText edtxt_newUsername = (EditText) view.findViewById(R.id.edtxt_newUsername);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        s_oldUsername = edtxt_oldUsername.getText().toString();
                        s_newUsername = edtxt_newUsername.getText().toString();
                        if (s_oldUsername.equals("") || s_newUsername.equals("")) {
                            Toast.makeText(MainActivity.this, "Please fill all fields first",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            int count = databaseSQLite.UpdateData(s_oldUsername, s_newUsername);
                            Toast.makeText(MainActivity.this, count+" data updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
}
