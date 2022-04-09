package com.raylabz.firestormandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button createButton;
    Button getButton;
    Button deleteButton;
    Button getManyButton;
    Button existsButton;
    Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createButton = findViewById(R.id.buttonCreate);
        getButton = findViewById(R.id.buttonGet);
        deleteButton = findViewById(R.id.buttonDelete);
        getManyButton = findViewById(R.id.buttonGetMany);
        existsButton = findViewById(R.id.buttonExists);
        updateButton = findViewById(R.id.buttonUpdate);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Person p = new Person("aaa", "Nicos", 29);
                Firestorm.create(p, p.getId());
                Toast.makeText(MainActivity.this, p.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firestorm.get(Person.class, "aaa")
                        .addOnSuccessListener(new OnSuccessListener<Person>() {
                            @Override
                            public void onSuccess(Person person) {
                                Toast.makeText(MainActivity.this, person.toString(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Failed to get object", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firestorm.delete(Person.class, "aaa");
            }
        });

        getManyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firestorm.getMany(Person.class, "aaa", "bbb")
                        .addOnSuccessListener(new OnSuccessListener<List<Person>>() {
                            @Override
                            public void onSuccess(List<Person> people) {
                                Toast.makeText(MainActivity.this, people.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        existsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firestorm.exists(Person.class, "aaa")
                        .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                            @Override
                            public void onSuccess(Boolean aBoolean) {
                                Toast.makeText(MainActivity.this, aBoolean.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Person personUpdate = new Person("aaa", "Panayiota", 27);
                Firestorm.update(personUpdate)
                        .addOnSuccessListener(new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(String s) {
                                Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        //Initialize:
        Firestorm.init();

        //Register classes:
        Firestorm.register(Person.class);


    }
}