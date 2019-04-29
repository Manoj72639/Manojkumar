package com.example.sankar.sqllitedatabase;

import android.content.Intent;
import android.database.Cursor;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText id, name, email, phone;
    Button adddata;
    Button delete;
    Button update;
    Button view,next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {

            myDb = new DatabaseHelper(this);

            id = (EditText) findViewById(R.id.id);
            name = (EditText) findViewById(R.id.name);
            email = (EditText) findViewById(R.id.email);
            phone = (EditText) findViewById(R.id.phone);
            adddata = (Button) findViewById(R.id.adddata);
            delete = (Button) findViewById(R.id.delete);
            update = (Button) findViewById(R.id.update);
            next = (Button) findViewById(R.id.next);
            view = (Button) findViewById(R.id.view);

            adddata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isInserted = myDb.insertData(id.getText().toString(), name.getText().toString(), email.getText().toString(), phone.getText().toString());
                    if (isInserted == true)
                        Toast.makeText(MainActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MainActivity.this, "Data not Inserted", Toast.LENGTH_LONG).show();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Integer deletedRows = myDb.deleteData(id.getText().toString());
                    if (deletedRows > 0)
                        Toast.makeText(MainActivity.this, "Data Deleted", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MainActivity.this, "Data not Deleted", Toast.LENGTH_LONG).show();
                }
            });

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean isUpdate = myDb.updateData(id.getText().toString(),
                            name.getText().toString(),
                            email.getText().toString(), phone.getText().toString());
                    if (isUpdate == true)
                        Toast.makeText(MainActivity.this, "Data Update", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(MainActivity.this, "Data not Updated", Toast.LENGTH_LONG).show();
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this,MainActivity1.class);
                    startActivity(intent);
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor res = myDb.getAllData();
                    if (res.getCount() == 0) {
                        // show message
                        showMessage("Error", "Nothing found");
                        return;
                    }

                    StringBuffer buffer = new StringBuffer();
                    while (res.moveToNext()) {
                        buffer.append("Id :" + res.getString(0) + "\n");
                        buffer.append("Name :" + res.getString(1) + "\n");
                        buffer.append("Email :" + res.getString(2) + "\n");
                        buffer.append("Phone :" + res.getString(3) + "\n\n");
                    }

                    // Show all data
                    showMessage("Data", buffer.toString());
                }


            });

        } catch (Exception e) {
        }


    }
    public void showMessage (String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }


}