package com.example.gp2021.ui.academic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.gp2021.R;
import com.example.gp2021.data.model.student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class addStudent extends AppCompatActivity {

    EditText  stdID, stdName, stdEmail, travelTime, studyTime, failures, activities, freeTime, goOut, health, gender, absence;
    Button addStd;
    ProgressDialog LoadingBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);
        LoadingBar = new ProgressDialog(this);


        travelTime = (EditText)findViewById(R.id.editStdTravelTime);
        travelTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                travelTime.setText("");
                loadTravelTimeMenu();
            }
        });
        travelTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                { travelTime.setText(""); loadTravelTimeMenu();  }
            }
        });

        studyTime = (EditText)findViewById(R.id.editStdStudyTime);
        studyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studyTime.setText("");
                loadStudyTimeMenu();
            }
        });
        studyTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {  studyTime.setText(""); loadStudyTimeMenu();}
            }
        });

        activities = (EditText)findViewById(R.id.editStdActivities);
        activities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activities.setText("");
                loadActivitiesMenu();
            }
        });
        activities.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {  activities.setText(""); loadActivitiesMenu();}
            }
        });

        freeTime = (EditText)findViewById(R.id.editStdFreeTime);
        freeTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                freeTime.setText("");
                loadfreeTimeAfterSchoolMenu();
            }
        });
        freeTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                { freeTime.setText(""); loadfreeTimeAfterSchoolMenu();}
            }
        });

        goOut = (EditText)findViewById(R.id.editStdGoingOut);
        goOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goOut.setText("");
                loadGoingOutWithFriendsMenu();
            }
        });
        goOut.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                { goOut.setText("");  loadGoingOutWithFriendsMenu();}
            }
        });

        health = (EditText)findViewById(R.id.editStdHealthStatus);
        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                health.setText("");
                loadHealthMenu();
            }
        });
        health.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {health.setText(""); loadHealthMenu();}
            }
        });

        gender = (EditText)findViewById(R.id.editStdGender);
        gender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gender.setText("");
               loadGenderMenu();
            }
        });
        gender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                { gender.setText(""); loadGenderMenu();}
            }
        });

        addStd = (Button)findViewById(R.id.btnAddStd);
        addStd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readEditTexts();
                if(stdID.getText().toString().equals("")||stdName.getText().toString().equals("")||stdEmail.getText().toString().equals("")||
                        gender.getText().toString().equals("")||travelTime.getText().toString().equals("")||studyTime.getText().toString().equals("")
                        ||failures.getText().toString().equals("")||activities.getText().toString().equals("")||freeTime.getText().toString().equals("")
                        ||goOut.getText().toString().equals("")||health.getText().toString().equals("")||absence.getText().toString().equals(""))
                {
                    Toast.makeText((addStudent.this), "Please Fill Empty Fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    LoadingBar.setTitle("Add Student");
                    LoadingBar.setMessage("please wait until student is added");
                    LoadingBar.setCanceledOnTouchOutside(false);


                    readEditTexts();
                    String std_id = stdID.getText().toString();
                    String std_name = stdName.getText().toString().toLowerCase();
                    String std_email= stdEmail.getText().toString();
                    String std_gender = gender.getText().toString();
                    String std_TravelTime = travel_time(travelTime.getText().toString());
                    String std_studyTime = study_time(studyTime.getText().toString());
                    String std_failures = failures.getText().toString();
                    String std_activities = activities.getText().toString();
                    String std_free_time = free_time_OR_go_out(freeTime.getText().toString());
                    String std_go_out = free_time_OR_go_out(goOut.getText().toString());
                    String std_health = health_status(health.getText().toString());
                    String std_absence = absence.getText().toString();

                    if(isEmailValid(std_email))
                    {
                        LoadingBar.show();
                        student std = new student(std_id,std_name,std_email,std_gender,std_TravelTime,std_studyTime,std_failures,std_activities,
                                std_free_time,std_go_out,std_health,std_absence);

                        add_student(std);
                    }
                    else
                    {
                        Toast.makeText((addStudent.this), "email is not valid", Toast.LENGTH_SHORT).show();
                    }



                }
            }
        });

    }


    public void loadTravelTimeMenu()
    {
        PopupMenu popup = new PopupMenu(addStudent.this, travelTime);
        popup.getMenu().add("less than 30 min");
        popup.getMenu().add("30 to 60 min");
        popup.getMenu().add("1 to 2 hours");
        popup.getMenu().add("more than 2 hours");
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                travelTime.setText(item.getTitle().toString());
                return true;
            }
        });
        popup.show();//showing popup menu
    }

    //---------------------------------------------------

    public void loadStudyTimeMenu()
    {
        PopupMenu popup = new PopupMenu(addStudent.this, studyTime);
        popup.getMenu().add("less than 2 hours");
        popup.getMenu().add("2 to 4 hours");
        popup.getMenu().add("4 to 6 hours");
        popup.getMenu().add("more than 6 hours");
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                studyTime.setText(item.getTitle().toString());
                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public void loadActivitiesMenu()
    {
        PopupMenu popup = new PopupMenu(addStudent.this, activities);
        popup.getMenu().add("yes");
        popup.getMenu().add("no");
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                activities.setText(item.getTitle().toString());
                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public void loadfreeTimeAfterSchoolMenu()
    {
        PopupMenu popup = new PopupMenu(addStudent.this, freeTime);
        popup.getMenu().add("very low");
        popup.getMenu().add("low");
        popup.getMenu().add("average");
        popup.getMenu().add("high");
        popup.getMenu().add("very high");
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                freeTime.setText(item.getTitle().toString());
                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public void loadGoingOutWithFriendsMenu()
    {
        PopupMenu popup = new PopupMenu(addStudent.this, goOut);
        popup.getMenu().add("very low");
        popup.getMenu().add("low");
        popup.getMenu().add("average");
        popup.getMenu().add("high");
        popup.getMenu().add("very high");
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                goOut.setText(item.getTitle().toString());
                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public void loadHealthMenu()
    {
        PopupMenu popup = new PopupMenu(addStudent.this, health);
        popup.getMenu().add("very bad");
        popup.getMenu().add("bad");
        popup.getMenu().add("moderate");
        popup.getMenu().add("good");
        popup.getMenu().add("very good");
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                health.setText(item.getTitle().toString());
                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public void loadGenderMenu()
    {
        PopupMenu popup = new PopupMenu(addStudent.this, gender);
        popup.getMenu().add("male");
        popup.getMenu().add("female");
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                gender.setText(item.getTitle().toString());
                return true;
            }
        });
        popup.show();//showing popup menu
    }

    public void readEditTexts()
    {
        stdID = (EditText)findViewById(R.id.editStdID);
        stdName = (EditText)findViewById(R.id.editStdName);
        stdEmail = (EditText)findViewById(R.id.editStdEmail);
        travelTime = (EditText)findViewById(R.id.editStdTravelTime);
        studyTime = (EditText)findViewById(R.id.editStdStudyTime);
        failures = (EditText)findViewById(R.id.editStdPastFailures);
        activities = (EditText)findViewById(R.id.editStdActivities);
        freeTime = (EditText)findViewById(R.id.editStdFreeTime);
        goOut = (EditText)findViewById(R.id.editStdGoingOut);
        health = (EditText)findViewById(R.id.editStdHealthStatus);
        gender = (EditText)findViewById(R.id.editStdGender);
        absence = (EditText)findViewById(R.id.editStdAbsecnce);

    }

    public String travel_time(String str)
    {
       if(str.equals("less than 30 min") )
           return ("1");
       else if(str.equals("30 to 60 min"))
            return "2";
        else if(str.equals("1 to 2 hours"))
            return "3";
        else
            return "4";
    }

    public String study_time(String str)
    {
        if(str.equals("less than 2 hours") )
            return ("1");
        else if(str.equals("2 to 4 hours"))
            return "2";
        else if(str.equals("4 to 6 hours"))
            return "3";
        else
            return "4";
    }

    public String free_time_OR_go_out(String str)
    {

        if(str.equals("very low") )
            return ("1");
        else if(str.equals("low"))
            return "2";
        else if(str.equals("average"))
            return "3";
        else if(str.equals("high"))
            return "4";
        else
            return "5";
    }


    public String health_status(String str)
    {
        if(str.equals("very bad") )
            return ("1");
        else if(str.equals("bad"))
            return "2";
        else if(str.equals("moderate"))
            return "3";
        else if(str.equals("good"))
            return "4";
        else
            return "5";
    }

    public void add_student(student std)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("student").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child(std.getStdID()).exists())
                {
                    boolean email_name = true;
                    for(DataSnapshot ds : snapshot.getChildren())
                    {
                        student s = ds.getValue(student.class);
                        if(s.getStdName().equals(std.getStdName()) || s.getEmail().equals(std.getEmail()))
                        {
                            email_name = false;
                            break;
                        }
                    }
                    if(email_name)
                    {
                        rootRef.child("student").child(std.getStdID()).setValue(std).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    LoadingBar.dismiss();
                                    Toast.makeText((addStudent.this), "student is added successfully", Toast.LENGTH_SHORT).show();
                                    clear();
                                }
                            }
                        });
                    }
                    else
                    {LoadingBar.dismiss(); Toast.makeText(getApplicationContext(),"name or email are already used",Toast.LENGTH_LONG).show(); }

                }
                else
                { LoadingBar.dismiss(); Toast.makeText(getApplicationContext(),"Id already used",Toast.LENGTH_LONG).show();   }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    public void clear()
    {
        stdID.setText("");
        stdName.setText("");
        stdEmail.setText("");
        travelTime.setText("");
        studyTime.setText("");
        failures.setText("");
        activities.setText("");
        freeTime.setText("");
        goOut.setText("");
        health.setText("");
        gender.setText("");
        absence.setText("");
    }
    public boolean isEmailValid(String email) {
        if(email.contains(".com"))
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        else return false;
    }
}