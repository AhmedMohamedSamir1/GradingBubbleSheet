package com.example.gp2021.ui.instructor;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.example.gp2021.ui.instructor.Util.getSource;

import static com.example.gp2021.ui.instructor.Util.sout;
import static com.example.gp2021.ui.instructor.IUtil.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gp2021.R;
import com.example.gp2021.ui.login.SignupActivity;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.royrodriguez.transitionbutton.TransitionButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;


public class CustomCamaraActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnClickListener {
    int NumOfQuestions;
    public String SelectedExam;
    Spinner ExamsSpinner;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    private Context context;
    private SurfaceView surfaceView;
    private CircleButton btnLoadFromGallary;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private CircleButton BtnCapturarSheet;
    private int cameraId;
    //  private boolean flashmode = false;
    private int rotation;
    private ActionBar actionBar;
    private CameraSource cameraSource;
    private StringBuilder builder;
    private TextView txTextoCapturado;

    public HashMap<String, String> MyQuestAndAns;
    private static CustomCamaraActivity instance;
    private Activity activity;
    private String nombreProyecto;
    public static final int REQUEST_IMAGE_CAPTURE = 0020;
    private File file;
    private String ubicacion;
    static final int REQUEST_IMAGE = 1;

    private static final String TAG = "FOTOGRAFIA";
    private int Height = 620, Width = 480;
    private TextRecognizer recognizer;
    public String[] ExamID;
    public final int SELECT_PICTURE=142;
    public  ImageView im;
    private final int Camera_Req=1888;
    Bitmap imageBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExamID = new String[1];
        setContentView(R.layout.activity_custom_camara);
        context = this;
        arrayList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnLoadFromGallary=findViewById(R.id.loadImageFromGallary);
        ExamsSpinner = (Spinner) findViewById(R.id.ListOfExams);

        im = findViewById(R.id.dfs);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("exam");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> are = new ArrayList<>();
                are.add("Select your exam's answer");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String ExamName = ds.child("examName").getValue().toString();
                    // Toast.makeText(CustomCamaraActivity.this, "You Select exam: "+userType, Toast.LENGTH_SHORT).show();
                    are.add(ExamName);

                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(CustomCamaraActivity.this, android.R.layout.simple_spinner_item, are);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // areasAdapter.add("Select your Exam"); //This is the text that will be displayed as hint.
                ExamsSpinner.setAdapter(areasAdapter);
                //ListViewExams.setPrompt("Select your Exam");
                ExamsSpinner.setSelection(0, false);

                //ListViewExams.setSelection(areasAdapter.getCount()-1);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        SwitchCompat flashSwitch=findViewById(R.id.switchFlash);
        flashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                   /* boolean isFlashAvailable = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                     CameraManager mCameraManager;
                    String mCameraId = null;
                    mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    try {
                        mCameraId = mCameraManager.getCameraIdList()[0];
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                    try {
                        mCameraManager.setTorchMode(mCameraId, true);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
*/


                    Toast.makeText(getApplicationContext(),"FlashON",Toast.LENGTH_LONG).show();

                } else {
                    Camera.Parameters param = camera.getParameters();
                    // boolean flashmode = false;
                    camera.stopPreview();
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(param);
                    camera.startPreview();

                    Toast.makeText(getApplicationContext(),"FlashOFF",Toast.LENGTH_LONG).show();

                }
            }
        });
        MyQuestAndAns = new HashMap<String, String>(); //Key : question Number , //value : Answer
        ExamsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SelectedExam = ExamsSpinner.getSelectedItem().toString();

                if (position > 0) {
                    Toast.makeText(CustomCamaraActivity.this, "You Select exam: " + SelectedExam, Toast.LENGTH_SHORT).show();
                    //Hna Elmfrod arg3 egabat el exam wna m3aya Elname bta3o ello howa 3nd "position"  hgebo w a7to fe list
                    // DatabaseReference Ref = FirebaseDatabase.getInstance().getReference().child("exam_question");
                    // QuestAndAns.put(0,"A");
                    //Start
                    /*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("exam");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String ExamName = ds.child("examName").getValue().toString();
                                String ID=ds.child("examID").getValue().toString();
                                // Toast.makeText(CustomCamaraActivity.this, "You Select exam: "+userType, Toast.LENGTH_SHORT).show();
                                if(ExamName.equals(selectedItem))
                                {

                                    ExamID[0] =ID;

                                   // Toast.makeText(CustomCamaraActivity.this, "examID: "+ ExamID[0]+" "+selectedItem, Toast.LENGTH_SHORT).show();
                                    break;


                                }

                            }






                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //END




                    //Start[]
//                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("exam_question").child(ExamID[0]);
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("exam_question").child("1");

                    databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String QuesID = ds.child("questionID").getValue().toString();
                                // Toast.makeText(CustomCamaraActivity.this, "You Select exam: "+userType, Toast.LENGTH_SHORT).show();
                                String QuesAns = ds.child("questionAnswer").getValue().toString();
                                QuestAndAns.put(QuesID, QuesAns);
                            }







                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //END []


                    Toast.makeText(CustomCamaraActivity.this, "AS", Toast.LENGTH_SHORT).show();


*/
                    readData(QuestAndAns -> {
                        // MyQuestAndAns.putAll(QuestAndAns);
                        Toast.makeText(getApplicationContext(), QuestAndAns.get("1"), Toast.LENGTH_LONG).show();

                        //KML HnA


                    });
                    //  Toast.makeText(getApplicationContext(),QuestAndAns.get("1"),Toast.LENGTH_LONG).show();

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnLoadFromGallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickImage();


            }
        });
        // camera surface view created
        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        BtnCapturarSheet = (CircleButton) findViewById(R.id.btnCapturarSheet);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        txTextoCapturado = (TextView) findViewById(R.id.tvTextoCapturado);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        BtnCapturarSheet.setOnClickListener(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    /*if (Camera.getNumberOfCameras() > 1) {
        flipCamera.setVisibility(View.VISIBLE);
    }*/
        if (!getBaseContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FLASH)) {
        }


        recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (recognizer.isOperational()) {

            cameraSource = new CameraSource.Builder(getApplicationContext(), recognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 960)
                    .setRequestedFps(30.0f)
                    .setAutoFocusEnabled(true)
                    .build();

            surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CustomCamaraActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                        return;
                    }
                    try {
                        releaseCamera();
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    if (cameraSource != null) {
                        cameraSource.stop();
                    }
                }
            });
            recognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {
                        builder = new StringBuilder();
                        for (int i = 0; i < items.size(); i++) {
                            TextBlock it = items.valueAt(i);
                            builder.append(it.getValue());
                        }
                        final String read = builder.toString().trim();
                        //String read = builder.toString().trim().replace(" ", "").replace("\n", "");
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < items.size(); i++) {
                                        TextBlock item = items.valueAt(i);

                                        stringBuilder.append(item.getValue());
                                        stringBuilder.append("\n");
                                    }
                                    if ((stringBuilder.toString()).contains("ID")) {

                                        // mTextView.setText("ContainID");
                                        String Detected = stringBuilder.toString();
                                        String StudID = "Waiting";
                                        String[] All = Detected.split(" ");
                                        //  mTextView.setText("Waiting ...");
                                        for (int k = 0; k < All.length; k++) {
                                            if (All[k].equals("ID")) {
                                                StudID = All[k + 1];
                                                StudID = stripNonDigits(StudID);
                                                txTextoCapturado.setText("ID :" + StudID);

                                                break;
                                            }
                                        }

                                        //  mTextView.setText(stringBuilder.toString());

                                    } else {
                                        txTextoCapturado.setText("Waiting");
                                    }


                                    // txTextoCapturado.setText(read);
                                }
                            });
                        } catch (Exception ex) {
                            Log.e("error", "Error al actualizar texto OCR");
                        }

                        //It continues doing other things here
                    }
                }
            });
        }

        NumOfQuestions = 0;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            NumOfQuestions = bundle.getInt("Questions");
        }
        // Toast.makeText(getApplicationContext(),"Number= "+NumOfQuestions,Toast.LENGTH_LONG).show();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }


    public String getUbicacion() {
        return ubicacion;
    }

    @Override
    protected void onResume() {

        super.onResume();



    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.setErrorCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.setErrorCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)) {
            alertCameraDialog();
        }

    }

    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        //releaseCamera();
        try {
            camera = Camera.open(cameraId);



        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            try {
                setUpCamera(camera);
                camera.setErrorCallback(new Camera.ErrorCallback() {

                    @Override
                    public void onError(int error, Camera camera) {

                    }
                });


                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void setUpCamera(Camera c) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;

            default:
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 330;
            rotation = (360 - rotation) % 360;
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
        }
        c.setDisplayOrientation(rotation);
        Camera.Parameters params = c.getParameters();


        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        params.setRotation(rotation);
    }


    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    private void releaseCameraSource() {
        try {
            if (cameraSource != null) {
                cameraSource.stop();
                cameraSource.release();
                cameraSource = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            cameraSource = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {


            case R.id.btnCapturarSheet:
                takeImage();


                break;

            default:
                break;
        }
    }
    private void Grading(Bitmap rotatedBitmap)
    {




    }
    private void takeImage() {
        try {
            //openCamera(CameraInfo.CAMERA_FACING_BACK);
            //releaseCameraSource();
            //releaseCamera();
            //openCamera(CameraInfo.CAMERA_FACING_BACK);
            //setUpCamera(camera);
            //Thread.sleep(1000);
            cameraSource.takePicture(null, new CameraSource.PictureCallback() {

                private File imageFile;

                @Override
                public void onPictureTaken(byte[] bytes) {
                    try {
                        //  BtnCapturarSheet.startAnimation();

                        // convert byte array into bitmap
                        Bitmap loadedImage = null;
                        Bitmap rotatedBitmap = null;
                        loadedImage = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
                        Matrix rotateMatrix = new Matrix();
                        rotateMatrix.postRotate(rotation);
                        rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,loadedImage.getWidth(), loadedImage.getHeight(),rotateMatrix, false);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
                        byte[] b = baos.toByteArray();
                        String encodedImage2 = Base64.encodeToString(b, Base64.DEFAULT);

                        //   im.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                        im.setImageBitmap(rotatedBitmap);
                        if (!OpenCVLoader.initDebug()) {
                            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
                        } else {
                            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
                        }

                        // rotate Image
                        Mat source = new Mat();
                        Bitmap bmp32 = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Utils.bitmapToMat(bmp32, source);


                        if(NumOfQuestions==60)
                        {
                            // Mat source = Imgcodecs.imread(getInput("60Quest2.jpg"));
                            Mat croppedimage = source;
                            Mat resizeimage = new Mat();
                            Size sz = new Size(960,1280);
                            Imgproc.resize( croppedimage, resizeimage, sz );
                            source = resizeimage;
                            Get60.Quadrilateral quad = Get60.findDocument(source);
                            Get60.setTrans(quad, Get60.mark4Point(source, quad.points));




                            Map<Integer,Object> AnsAndImage= Get60.findBubble(quad);
                            String[]Answers=(String[]) AnsAndImage.get(0);
                            Mat ImageResult=(Mat)AnsAndImage.get(1);
                            System.out.println("finished");
                            TextView ansTxtView=findViewById(R.id.Answers);
                            StringBuilder A= new StringBuilder();
                            for(int i = 0; i < 60; i+=3){
                                A.append((i+1)+"."+Answers[i] +"-"+(i+2)+"."+Answers[i+1]+"-"+(i+3)+"."+Answers[i+2]).append("\n");
                            }
                            ansTxtView.setText(A.toString());
                            im.setImageBitmap(convertMatToBitMap(ImageResult));



                        }
                        else if (NumOfQuestions==30)
                        {
                            Mat croppedimage = source;
                            Mat resizeimage = new Mat();
                            Size sz = new Size(960,1280);
                            Imgproc.resize( croppedimage, resizeimage, sz );
                            source = resizeimage;
                            Get30.Quadrilateral quad = Get30.findDocument(source);
                            Get30.setTrans(quad, Get30.mark4Point(source, quad.points));




                            Map<Integer,Object> AnsAndImage= Get30.findBubble(quad);
                            String[]Answers=(String[]) AnsAndImage.get(0);
                            Mat ImageResult=(Mat)AnsAndImage.get(1);
                            System.out.println("finished");
                            TextView ansTxtView=findViewById(R.id.Answers);
                            StringBuilder A= new StringBuilder();
                            for(int i = 0; i < 30; i+=3){
                                A.append((i+1)+"."+Answers[i] +"-"+(i+2)+"."+Answers[i+1]+"-"+(i+3)+"."+Answers[i+2]).append("\n");
                            }
                            ansTxtView.setText(A.toString());
                            im.setImageBitmap(convertMatToBitMap(ImageResult));
                        }

                        else if(NumOfQuestions==20)
                        {
                            //  Toast.makeText(getApplicationContext(),"t_20Start",Toast.LENGTH_SHORT).show();

                            Mat croppedimage = source;
                            Mat resizeimage = new Mat();
                            Size sz = new Size(960,1280);
                            Imgproc.resize( croppedimage, resizeimage, sz );
                            source = resizeimage;
                            Get20.Quadrilateral quad = Get20.findDocument(source);
                            Get20.setTrans(quad, Get20.mark4Point(source, quad.points));




                            Map<Integer,Object> AnsAndImage= Get20.findBubble(quad);
                            String[]Answers=(String[]) AnsAndImage.get(0);
                            Mat ImageResult=(Mat)AnsAndImage.get(1);
                            System.out.println("finished");
                            TextView ansTxtView=findViewById(R.id.Answers);
                            StringBuilder A= new StringBuilder();
                            for(int i = 0; i < 20; i+=2){
                                A.append((i+1)+"."+Answers[i] +"-"+(i+2)+"."+Answers[i+1]).append("\n");
                            }
                            ansTxtView.setText(A.toString());
                            im.setImageBitmap(convertMatToBitMap(ImageResult));
                            // Toast.makeText(getApplicationContext(),"20",Toast.LENGTH_SHORT).show();
                        }
                        else if(NumOfQuestions==10)
                        {

                          //  byte[] bytes=getBytes(getApplicationContext(),selectedImageURI);
                            serVerRamy(bytes);
                        }




                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception ex) {
            txTextoCapturado.setText("Error al capturar fotografia!");
        }

    }

    private void flipCamera() {
        int id = (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_BACK);
        if (!openCamera(id)) {
            alertCameraDialog();
        }
    }

    private void alertCameraDialog() {
        AlertDialog.Builder dialog = createAlert(CustomCamaraActivity.this, "Camera info", "error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private AlertDialog.Builder createAlert(Context context, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog));
        dialog.setIcon(R.drawable.ic_launcher);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }


    /**
     * Metodo para cambiar el tamaño de la fotografia una resolucion predeterminada.
     *
     * @param image
     * @param maxWidth
     * @param maxHeight
     * @return
     */
    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    public static String stripNonDigits(
            final CharSequence input /* inspired by seh's comment */) {
        final StringBuilder sb = new StringBuilder(
                input.length() /* also inspired by seh's comment */);
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c > 47 && c < 58) {
                sb.append(c);
            }
            if (c == 'o' || c == 'O') {
                sb.append('0');
            }
            if (c == 'i' || c == 'l' || c == 'I') {
                sb.append('1');
            }
            if (c == 's' || c == 'S') {
                sb.append('5');
            }
            if (c == 'g') {
                sb.append('9');
            }
            if (c == 'z') {
                sb.append('2');
            }
        }
        return sb.toString();
    }


    public interface MyCallback {
        void onCallback(HashMap<String, String> QuestAndAns);
    }

    public void readData(MyCallback myCallback) {


        //databaseReference = FirebaseDatabase.getInstance().getReference().child("exam");
        databaseReference.child(String.format("exam")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String ExamName = ds.child("examName").getValue().toString();
                    String ID = ds.child("examID").getValue().toString();
                    // Toast.makeText(CustomCamaraActivity.this, "You Select exam: "+userType, Toast.LENGTH_SHORT).show();
                    if (ExamName.equals(SelectedExam)) {

                        ExamID[0] = ID;


                        databaseReference.child(String.format("exam_question")).child(ExamID[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //String value = dataSnapshot.getValue(String.class);

                                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                                    //  Toast.makeText(CustomCamaraActivity.this, "examID KEL: "+ ExamID[0]+" "+SelectedExam, Toast.LENGTH_SHORT).show();

                                    String QuesID = dss.child("questionID").getValue().toString(); //rkm elexam,rkm elso2al
                                    String QuesAns = dss.child("questionAnswer").getValue().toString();
                                    // Toast.makeText(CustomCamaraActivity.this, "You Select exam: "+userType, Toast.LENGTH_SHORT).show();

                                    MyQuestAndAns.put(QuesID, QuesAns);

                                }

                                myCallback.onCallback(MyQuestAndAns);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });


                        break;


                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    public void pickImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Camera_Req&&resultCode==RESULT_OK)
        {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            im.setImageBitmap(imageBitmap);










        }

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                //Get ImageURi and load with help of picasso
                //Uri selectedImageURI = data.getData();
                Uri selectedImageURI = data.getData();
                im.setImageURI(null);
                im.setImageURI(selectedImageURI);
                try {


                    // serVerRamy(bytes);
                    //  System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                    if (!OpenCVLoader.initDebug()) {
                        Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
                    } else {
                        Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
                    }

                    String pth=ImageFilePath.getPath(CustomCamaraActivity.this, data.getData());
                    Mat source = Imgcodecs.imread(pth); //Elmoshkla f get source DEEE !

                    //New Func


                    //String []Answers=new String[NumOfQuestions];
                    if(NumOfQuestions==60)
                    {
                        // Mat source = Imgcodecs.imread(getInput("60Quest2.jpg"));
                        Mat croppedimage = source;
                        Mat resizeimage = new Mat();
                        Size sz = new Size(960,1280);
                        Imgproc.resize( croppedimage, resizeimage, sz );
                        source = resizeimage;
                        Get60.Quadrilateral quad = Get60.findDocument(source);
                        Get60.setTrans(quad, Get60.mark4Point(source, quad.points));




                        Map<Integer,Object> AnsAndImage= Get60.findBubble(quad);
                        String[]Answers=(String[]) AnsAndImage.get(0);
                        Mat ImageResult=(Mat)AnsAndImage.get(1);
                        System.out.println("finished");
                        TextView ansTxtView=findViewById(R.id.Answers);
                        StringBuilder A= new StringBuilder();
                        for(int i = 0; i < 60; i+=3){
                            A.append((i+1)+"."+Answers[i] +"-"+(i+2)+"."+Answers[i+1]+"-"+(i+3)+"."+Answers[i+2]).append("\n");
                        }
                        ansTxtView.setText(A.toString());
                        im.setImageBitmap(convertMatToBitMap(ImageResult));



                    }
                    else if (NumOfQuestions==30)
                    {
                        Mat croppedimage = source;
                        Mat resizeimage = new Mat();
                        Size sz = new Size(960,1280);
                        Imgproc.resize( croppedimage, resizeimage, sz );
                        source = resizeimage;
                        Get30.Quadrilateral quad = Get30.findDocument(source);
                        Get30.setTrans(quad, Get30.mark4Point(source, quad.points));




                        Map<Integer,Object> AnsAndImage= Get30.findBubble(quad);
                        String[]Answers=(String[]) AnsAndImage.get(0);
                        Mat ImageResult=(Mat)AnsAndImage.get(1);
                        System.out.println("finished");
                        TextView ansTxtView=findViewById(R.id.Answers);
                        StringBuilder A= new StringBuilder();
                        for(int i = 0; i < 30; i+=3){
                            A.append((i+1)+"."+Answers[i] +"-"+(i+2)+"."+Answers[i+1]+"-"+(i+3)+"."+Answers[i+2]).append("\n");
                        }
                        ansTxtView.setText(A.toString());
                        im.setImageBitmap(convertMatToBitMap(ImageResult));
                    }

                    else if(NumOfQuestions==20)
                    {
                        Mat croppedimage = source;
                        Mat resizeimage = new Mat();
                        Size sz = new Size(960,1280);
                        Imgproc.resize( croppedimage, resizeimage, sz );
                        source = resizeimage;
                        Get20.Quadrilateral quad = Get20.findDocument(source);
                        Get20.setTrans(quad, Get20.mark4Point(source, quad.points));




                        Map<Integer,Object> AnsAndImage= Get20.findBubble(quad);
                        String[]Answers=(String[]) AnsAndImage.get(0);
                        Mat ImageResult=(Mat)AnsAndImage.get(1);
                        System.out.println("finished");
                        TextView ansTxtView=findViewById(R.id.Answers);
                        StringBuilder A= new StringBuilder();
                        for(int i = 0; i < 20; i+=2){
                            A.append((i+1)+"."+Answers[i] +"-"+(i+2)+"."+Answers[i+1]).append("\n");
                        }
                        ansTxtView.setText(A.toString());
                        im.setImageBitmap(convertMatToBitMap(ImageResult));
                    }
                    else if(NumOfQuestions==10)
                    {

                        byte[] bytes=getBytes(getApplicationContext(),selectedImageURI);
                        serVerRamy(bytes);
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        }
    }
    public void serVerRamy(byte[] bytes)
    {

        //openCamera(CameraInfo.CAMERA_FACING_BACK);
        //releaseCameraSource();
        //releaseCamera();
        //openCamera(CameraInfo.CAMERA_FACING_BACK);
        //setUpCamera(camera);
        //Thread.sleep(1000);


        File imageFile;


        try {
            // BtnCapturarSheet.startAnimation();

            // convert byte array into bitmap
            Bitmap loadedImage = null;
            Bitmap rotatedBitmap = null;
            loadedImage = BitmapFactory.decodeByteArray(bytes, 0,bytes.length);
            Matrix rotateMatrix = new Matrix();
            rotateMatrix.postRotate(rotation);
            rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,loadedImage.getWidth(), loadedImage.getHeight(),rotateMatrix, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
            byte[] b = baos.toByteArray();
            String encodedImage2 = Base64.encodeToString(b, Base64.DEFAULT);

            //   im.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
            im.setImageBitmap(rotatedBitmap);
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                /*String URL = "http://192.168.1.104:64839/Process";*/
                String URL = "http://uramitsys-001-site3.htempurl.com/Process";
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("ID", "10");
                JSONObject jsonBodyImages = new JSONObject();
                jsonBodyImages.put("Answares", "1,2,1,2,0,3,2,1,2,2");
                jsonBodyImages.put("Base64", encodedImage2);
                jsonBody.put("Images", jsonBodyImages);
                final String requestBody = jsonBody.toString();
                String dd55="";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("VOLLEY", response);

                        //Toast.makeText(getApplicationContext(), "Done withmail", Toast.LENGTH_LONG).show();

                                  /*  Intent intent = new Intent(getBaseContext(), NewActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);*/
                        //HEEEEEEEEEEEEEEEEEEEEERE
                        String Grade = response;
                        //  BtnCapturarSheet.setText("Grade : " + Grade);
                        Toast.makeText(getApplicationContext(),Grade+Grade,Toast.LENGTH_LONG).show();



                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", error.toString());

                        //Toast.makeText(getApplicationContext(), "Done withmail", Toast.LENGTH_LONG).show();

                                  /*  Intent intent = new Intent(getBaseContext(), NewActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);*/
                        //HEEEEEEEEEEEEEEEEEEEEERE
                        String Grade = "ERROR";
                        //BtnCapturarSheet.setText(Grade);
                        Toast.makeText(getApplicationContext(),Grade,Toast.LENGTH_LONG).show();


                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        try {
                            return requestBody == null ? null : requestBody.getBytes("utf-8");
                        } catch (UnsupportedEncodingException uee) {
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String responseString = "";
                        if (response != null) {
                            responseString = String.valueOf(response.statusCode);
                            // can get more details such as response.headers
                        }
                        return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                    }
                };
                requestQueue.add(stringRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // rotate Image





        } catch (Exception e) {
            e.printStackTrace();
        }








    }
    public static byte[] getBytes(Context context, Uri uri) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        try {
            return getBytes(iStream);
        } finally {
            // close the stream
            try {
                iStream.close();
            } catch (IOException ignored) { /* do nothing */ }
        }
    }



    /**
     * get bytes from input stream.
     *
     * @param inputStream inputStream.
     * @return byte array read from the inputStream.
     * @throws IOException
     */
    public static byte[] getBytes(InputStream inputStream) throws IOException {

        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            // close the stream
            try{ byteBuffer.close(); } catch (IOException ignored){ /* do nothing */ }
        }
        return bytesResult;
    }
    private static Bitmap convertMatToBitMap(Mat input){
        Bitmap bmp = null;
        Mat rgb = new Mat();
        Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGB);

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rgb, bmp);
        }
        catch (CvException e){
            Log.d("Exception",e.getMessage());
        }
        return bmp;
    }

}