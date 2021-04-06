package com.example.gp2021.ui.instructor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gp2021.R;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CustomCamaraActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnClickListener {
   public String SelectedExam;
    Spinner ExamsSpinner;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> arrayList ;
    ArrayAdapter<String> arrayAdapter;
    private Context context;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private TransitionButton BtnCapturarSheet;
    private int cameraId;
    private boolean flashmode = false;
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
    private static final String TAG = "FOTOGRAFIA";
    private int Height = 620, Width = 480;
    private TextRecognizer recognizer;
  public   String[] ExamID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ExamID=new String[1];
        setContentView(R.layout.activity_custom_camara);
        context = this;
        arrayList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        ExamsSpinner = (Spinner) findViewById(R.id.ListOfExams);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("exam");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String > are = new ArrayList<>();
                are.add("Select your exam's answer");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String ExamName = ds.child("examName").getValue().toString();
                   // Toast.makeText(CustomCamaraActivity.this, "You Select exam: "+userType, Toast.LENGTH_SHORT).show();
                    are.add(ExamName);

                }

                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String> (CustomCamaraActivity.this, android.R.layout.simple_spinner_item,  are);
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
        MyQuestAndAns = new HashMap<String, String>(); //Key : question Number , //value : Answer
        ExamsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 SelectedExam = ExamsSpinner.getSelectedItem().toString();

                if (position > 0) {
                    Toast.makeText(CustomCamaraActivity.this, "You Select exam: " + SelectedExam, Toast.LENGTH_SHORT).show();

                    readData(QuestAndAns -> {
                           // MyQuestAndAns.putAll(QuestAndAns);
                           Toast.makeText(getApplicationContext(),QuestAndAns.get("1"),Toast.LENGTH_LONG).show();

                            //KML HnA


                    });
                  //  Toast.makeText(getApplicationContext(),QuestAndAns.get("1"),Toast.LENGTH_LONG).show();

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // camera surface view created
        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        BtnCapturarSheet = (TransitionButton) findViewById(R.id.btnCapturarSheet);
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
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(15.0f)
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
                    if(cameraSource != null){
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
                        try{
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
                                        for (int k = 0; k < All.length; k++)
                                        {
                                            if(All[k].equals("ID"))
                                            {
                                                StudID=All[k+1];
                                                StudID = stripNonDigits(StudID);
                                                txTextoCapturado.setText("ID :" +StudID);

                                                break;
                                            }
                                        }

                                        //  mTextView.setText(stringBuilder.toString());

                                    } else
                                    {
                                        txTextoCapturado.setText("Waiting");
                                    }


                                   // txTextoCapturado.setText(read);
                                }
                            });
                        }catch (Exception ex){
                            Log.e("error","Error al actualizar texto OCR");
                        }

                        //It continues doing other things here
                    }
                }
            });
        }
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
        if(camera != null){
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

    private void takeImage() {
        try{
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
                        BtnCapturarSheet.startAnimation();

                        // convert byte array into bitmap
                        Bitmap loadedImage = null;
                        Bitmap rotatedBitmap = null;
                        loadedImage = BitmapFactory.decodeByteArray(bytes, 0,
                                bytes.length);

                        // rotate Image
                        Matrix rotateMatrix = new Matrix();
                        rotateMatrix.postRotate(rotation);
                        rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
                                loadedImage.getWidth(), loadedImage.getHeight(),
                                rotateMatrix, false);
                        ImageView im=findViewById(R.id.dfs);
                     //   im.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
                        im.setImageBitmap(rotatedBitmap);

                        BtnCapturarSheet.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, new TransitionButton.OnAnimationStopEndListener() {
                            @Override
                            public void onAnimationStopEnd() {
                                //Toast.makeText(getApplicationContext(), "Done withmail", Toast.LENGTH_LONG).show();

                                  /*  Intent intent = new Intent(getBaseContext(), NewActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivity(intent);*/
                                //HEEEEEEEEEEEEEEEEEEEEERE
                                String Grade="S";
                        BtnCapturarSheet.setText("Grade : "+Grade);

                            }
                        });


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        }catch (Exception ex){
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
        AlertDialog.Builder dialog = createAlert(CustomCamaraActivity.this,"Camera info", "error to open camera");
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
            final CharSequence input /* inspired by seh's comment */){
        final StringBuilder sb = new StringBuilder(
                input.length() /* also inspired by seh's comment */);
        for(int i = 0; i < input.length(); i++){
            final char c = input.charAt(i);
            if(c > 47 && c < 58){
                sb.append(c);
            }
            if(c=='o'||c=='O')
            {
                sb.append('0');
            }
            if(c=='i'||c=='l'||c=='I')
            {
                sb.append('1');
            }
            if(c=='s'||c=='S')
            {
                sb.append('5');
            }
            if(c=='g')
            {
                sb.append('9');
            }
            if(c=='z')
            {
                sb.append('2');
            }
        }
        return sb.toString();
    }


    public interface MyCallback {
        void onCallback( HashMap<String, String> QuestAndAns);
    }

    public void readData(MyCallback myCallback) {




        //databaseReference = FirebaseDatabase.getInstance().getReference().child("exam");
        databaseReference.child(String.format("exam")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String ExamName = ds.child("examName").getValue().toString();
                    String ID=ds.child("examID").getValue().toString();
                    // Toast.makeText(CustomCamaraActivity.this, "You Select exam: "+userType, Toast.LENGTH_SHORT).show();
                    if(ExamName.equals(SelectedExam))
                    {

                        ExamID[0] =ID;







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
                            public void onCancelled(DatabaseError databaseError) {}
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



}