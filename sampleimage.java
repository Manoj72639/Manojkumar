package com.ardhas.sch.checklist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ardhas.sch.checklist.API.ApiInterface;
import com.ardhas.sch.checklist.API.Model.Request.SubCategoryFinalSubmitRequestModel;
import com.ardhas.sch.checklist.API.Model.Request.SubmitRequestModel;
import com.ardhas.sch.checklist.API.Model.Responce.OfflineScheduleResponceModel;
import com.ardhas.sch.checklist.API.Model.Responce.SubCategoryFinalSubmitResponseModel;
import com.ardhas.sch.checklist.API.Model.Responce.SubCategoryResponseModel;
import com.ardhas.sch.checklist.API.Model.Responce.SubmitResponseModel;
import com.ardhas.sch.checklist.API.NetworkClass;
import com.ardhas.sch.checklist.API.PreferenceConnector;
import com.ardhas.sch.checklist.API.Utils;
import com.ardhas.sch.checklist.CrashActivity.ExceptionHandler;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SubcatList extends AppCompatActivity {

    private RecyclerView InspectionRecylerview;
    private JSONObject jsondata;
    private ProgressDialog mProgressDialog;
    NetworkClass networkClass = new NetworkClass();
    RecycleViewAdapterList adapter1;
    List<SubCategoryResponseModel.SubCat> InspectionListDetails1;
    List<SubCategoryResponseModel.offlineSubcat> OffineList;

    TextView categoryname, subcategory;
    ImageView pencolr;
    RelativeLayout relativeLayout;
    Paint paint;
    View view_draw;
    Path path2;
    Bitmap bitmap, bitmap1;
    ImageView imagev, cancel_button_pic, clearbutton, backpress;
    LinearLayout pen, save;
    ImageButton photo;
    String imageString, radio = "", check;
    CardView pencolor;
    Canvas canvas;
    Utils utils = new Utils();
    int delecount = 0;
    Button submit;
    int requestcode;
    Integer success = 0;
    String imageStringbefore, imageStringafter, subcat_id, checkIds = "", chechIdCommaRem, frequencystatus, timenow;
    CheckBox Tv;
    Button finalbttn;
    TextView timenw, insptitle, schduleid;
    String bfrimgdatetime, aftrimgdatetime;
    EditText search;
    SQLiteDatabase checklist_db;
    String insid;
    String inspectiontypeid;
    String catid;
    String des, loc, fre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.recyclesubcatlist);

        InspectionRecylerview = (RecyclerView) findViewById(R.id.inspection_list);
        Tv = new CheckBox(SubcatList.this);
        view_draw = new SketchSheetView(SubcatList.this);
        finalbttn = (Button) findViewById(R.id.finalbttn);
        search = (EditText) findViewById(R.id.search);
        insptitle = (TextView) findViewById(R.id.insptitle);
        schduleid = (TextView) findViewById(R.id.schduleid);

        checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
        DB.create_table_subcat_check_list(checklist_db);

        insid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, "");
        inspectiontypeid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.head_id, "");
        catid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_id, "");

        schduleid.setText(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_full_id, ""));

        insptitle.setText(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.type_name, "Inspection"));

        search.setVisibility(View.GONE);

        finalbttn.setVisibility(View.VISIBLE);

        pencolr = (ImageView) findViewById(R.id.pencolr);

        backpress = (ImageView) findViewById(R.id.backpress);

        categoryname = (TextView) findViewById(R.id.categoryname);
        subcategory = (TextView) findViewById(R.id.subcategory);

        categoryname.setText(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_name, ""));

        submit = (Button) findViewById(R.id.submit);
        final Calendar calendar = Calendar.getInstance();
        timenow = String.valueOf(calendar.getTime());

        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SubcatList.this, MaincatList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        SubCat_list(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_id, ""), PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""));

        finalbttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SubmitResponceFinalSub();
                checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
                ContentValues cv = new ContentValues();
                int id = Integer.parseInt(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""));
                cv.put("data_status", "1");
                cv.put("insp_options", PreferenceConnector.readString(SubcatList.this, PreferenceConnector.turf_options, ""));
                DB.update(checklist_db, "schList", cv, id, "id");
            }
        });


    }


    private void SubCat_list(String id, String sch_id) {


        try {

            checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
            Cursor allrows = checklist_db.rawQuery("SELECT * FROM subCatList where sch_id = '" + insid + "' AND chk_cat_id = '" + catid + "'", null);
            //  System.out.println("COUNT : " + allrows.getCount());
            Log.e("subcatsize", "" + allrows.getCount());
            OffineList = new ArrayList<SubCategoryResponseModel.offlineSubcat>();
            // checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);


            String schid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, "");
            String catid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_id, "");
            Cursor allrows11 = checklist_db.rawQuery("select * from  subcatcheckList where insid ='" + schid + "'AND catid = '" + catid + "'", null);

            if (PreferenceConnector.readString(SubcatList.this, PreferenceConnector.turf_options, "").isEmpty()) {
                if (allrows.moveToFirst()) {

                    do {
                        // Thread.sleep(1000);
                        String frequencyname = allrows.getString(5);
                        String subcat_name = allrows.getString(3);
                        String item_id = allrows.getString(4);
                        String item_name = allrows.getString(8);
                        String subcat_id1 = allrows.getString(1);

                        radio = "0";
                        subcat_id = subcat_id1;
                        fre = frequencyname;

                        // Do something after 5s = 5000ms


                        if (allrows11.getCount() == 0) {
                            DBSubCatCheckinsert(utils.UnixTime());

                            // long unixTime = System.currentTimeMillis() / 1000L;
                            Log.e("time check", utils.UnixTime());

                        }


                        OffineList.add(new SubCategoryResponseModel.offlineSubcat(frequencyname, subcat_name, item_id, item_name, subcat_id1));

                    } while (allrows.moveToNext());
                }
            } else {
                if (allrows.moveToFirst()) {

                    do {
                        // Thread.sleep(1000);
                        String frequencyname = allrows.getString(5);
                        String subcat_name = allrows.getString(3);
                        String item_id = allrows.getString(4);
                        String item_name = allrows.getString(8);
                        String subcat_id1 = allrows.getString(1);

                        radio = "-1";
                        subcat_id = subcat_id1;
                        fre = frequencyname;

                        // Do something after 5s = 5000ms


                        if (allrows11.getCount() == 0) {
                            DBSubCatCheckinsert(utils.UnixTime());

                            // long unixTime = System.currentTimeMillis() / 1000L;
                            Log.e("time check", utils.UnixTime());

                        }


                        OffineList.add(new SubCategoryResponseModel.offlineSubcat(frequencyname, subcat_name, item_id, item_name, subcat_id1));

                    } while (allrows.moveToNext());
                }

            }
            allrows.close();
            checklist_db.close();
            // dismissProgressDialog();
            if (OffineList.size() > 0) {

                adapter1 = new RecycleViewAdapterList(SubcatList.this, OffineList);

                InspectionRecylerview.setAdapter(adapter1);
                InspectionRecylerview.setLayoutManager(new LinearLayoutManager(SubcatList.this, LinearLayoutManager.VERTICAL, false));


            } else {
                Log.e("IncidentList size", "" + OffineList.size());
            }

        } catch (Exception e) {
            Log.e("Offline error", e.toString());
            dismissProgressDialog();

        }


    }


    public class RecycleViewAdapterList extends RecyclerView.Adapter<RecycleViewAdapterList.ListItemViewHolder> {

        Activity context;
        //  List<SubCategoryResponseModel.SubCat> arrayLists = new ArrayList<SubCategoryResponseModel.SubCat>();
        List<SubCategoryResponseModel.offlineSubcat> arrayLists = new ArrayList<SubCategoryResponseModel.offlineSubcat>();

/*
        public RecycleViewAdapterList(Activity context, List<SubCategoryResponseModel.SubCat> member) {
            this.context = context;
            this.arrayLists = member;
        }
*/


        public RecycleViewAdapterList(Activity context, List<SubCategoryResponseModel.offlineSubcat> member) {
            this.context = context;
            this.arrayLists = member;

        }

        @Override
        public RecycleViewAdapterList.ListItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.form, viewGroup, false);

            return new ListItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull final ListItemViewHolder viewHolder, final int position) {

            //  final SubCategoryResponseModel.SubCat model = arrayLists.get(position);
            final SubCategoryResponseModel.offlineSubcat model = arrayLists.get(position);

            if (PreferenceConnector.readString(SubcatList.this, PreferenceConnector.turf_options, "").isEmpty()) {
                viewHolder.turf_visible.setVisibility(View.GONE);
                viewHolder.if_turf_visible.setVisibility(View.GONE);
            } else {
                viewHolder.turf_visible.setVisibility(View.VISIBLE);
                viewHolder.if_turf_visible.setVisibility(View.VISIBLE);
            }


            viewHolder.frequency.setText(model.getFrequencyname());

            viewHolder.subcategory.setText(model.getSubcat_name());


            if (model.getItem_id() == null) {

            } else {
                String item_name = model.getItem_name();
                String item_ids = model.getItem_id();


                String[] arrOfStr = item_name.split("&&");
                ArrayList<String> itemlist = new ArrayList<>();

                String[] arrOfStr2 = item_ids.split(",");
                ArrayList<String> itemidlist = new ArrayList<>();

                for (String a : arrOfStr) {

                    itemlist.add(a);

                }

                for (String a : arrOfStr2) {

                    itemidlist.add(a);

                }

                for (int i = 0; i < itemlist.size(); i++) {

                }
                try {


                    if (itemidlist.size() == 1) {
                        if (itemlist.get(0).trim().equalsIgnoreCase("Not Applicable-NA"))
                        {
                            viewHolder.check1.setVisibility(View.GONE);
                            viewHolder.check5.setVisibility(View.GONE);

                        }
                        viewHolder.check1.setText(itemlist.get(0));
                        viewHolder.check1.setTag(itemidlist.get(0));

                        viewHolder.check2.setVisibility(View.GONE);

                        
                    }
                    if (PreferenceConnector.readString(SubcatList.this, PreferenceConnector.display_others, "").equals("0")) {
                        viewHolder.checklayout.setVisibility(View.GONE);
                    } else {
                        viewHolder.checklayout.setVisibility(View.VISIBLE);

                    }
                } catch (Exception e) {
                    Log.e("Error Check Box", e.toString());
                }

                viewHolder.check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            // Toast.makeText(context, viewHolder.check1.getTag().toString(), Toast.LENGTH_SHORT).show();

                        } else {

                        }
                    }
                });

                viewHolder.check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            // Toast.makeText(context, viewHolder.check2.getTag().toString(), Toast.LENGTH_SHORT).show();

                        } else {

                        }
                    }
                });
               

            }


 


            //...........................................................
            // viewHolder.radioGroup.clearCheck();
            if (!model.isClicked()) {
                viewHolder.imagelayout.setVisibility(View.GONE);

            } else {
                viewHolder.imagelayout.setVisibility(View.VISIBLE);

            }


            viewHolder.yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        InspectionRecylerview.getLayoutManager().scrollToPosition(viewHolder.getAdapterPosition());
                        model.setClicked(true);
                        viewHolder.imagelayout.setVisibility(View.VISIBLE);
                        viewHolder.submit.setVisibility(View.VISIBLE);
                        viewHolder.add_field_button.setVisibility(View.VISIBLE);
                        viewHolder.no.setChecked(false);
                        viewHolder.na.setChecked(false);

                    } else {
                        viewHolder.imagelayout.setVisibility(View.GONE);
                    }
                }
            });
            viewHolder.no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    InspectionRecylerview.getLayoutManager().scrollToPosition(viewHolder.getAdapterPosition());

                    if (b) {
                        viewHolder.yes.setChecked(false);
                        viewHolder.na.setChecked(false);
                        viewHolder.submit.setVisibility(View.VISIBLE);
                        viewHolder.add_field_button.setVisibility(View.GONE);
                    } else {

                    }
                }
            });
            viewHolder.na.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    InspectionRecylerview.getLayoutManager().scrollToPosition(viewHolder.getAdapterPosition());

                    if (b) {
                        viewHolder.yes.setChecked(false);
                        viewHolder.no.setChecked(false);
                        viewHolder.submit.setVisibility(View.VISIBLE);
                        viewHolder.add_field_button.setVisibility(View.GONE);

                    } else {

                    }
                }
            });
            viewHolder.add_field_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //   InspectionRecylerview.getLayoutManager().scrollToPosition(viewHolder.getLayoutPosition());

                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View rowView = inflater.inflate(R.layout.field, null);
                    // Add the new row before the add field button.

                    final ImageView before = (ImageView) rowView.findViewById(R.id.before);
                    final ImageView after = (ImageView) rowView.findViewById(R.id.after);
                    final TextView describtion = (TextView) rowView.findViewById(R.id.describtion);
                    final TextView location = (TextView) rowView.findViewById(R.id.location);
                    final Button save_in = (Button) rowView.findViewById(R.id.save_in);
                    final EditText value_tid = (EditText) rowView.findViewById(R.id.value_tid);
                    final Button delete_button = (Button) rowView.findViewById(R.id.delete_button);

                    before.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog dialog1 = new Dialog(SubcatList.this);

                            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            dialog1.setContentView(R.layout.activity_choose_pic);


                            ImageButton photo = (ImageButton) dialog1.findViewById(R.id.camera);

                            ImageButton gallery = (ImageButton) dialog1.findViewById(R.id.gallery);

                            ImageButton cancel_button = (ImageButton) dialog1.findViewById(R.id.cancel_button_choose);

                            cancel_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog1.dismiss();

                                }
                            });

                            photo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestcode = 2;
                                    Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(photoCaptureIntent, requestcode);
                                    dialog1.dismiss();
                                    openEdit();

                                }
                            });

                            gallery.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestcode = 1;
                                    Intent intent = new Intent();
                                    intent.setType("*/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Choose file"), requestcode);
                                    dialog1.dismiss();
                                    openEdit();

                                }
                            });

                            dialog1.show();

                        }

                        private void openEdit() {

                            final Dialog dialog = new Dialog(SubcatList.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            dialog.setContentView(R.layout.activity_image);
                            final Calendar calendar = Calendar.getInstance();
                            timenow = String.valueOf(calendar.getTime());

                            relativeLayout = (RelativeLayout) dialog.findViewById(R.id.relativelayout1_image);

                            timenw = (TextView) dialog.findViewById(R.id.timenow);


                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String str = sdf.format(new Date());

                            clearbutton = (ImageView) dialog.findViewById(R.id.clear_button);

                            cancel_button_pic = (ImageView) dialog.findViewById(R.id.cancel_button_edit);

                            save = (LinearLayout) dialog.findViewById(R.id.save);

                            pen = (LinearLayout) dialog.findViewById(R.id.pen);

                            imagev = (ImageView) dialog.findViewById(R.id.image);
                            pencolor = (CardView) dialog.findViewById(R.id.pencolor);

                            pencolr = (ImageView) dialog.findViewById(R.id.pencolr);

                            pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));

                            BitmapDrawable drawable = (BitmapDrawable) imagev.getDrawable();
                            Bitmap bitmap2 = drawable.getBitmap();
                            imagev.setImageBitmap(bitmap2);

                            Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                            timenw.setTypeface(boldTypeface);

                            timenw.setTextColor(Color.parseColor("#000000"));
                            timenw.setTextSize(25);
                            timenw.setText(str);
                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                                    pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                    pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));


                                    relativeLayout.setDrawingCacheEnabled(true);

                                    imagev.buildDrawingCache(true);

                                    bitmap1 = Bitmap.createBitmap(relativeLayout.getDrawingCache());

                                    relativeLayout.setDrawingCacheEnabled(false);
                                    view_draw.setDrawingCacheEnabled(true);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                                    final String str = sdf.format(new Date());
                                    bfrimgdatetime = str;

                                    String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap1, "Test123", "drawing");


                                    if (imgSaved != null) {
                                        Log.e("Drawing", "Saved to Gallery");
                                    } else {
                                       /* Toast unsavedToast = Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT);

                                        unsavedToast.show();*/
                                    }
                                    imagev.setImageBitmap(bitmap1);
                        /*image1.setMaxWidth(256);
                        image1.setMaxHeight(256);
*/
                                    before.setImageBitmap(bitmap1);
                                    view_draw.destroyDrawingCache();

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    // BitmapDrawable drawable = (BitmapDrawable) incident_photo.getDrawable();
                                    Bitmap bitmap = bitmap1;
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] imageBytes = baos.toByteArray();
                                    imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                    //   Toast.makeText(Inspection.this, "image Bytes" + imageString, Toast.LENGTH_SHORT).show();
                                    //   Log.e("image1", imageString);
                                    dialog.dismiss();
                                }
                            });
                            clearbutton.setVisibility(View.INVISIBLE);

                            cancel_button_pic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            clearbutton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                                    pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                    pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                    try {
                                        path2.reset();
                                        //Toast.makeText(Inspection.this,"Clicked",Toast.LENGTH_SHORT).show();
                                        view_draw.invalidate();
                                        pen.setClickable(false);
                                    } catch (Exception e) {
                                        Log.e("clearbutton", e.toString());
                                    }
                                }
                            });

                            pen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    clearbutton.setVisibility(View.VISIBLE);
                                    pencolor.setCardBackgroundColor(Color.parseColor("#a9a9a8"));
                                    pencolr.setBackgroundColor(Color.parseColor("#a9a9a8"));

                                    view_draw = new SketchSheetView(SubcatList.this);

                                    paint = new Paint();

                                    path2 = new Path();

                                    relativeLayout.addView(view_draw, new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                                    paint.setDither(true);

                                    paint.setColor(Color.parseColor("#FF0000"));

                                    paint.setStyle(Paint.Style.STROKE);

                                    paint.setStrokeJoin(Paint.Join.ROUND);

                                    paint.setStrokeCap(Paint.Cap.ROUND);

                                    paint.setStrokeWidth(5);


                                }

                            });
                            dialog.show();
                        }
                    });
                    after.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog dialog1 = new Dialog(SubcatList.this);

                            dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            dialog1.setContentView(R.layout.activity_choose_pic);


                            ImageButton photo = (ImageButton) dialog1.findViewById(R.id.camera);

                            ImageButton gallery = (ImageButton) dialog1.findViewById(R.id.gallery);

                            ImageButton cancel_button = (ImageButton) dialog1.findViewById(R.id.cancel_button_choose);

                            cancel_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialog1.dismiss();

                                }
                            });

                            photo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestcode = 2;
                                    Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(photoCaptureIntent, requestcode);
                                    dialog1.dismiss();
                                    openEdit();

                                }
                            });

                            gallery.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestcode = 1;
                                    Intent intent = new Intent();
                                    intent.setType("*/*");
                                    intent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(intent, "Choose file"), requestcode);
                                    dialog1.dismiss();
                                    openEdit();

                                }
                            });

                            dialog1.show();

                        }

                        private void openEdit() {

                            final Dialog dialog = new Dialog(SubcatList.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                            dialog.setContentView(R.layout.activity_image);

                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            String str = sdf.format(new Date());

                            relativeLayout = (RelativeLayout) dialog.findViewById(R.id.relativelayout1_image);

                            timenw = (TextView) dialog.findViewById(R.id.timenow);

                            clearbutton = (ImageView) dialog.findViewById(R.id.clear_button);

                            cancel_button_pic = (ImageView) dialog.findViewById(R.id.cancel_button_edit);

                            save = (LinearLayout) dialog.findViewById(R.id.save);

                            pen = (LinearLayout) dialog.findViewById(R.id.pen);

                            imagev = (ImageView) dialog.findViewById(R.id.image);
                            pencolor = (CardView) dialog.findViewById(R.id.pencolor);

                            pencolr = (ImageView) dialog.findViewById(R.id.pencolr);

                            pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));

                            BitmapDrawable drawable = (BitmapDrawable) imagev.getDrawable();
                            Bitmap bitmap2 = drawable.getBitmap();
                            imagev.setImageBitmap(bitmap2);

                            Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                            timenw.setTypeface(boldTypeface);

                            timenw.setTextColor(Color.parseColor("#000000"));
                            timenw.setTextSize(25);
                            timenw.setText(str);

                            save.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                                    pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                    pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));


                                    relativeLayout.setDrawingCacheEnabled(true);

                                    imagev.buildDrawingCache(true);

                                    bitmap1 = Bitmap.createBitmap(relativeLayout.getDrawingCache());

                                    relativeLayout.setDrawingCacheEnabled(false);
                                    view_draw.setDrawingCacheEnabled(true);
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                                    final String str1 = sdf.format(new Date());
                                    aftrimgdatetime = str1;

                                    String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap1, "Test123", "drawing");


                                    if (imgSaved != null) {
                                        Log.e("Drawing", "Saved to Gallery");
                                    } else {
                                       /* Toast unsavedToast = Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT);

                                        unsavedToast.show();*/
                                    }
                                    imagev.setImageBitmap(bitmap1);
                        /*image1.setMaxWidth(256);
                        image1.setMaxHeight(256);
*/
                                    after.setImageBitmap(bitmap1);
                                    view_draw.destroyDrawingCache();

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    // BitmapDrawable drawable = (BitmapDrawable) incident_photo.getDrawable();
                                    Bitmap bitmap = bitmap1;
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] imageBytes = baos.toByteArray();
                                    imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                    //   Toast.makeText(Inspection.this, "image Bytes" + imageString, Toast.LENGTH_SHORT).show();
                                    //   Log.e("image1", imageString);
                                    dialog.dismiss();
                                }
                            });
                            clearbutton.setVisibility(View.INVISIBLE);

                            cancel_button_pic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            clearbutton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                                    pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                    pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                    try {
                                        path2.reset();
                                        //Toast.makeText(Inspection.this,"Clicked",Toast.LENGTH_SHORT).show();
                                        view_draw.invalidate();
                                        pen.setClickable(false);
                                    } catch (Exception e) {
                                        Log.e("clearbutton", e.toString());
                                    }
                                }
                            });

                            pen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    clearbutton.setVisibility(View.VISIBLE);
                                    pencolor.setCardBackgroundColor(Color.parseColor("#a9a9a8"));
                                    pencolr.setBackgroundColor(Color.parseColor("#a9a9a8"));

                                    view_draw = new SketchSheetView(SubcatList.this);

                                    paint = new Paint();

                                    path2 = new Path();

                                    relativeLayout.addView(view_draw, new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                                    paint.setDither(true);

                                    paint.setColor(Color.parseColor("#FF0000"));

                                    paint.setStyle(Paint.Style.STROKE);

                                    paint.setStrokeJoin(Paint.Join.ROUND);

                                    paint.setStrokeCap(Paint.Cap.ROUND);

                                    paint.setStrokeWidth(5);


                                }

                            });
                            dialog.show();

                        }
                    });

                    des = describtion.getText().toString();
                    loc = location.getText().toString();


                    save_in.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            InspectionRecylerview.getLayoutManager().scrollToPosition(viewHolder.getAdapterPosition());
                            if (viewHolder.yes.isChecked()) {
                                radio = viewHolder.yes.getTag().toString();
                            } else if (viewHolder.no.isChecked()) {
                                radio = viewHolder.no.getTag().toString();
                            } else if (viewHolder.na.isChecked()) {
                                radio = viewHolder.na.getTag().toString();
                            }


                            if (radio.equalsIgnoreCase("1")) {
                                if (describtion.getText().toString().length() == 0) {
                                    describtion.setError("Please Enter Describtion");
                                } else if (location.getText().toString().length() == 0) {
                                    location.setError("Please Enter Location");
                                } else {
                                    checkIds = "";
                                    if (viewHolder.check1.isChecked()) {
                                        checkIds += ("," + viewHolder.check1.getTag().toString());
                                    }
                                    if (viewHolder.check2.isChecked()) {
                                        checkIds += ("," + viewHolder.check2.getTag().toString());
                                    }
                                    if (viewHolder.check3.isChecked()) {
                                        checkIds += ("," + viewHolder.check3.getTag().toString());
                                    }
                                    if (viewHolder.check4.isChecked()) {
                                        checkIds += ("," + viewHolder.check4.getTag().toString());
                                    }
                                    if (viewHolder.check5.isChecked()) {
                                        checkIds += ("," + viewHolder.check5.getTag().toString());
                                    }
                                    if (viewHolder.check6.isChecked()) {
                                        checkIds += ("," + viewHolder.check6.getTag().toString());
                                    }
                                    if (viewHolder.check7.isChecked()) {
                                        checkIds += ("," + viewHolder.check7.getTag().toString());
                                    }
                                    if (viewHolder.check8.isChecked()) {
                                        checkIds += ("," + viewHolder.check8.getTag().toString());
                                    }
                                    if (viewHolder.check9.isChecked()) {
                                        checkIds += ("," + viewHolder.check9.getTag().toString());
                                    }
                                    if (viewHolder.check10.isChecked()) {
                                        checkIds += ("," + viewHolder.check10.getTag().toString());
                                    }
                                    if (viewHolder.check11.isChecked()) {
                                        checkIds += ("," + viewHolder.check11.getTag().toString());
                                    }
                                    if (viewHolder.check12.isChecked()) {
                                        checkIds += ("," + viewHolder.check12.getTag().toString());
                                    }
                                    if (viewHolder.check13.isChecked()) {
                                        checkIds += ("," + viewHolder.check13.getTag().toString());
                                    }
                                    if (viewHolder.check14.isChecked()) {
                                        checkIds += ("," + viewHolder.check14.getTag().toString());
                                    }
                                    if (viewHolder.check15.isChecked()) {
                                        checkIds += ("," + viewHolder.check15.getTag().toString());
                                    }
                                    if (viewHolder.check16.isChecked()) {
                                        checkIds += ("," + viewHolder.check16.getTag().toString());
                                    }
                                    if (viewHolder.check17.isChecked()) {
                                        checkIds += ("," + viewHolder.check17.getTag().toString());
                                    }
                                    if (viewHolder.check18.isChecked()) {
                                        checkIds += ("," + viewHolder.check18.getTag().toString());
                                    }
                                    if (viewHolder.check19.isChecked()) {
                                        checkIds += ("," + viewHolder.check19.getTag().toString());
                                    }
                                    if (viewHolder.check20.isChecked()) {
                                        checkIds += ("," + viewHolder.check20.getTag().toString());
                                    }
                                    if (viewHolder.check21.isChecked()) {
                                        checkIds += ("," + viewHolder.check21.getTag().toString());
                                    }

                                    chechIdCommaRem = checkIds.replaceFirst(",", "");
                                    // Toast.makeText(context, chechIdCommaRem, Toast.LENGTH_SHORT).show();

                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    BitmapDrawable drawable = (BitmapDrawable) before.getDrawable();
                                    Bitmap bitmap = drawable.getBitmap();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    byte[] imageBytes = baos.toByteArray();
                                    imageStringbefore = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                    // Log.e("imageStringbefore", imageStringbefore);

                                    ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                                    BitmapDrawable drawable1 = (BitmapDrawable) after.getDrawable();
                                    Bitmap bitmap1 = drawable1.getBitmap();
                                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
                                    byte[] imageBytes1 = baos1.toByteArray();
                                    imageStringafter = Base64.encodeToString(imageBytes1, Base64.DEFAULT);
                                    Log.e("imageStringafter", imageStringafter);

                                    des = describtion.getText().toString();
                                    loc = location.getText().toString();
                                    fre = viewHolder.frequency.getText().toString();


                                    subcat_id = model.getSubcat_id();

                                    checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
                                    ContentValues cv = new ContentValues();
                                    int id = Integer.parseInt(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""));
                                    cv.put("data_status", "1");
                                    cv.put("insp_options", PreferenceConnector.readString(SubcatList.this, PreferenceConnector.turf_options, ""));
                                    DB.update(checklist_db, "schList", cv, id, "id");

                                    // SubmitResponceApiCalling();
                                    if (save_in.getTag().toString().equalsIgnoreCase("save")) {
                                        String tid = utils.UnixTime();
                                        value_tid.setText(tid);
                                        DBSubCatCheckinsert(tid);
                                    } else {
                                        String tid = value_tid.getText().toString();
                                        DBSubCatCheckUpdate(tid);
                                        Toast.makeText(SubcatList.this, "Updated Sucessfully", Toast.LENGTH_SHORT).show();

                                    }

                                    save_in.setBackgroundColor(Color.parseColor("#FF3C9322"));
                                    save_in.setTag("UPDATE");
                                    save_in.setText("UPDATE");
                    /*viewHolder.submit.setClickable(false);
                    viewHolder.submit.setEnabled(false);*/
                                }

                            } else {
                                checkIds = "";
                                if (viewHolder.check1.isChecked()) {
                                    checkIds += ("," + viewHolder.check1.getTag().toString());
                                }
                                if (viewHolder.check2.isChecked()) {
                                    checkIds += ("," + viewHolder.check2.getTag().toString());
                                }
                                if (viewHolder.check3.isChecked()) {
                                    checkIds += ("," + viewHolder.check3.getTag().toString());
                                }
                                if (viewHolder.check4.isChecked()) {
                                    checkIds += ("," + viewHolder.check4.getTag().toString());
                                }
                                if (viewHolder.check5.isChecked()) {
                                    checkIds += ("," + viewHolder.check5.getTag().toString());
                                }
                                if (viewHolder.check6.isChecked()) {
                                    checkIds += ("," + viewHolder.check6.getTag().toString());
                                }
                                if (viewHolder.check7.isChecked()) {
                                    checkIds += ("," + viewHolder.check7.getTag().toString());
                                }
                                if (viewHolder.check8.isChecked()) {
                                    checkIds += ("," + viewHolder.check8.getTag().toString());
                                }
                                if (viewHolder.check9.isChecked()) {
                                    checkIds += ("," + viewHolder.check9.getTag().toString());
                                }
                                if (viewHolder.check10.isChecked()) {
                                    checkIds += ("," + viewHolder.check10.getTag().toString());
                                }
                                if (viewHolder.check11.isChecked()) {
                                    checkIds += ("," + viewHolder.check11.getTag().toString());
                                }
                                if (viewHolder.check12.isChecked()) {
                                    checkIds += ("," + viewHolder.check12.getTag().toString());
                                }
                                if (viewHolder.check13.isChecked()) {
                                    checkIds += ("," + viewHolder.check13.getTag().toString());
                                }
                                if (viewHolder.check14.isChecked()) {
                                    checkIds += ("," + viewHolder.check14.getTag().toString());
                                }
                                if (viewHolder.check15.isChecked()) {
                                    checkIds += ("," + viewHolder.check15.getTag().toString());
                                }
                                if (viewHolder.check16.isChecked()) {
                                    checkIds += ("," + viewHolder.check16.getTag().toString());
                                }
                                if (viewHolder.check17.isChecked()) {
                                    checkIds += ("," + viewHolder.check17.getTag().toString());
                                }
                                if (viewHolder.check18.isChecked()) {
                                    checkIds += ("," + viewHolder.check18.getTag().toString());
                                }
                                if (viewHolder.check19.isChecked()) {
                                    checkIds += ("," + viewHolder.check19.getTag().toString());
                                }
                                if (viewHolder.check20.isChecked()) {
                                    checkIds += ("," + viewHolder.check20.getTag().toString());
                                }
                                if (viewHolder.check21.isChecked()) {
                                    checkIds += ("," + viewHolder.check21.getTag().toString());
                                }

                                chechIdCommaRem = checkIds.replaceFirst(",", "");
                                // Toast.makeText(context, chechIdCommaRem, Toast.LENGTH_SHORT).show();

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                BitmapDrawable drawable = (BitmapDrawable) before.getDrawable();
                                Bitmap bitmap = drawable.getBitmap();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] imageBytes = baos.toByteArray();
                                imageStringbefore = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                // Log.e("imageStringbefore", imageStringbefore);

                                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                                BitmapDrawable drawable1 = (BitmapDrawable) after.getDrawable();
                                Bitmap bitmap1 = drawable1.getBitmap();
                                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
                                byte[] imageBytes1 = baos1.toByteArray();
                                imageStringafter = Base64.encodeToString(imageBytes1, Base64.DEFAULT);
                                Log.e("imageStringafter", imageStringafter);

                                des = describtion.getText().toString();
                                loc = location.getText().toString();
                                fre = viewHolder.frequency.getText().toString();


                                subcat_id = model.getSubcat_id();

                                checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
                                ContentValues cv = new ContentValues();
                                int id = Integer.parseInt(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""));
                                cv.put("data_status", "1");
                                cv.put("insp_options", PreferenceConnector.readString(SubcatList.this, PreferenceConnector.turf_options, ""));
                                DB.update(checklist_db, "schList", cv, id, "id");
                                // SubmitResponceApiCalling();

                                if (save_in.getTag().toString().equalsIgnoreCase("save")) {

                                    String tid = utils.UnixTime();
                                    value_tid.setText(tid);
                                    DBSubCatCheckinsert(tid);
                                } else {

                                    String tid = value_tid.getText().toString();
                                    DBSubCatCheckUpdate(tid);
                                    Toast.makeText(SubcatList.this, "Updated Sucessfully", Toast.LENGTH_SHORT).show();

                                }

                                save_in.setBackgroundColor(Color.parseColor("#FF3C9322"));
                                save_in.setTag("update");
                                save_in.setText("UPDATE");
                            }
                        }
                    });
                    Log.e("count linear", String.valueOf(viewHolder.linearphotoupload.getChildCount() - 4));
                    viewHolder.linearphotoupload.addView(rowView, viewHolder.linearphotoupload.getChildCount() - 1);

                    delete_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Log.e("indexlay", String.valueOf(delecount));

                                // InspectionRecylerview.getLayoutManager().scrollToPosition(viewHolder.getAdapterPosition());

                            } catch (Exception e) {
                                Log.e("photodel", e.toString());
                                Log.e("indexlay", String.valueOf(delecount));

                            }
                            if (value_tid.getText().toString().isEmpty()) {

                            } else {
                                checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);

                                checklist_db.execSQL("delete from subcatchecklist where tid='" + value_tid.getText().toString() + "'");

                            }
                            //  onDelete(view);
                            viewHolder.linearphotoupload.removeView(rowView);
                            delecount--;
                            Log.e("afterdel", String.valueOf(delecount));
                        }
                    });
                }
            });

            viewHolder.before.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    final Dialog dialog1 = new Dialog(SubcatList.this);

                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog1.setContentView(R.layout.activity_choose_pic);


                    ImageButton photo = (ImageButton) dialog1.findViewById(R.id.camera);

                    ImageButton gallery = (ImageButton) dialog1.findViewById(R.id.gallery);

                    ImageButton cancel_button = (ImageButton) dialog1.findViewById(R.id.cancel_button_choose);

                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog1.dismiss();

                        }
                    });

                    photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestcode = 2;
                            Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(photoCaptureIntent, requestcode);
                            dialog1.dismiss();
                            openEdit();

                        }
                    });

                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestcode = 1;
                            Intent intent = new Intent();
                            intent.setType("*/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Choose file"), requestcode);
                            dialog1.dismiss();
                            openEdit();

                        }
                    });

                    dialog1.show();

                }

                private void openEdit() {

                    final Dialog dialog = new Dialog(SubcatList.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog.setContentView(R.layout.activity_image);
                    final Calendar calendar = Calendar.getInstance();
                    timenow = String.valueOf(calendar.getTime());

                    relativeLayout = (RelativeLayout) dialog.findViewById(R.id.relativelayout1_image);

                    timenw = (TextView) dialog.findViewById(R.id.timenow);


                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String str = sdf.format(new Date());

                    clearbutton = (ImageView) dialog.findViewById(R.id.clear_button);

                    cancel_button_pic = (ImageView) dialog.findViewById(R.id.cancel_button_edit);

                    save = (LinearLayout) dialog.findViewById(R.id.save);

                    pen = (LinearLayout) dialog.findViewById(R.id.pen);

                    imagev = (ImageView) dialog.findViewById(R.id.image);
                    pencolor = (CardView) dialog.findViewById(R.id.pencolor);

                    pencolr = (ImageView) dialog.findViewById(R.id.pencolr);

                    pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));

                    BitmapDrawable drawable = (BitmapDrawable) imagev.getDrawable();
                    Bitmap bitmap2 = drawable.getBitmap();
                    imagev.setImageBitmap(bitmap2);

                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                    timenw.setTypeface(boldTypeface);

                    timenw.setTextColor(Color.parseColor("#000000"));
                    timenw.setTextSize(25);
                    timenw.setText(str);

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                            pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                            pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));


                            relativeLayout.setDrawingCacheEnabled(true);

                            imagev.buildDrawingCache(true);

                            bitmap1 = Bitmap.createBitmap(relativeLayout.getDrawingCache());

                            relativeLayout.setDrawingCacheEnabled(false);
                            view_draw.setDrawingCacheEnabled(true);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                            final String str = sdf.format(new Date());
                            bfrimgdatetime = str;

                            String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap1, "Test123", "drawing");


                            if (imgSaved != null) {
                                Log.e("Drawing", "Saved to Gallery");
                            } else {
                               /* Toast unsavedToast = Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT);

                                unsavedToast.show();*/
                            }
                            imagev.setImageBitmap(bitmap1);
                        /*image1.setMaxWidth(256);
                        image1.setMaxHeight(256);
*/
                            viewHolder.before.setImageBitmap(bitmap1);
                            view_draw.destroyDrawingCache();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            // BitmapDrawable drawable = (BitmapDrawable) incident_photo.getDrawable();
                            Bitmap bitmap = bitmap1;
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageBytes = baos.toByteArray();
                            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                            //   Toast.makeText(Inspection.this, "image Bytes" + imageString, Toast.LENGTH_SHORT).show();
                            //   Log.e("image1", imageString);
                            dialog.dismiss();
                        }
                    });
                    clearbutton.setVisibility(View.INVISIBLE);

                    cancel_button_pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    clearbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                            pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                            pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));
                            try {
                                path2.reset();
                                //Toast.makeText(Inspection.this,"Clicked",Toast.LENGTH_SHORT).show();
                                view_draw.invalidate();
                                pen.setClickable(false);
                            } catch (Exception e) {
                                Log.e("clearbutton", e.toString());
                            }
                        }
                    });

                    pen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            clearbutton.setVisibility(View.VISIBLE);
                            pencolor.setCardBackgroundColor(Color.parseColor("#a9a9a8"));
                            pencolr.setBackgroundColor(Color.parseColor("#a9a9a8"));

                            view_draw = new SketchSheetView(SubcatList.this);

                            paint = new Paint();

                            path2 = new Path();

                            relativeLayout.addView(view_draw, new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                            paint.setDither(true);

                            paint.setColor(Color.parseColor("#FF0000"));

                            paint.setStyle(Paint.Style.STROKE);

                            paint.setStrokeJoin(Paint.Join.ROUND);

                            paint.setStrokeCap(Paint.Cap.ROUND);

                            paint.setStrokeWidth(5);


                        }

                    });
                    dialog.show();


//close
                }


            });


            viewHolder.after.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    final Dialog dialog1 = new Dialog(SubcatList.this);

                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog1.setContentView(R.layout.activity_choose_pic);


                    ImageButton photo = (ImageButton) dialog1.findViewById(R.id.camera);

                    ImageButton gallery = (ImageButton) dialog1.findViewById(R.id.gallery);

                    ImageButton cancel_button = (ImageButton) dialog1.findViewById(R.id.cancel_button_choose);

                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialog1.dismiss();

                        }
                    });

                    photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestcode = 2;
                            Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(photoCaptureIntent, requestcode);
                            dialog1.dismiss();
                            openEdit();

                        }
                    });

                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestcode = 1;
                            Intent intent = new Intent();
                            intent.setType("*/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Choose file"), requestcode);
                            dialog1.dismiss();
                            openEdit();

                        }
                    });

                    dialog1.show();

                }

                private void openEdit() {

                    final Dialog dialog = new Dialog(SubcatList.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                    dialog.setContentView(R.layout.activity_image);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String str = sdf.format(new Date());

                    relativeLayout = (RelativeLayout) dialog.findViewById(R.id.relativelayout1_image);

                    timenw = (TextView) dialog.findViewById(R.id.timenow);

                    clearbutton = (ImageView) dialog.findViewById(R.id.clear_button);

                    cancel_button_pic = (ImageView) dialog.findViewById(R.id.cancel_button_edit);

                    save = (LinearLayout) dialog.findViewById(R.id.save);

                    pen = (LinearLayout) dialog.findViewById(R.id.pen);

                    imagev = (ImageView) dialog.findViewById(R.id.image);
                    pencolor = (CardView) dialog.findViewById(R.id.pencolor);

                    pencolr = (ImageView) dialog.findViewById(R.id.pencolr);

                    pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));

                    BitmapDrawable drawable = (BitmapDrawable) imagev.getDrawable();
                    Bitmap bitmap2 = drawable.getBitmap();
                    imagev.setImageBitmap(bitmap2);

                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                    timenw.setTypeface(boldTypeface);

                    timenw.setTextColor(Color.parseColor("#000000"));
                    timenw.setTextSize(25);
                    timenw.setText(str);

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                            pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                            pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));


                            relativeLayout.setDrawingCacheEnabled(true);

                            imagev.buildDrawingCache(true);

                            bitmap1 = Bitmap.createBitmap(relativeLayout.getDrawingCache());

                            relativeLayout.setDrawingCacheEnabled(false);
                            view_draw.setDrawingCacheEnabled(true);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                            final String str1 = sdf.format(new Date());
                            aftrimgdatetime = str1;

                            String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap1, "Test123", "drawing");


                            if (imgSaved != null) {
                                Log.e("Drawing", "Saved to Gallery");
                            } else {
                               /* Toast unsavedToast = Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT);

                                unsavedToast.show();*/
                            }
                            imagev.setImageBitmap(bitmap1);
                        /*image1.setMaxWidth(256);
                        image1.setMaxHeight(256);
*/
                            viewHolder.after.setImageBitmap(bitmap1);
                            view_draw.destroyDrawingCache();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            // BitmapDrawable drawable = (BitmapDrawable) incident_photo.getDrawable();
                            Bitmap bitmap = bitmap1;
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageBytes = baos.toByteArray();
                            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                            //   Toast.makeText(Inspection.this, "image Bytes" + imageString, Toast.LENGTH_SHORT).show();
                            //   Log.e("image1", imageString);
                            dialog.dismiss();
                        }
                    });
                    clearbutton.setVisibility(View.INVISIBLE);

                    cancel_button_pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    clearbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                            pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                            pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));
                            try {
                                path2.reset();
                                //Toast.makeText(Inspection.this,"Clicked",Toast.LENGTH_SHORT).show();
                                view_draw.invalidate();
                                pen.setClickable(false);
                            } catch (Exception e) {
                                Log.e("clearbutton", e.toString());
                            }
                        }
                    });

                    pen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            clearbutton.setVisibility(View.VISIBLE);
                            pencolor.setCardBackgroundColor(Color.parseColor("#a9a9a8"));
                            pencolr.setBackgroundColor(Color.parseColor("#a9a9a8"));

                            view_draw = new SketchSheetView(SubcatList.this);

                            paint = new Paint();

                            path2 = new Path();

                            relativeLayout.addView(view_draw, new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                            paint.setDither(true);

                            paint.setColor(Color.parseColor("#FF0000"));

                            paint.setStyle(Paint.Style.STROKE);

                            paint.setStrokeJoin(Paint.Join.ROUND);

                            paint.setStrokeCap(Paint.Cap.ROUND);

                            paint.setStrokeWidth(5);


                        }

                    });
                    dialog.show();


//close
                }


            });

//       below code is reciving data from offline

            subcat_id = model.getSubcat_id();
            checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);


            String schid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, "");
            String catid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_id, "");
            String subcatid = subcat_id;
            Cursor allrows = checklist_db.rawQuery("select * from  subcatcheckList where insid ='" + schid + "'AND catid = '" + catid + "'AND subcatid='" + subcatid + "'", null);


            ///data get from local db
            if (allrows.getCount() == 0) {

                Log.e("empty", "ella");

            } else {

                Log.e("Available", "ana ella");

                //    viewHolder.submit.setBackgroundColor(Color.parseColor("#FF3C9322"));
                viewHolder.submit.setText("Save");
                viewHolder.submit.setTag("Update");


                checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);

                int rowPos = 0;
                if (allrows.moveToFirst()) {
                    do {

                        rowPos++;
                        viewHolder.describtion.setText(allrows.getString(7));
                        viewHolder.location.setText(allrows.getString(9));
                        viewHolder.frequency.setText(allrows.getString(8));
                        viewHolder.value_tid.setText(allrows.getString(0));

                        Log.e("checkRadio", allrows.getString(6));
                        if (allrows.getString(6).equalsIgnoreCase("1")) {

                            viewHolder.yes.setChecked(true);
                            viewHolder.imagelayout.setVisibility(View.VISIBLE);
                            viewHolder.submit.setVisibility(View.VISIBLE);
                            viewHolder.add_field_button.setVisibility(View.VISIBLE);
                            // viewHolder.submit.setVisibility(View.VISIBLE);

                        } else if (allrows.getString(6).equalsIgnoreCase("0")) {
                            viewHolder.no.setChecked(true);
                            viewHolder.imagelayout.setVisibility(View.GONE);
                            viewHolder.submit.setVisibility(View.GONE);
                            viewHolder.add_field_button.setVisibility(View.GONE);


                        } else if (allrows.getString(6).equalsIgnoreCase("-1")) {
                            viewHolder.na.setChecked(true);
                            viewHolder.submit.setVisibility(View.GONE);

                            viewHolder.imagelayout.setVisibility(View.GONE);


                        } else {
                            // viewHolder.radioGroup.clearCheck();
                            viewHolder.no.setChecked(true);
                            viewHolder.imagelayout.setVisibility(View.GONE);

                        }


                        if (allrows.getString(5).isEmpty()) {

                        } else {
                            String item_ids = allrows.getString(5);


                            String[] arrOfStr2 = item_ids.split(",");
                            ArrayList<String> itemidlist = new ArrayList<>();

                            for (String a : arrOfStr2) {

                                itemidlist.add(a);

                            }
                            for (int i = 0; i < itemidlist.size(); i++) {
                                if (viewHolder.check1.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check1.setChecked(true);

                                } else if (viewHolder.check2.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check2.setChecked(true);

                                } else if (viewHolder.check3.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check3.setChecked(true);

                                } else if (viewHolder.check4.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check4.setChecked(true);

                                } else if (viewHolder.check5.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check5.setChecked(true);

                                } else if (viewHolder.check6.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check6.setChecked(true);

                                } else if (viewHolder.check7.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check7.setChecked(true);

                                } else if (viewHolder.check8.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check8.setChecked(true);

                                } else if (viewHolder.check9.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check9.setChecked(true);

                                } else if (viewHolder.check10.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check10.setChecked(true);

                                } else if (viewHolder.check11.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check11.setChecked(true);

                                } else if (viewHolder.check12.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check12.setChecked(true);

                                } else if (viewHolder.check13.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check13.setChecked(true);

                                } else if (viewHolder.check14.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check14.setChecked(true);

                                } else if (viewHolder.check15.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check15.setChecked(true);

                                } else if (viewHolder.check16.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check16.setChecked(true);

                                } else if (viewHolder.check17.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check17.setChecked(true);

                                } else if (viewHolder.check18.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check18.setChecked(true);

                                } else if (viewHolder.check19.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check19.setChecked(true);

                                } else if (viewHolder.check20.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check20.setChecked(true);

                                } else if (viewHolder.check21.getTag().equals(itemidlist.get(i))) {
                                    viewHolder.check21.setChecked(true);

                                }
                            }
                        }


                        if (allrows.getString(6).equalsIgnoreCase("1")) {
                            // * Newly insert for view ////////////////////////////////////////////////////////////////////////////////
                            //   InspectionRecylerview.getLayoutManager().scrollToPosition(viewHolder.getLayoutPosition());

                            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View rowView = inflater.inflate(R.layout.field, null);
                            // Add the new row before the add field button.
                            final ImageView before = (ImageView) rowView.findViewById(R.id.before);
                            final ImageView after = (ImageView) rowView.findViewById(R.id.after);
                            final TextView describtion = (TextView) rowView.findViewById(R.id.describtion);
                            final TextView location = (TextView) rowView.findViewById(R.id.location);
                            final Button save_in = (Button) rowView.findViewById(R.id.save_in);
                            final EditText value_tid = (EditText) rowView.findViewById(R.id.value_tid);
                            final Button delete_button = (Button) rowView.findViewById(R.id.delete_button);

                            before.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final Dialog dialog1 = new Dialog(SubcatList.this);

                                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                    dialog1.setContentView(R.layout.activity_choose_pic);


                                    ImageButton photo = (ImageButton) dialog1.findViewById(R.id.camera);

                                    ImageButton gallery = (ImageButton) dialog1.findViewById(R.id.gallery);

                                    ImageButton cancel_button = (ImageButton) dialog1.findViewById(R.id.cancel_button_choose);

                                    cancel_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            dialog1.dismiss();

                                        }
                                    });

                                    photo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            requestcode = 2;
                                            Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            startActivityForResult(photoCaptureIntent, requestcode);
                                            dialog1.dismiss();
                                            openEdit();

                                        }
                                    });

                                    gallery.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            requestcode = 1;
                                            Intent intent = new Intent();
                                            intent.setType("*/*");
                                            intent.setAction(Intent.ACTION_GET_CONTENT);
                                            startActivityForResult(Intent.createChooser(intent, "Choose file"), requestcode);
                                            dialog1.dismiss();
                                            openEdit();

                                        }
                                    });

                                    dialog1.show();

                                }

                                private void openEdit() {

                                    final Dialog dialog = new Dialog(SubcatList.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                    dialog.setContentView(R.layout.activity_image);
                                    final Calendar calendar = Calendar.getInstance();
                                    timenow = String.valueOf(calendar.getTime());

                                    relativeLayout = (RelativeLayout) dialog.findViewById(R.id.relativelayout1_image);

                                    timenw = (TextView) dialog.findViewById(R.id.timenow);


                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    String str = sdf.format(new Date());

                                    clearbutton = (ImageView) dialog.findViewById(R.id.clear_button);

                                    cancel_button_pic = (ImageView) dialog.findViewById(R.id.cancel_button_edit);

                                    save = (LinearLayout) dialog.findViewById(R.id.save);

                                    pen = (LinearLayout) dialog.findViewById(R.id.pen);

                                    imagev = (ImageView) dialog.findViewById(R.id.image);
                                    pencolor = (CardView) dialog.findViewById(R.id.pencolor);

                                    pencolr = (ImageView) dialog.findViewById(R.id.pencolr);

                                    pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));

                                    BitmapDrawable drawable = (BitmapDrawable) imagev.getDrawable();
                                    Bitmap bitmap2 = drawable.getBitmap();
                                    imagev.setImageBitmap(bitmap2);

                                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                                    timenw.setTypeface(boldTypeface);

                                    timenw.setTextColor(Color.parseColor("#000000"));
                                    timenw.setTextSize(25);
                                    timenw.setText(str);
                                    save.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                                            pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                            pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));


                                            relativeLayout.setDrawingCacheEnabled(true);

                                            imagev.buildDrawingCache(true);

                                            bitmap1 = Bitmap.createBitmap(relativeLayout.getDrawingCache());

                                            relativeLayout.setDrawingCacheEnabled(false);
                                            view_draw.setDrawingCacheEnabled(true);
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                                            final String str = sdf.format(new Date());
                                            bfrimgdatetime = str;

                                            String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap1, "Test123", "drawing");


                                            if (imgSaved != null) {
                                                Log.e("Drawing", "Saved to Gallery");
                                            } else {
                                              /*  Toast unsavedToast = Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT);

                                            unsavedToast.show();*/
                                            }
                                            imagev.setImageBitmap(bitmap1);
                        /*image1.setMaxWidth(256);
                        image1.setMaxHeight(256);
*/
                                            before.setImageBitmap(bitmap1);
                                            view_draw.destroyDrawingCache();

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            // BitmapDrawable drawable = (BitmapDrawable) incident_photo.getDrawable();
                                            Bitmap bitmap = bitmap1;
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] imageBytes = baos.toByteArray();
                                            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                            //   Toast.makeText(Inspection.this, "image Bytes" + imageString, Toast.LENGTH_SHORT).show();
                                            //   Log.e("image1", imageString);
                                            dialog.dismiss();
                                        }
                                    });
                                    clearbutton.setVisibility(View.INVISIBLE);

                                    cancel_button_pic.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    clearbutton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                                            pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                            pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                            try {
                                                path2.reset();
                                                //Toast.makeText(Inspection.this,"Clicked",Toast.LENGTH_SHORT).show();
                                                view_draw.invalidate();
                                                pen.setClickable(false);
                                            } catch (Exception e) {
                                                Log.e("clearbutton", e.toString());
                                            }
                                        }
                                    });

                                    pen.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            clearbutton.setVisibility(View.VISIBLE);
                                            pencolor.setCardBackgroundColor(Color.parseColor("#a9a9a8"));
                                            pencolr.setBackgroundColor(Color.parseColor("#a9a9a8"));

                                            view_draw = new SketchSheetView(SubcatList.this);

                                            paint = new Paint();

                                            path2 = new Path();

                                            relativeLayout.addView(view_draw, new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                                            paint.setDither(true);

                                            paint.setColor(Color.parseColor("#FF0000"));

                                            paint.setStyle(Paint.Style.STROKE);

                                            paint.setStrokeJoin(Paint.Join.ROUND);

                                            paint.setStrokeCap(Paint.Cap.ROUND);

                                            paint.setStrokeWidth(5);


                                        }

                                    });
                                    dialog.show();
                                }
                            });
                            after.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    final Dialog dialog1 = new Dialog(SubcatList.this);

                                    dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                    dialog1.setContentView(R.layout.activity_choose_pic);


                                    ImageButton photo = (ImageButton) dialog1.findViewById(R.id.camera);

                                    ImageButton gallery = (ImageButton) dialog1.findViewById(R.id.gallery);

                                    ImageButton cancel_button = (ImageButton) dialog1.findViewById(R.id.cancel_button_choose);

                                    cancel_button.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            dialog1.dismiss();

                                        }
                                    });

                                    photo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            requestcode = 2;
                                            Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            startActivityForResult(photoCaptureIntent, requestcode);
                                            dialog1.dismiss();
                                            openEdit();

                                        }
                                    });

                                    gallery.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            requestcode = 1;
                                            Intent intent = new Intent();
                                            intent.setType("*/*");
                                            intent.setAction(Intent.ACTION_GET_CONTENT);
                                            startActivityForResult(Intent.createChooser(intent, "Choose file"), requestcode);
                                            dialog1.dismiss();
                                            openEdit();

                                        }
                                    });

                                    dialog1.show();

                                }

                                private void openEdit() {

                                    final Dialog dialog = new Dialog(SubcatList.this);
                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                                    dialog.setContentView(R.layout.activity_image);

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    String str = sdf.format(new Date());

                                    relativeLayout = (RelativeLayout) dialog.findViewById(R.id.relativelayout1_image);

                                    timenw = (TextView) dialog.findViewById(R.id.timenow);

                                    clearbutton = (ImageView) dialog.findViewById(R.id.clear_button);

                                    cancel_button_pic = (ImageView) dialog.findViewById(R.id.cancel_button_edit);

                                    save = (LinearLayout) dialog.findViewById(R.id.save);

                                    pen = (LinearLayout) dialog.findViewById(R.id.pen);

                                    imagev = (ImageView) dialog.findViewById(R.id.image);
                                    pencolor = (CardView) dialog.findViewById(R.id.pencolor);

                                    pencolr = (ImageView) dialog.findViewById(R.id.pencolr);

                                    pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));

                                    BitmapDrawable drawable = (BitmapDrawable) imagev.getDrawable();
                                    Bitmap bitmap2 = drawable.getBitmap();
                                    imagev.setImageBitmap(bitmap2);

                                    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
                                    timenw.setTypeface(boldTypeface);

                                    timenw.setTextColor(Color.parseColor("#000000"));
                                    timenw.setTextSize(25);
                                    timenw.setText(str);

                                    save.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                                            pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                            pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));


                                            relativeLayout.setDrawingCacheEnabled(true);

                                            imagev.buildDrawingCache(true);

                                            bitmap1 = Bitmap.createBitmap(relativeLayout.getDrawingCache());

                                            relativeLayout.setDrawingCacheEnabled(false);
                                            view_draw.setDrawingCacheEnabled(true);
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                                            final String str1 = sdf.format(new Date());
                                            aftrimgdatetime = str1;

                                            String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap1, "Test123", "drawing");


                                            if (imgSaved != null) {
                                                Log.e("Drawing", "Saved to Gallery");
                                            } else {
                                               /* Toast unsavedToast = Toast.makeText(getApplicationContext(), "Oops! Image could not be saved.", Toast.LENGTH_SHORT);

                                                unsavedToast.show();*/
                                            }
                                            imagev.setImageBitmap(bitmap1);
                        /*image1.setMaxWidth(256);
                        image1.setMaxHeight(256);
*/
                                            after.setImageBitmap(bitmap1);
                                            view_draw.destroyDrawingCache();

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            // BitmapDrawable drawable = (BitmapDrawable) incident_photo.getDrawable();
                                            Bitmap bitmap = bitmap1;
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] imageBytes = baos.toByteArray();
                                            imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                            //   Toast.makeText(Inspection.this, "image Bytes" + imageString, Toast.LENGTH_SHORT).show();
                                            //   Log.e("image1", imageString);
                                            dialog.dismiss();
                                        }
                                    });
                                    clearbutton.setVisibility(View.INVISIBLE);

                                    cancel_button_pic.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });

                                    clearbutton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            pencolor.setCardBackgroundColor(Color.parseColor("#d7d7d7"));
                                            pen.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                            pencolr.setBackgroundColor(Color.parseColor("#d7d7d7"));
                                            try {
                                                path2.reset();
                                                //Toast.makeText(Inspection.this,"Clicked",Toast.LENGTH_SHORT).show();
                                                view_draw.invalidate();
                                                pen.setClickable(false);
                                            } catch (Exception e) {
                                                Log.e("clearbutton", e.toString());
                                            }
                                        }
                                    });

                                    pen.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            clearbutton.setVisibility(View.VISIBLE);
                                            pencolor.setCardBackgroundColor(Color.parseColor("#a9a9a8"));
                                            pencolr.setBackgroundColor(Color.parseColor("#a9a9a8"));

                                            view_draw = new SketchSheetView(SubcatList.this);

                                            paint = new Paint();

                                            path2 = new Path();

                                            relativeLayout.addView(view_draw, new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

                                            paint.setDither(true);

                                            paint.setColor(Color.parseColor("#FF0000"));

                                            paint.setStyle(Paint.Style.STROKE);

                                            paint.setStrokeJoin(Paint.Join.ROUND);

                                            paint.setStrokeCap(Paint.Cap.ROUND);

                                            paint.setStrokeWidth(5);


                                        }

                                    });
                                    dialog.show();

                                }
                            });

                            des = describtion.getText().toString();
                            loc = location.getText().toString();


                            save_in.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    InspectionRecylerview.getLayoutManager().scrollToPosition(viewHolder.getAdapterPosition());
                                    if (viewHolder.yes.isChecked()) {
                                        radio = viewHolder.yes.getTag().toString();
                                    } else if (viewHolder.no.isChecked()) {
                                        radio = viewHolder.no.getTag().toString();
                                    } else if (viewHolder.na.isChecked()) {
                                        radio = viewHolder.na.getTag().toString();
                                    }


                                    if (radio.equalsIgnoreCase("1")) {
                                        if (describtion.getText().toString().length() == 0) {
                                            describtion.setError("Please Enter Describtion");
                                        } else if (location.getText().toString().length() == 0) {
                                            location.setError("Please Enter Location");
                                        } else {
                                            checkIds = "";
                                            if (viewHolder.check1.isChecked()) {
                                                checkIds += ("," + viewHolder.check1.getTag().toString());
                                            }
                                            if (viewHolder.check2.isChecked()) {
                                                checkIds += ("," + viewHolder.check2.getTag().toString());
                                            }
                                            if (viewHolder.check3.isChecked()) {
                                                checkIds += ("," + viewHolder.check3.getTag().toString());
                                            }
                                            if (viewHolder.check4.isChecked()) {
                                                checkIds += ("," + viewHolder.check4.getTag().toString());
                                            }
                                            if (viewHolder.check5.isChecked()) {
                                                checkIds += ("," + viewHolder.check5.getTag().toString());
                                            }
                                            if (viewHolder.check6.isChecked()) {
                                                checkIds += ("," + viewHolder.check6.getTag().toString());
                                            }
                                            if (viewHolder.check7.isChecked()) {
                                                checkIds += ("," + viewHolder.check7.getTag().toString());
                                            }
                                            if (viewHolder.check8.isChecked()) {
                                                checkIds += ("," + viewHolder.check8.getTag().toString());
                                            }
                                            if (viewHolder.check9.isChecked()) {
                                                checkIds += ("," + viewHolder.check9.getTag().toString());
                                            }
                                            if (viewHolder.check10.isChecked()) {
                                                checkIds += ("," + viewHolder.check10.getTag().toString());
                                            }
                                            if (viewHolder.check11.isChecked()) {
                                                checkIds += ("," + viewHolder.check11.getTag().toString());
                                            }
                                            if (viewHolder.check12.isChecked()) {
                                                checkIds += ("," + viewHolder.check12.getTag().toString());
                                            }
                                            if (viewHolder.check13.isChecked()) {
                                                checkIds += ("," + viewHolder.check13.getTag().toString());
                                            }
                                            if (viewHolder.check14.isChecked()) {
                                                checkIds += ("," + viewHolder.check14.getTag().toString());
                                            }
                                            if (viewHolder.check15.isChecked()) {
                                                checkIds += ("," + viewHolder.check15.getTag().toString());
                                            }
                                            if (viewHolder.check16.isChecked()) {
                                                checkIds += ("," + viewHolder.check16.getTag().toString());
                                            }
                                            if (viewHolder.check17.isChecked()) {
                                                checkIds += ("," + viewHolder.check17.getTag().toString());
                                            }
                                            if (viewHolder.check18.isChecked()) {
                                                checkIds += ("," + viewHolder.check18.getTag().toString());
                                            }
                                            if (viewHolder.check19.isChecked()) {
                                                checkIds += ("," + viewHolder.check19.getTag().toString());
                                            }
                                            if (viewHolder.check20.isChecked()) {
                                                checkIds += ("," + viewHolder.check20.getTag().toString());
                                            }
                                            if (viewHolder.check21.isChecked()) {
                                                checkIds += ("," + viewHolder.check21.getTag().toString());
                                            }

                                            chechIdCommaRem = checkIds.replaceFirst(",", "");
                                            //  Toast.makeText(context, chechIdCommaRem, Toast.LENGTH_SHORT).show();

                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            BitmapDrawable drawable = (BitmapDrawable) before.getDrawable();
                                            Bitmap bitmap = drawable.getBitmap();
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                            byte[] imageBytes = baos.toByteArray();
                                            imageStringbefore = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                            // Log.e("imageStringbefore", imageStringbefore);

                                            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                                            BitmapDrawable drawable1 = (BitmapDrawable) after.getDrawable();
                                            Bitmap bitmap1 = drawable1.getBitmap();
                                            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
                                            byte[] imageBytes1 = baos1.toByteArray();
                                            imageStringafter = Base64.encodeToString(imageBytes1, Base64.DEFAULT);
                                            Log.e("imageStringafter", imageStringafter);

                                            des = describtion.getText().toString();
                                            loc = location.getText().toString();
                                            fre = viewHolder.frequency.getText().toString();


                                            subcat_id = model.getSubcat_id();

                                            checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
                                            ContentValues cv = new ContentValues();
                                            int id = Integer.parseInt(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""));
                                            cv.put("data_status", "1");
                                            cv.put("insp_options", PreferenceConnector.readString(SubcatList.this, PreferenceConnector.turf_options, ""));
                                            DB.update(checklist_db, "schList", cv, id, "id");

                                            // SubmitResponceApiCalling();
                                            if (save_in.getTag().toString().equalsIgnoreCase("save")) {

                                                String tid = utils.UnixTime();
                                                value_tid.setText(tid);
                                                DBSubCatCheckinsert(tid);
                                            } else {
                                                String tid = value_tid.getText().toString();
                                                DBSubCatCheckUpdate(tid);
                                                Toast.makeText(SubcatList.this, "Updated Sucessfully", Toast.LENGTH_SHORT).show();
                                            }

                                            save_in.setBackgroundColor(Color.parseColor("#FF3C9322"));
                                            save_in.setTag("UPDATE");
                                            save_in.setText("UPDATE");
                    /*viewHolder.submit.setClickable(false);
                    viewHolder.submit.setEnabled(false);*/
                                        }

                                    } else {
                                        checkIds = "";
                                        if (viewHolder.check1.isChecked()) {
                                            checkIds += ("," + viewHolder.check1.getTag().toString());
                                        }
                                        if (viewHolder.check2.isChecked()) {
                                            checkIds += ("," + viewHolder.check2.getTag().toString());
                                        }
                                        if (viewHolder.check3.isChecked()) {
                                            checkIds += ("," + viewHolder.check3.getTag().toString());
                                        }
                                        if (viewHolder.check4.isChecked()) {
                                            checkIds += ("," + viewHolder.check4.getTag().toString());
                                        }
                                        if (viewHolder.check5.isChecked()) {
                                            checkIds += ("," + viewHolder.check5.getTag().toString());
                                        }
                                        if (viewHolder.check6.isChecked()) {
                                            checkIds += ("," + viewHolder.check6.getTag().toString());
                                        }
                                        if (viewHolder.check7.isChecked()) {
                                            checkIds += ("," + viewHolder.check7.getTag().toString());
                                        }
                                        if (viewHolder.check8.isChecked()) {
                                            checkIds += ("," + viewHolder.check8.getTag().toString());
                                        }
                                        if (viewHolder.check9.isChecked()) {
                                            checkIds += ("," + viewHolder.check9.getTag().toString());
                                        }
                                        if (viewHolder.check10.isChecked()) {
                                            checkIds += ("," + viewHolder.check10.getTag().toString());
                                        }
                                        if (viewHolder.check11.isChecked()) {
                                            checkIds += ("," + viewHolder.check11.getTag().toString());
                                        }
                                        if (viewHolder.check12.isChecked()) {
                                            checkIds += ("," + viewHolder.check12.getTag().toString());
                                        }
                                        if (viewHolder.check13.isChecked()) {
                                            checkIds += ("," + viewHolder.check13.getTag().toString());
                                        }
                                        if (viewHolder.check14.isChecked()) {
                                            checkIds += ("," + viewHolder.check14.getTag().toString());
                                        }
                                        if (viewHolder.check15.isChecked()) {
                                            checkIds += ("," + viewHolder.check15.getTag().toString());
                                        }
                                        if (viewHolder.check16.isChecked()) {
                                            checkIds += ("," + viewHolder.check16.getTag().toString());
                                        }
                                        if (viewHolder.check17.isChecked()) {
                                            checkIds += ("," + viewHolder.check17.getTag().toString());
                                        }
                                        if (viewHolder.check18.isChecked()) {
                                            checkIds += ("," + viewHolder.check18.getTag().toString());
                                        }
                                        if (viewHolder.check19.isChecked()) {
                                            checkIds += ("," + viewHolder.check19.getTag().toString());
                                        }
                                        if (viewHolder.check20.isChecked()) {
                                            checkIds += ("," + viewHolder.check20.getTag().toString());
                                        }
                                        if (viewHolder.check21.isChecked()) {
                                            checkIds += ("," + viewHolder.check21.getTag().toString());
                                        }

                                        chechIdCommaRem = checkIds.replaceFirst(",", "");
                                        //  Toast.makeText(context, chechIdCommaRem, Toast.LENGTH_SHORT).show();

                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        BitmapDrawable drawable = (BitmapDrawable) before.getDrawable();
                                        Bitmap bitmap = drawable.getBitmap();
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                        byte[] imageBytes = baos.toByteArray();
                                        imageStringbefore = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                        // Log.e("imageStringbefore", imageStringbefore);

                                        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                                        BitmapDrawable drawable1 = (BitmapDrawable) after.getDrawable();
                                        Bitmap bitmap1 = drawable1.getBitmap();
                                        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
                                        byte[] imageBytes1 = baos1.toByteArray();
                                        imageStringafter = Base64.encodeToString(imageBytes1, Base64.DEFAULT);
                                        Log.e("imageStringafter", imageStringafter);

                                        des = describtion.getText().toString();
                                        loc = location.getText().toString();
                                        fre = viewHolder.frequency.getText().toString();


                                        subcat_id = model.getSubcat_id();

                                        checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
                                        ContentValues cv = new ContentValues();
                                        int id = Integer.parseInt(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""));
                                        cv.put("data_status", "1");
                                        cv.put("insp_options", PreferenceConnector.readString(SubcatList.this, PreferenceConnector.turf_options, ""));
                                        DB.update(checklist_db, "schList", cv, id, "id");
                                        // SubmitResponceApiCalling();

                                        if (save_in.getTag().toString().equalsIgnoreCase("save")) {

                                            String tid = utils.UnixTime();
                                            value_tid.setText(tid);
                                            DBSubCatCheckinsert(tid);
                                        } else {

                                            String tid = value_tid.getText().toString();
                                            DBSubCatCheckUpdate(tid);
                                            Toast.makeText(SubcatList.this, "Updated Sucessfully", Toast.LENGTH_SHORT).show();

                                        }

                                        save_in.setBackgroundColor(Color.parseColor("#FF3C9322"));
                                        save_in.setTag("update");
                                        save_in.setText("UPDATE");
                                    }
                                }
                            });
                            Log.e("count linear", String.valueOf(viewHolder.linearphotoupload.getChildCount() - 4));
                            viewHolder.linearphotoupload.addView(rowView, viewHolder.linearphotoupload.getChildCount() - 1);

                            delete_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        Log.e("indexlay", String.valueOf(delecount));
                                        //   InspectionRecylerview.getLayoutManager().scrollToPosition(viewHolder.getLayoutPosition());


                                    } catch (Exception e) {
                                        Log.e("photodel", e.toString());
                                        Log.e("indexlay", String.valueOf(delecount));

                                    }

                                    if (value_tid.getText().toString().isEmpty()) {

                                    } else {
                                        checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);

                                        checklist_db.execSQL("delete from subcatchecklist where tid='" + value_tid.getText().toString() + "'");

                                    }

                                    //  onDelete(view);
                                    viewHolder.linearphotoupload.removeView(rowView);
                                    delecount--;
                                    Log.e("afterdel", String.valueOf(delecount));
                                }
                            });
                            viewHolder.image_visible.setVisibility(View.GONE);
                            viewHolder.submit.setVisibility(View.GONE);
                            location.setText(allrows.getString(9));
                            describtion.setText(allrows.getString(7));
                            save_in.setText("save");
                            save_in.setTag("Update");
                            value_tid.setText(allrows.getString(0));
                            try {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                byte[] imageBytes = baos.toByteArray();
                                imageBytes = Base64.decode(allrows.getString(13), Base64.DEFAULT);
                                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                before.setImageBitmap(decodedImage);
                            } catch (Exception e) {
                                Log.e("Bad Base64", "before-" + e.toString());
                            }

                            try {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                byte[] imageBytes = baos.toByteArray();
                                imageBytes = Base64.decode(allrows.getString(17), Base64.DEFAULT);
                                Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                after.setImageBitmap(decodedImage);
                            } catch (Exception e) {
                                Log.e("Bad Base64", "after-" + e.toString());
                            }

                        }


                    } while (allrows.moveToNext());
                }
                allrows.close();
                checklist_db.close();


            }

            viewHolder.submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (viewHolder.yes.isChecked()) {
                        radio = viewHolder.yes.getTag().toString();
                    } else if (viewHolder.no.isChecked()) {
                        radio = viewHolder.no.getTag().toString();
                    } else if (viewHolder.na.isChecked()) {
                        radio = viewHolder.na.getTag().toString();
                    }

                    try {
                        if (radio.equalsIgnoreCase("1")) {
                            if (viewHolder.describtion.getText().toString().length() == 0) {
                                viewHolder.describtion.setError("Please Enter Describtion");
                            } else if (viewHolder.location.getText().toString().length() == 0) {
                                viewHolder.location.setError("Please Enter Location");
                            } else {
                                checkIds = "";
                                if (viewHolder.check1.isChecked()) {
                                    checkIds += ("," + viewHolder.check1.getTag().toString());
                                }
                                if (viewHolder.check2.isChecked()) {
                                    checkIds += ("," + viewHolder.check2.getTag().toString());
                                }
                                if (viewHolder.check3.isChecked()) {
                                    checkIds += ("," + viewHolder.check3.getTag().toString());
                                }
                                if (viewHolder.check4.isChecked()) {
                                    checkIds += ("," + viewHolder.check4.getTag().toString());
                                }
                                if (viewHolder.check5.isChecked()) {
                                    checkIds += ("," + viewHolder.check5.getTag().toString());
                                }
                                if (viewHolder.check6.isChecked()) {
                                    checkIds += ("," + viewHolder.check6.getTag().toString());
                                }
                                if (viewHolder.check7.isChecked()) {
                                    checkIds += ("," + viewHolder.check7.getTag().toString());
                                }
                                if (viewHolder.check8.isChecked()) {
                                    checkIds += ("," + viewHolder.check8.getTag().toString());
                                }
                                if (viewHolder.check9.isChecked()) {
                                    checkIds += ("," + viewHolder.check9.getTag().toString());
                                }
                                if (viewHolder.check10.isChecked()) {
                                    checkIds += ("," + viewHolder.check10.getTag().toString());
                                }
                                if (viewHolder.check11.isChecked()) {
                                    checkIds += ("," + viewHolder.check11.getTag().toString());
                                }
                                if (viewHolder.check12.isChecked()) {
                                    checkIds += ("," + viewHolder.check12.getTag().toString());
                                }
                                if (viewHolder.check13.isChecked()) {
                                    checkIds += ("," + viewHolder.check13.getTag().toString());
                                }
                                if (viewHolder.check14.isChecked()) {
                                    checkIds += ("," + viewHolder.check14.getTag().toString());
                                }
                                if (viewHolder.check15.isChecked()) {
                                    checkIds += ("," + viewHolder.check15.getTag().toString());
                                }
                                if (viewHolder.check16.isChecked()) {
                                    checkIds += ("," + viewHolder.check16.getTag().toString());
                                }
                                if (viewHolder.check17.isChecked()) {
                                    checkIds += ("," + viewHolder.check17.getTag().toString());
                                }
                                if (viewHolder.check18.isChecked()) {
                                    checkIds += ("," + viewHolder.check18.getTag().toString());
                                }
                                if (viewHolder.check19.isChecked()) {
                                    checkIds += ("," + viewHolder.check19.getTag().toString());
                                }
                                if (viewHolder.check20.isChecked()) {
                                    checkIds += ("," + viewHolder.check20.getTag().toString());
                                }
                                if (viewHolder.check21.isChecked()) {
                                    checkIds += ("," + viewHolder.check21.getTag().toString());
                                }

                                chechIdCommaRem = checkIds.replaceFirst(",", "");
                                // Toast.makeText(context, chechIdCommaRem, Toast.LENGTH_SHORT).show();

                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                BitmapDrawable drawable = (BitmapDrawable) viewHolder.before.getDrawable();
                                Bitmap bitmap = drawable.getBitmap();
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                byte[] imageBytes = baos.toByteArray();
                                imageStringbefore = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                                // Log.e("imageStringbefore", imageStringbefore);

                                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                                BitmapDrawable drawable1 = (BitmapDrawable) viewHolder.after.getDrawable();
                                Bitmap bitmap1 = drawable1.getBitmap();
                                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
                                byte[] imageBytes1 = baos1.toByteArray();
                                imageStringafter = Base64.encodeToString(imageBytes1, Base64.DEFAULT);
                                Log.e("imageStringafter", imageStringafter);

                                des = viewHolder.describtion.getText().toString();
                                loc = viewHolder.location.getText().toString();
                                fre = viewHolder.frequency.getText().toString();


                                subcat_id = model.getSubcat_id();

                                checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
                                ContentValues cv = new ContentValues();
                                int id = Integer.parseInt(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""));
                                cv.put("data_status", "1");
                                cv.put("insp_options", PreferenceConnector.readString(SubcatList.this, PreferenceConnector.turf_options, ""));
                                DB.update(checklist_db, "schList", cv, id, "id");
                                // SubmitResponceApiCalling();
                                if (viewHolder.submit.getTag().toString().equalsIgnoreCase("save")) {
                                    String tid = utils.UnixTime();
                                    viewHolder.value_tid.setText(tid);
                                    DBSubCatCheckinsert(tid);
                                } else {
                                    String tid = viewHolder.value_tid.getText().toString();
                                    DBSubCatCheckUpdate(tid);
                                    Toast.makeText(SubcatList.this, "Updated Sucessfully", Toast.LENGTH_SHORT).show();

                                }

                                viewHolder.submit.setBackgroundColor(Color.parseColor("#FF3C9322"));
                                viewHolder.submit.setTag("UPDATE");
                                viewHolder.submit.setText("UPDATE");
                    /*viewHolder.submit.setClickable(false);
                    viewHolder.submit.setEnabled(false);*/
                            }

                        } else {
                            checkIds = "";
                            if (viewHolder.check1.isChecked()) {
                                checkIds += ("," + viewHolder.check1.getTag().toString());
                            }
                            if (viewHolder.check2.isChecked()) {
                                checkIds += ("," + viewHolder.check2.getTag().toString());
                            }
                            if (viewHolder.check3.isChecked()) {
                                checkIds += ("," + viewHolder.check3.getTag().toString());
                            }
                            if (viewHolder.check4.isChecked()) {
                                checkIds += ("," + viewHolder.check4.getTag().toString());
                            }
                            if (viewHolder.check5.isChecked()) {
                                checkIds += ("," + viewHolder.check5.getTag().toString());
                            }
                            if (viewHolder.check6.isChecked()) {
                                checkIds += ("," + viewHolder.check6.getTag().toString());
                            }
                            if (viewHolder.check7.isChecked()) {
                                checkIds += ("," + viewHolder.check7.getTag().toString());
                            }
                            if (viewHolder.check8.isChecked()) {
                                checkIds += ("," + viewHolder.check8.getTag().toString());
                            }
                            if (viewHolder.check9.isChecked()) {
                                checkIds += ("," + viewHolder.check9.getTag().toString());
                            }
                            if (viewHolder.check10.isChecked()) {
                                checkIds += ("," + viewHolder.check10.getTag().toString());
                            }
                            if (viewHolder.check11.isChecked()) {
                                checkIds += ("," + viewHolder.check11.getTag().toString());
                            }
                            if (viewHolder.check12.isChecked()) {
                                checkIds += ("," + viewHolder.check12.getTag().toString());
                            }
                            if (viewHolder.check13.isChecked()) {
                                checkIds += ("," + viewHolder.check13.getTag().toString());
                            }
                            if (viewHolder.check14.isChecked()) {
                                checkIds += ("," + viewHolder.check14.getTag().toString());
                            }
                            if (viewHolder.check15.isChecked()) {
                                checkIds += ("," + viewHolder.check15.getTag().toString());
                            }
                            if (viewHolder.check16.isChecked()) {
                                checkIds += ("," + viewHolder.check16.getTag().toString());
                            }
                            if (viewHolder.check17.isChecked()) {
                                checkIds += ("," + viewHolder.check17.getTag().toString());
                            }
                            if (viewHolder.check18.isChecked()) {
                                checkIds += ("," + viewHolder.check18.getTag().toString());
                            }
                            if (viewHolder.check19.isChecked()) {
                                checkIds += ("," + viewHolder.check19.getTag().toString());
                            }
                            if (viewHolder.check20.isChecked()) {
                                checkIds += ("," + viewHolder.check20.getTag().toString());
                            }
                            if (viewHolder.check21.isChecked()) {
                                checkIds += ("," + viewHolder.check21.getTag().toString());
                            }

                            chechIdCommaRem = checkIds.replaceFirst(",", "");
                            // Toast.makeText(context, chechIdCommaRem, Toast.LENGTH_SHORT).show();

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            BitmapDrawable drawable = (BitmapDrawable) viewHolder.before.getDrawable();
                            Bitmap bitmap = drawable.getBitmap();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] imageBytes = baos.toByteArray();
                            imageStringbefore = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                            // Log.e("imageStringbefore", imageStringbefore);

                            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                            BitmapDrawable drawable1 = (BitmapDrawable) viewHolder.after.getDrawable();
                            Bitmap bitmap1 = drawable1.getBitmap();
                            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
                            byte[] imageBytes1 = baos1.toByteArray();
                            imageStringafter = Base64.encodeToString(imageBytes1, Base64.DEFAULT);
                            Log.e("imageStringafter", imageStringafter);

                            des = viewHolder.describtion.getText().toString();
                            loc = viewHolder.location.getText().toString();
                            fre = viewHolder.frequency.getText().toString();


                            subcat_id = model.getSubcat_id();

                            checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
                            ContentValues cv = new ContentValues();
                            int id = Integer.parseInt(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""));
                            cv.put("data_status", "1");
                            cv.put("insp_options", PreferenceConnector.readString(SubcatList.this, PreferenceConnector.turf_options, ""));
                            DB.update(checklist_db, "schList", cv, id, "id");
                            // SubmitResponceApiCalling();
                            if (viewHolder.submit.getTag().toString().equalsIgnoreCase("save")) {
                                String tid = utils.UnixTime();
                                viewHolder.value_tid.setText(tid);
                                DBSubCatCheckinsert(tid);
                            } else {
                                String tid = viewHolder.value_tid.getText().toString();
                                DBSubCatCheckUpdate(tid);
                                Toast.makeText(SubcatList.this, "Updated Sucessfully", Toast.LENGTH_SHORT).show();

                            }
                            viewHolder.submit.setBackgroundColor(Color.parseColor("#FF3C9322"));
                            viewHolder.submit.setText("UPDATE");
                            viewHolder.submit.setTag("Update");
                        }


                    } catch (Exception e) {
                        Log.e("error puriyathu", e.toString());

                    }


                }
            });

           /* viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    PreferenceConnector.writeString(SubcatList.this,PreferenceConnector.head_id,model.getId());
                    Intent i = new Intent(SubcatList.this,MaincatList.class);
                    startActivity(i);

                }
            });*/


        }

        @Override
        public int getItemCount() {
            dismissProgressDialog();

            return arrayLists.size();
        }

        // Recyclerview Changing Items During Scrolling
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public final class ListItemViewHolder extends RecyclerView.ViewHolder {

            //  private RadioGroup radioGroup;
            ImageView before, after;
            TextView subcategory, turf_visible, if_turf_visible;
            ImageView pencolr;
            EditText describtion, location, frequency, value_tid;
            View view_draw;
            RadioButton yes, no, na;

            Button submit, add_field_button;
            LinearLayout checklayout, imagelayout, image_visible, linearphotoupload;
            CheckBox check1, check2, check3, check4, check5, check6, check7, check8, check9, check10, check11, check12, check13, check14, check15, check16, check17, check18, check19, check20, check21;
            //CheckBox Tv;


            public ListItemViewHolder(View itemView) {
                super(itemView);
                view_draw = new SketchSheetView(SubcatList.this);
                //  radioGroup = (RadioGroup) itemView.findViewById(R.id.radiogrp);
                before = (ImageView) itemView.findViewById(R.id.before);
                turf_visible = (TextView) itemView.findViewById(R.id.turf_visible);
                if_turf_visible = (TextView) itemView.findViewById(R.id.if_turf_visible);
                after = (ImageView) itemView.findViewById(R.id.after);
                pencolr = (ImageView) itemView.findViewById(R.id.pencolr);

                subcategory = (TextView) itemView.findViewById(R.id.subcategory);
                describtion = (EditText) itemView.findViewById(R.id.describtion);
                location = (EditText) itemView.findViewById(R.id.location);
                frequency = (EditText) itemView.findViewById(R.id.frequency);

                imagelayout = (LinearLayout) itemView.findViewById(R.id.imagelayout);
                checklayout = (LinearLayout) itemView.findViewById(R.id.checklayout);


                yes = (RadioButton) itemView.findViewById(R.id.yes);
                no = (RadioButton) itemView.findViewById(R.id.no);
                na = (RadioButton) itemView.findViewById(R.id.na);


                check1 = (CheckBox) itemView.findViewById(R.id.check1);
                check2 = (CheckBox) itemView.findViewById(R.id.check2);
            

                submit = (Button) itemView.findViewById(R.id.submit);
                add_field_button = (Button) itemView.findViewById(R.id.add_field_button);

                linearphotoupload = (LinearLayout) itemView.findViewById(R.id.linearphotoupload);
                image_visible = (LinearLayout) itemView.findViewById(R.id.image_visible);
                value_tid = (EditText) itemView.findViewById(R.id.value_tid);
            }
        }
    }

    private void SubmitResponceApiCalling() {
        try {
            System.out.println("==============Start API=========");
            if (Utils.isNetworkAvailable(SubcatList.this)) {


                //showProgressDialog();
                SubmitRequestModel userDatas = new SubmitRequestModel();
                System.out.println("==============Submit Response=========");

                if (radio.equalsIgnoreCase("1")) {
                    userDatas.setInspId(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""));
                    userDatas.setInspTypeId(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.head_id, ""));
                    userDatas.setMainCatId(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_id, ""));
                    userDatas.setSubCatId(subcat_id);
                    userDatas.setItemids(chechIdCommaRem);
                    userDatas.setContent(des);
                    userDatas.setLoc(loc);
                    userDatas.setFrequency(fre);
                    userDatas.setBfrimgpath(imageStringbefore);
                    userDatas.setAftrimgpath(imageStringafter);
                    userDatas.setCheckstatus(radio);
                    userDatas.setBfrimgdatetime(bfrimgdatetime);
                    userDatas.setAftrimgdatetime(aftrimgdatetime);
                } else {
                    userDatas.setInspId(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""));
                    userDatas.setInspTypeId(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.head_id, ""));
                    userDatas.setMainCatId(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_id, ""));
                    userDatas.setSubCatId(subcat_id);
                    userDatas.setItemids("");
                    userDatas.setContent("");
                    userDatas.setLoc("");
                    userDatas.setFrequency("");
                    userDatas.setBfrimgpath("");
                    userDatas.setAftrimgpath("");
                    userDatas.setCheckstatus(radio);
                    userDatas.setBfrimgdatetime("");
                    userDatas.setAftrimgdatetime("");
                }

                Retrofit retrofit = Utils.callRetrofit();
                ApiInterface service = retrofit.create(ApiInterface.class);

                Log.e("reg==datas", String.valueOf(userDatas));

                Call<SubmitResponseModel> call = service.getsubmitResponse("", userDatas);
                call.enqueue(new Callback<SubmitResponseModel>() {
                    @Override
                    public void onResponse(Call<SubmitResponseModel> call, Response<SubmitResponseModel> response) {
                        System.out.println("================" + new Gson().toJson(response.body()));

                        Log.e("register_success", new Gson().toJson(response.body()));

                        // dismissProgressDialog();
                        if (response.isSuccessful()) {
                            System.out.println("=======success===========" + response.body().getStatus());

                            String message = response.body().getMessage().toString();
                            // //Toast.makeText(getActivity(),message,Toast.LENGTH_SHORT).show();
                            //  Intent intent = new Intent(MainActivity.this,IntialInvestigationListActivity.class);

                           /* Fragment fragment = new InitialInvestigationListFragment();
                            FragmentManager fragentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.frame, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();*/

                        }
                    }

                    @Override
                    public void onFailure(Call<SubmitResponseModel> call, Throwable t) {
                        t.printStackTrace();
                        System.out.println("============failure=====" + t.getMessage());
                        //Toast.makeText(AddIncidentActivity.this, "Response Failure" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Error", t.getMessage());
                        dismissProgressDialog();
                    }
                });
            } else {
                Toast.makeText(SubcatList.this, "Storing Datas", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            dismissProgressDialog();
            Log.e("catch", e.toString());
            e.printStackTrace();
        }
    }

    private void SubmitResponceFinalSub() {
        try {
            System.out.println("==============Start API=========");
            if (Utils.isNetworkAvailable(SubcatList.this)) {


                showProgressDialog();
                SubCategoryFinalSubmitRequestModel userDatas = new SubCategoryFinalSubmitRequestModel();
                System.out.println("==============Submit Response Final =========");

                userDatas.setScheduleid(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""));
                userDatas.setCategory(PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_id, ""));

                Retrofit retrofit = Utils.callRetrofit();
                ApiInterface service = retrofit.create(ApiInterface.class);

                Log.e("reg===datas", String.valueOf(userDatas));

                Call<SubCategoryFinalSubmitResponseModel> call = service.getsubmitfinalsub("", userDatas);
                call.enqueue(new Callback<SubCategoryFinalSubmitResponseModel>() {
                    @Override
                    public void onResponse(Call<SubCategoryFinalSubmitResponseModel> call, Response<SubCategoryFinalSubmitResponseModel> response) {
                        System.out.println("================" + new Gson().toJson(response.body()));

                        Log.e("register_success_Final", new Gson().toJson(response.body()));

                        dismissProgressDialog();
                        if (response.isSuccessful()) {
                            System.out.println("=======success Final===========" + response.body().getStatus());

                            String message = response.body().getMessage().toString();
                            Intent intent = new Intent(SubcatList.this, MaincatList.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);


                            ContentValues cv_maincat = new ContentValues();
                            cv_maincat.put("updated", "1");
                            checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);

                            DB.update2(checklist_db, "mainCatList", cv_maincat, catid, insid, "id", "sch_id");

                        }
                    }

                    @Override
                    public void onFailure(Call<SubCategoryFinalSubmitResponseModel> call, Throwable t) {
                        t.printStackTrace();
                        System.out.println("============failure=====" + t.getMessage());
                        //Toast.makeText(AddIncidentActivity.this, "Response Failure" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Error", t.getMessage());
                        dismissProgressDialog();
                    }
                });
            } else {
                Toast.makeText(SubcatList.this, "Storing Datas in Offline2", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SubcatList.this, MaincatList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ContentValues cv_maincat = new ContentValues();
                cv_maincat.put("updated", "1");
                checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);

                DB.update2(checklist_db, "mainCatList", cv_maincat, catid, insid, "id", "sch_id");
            }
        } catch (Exception e) {
            dismissProgressDialog();
            Log.e("catch", e.toString());
            e.printStackTrace();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(SubcatList.this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        dismissProgressDialog();
        super.onDestroy();
    }

    class SketchSheetView extends View {

        public SketchSheetView(Context context) {
            super(context);
            bitmap = Bitmap.createBitmap(480, 640, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
        }

        private ArrayList<DrawingClass> DrawingClassArrayList = new ArrayList<DrawingClass>();

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            DrawingClass pathWithPaint = new DrawingClass();

            canvas.drawPath(path2, paint);

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                path2.moveTo(event.getX(), event.getY());

                path2.lineTo(event.getX(), event.getY());
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                path2.lineTo(event.getX(), event.getY());

                pathWithPaint.setPath(path2);

                pathWithPaint.setPaint(paint);

                DrawingClassArrayList.add(pathWithPaint);
            }

            invalidate();
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (DrawingClassArrayList.size() > 5) {

                canvas.drawPath(DrawingClassArrayList.get(DrawingClassArrayList.size() - 5).getPath(),

                        DrawingClassArrayList.get(DrawingClassArrayList.size() - 5).getPaint());
            }
        }
    }

    public class DrawingClass {

        Path DrawingClassPath;
        Paint DrawingClassPaint;

        public Path getPath() {
            return DrawingClassPath;
        }

        public void setPath(Path path) {
            this.DrawingClassPath = path;
        }


        public Paint getPaint() {
            return DrawingClassPaint;
        }

        public void setPaint(Paint paint) {
            this.DrawingClassPaint = paint;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri filePath = data.getData();

                try {
                    //getting image from gallery
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    imagev.setImageBitmap(bitmap);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(new File(filePath.getPath()).getAbsolutePath(), options);
                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (requestCode == 2) {

            if (resultCode == RESULT_OK) {

                try {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imagev.setImageBitmap(bitmap);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void DBSubCatCheckinsert(String tid) {

        if (radio.equalsIgnoreCase("1")) {

            // Integer tid = Integer.valueOf(utils.UnixTime());
            Log.e("unixtime", "" + tid);
            String insid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, "");
            String inspectiontypeid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.head_id, "");
            String catid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_id, "");
            String subcatid = subcat_id;
            String itemid = chechIdCommaRem;
            String checkstatus = radio;
            String content = des;
            String frequency = fre;
            String location = loc;
            String befimgid = "";
            String aftimgid = "";
            String bfrimgpath = "";
            String bfrimgbase64 = imageStringbefore;
            String bfrimgdate_time = bfrimgdatetime;
            String aftrimgdate_time = aftrimgdatetime;
            String aftrimgpath = "";
            String aftrimgbase64 = imageStringafter;
            String createdon = utils.UnixTime();
            String updatedon = "";
            String status = "0";
            String submit_status = "0";
            String insp_option = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.turf_options, "");
            String pending_tid = "";

            checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
            DB.insert_table_subcat_check_list(checklist_db, tid, insid, inspectiontypeid, catid, subcatid, itemid, checkstatus, content, frequency, location, befimgid, aftimgid, bfrimgpath, bfrimgbase64, bfrimgdate_time, aftrimgdate_time, aftrimgpath, aftrimgbase64, createdon, updatedon, status, submit_status, insp_option, pending_tid);
            Log.e("Insert Complete", "Radio 1 subcat check list");


        } else {

            // Integer tid = Integer.valueOf(utils.UnixTime());
            Log.e("unixtime", "" + tid);
            String insid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, "");
            String inspectiontypeid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.head_id, "");
            String catid = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_id, "");
            String subcatid = subcat_id;
            String itemid = "";
            String checkstatus = radio;
            String content = "";
            String frequency = fre;
            String location = "";
            String befimgid = "";
            String aftimgid = "";
            String bfrimgpath = "";
            String bfrimgbase64 = "";
            String bfrimgdate_time = "";
            String aftrimgdate_time = "";
            String aftrimgpath = "";
            String aftrimgbase64 = "";
            String createdon = utils.UnixTime();
            String updatedon = "";
            String status = "0";
            String submit_status = "0";
            String insp_option = PreferenceConnector.readString(SubcatList.this, PreferenceConnector.turf_options, "");
            String pending_tid = "";

            checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
            DB.insert_table_subcat_check_list(checklist_db, tid, insid, inspectiontypeid, catid, subcatid, itemid, checkstatus, content, frequency, location, befimgid, aftimgid, bfrimgpath, bfrimgbase64, bfrimgdate_time, aftrimgdate_time, aftrimgpath, aftrimgbase64, createdon, updatedon, status, submit_status, insp_option, pending_tid);
            Log.e("Insert Complete", "Radio else subcat check list");


        }


    }

    private void DBSubCatCheckUpdate(String tid)

    {

        if (radio.equalsIgnoreCase("1")) {

            checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
            Log.e("Updating!! ", "Updating...");

            ContentValues cv = new ContentValues();
            //cv.put("tid",utils.UnixTime());
            // cv.put("insid",);
            // cv.put("inspectiontypeid",);
            // cv.put("catid",);
            // cv.put("subcatid",);
            cv.put("itemids", chechIdCommaRem);
            cv.put("checkstatus", radio);
            cv.put("content", des);
            cv.put("frequency", fre);
            cv.put("location", loc);
            cv.put("befimgid", "");
            cv.put("aftimgid", "");
            cv.put("bfrimgpath", "");
            cv.put("bfrimgbase64", imageStringbefore);
            cv.put("bfrimgdatetime", bfrimgdatetime);
            cv.put("aftrimgdatetime", aftrimgdatetime);
            cv.put("aftrimgpath", "");
            cv.put("aftrimgbase64", imageStringafter);
            //cv.put("created_on",);
            cv.put("updated_on", utils.UnixTime());
            cv.put("status", 0);
            cv.put("submit_status", 0);

            checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
            DB.updatesubcatchecklist(checklist_db, "subcatcheckList", cv, PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""), PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_id, ""), subcat_id, tid, "insid", "catid", "subcatid", "tid");
            Log.e("Updated", "Updated");
        } else {

            checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
            Log.e("Updating!! ", "Updating...");

            ContentValues cv = new ContentValues();
            //cv.put("tid",utils.UnixTime());
            // cv.put("insid",);
            // cv.put("inspectiontypeid",);
            // cv.put("catid",);
            // cv.put("subcatid",);
            cv.put("itemids", "");
            cv.put("checkstatus", radio);
            cv.put("content", "");
            cv.put("frequency", fre);
            cv.put("location", "");
            cv.put("befimgid", "");
            cv.put("aftimgid", "");
            cv.put("bfrimgpath", "");
            cv.put("bfrimgbase64", "");
            cv.put("bfrimgdatetime", "");
            cv.put("aftrimgdatetime", "");
            cv.put("aftrimgpath", "");
            cv.put("aftrimgbase64", "");
            //cv.put("created_on",);
            cv.put("updated_on", utils.UnixTime());
            cv.put("status", 0);
            cv.put("submit_status", 0);

            checklist_db = SubcatList.this.openOrCreateDatabase("checklist_db", MODE_PRIVATE, null);
            DB.updatesubcatchecklist(checklist_db, "subcatcheckList", cv, PreferenceConnector.readString(SubcatList.this, PreferenceConnector.sch_id, ""), PreferenceConnector.readString(SubcatList.this, PreferenceConnector.cat_id, ""), subcat_id, tid, "insid", "catid", "subcatid", "tid");
            Log.e("Updated", "Updated");


        }

    }


}
