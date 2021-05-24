package com.example.gp2021.ui.instructor;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gp2021.R;
import com.example.gp2021.data.model.exam_question;
import com.example.gp2021.ui.instructor.CustomCamaraActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class GenratePDFActivity extends AppCompatActivity {

    // variables for our buttons.
    ;

    // declaring width and height for pdf page

    int pageHeight = 1120;
    int pagewidth = 792;

    // creating a bitmap variable and store img

    Bitmap bmp, scaledbmp;
    List<String> are;
    List<String> ExamIDs;
    String SlectedExamID;
    String SlectedExamName;
    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        Button generatePDFbtn = findViewById(R.id.button2);
        Spinner SpinExam = findViewById(R.id.spinner2);
        SpinExam.setBackgroundColor(Color.rgb(240, 240, 240));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("exam");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                are = new ArrayList<>();
                ExamIDs = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String ExamName = ds.child("examName").getValue().toString();
                    String ExamID = ds.child("examID").getValue().toString();
                    are.add(ExamName);
                    ExamIDs.add(ExamID);
                }
                ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, are);
                areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                SpinExam.setAdapter(areasAdapter);
                SpinExam.setSelection(0, false);
                SlectedExamID = ExamIDs.get(0);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        SpinExam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SlectedExamName = SpinExam.getSelectedItem().toString();
                SlectedExamID = ExamIDs.get(are.indexOf(SlectedExamName));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.acad);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        generatePDFbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePDF();
            }
        });
    }

    private void generatePDF() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("exam_student").child(SlectedExamID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PdfDocument pdfDocument = new PdfDocument();
                Paint paint = new Paint();
                Paint title = new Paint();
                PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
                PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
                Canvas canvas = myPage.getCanvas();
                canvas.drawBitmap(scaledbmp, 56, 40, paint);
                title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
                title.setTextSize(20);
                title.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorRed_900));
                canvas.drawText("Bubble Sheet GP Project", 209, 90, title);
                canvas.drawText("Exam Export For Students in " + SlectedExamName + " Exam", 209, 110, title);
                canvas.drawText("Date: " + new Date(), 209, 130, title);

                title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                title.setColor(ContextCompat.getColor(getApplicationContext(), R.color.blue_btn_bg_color));
                title.setTextSize(20);
                title.setTextAlign(Paint.Align.CENTER);

                canvas.drawText("Student ID", 150, 200, title);

                canvas.drawText("Grade", 350, 200, title);


                title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
                title.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                title.setTextSize(15);
                title.setTextAlign(Paint.Align.CENTER);
                int heigth = 250;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String stdID = ds.child("stdID").getValue().toString();
                    String grade = ds.child("grade").getValue().toString();
                    String stdName = /*ds.child("stdID").getValue().toString()*/"";
                    canvas.drawText(stdID, 150, heigth, title);
                    /*canvas.drawText(stdName, 350, heigth, title);*/
                    canvas.drawText(grade, 350, heigth, title);
                    heigth += 50;
                    if (heigth >= pageHeight) {
                        pdfDocument.finishPage(myPage);
                        myPage = pdfDocument.startPage(mypageInfo);
                        heigth = 70;
                        canvas = myPage.getCanvas();
                    }
                }
                pdfDocument.finishPage(myPage);
                File file = new File(Environment.getExternalStorageDirectory() + "/BubbleSheetPDF/");
                Log.println(Log.INFO, "DATA INPUT", String.valueOf(file.exists()));
                if (!file.exists()) {
                    file.mkdirs();
                }
                try {
                    android.text.format.DateFormat df = new android.text.format.DateFormat();
                    String FileName = SlectedExamName + df.format("yyyy-MM-dd HH:mm:ss", new java.util.Date()) + ".pdf";
                    File f = new File(Environment.getExternalStorageDirectory().getPath() + "/BubbleSheetPDF/" + FileName);
                    f.createNewFile();
                    FileOutputStream fos = new FileOutputStream(f);
                    pdfDocument.writeTo(fos);
                    pdfDocument.close();
                    fos.close();
                    Toast.makeText(getApplicationContext(), "PDF Save on" + f.getPath(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}
