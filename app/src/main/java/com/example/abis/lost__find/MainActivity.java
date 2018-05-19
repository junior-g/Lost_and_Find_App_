package com.example.abis.lost__find;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.widget.EditText;
import android.widget.ImageView;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import android.provider.MediaStore;
import android.graphics.Bitmap;
import java.io.IOException;
import com.example.abis.lost__find.CustomVolley.VolleyMultipartRequest;


import java.io.ByteArrayOutputStream;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.NetworkResponse;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.abis.lost__find.ForRecycleView.CustomAdapter;
import com.example.abis.lost__find.ForRecycleView.CustomPojo;
import com.example.abis.lost__find.JSONCustom.CustomRequest;
import com.example.abis.lost__find.Urlhandling.centerDetails;

public class MainActivity extends AppCompatActivity {


    private EditText sendMessage;
    private ImageView sendButton, addphotoButton, selectedImage, refreshButton;
    private boolean isImage=false;
    private Bitmap bitmap;

    private RecyclerView recyclerView;
    private List<CustomPojo> customPojoList;
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Init
        sendMessage=(EditText) findViewById(R.id.editText);
        sendButton=(ImageView)findViewById(R.id.sendButton);
        addphotoButton=(ImageView)findViewById(R.id.addPhotoButton);
        selectedImage=(ImageView)findViewById(R.id.selectedImage);
        refreshButton=(ImageView)findViewById(R.id.refreshButton);
        //Recycle View Init
        recyclerView=(RecyclerView)findViewById(R.id.recycleMessage);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        customPojoList=new ArrayList<>();
        setAdapter();
        selectedImage.setImageResource(0);
        //checking the permission
        //if the permission is not given we will open setting to add permission
        //else app will not open
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            finish();
            startActivity(intent);
            return;
        }
        ReadDB();
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });

        addphotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendImage();
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customPojoList.clear();
                customAdapter.notifyDataSetChanged();
                ReadDB();
            }
        });

    }
    private void setAdapter()
    {
        customAdapter=new CustomAdapter(customPojoList, MainActivity.this);
        recyclerView.setAdapter(customAdapter);
    }

    private void ReadDB()
    {
            String url=centerDetails.BASE_URL+"/notes";

        RequestQueue queue = Volley.newRequestQueue(this);
        // prepare the Request
        CustomRequest getRequest = new CustomRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());

                        for(int i=0; i<response.length(); ++i)
                        {
                            JSONObject jsonObject=new JSONObject();
                            try {

                                jsonObject=response.getJSONObject(i);

                                CustomPojo customPojo=new CustomPojo();
                                if(!jsonObject.isNull("message"))
                                customPojo.setMessage(jsonObject.getString("message"));
                                else
                                    customPojo.setMessage(null);
                                if(jsonObject.isNull("imgpath"))
                                 customPojo.setImgpath(null);
                                 else
                                {
                                    String pathUrl=centerDetails.BASE_URL+"/images/"+jsonObject.getString("imgpath");
                                    customPojo.setImgpath(pathUrl);
                                }
                                customPojoList.add(customPojo);



                            }catch (Exception e)
                            {
                                    Log.e("reading jsonobjectat-"+i+" and got->",e.toString() );
                            }

                        }
                        customAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);
    }

    private void sendImage()
    {
        //if everything is ok we will open image chooser
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 100);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                 bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                //displaying selected image to imageview
                selectedImage.setImageBitmap(bitmap);

                //calling the method uploadBitmap to upload image
                //uploadBitmap(bitmap);
                isImage=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * The method is taking Bitmap as an argument
     * then it will return the byte[] array for the given bitmap
     * and we will send this array to the server
     * here we are using PNG Compression with 80% quality
     * you can give quality between 0 to 100
     * 0 means worse quality
     * 100 means best quality
     * */
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void uploadBitmap(final Bitmap bitmap) {

        String url=centerDetails.BASE_URL+"/images/upload";

        //getting the tag from the edittext
        final String tags = "image";

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                            Log.e("Image Upload Response",response.toString());

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Image upload error->", error.toString());
                    }
                }) {



            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tags", tags);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("image", new DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
        isImage=false;
        selectedImage.setImageResource(0);
    }

    private void sendData() {
        if(isImage)
        {
            uploadBitmap(bitmap);
        }
        if(!sendMessage.getText().toString().isEmpty()) {
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject1.put("message", sendMessage.getText().toString());
            } catch (Exception e) {

            }
            sendMessage.setText(null);
            RequestQueue MyRequestQueue = Volley.newRequestQueue(MainActivity.this);
            String url = centerDetails.BASE_URL + "/notes";
            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.DEPRECATED_GET_OR_POST, url, jsonObject1,
                    new com.android.volley.Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("MainActivity", response.toString());
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("MainActivity", "Response parsing problem-" + error);
                        }
                    });
// Add the request to the RequestQueue.
            MyRequestQueue.add(stringRequest);
        }
    }
}
