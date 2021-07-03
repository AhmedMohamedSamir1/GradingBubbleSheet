package com.example.gp2021.ui.instructor;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvException;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gp2021.R;
import com.example.gp2021.data.model.exam_question;
import com.example.gp2021.data.model.exam_question_student;
import com.example.gp2021.data.model.exam_student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.markushi.ui.CircleButton;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class CustomCamaraActivity extends AppCompatActivity implements SurfaceHolder.Callback,
        View.OnClickListener {

    /*My Vars*/
    int StudentID = -1;
    int EammNumber = -1;
    List<exam_question> ExamAnswares = new ArrayList<>(); // model answer
    String[] UserAnswares = null;
    int Grade = 0;
    /*--------*/
    int NumOfQuestions;
    public String SelectedExam;
    Spinner ExamsSpinner;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    private Context context;
    TextView ansTxtView;
    private SurfaceView surfaceView;
    private CircleButton btnLoadFromGallary;
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private CircleButton BtnCapturarSheet;
    byte[] MyBytes;
    String[] Answers;
    private CircleButton BtnRepeat;
    private CircleButton BtnConfirm;
    private int cameraId;
    //  private boolean flashmode = false;
    private int rotation;
    private ActionBar actionBar;
    private CameraSource cameraSource;
    private StringBuilder builder;
    private TextView txTextoCapturado;
    Mat source;
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
    public final int SELECT_PICTURE = 142;
    public ImageView im;
    private final int Camera_Req = 1888;
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

        btnLoadFromGallary = findViewById(R.id.loadImageFromGallary);
        ExamsSpinner = (Spinner) findViewById(R.id.ListOfExams);
        ExamsSpinner.setBackgroundColor(Color.rgb(226, 73, 138));


        im = findViewById(R.id.dfs);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("exam");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> are = new ArrayList<>();
                are.add("Select your exam's answer -> ");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String ExamName = ds.child("examName").getValue().toString();
                    are.add(ExamName);
                }
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(CustomCamaraActivity.this, android.R.layout.simple_spinner_item, are);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ExamsSpinner.setAdapter(areasAdapter);
                ExamsSpinner.setSelection(0, false);
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
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("exam").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds2 : dataSnapshot.getChildren()) {
                                String ExamName = ds2.child("examName").getValue().toString();
                                if (ExamName.equals(SelectedExam)) {
                                    String ID = ds2.child("examID").getValue().toString();
                                    EammNumber = Integer.valueOf(ID);
                                    ExamAnswares.clear();
                                    databaseReference.child("exam_question").child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                            for (DataSnapshot ds : dataSnapshot2.getChildren()) {
                                                String catID = ds.child("catID").getValue().toString();
                                                String examID = ds.child("examID").getValue().toString();
                                                String questionAnswer = ds.child("questionAnswer").getValue().toString();
                                                String questionGrade = ds.child("questionGrade").getValue().toString();
                                                String questionID = ds.child("questionID").getValue().toString();
                                                exam_question question = new exam_question(catID, examID, questionAnswer, questionGrade, questionID);
                                                ExamAnswares.add(question);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                    ExamID[0] = ID;
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //END


/*

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
        cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        BtnCapturarSheet = (CircleButton) findViewById(R.id.btnCapturarSheet);
        BtnConfirm = (CircleButton) findViewById(R.id.confirmScan);
        BtnRepeat = (CircleButton) findViewById(R.id.tryAgain);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        txTextoCapturado = (TextView) findViewById(R.id.tvTextoCapturado);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        BtnCapturarSheet.setOnClickListener(this);
        BtnConfirm.setOnClickListener(this);
        BtnRepeat.setOnClickListener(this);
        BtnRepeat.setVisibility(View.INVISIBLE);
        BtnConfirm.setVisibility(View.INVISIBLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
                BtnCapturarSheet.setVisibility(View.INVISIBLE);
                BtnRepeat.setVisibility(View.VISIBLE);
                BtnConfirm.setVisibility(View.VISIBLE);
                ansTxtView = findViewById(R.id.Answers);
                ansTxtView.setText("");
                takeImage();
                break;
            case R.id.tryAgain:
                BtnRepeat.setVisibility(View.INVISIBLE);
                BtnConfirm.setVisibility(View.INVISIBLE);
                BtnCapturarSheet.setVisibility(View.VISIBLE);
                ansTxtView = findViewById(R.id.Answers);
                ansTxtView.setText("");
                im.setImageDrawable(null);


                break;
            case R.id.confirmScan:
                BtnRepeat.setVisibility(View.INVISIBLE);
                BtnConfirm.setVisibility(View.INVISIBLE);
                BtnCapturarSheet.setVisibility(View.VISIBLE);
                Confirmation();
                ansTxtView = findViewById(R.id.Answers);
                ansTxtView.setText("");
                im.setImageDrawable(null);

                break;

            default:
                break;
        }
    }

    /*GetStudenID*/
    private void Confirmation() {
        EditText inputID = new EditText(this);
        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Enter student ID from paper").setCustomImage(R.drawable.app_logo_2)
                .setCustomView(inputID)
                .setConfirmText("Ok").setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Grade = 0;
                        StudentID = Integer.valueOf(inputID.getText().toString());
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        int count = 0;
                        for (exam_question que : ExamAnswares) {
                            if (UserAnswares[count].equals(que.getQuestionAnswer()))
                                Grade += Integer.valueOf(que.getQuestionGrade());
                            rootRef.child("exam_question_student").child(String.valueOf(EammNumber))

                                    .child(String.valueOf(count + 1))
                                    .setValue(
                                            new exam_question_student(
                                                    String.valueOf(EammNumber),
                                                    que.getQuestionID(),
                                                    UserAnswares[count],
                                                    String.valueOf(StudentID)
                                            )
                                    );
                            count++;
                        }
                        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                exam_student examData = new exam_student("" + EammNumber, String.valueOf(StudentID), String.valueOf(Grade));
                                rootRef.child("exam_student").child("" + EammNumber).child("" + StudentID).setValue(examData);
                                rootRef.child("exam_student").child("" + EammNumber).child("" + StudentID).setValue(examData).addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "your Grade " + Grade, Toast.LENGTH_LONG).show();
                                        } else
                                            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Log.println(Log.INFO, "StudentID", String.valueOf(StudentID));
                        sDialog.cancel();
                    }
                })
                .show();
    }


    private void takeImage() {
        try {
            cameraSource.takePicture(null, new CameraSource.PictureCallback() {
                private File imageFile;

                @Override
                public void onPictureTaken(byte[] bytes) {
                    try {
                        //  BtnCapturarSheet.startAnimation();
                        // convert byte array into bitmap
                        Bitmap loadedImage = null;
                        Bitmap rotatedBitmap = null;
                        loadedImage = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        Matrix rotateMatrix = new Matrix();
                        rotateMatrix.postRotate(rotation);
                        rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0, loadedImage.getWidth(), loadedImage.getHeight(), rotateMatrix, false);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object

                        im.setImageBitmap(rotatedBitmap);
                        if (!OpenCVLoader.initDebug()) {
                            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
                        } else {
                            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
                        }

                        // rotate Image
                        source = new Mat();
                        Bitmap bmp32 = rotatedBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Utils.bitmapToMat(bmp32, source);
                        MyBytes = bytes;
                        if (NumOfQuestions == 60) {
                            Mat croppedimage = source;
                            Mat resizeimage = new Mat();
                            Size sz = new Size(960, 1280);
                            Imgproc.resize(croppedimage, resizeimage, sz);
                            source = resizeimage;
                            Get60.Quadrilateral quad = Get60.findDocument(source);
                            Get60.setTrans(quad, Get60.mark4Point(source, quad.points));
                            Map<Integer, Object> AnsAndImage = Get60.findBubble(quad);
                            Answers = (String[]) AnsAndImage.get(0);
                            Mat ImageResult = (Mat) AnsAndImage.get(1);
                            System.out.println("finished");
                            ansTxtView = findViewById(R.id.Answers);
                            StringBuilder A = new StringBuilder();
                            UserAnswares = new String[60];
                            for (int i = 0; i < 60; i += 3) {
                                UserAnswares[i] = Answers[i];
                                UserAnswares[i + 1] = Answers[i + 1];
                                UserAnswares[i + 2] = Answers[i + 2];
                                A.append((i + 1) + "." + Answers[i] + "-" + (i + 2) + "." + Answers[i + 1] + "-" + (i + 3) + "." + Answers[i + 2]).append("\n");
                            }
                            ansTxtView.setText(A.toString());
                            im.setImageBitmap(convertMatToBitMap(ImageResult));
                        } else if (NumOfQuestions == 30) {
                            Mat croppedimage = source;
                            Mat resizeimage = new Mat();
                            Size sz = new Size(960, 1280);
                            Imgproc.resize(croppedimage, resizeimage, sz);
                            source = resizeimage;
                            Get30.Quadrilateral quad = Get30.findDocument(source);
                            Get30.setTrans(quad, Get30.mark4Point(source, quad.points));
                            Map<Integer, Object> AnsAndImage = Get30.findBubble(quad);
                            Answers = (String[]) AnsAndImage.get(0);
                            Mat ImageResult = (Mat) AnsAndImage.get(1);
                            System.out.println("finished");
                            ansTxtView = findViewById(R.id.Answers);
                            StringBuilder A = new StringBuilder();
                            UserAnswares = new String[30];
                            for (int i = 0; i < 30; i += 3) {
                                UserAnswares[i] = Answers[i];
                                UserAnswares[i + 1] = Answers[i + 1];
                                UserAnswares[i + 2] = Answers[i + 2];
                                A.append((i + 1) + "." + Answers[i] + "-" + (i + 2) + "." + Answers[i + 1] + "-" + (i + 3) + "." + Answers[i + 2]).append("\n");
                            }
                            ansTxtView.setText(A.toString());
                            im.setImageBitmap(convertMatToBitMap(ImageResult));
                        } else if (NumOfQuestions == 20) {
                            Mat croppedimage = source;
                            Mat resizeimage = new Mat();
                            Size sz = new Size(960, 1280);
                            Imgproc.resize(croppedimage, resizeimage, sz);
                            source = resizeimage;
                            Get20.Quadrilateral quad = Get20.findDocument(source);
                            Get20.setTrans(quad, Get20.mark4Point(source, quad.points));
                            Map<Integer, Object> AnsAndImage = Get20.findBubble(quad);
                            Answers = (String[]) AnsAndImage.get(0);
                            Mat ImageResult = (Mat) AnsAndImage.get(1);
                            System.out.println("finished");
                            ansTxtView = findViewById(R.id.Answers);
                            StringBuilder A = new StringBuilder();
                            UserAnswares = new String[20];
                            for (int i = 0; i < 20; i += 2) {
                                UserAnswares[i] = Answers[i];
                                UserAnswares[i + 1] = Answers[i + 1];
                                A.append((i + 1) + "." + Answers[i] + "-" + (i + 2) + "." + Answers[i + 1]).append("\n");
                            }
                            ansTxtView.setText(A.toString());
                            im.setImageBitmap(convertMatToBitMap(ImageResult));
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

        if (requestCode == Camera_Req && resultCode == RESULT_OK) {
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

                    String pth = ImageFilePath.getPath(CustomCamaraActivity.this, data.getData());
                    source = Imgcodecs.imread(pth); //Elmoshkla f get source DEEE !

                    //New Func


                    //String []Answers=new String[NumOfQuestions];
                    if (NumOfQuestions == 60) {
                        // Mat source = Imgcodecs.imread(getInput("60Quest2.jpg"));
                        Mat croppedimage = source;
                        Mat resizeimage = new Mat();
                        Size sz = new Size(960, 1280);
                        Imgproc.resize(croppedimage, resizeimage, sz);
                        source = resizeimage;
                        Get60.Quadrilateral quad = Get60.findDocument(source);
                        Get60.setTrans(quad, Get60.mark4Point(source, quad.points));


                        Map<Integer, Object> AnsAndImage = Get60.findBubble(quad);
                        Answers = (String[]) AnsAndImage.get(0);
                        Mat ImageResult = (Mat) AnsAndImage.get(1);
                        System.out.println("finished");
                        TextView ansTxtView = findViewById(R.id.Answers);
                        StringBuilder A = new StringBuilder();
                        for (int i = 0; i < 60; i += 3) {
                            A.append((i + 1) + "." + Answers[i] + "-" + (i + 2) + "." + Answers[i + 1] + "-" + (i + 3) + "." + Answers[i + 2]).append("\n");
                        }
                        ansTxtView.setText(A.toString());
                        im.setImageBitmap(convertMatToBitMap(ImageResult));


                    } else if (NumOfQuestions == 30) {
                        Mat croppedimage = source;
                        Mat resizeimage = new Mat();
                        Size sz = new Size(960, 1280);
                        Imgproc.resize(croppedimage, resizeimage, sz);
                        source = resizeimage;
                        Get30.Quadrilateral quad = Get30.findDocument(source);
                        Get30.setTrans(quad, Get30.mark4Point(source, quad.points));


                        Map<Integer, Object> AnsAndImage = Get30.findBubble(quad);
                        Answers = (String[]) AnsAndImage.get(0);
                        Mat ImageResult = (Mat) AnsAndImage.get(1);
                        System.out.println("finished");
                        TextView ansTxtView = findViewById(R.id.Answers);
                        StringBuilder A = new StringBuilder();
                        for (int i = 0; i < 30; i += 3) {
                            A.append((i + 1) + "." + Answers[i] + "-" + (i + 2) + "." + Answers[i + 1] + "-" + (i + 3) + "." + Answers[i + 2]).append("\n");
                        }
                        ansTxtView.setText(A.toString());
                        im.setImageBitmap(convertMatToBitMap(ImageResult));
                    } else if (NumOfQuestions == 20) {
                        Mat croppedimage = source;
                        Mat resizeimage = new Mat();
                        Size sz = new Size(960, 1280);
                        Imgproc.resize(croppedimage, resizeimage, sz);
                        source = resizeimage;
                        Get20.Quadrilateral quad = Get20.findDocument(source);
                        Get20.setTrans(quad, Get20.mark4Point(source, quad.points));


                        Map<Integer, Object> AnsAndImage = Get20.findBubble(quad);
                        Answers = (String[]) AnsAndImage.get(0);
                        Mat ImageResult = (Mat) AnsAndImage.get(1);
                        System.out.println("finished");
                        TextView ansTxtView = findViewById(R.id.Answers);
                        StringBuilder A = new StringBuilder();
                        for (int i = 0; i < 20; i += 2) {
                            A.append((i + 1) + "." + Answers[i] + "-" + (i + 2) + "." + Answers[i + 1]).append("\n");
                        }
                        ansTxtView.setText(A.toString());
                        im.setImageBitmap(convertMatToBitMap(ImageResult));
                    }
                    Confirmation(); // momken mt4t8lsh m3 el server
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

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
            try {
                byteBuffer.close();
            } catch (IOException ignored) { /* do nothing */ }
        }
        return bytesResult;
    }

    private static Bitmap convertMatToBitMap(Mat input) {
        Bitmap bmp = null;
        Mat rgb = new Mat();
        Imgproc.cvtColor(input, rgb, Imgproc.COLOR_BGR2RGB);

        try {
            bmp = Bitmap.createBitmap(rgb.cols(), rgb.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(rgb, bmp);
        } catch (CvException e) {
            Log.d("Exception", e.getMessage());
        }
        return bmp;
    }

    void ExportPDF(String ExamName, String ExamID) {
        Intent instructorActivity = new Intent(this, GenratePDFActivity.class);
        instructorActivity.putExtra("ExamID", ExamID);
        instructorActivity.putExtra("ExamName", ExamName);
        startActivity(instructorActivity);
    }
}