package com.npdevs.healthcastle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Friends extends AppCompatActivity {
    private String MOB;
    private TextView name,heartStats,stepStats,calorieStats;
    private DatabaseReference databaseReference;
    private Button doctors;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        MOB = getIntent().getStringExtra("MOB");
        name = findViewById(R.id.name);
        doctors = findViewById(R.id.button9);
        heartStats = findViewById(R.id.textView14);
        stepStats = findViewById(R.id.textView15);
        calorieStats = findViewById(R.id.textView16);
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.child(MOB).getValue(Users.class);
                String nameMob = users.getName();
                name.setText(nameMob);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        heartStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Friends.this,HeartGraph.class);
                intent.putExtra("MOB_NUMBER",MOB);
                startActivity(intent);
            }
        });
        stepStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Friends.this,StepsGraph.class);
                intent.putExtra("MOB_NUMBER",MOB);
                startActivity(intent);
            }
        });
        calorieStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Friends.this,CalorieGraph.class);
                intent.putExtra("MOB_NUMBER",MOB);
                startActivity(intent);
            }
        });
        doctors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Friends.this,Doctors.class);
                intent.putExtra("MOB_NUMBER",MOB);
                startActivity(intent);
            }
        });
    }
}
