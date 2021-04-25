package com.example.gp2021.ui.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapRegionDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.gp2021.R;
import com.example.gp2021.ui.academic.AcademicHome;
import com.example.gp2021.ui.instructor.InstructorHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.app.Activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class SignupActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    private  static final int Gallery_Request_Code=123;
    Uri ProfImgURI;
    String EmailAddress;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        ProfImgURI=Uri.parse("https://upload.wikimedia.org/wikipedia/commons/thumb/7/7e/Circle-icons-profile.svg/1024px-Circle-icons-profile.svg.png");
        findViewById(R.id.toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.pick_Profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),Gallery_Request_Code);


            }
        });
        findViewById(R.id.sign_up_buttonGoogle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView myButton=(ImageView) LoginActivity.myActivity.findViewById(R.id.sign_in_buttonGoogle);
              //  myButton.setOnClickListener(this);
                finish();
                myButton.performClick();
            }
        });
        findViewById(R.id.sign_up_buttonface).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView myButton=(ImageView) LoginActivity.myActivity.findViewById(R.id.signinFacebook);
              //  myButton.setOnClickListener(this);
                finish();
                myButton.performClick();
            }
        });
        findViewById(R.id.button_Finish_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EmailAddress = ((TextInputEditText) findViewById(R.id.et_email)).getText().toString();
                EmailAddress=EmailAddress.replaceAll("\\s", "");
                String UserName = ((TextInputEditText) findViewById(R.id.et_username)).getText().toString();
                String PhoneNumber = ((TextInputEditText) findViewById(R.id.et_phone)).getText().toString();
                String Password = ((EditText) findViewById(R.id.et_password)).getText().toString();
                String ConfirmPassword = ((EditText) findViewById(R.id.et_confirm_password)).getText().toString();
                if (!EmailAddress.equals("") && !UserName.equals("") && !PhoneNumber.equals("") && !Password.equals("") && !ConfirmPassword.equals("")) {
                    if (Password.equals(ConfirmPassword)) {

                        mAuth.createUserWithEmailAndPassword(EmailAddress, Password).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Email= " + EmailAddress, Toast.LENGTH_LONG).show();

                               /* FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(UserName)
                                        .setPhotoUri(ProfImgURI)
                                        .build();*/
                                    UploadImageToFirebase();

                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(UserName)
                                            .setPhotoUri(ProfImgURI)

                                            .build();
                                    user.updateProfile(profileUpdates)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "UserNameAdded", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                    if (EmailAddress.equals("ibrahimkelany98@gmail.com")) {


                                        Intent Acade = new Intent(getApplicationContext(), AcademicHome.class);


                                        startActivity(Acade);

                                    } else {
                                        // Toast.makeText(getApplicationContext(), "Instructor", Toast.LENGTH_LONG).show();
                                        Intent instructorActivity = new Intent(getApplicationContext(), InstructorHome.class);


                                        startActivity(instructorActivity);

                                    }

                                } else //failed
                                {
                                    Toast.makeText(getApplicationContext(), "Failed Try Again", Toast.LENGTH_LONG).show();
                                    Toast.makeText(getApplicationContext(), "Make sure password > 5 chars", Toast.LENGTH_LONG).show();

                                }
                            }
                        });


                    } else {
                        Toast.makeText(getApplicationContext(), "Passwords not same ", Toast.LENGTH_LONG).show();


                    }


                }
            }
        });

    }

    private void UploadImageToFirebase() {
        StorageReference fileref=storageReference.child(FirebaseAuth.getInstance().getUid()+".jpg");
        fileref.putFile(ProfImgURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"ImageUploaded Failed",Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Request_Code && resultCode == RESULT_OK && data != null) {
            ProfImgURI = data.getData();
            ImageView imageView=findViewById(R.id.imageProfile);
            //imageView.setImageURI(imageData);
            Glide.with(getApplicationContext()).load(ProfImgURI).circleCrop().into(imageView);

        }
    }

}