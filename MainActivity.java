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

    ////////////////////////////////////// My Sample
    
   package com.androidtutorialpoint.mycontacts;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private LinearLayout parentLinearLayout;
    EditText number_edit_text;
    Button delete_button;
    View view;

    String value="",valu1="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        number_edit_text = (EditText) findViewById(R.id.number_edit_text);
        delete_button = (Button) findViewById(R.id.delete_button);
       /* if (number_edit_text.getText().toString().length()>=1)
        {

            onAddField(view);
        }
        else
        {

        }*/

        number_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (number_edit_text.getText().toString().isEmpty())
                {
                    valu1 ="";
                }
                else
                {
                    if (valu1.isEmpty())
                    {
                        if (number_edit_text.getText().toString().length()>=1)
                        {

                            onAddField(view);
                            valu1 ="1";
                        }
                        else
                        {

                        }
                    }
                    else
                    {

                    }
                }


            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Do Not Delete Main Layout", Toast.LENGTH_SHORT).show();
            }
        });


    }
    public void onAddField(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.field, null);
        // Add the new row before the add field button.
        final EditText number_edit_text1=(EditText)rowView.findViewById(R.id.number_edit_text1);
        Button delete_button1=(Button) rowView.findViewById(R.id.delete_button1);


        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1);

        number_edit_text1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (number_edit_text1.getText().toString().isEmpty())
                {
                    value ="";
                }

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                Log.e("myvalue",""+value);
                if (number_edit_text1.getText().toString().isEmpty())
                {
                    value ="";
                }
                else
                {
                    if (value.isEmpty())
                    {
                        if (number_edit_text1.getText().toString().length()>=1)
                        {

                            onAddField(view);
                            value ="1";
                        }
                        else
                        {

                        }
                    }
                    else
                    {

                    }
                }


            }
        });

    }

    public void onDelete(View v) {
        parentLinearLayout.removeView((View) v.getParent());
    }

}

 ////// recyclerview


    package com.ardhas.samplemy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText edit_text1;
    ImageView delete_text1;
    RecyclerView recycle_main;
    String value="",valu1="";
    View view;
    private LinearLayout parentLinearLayout;
    RecyclerViewAdapter adapter;
   // private List<ModelClass> IncidentListDetails;
    private List<SampleModel> movieList = new ArrayList<>();
    SampleModel Model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
        edit_text1 = (EditText) findViewById(R.id.edit_text1);
        delete_text1 = (ImageView) findViewById(R.id.delete_text1);
        recycle_main = (RecyclerView) findViewById(R.id.recycle_main);



        adapter = new RecyclerViewAdapter(movieList,MainActivity.this);

        recycle_main.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        recycle_main.setLayoutManager(mLayoutManager);
        recycle_main.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.VERTICAL));
        recycle_main.setItemAnimator(new DefaultItemAnimator());
        recycle_main.setAdapter(adapter);


        delete_text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Model = new SampleModel("");
                movieList.add(Model);
                prepareMovieData();*/
            }
        });

        edit_text1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.e("1",edit_text1.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("2",edit_text1.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (edit_text1.getText().toString().length()==1)
                {
                    Model= new SampleModel("");
                    movieList.add(Model);
                    prepareMovieData();
                }

               /* Model = new SampleModel("1");
                movieList.add(Model);
                prepareMovieData();
                Log.e("3",edit_text1.getText().toString());
                if (edit_text1.getText().toString().isEmpty())
                {
                    valu1 ="";
                    Log.e("4",edit_text1.getText().toString());
                }
                else
                {
                    if (valu1.isEmpty())
                    {
                        if (edit_text1.getText().toString().length()>=2)
                        {
                            Model = new SampleModel("1");
                            movieList.add(Model);
                            prepareMovieData();
                            Log.e("5",edit_text1.getText().toString());
                        }
                        else
                        {

                        }
                    }
                    else
                    {

                    }
                }*/


            }
        });

       // prepareMovieData();
    }
    public void prepareMovieData() {


        // notify adapter about data set changes
        // so that it will render the list with new data

                adapter.notifyDataSetChanged();


    }


    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ListItemViewHolder> {

        Activity context;
        private List<SampleModel> sampleList;

        public RecyclerViewAdapter(List<SampleModel> sampleList, Activity context) {
            this.context = context;
            this.sampleList = sampleList;

        }

        @Override
        public ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.list_item, viewGroup, false);
            return new ListItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ListItemViewHolder viewHolder, final int position) {
            final SampleModel list = sampleList.get(position);


            viewHolder.edit_text.setText(list.getTitle());
            viewHolder.edit_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if ( viewHolder.edit_text.getText().toString().length()==1)
                    {
                        Toast.makeText(context, "check", Toast.LENGTH_SHORT).show();
                        Model= new SampleModel("");
                        sampleList.add(Model);
                        notifyDataSetChanged();
                    }

                   /* if ( viewHolder.edit_text.getText().toString().isEmpty())
                    {
                        valu1 ="";
                    }
                    else
                    {
                        if (valu1.isEmpty())
                        {
                            if ( viewHolder.edit_text.getText().toString().length()>=1)
                            {
                                Model = new SampleModel(viewHolder.edit_text.getText().toString());
                                movieList.add(Model);
                                prepareMovieData();
                            }
                            else
                            {

                            }
                        }
                        else
                        {

                        }
                    }*/


                }
            });

            viewHolder.delete_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sampleList.remove(position);
//                    movieList.remove(position);
                    notifyDataSetChanged();
                }
            });


        }

        @Override
        public int getItemCount() {
            return sampleList.size();
        }


        public final class ListItemViewHolder extends RecyclerView.ViewHolder {


            EditText edit_text;
            ImageView delete_text;

            public ListItemViewHolder(View itemView) {
                super(itemView);
                edit_text = (EditText) itemView.findViewById(R.id.edit_text);
                delete_text = (ImageView) itemView.findViewById(R.id.delete_text);

            }
        }
    }

}

