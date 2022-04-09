package com.raylabz.firestormandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button createButton;
    Button getButton;
    Button deleteButton;
    Button getManyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createButton = findViewById(R.id.buttonCreate);
        getButton = findViewById(R.id.buttonGet);
        deleteButton = findViewById(R.id.buttonDelete);
        getManyButton = findViewById(R.id.buttonGetMany);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Person p = new Person("aaa", "Nicos", 29);
                Firestorm.create(p, p.getId());
            }
        });

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firestorm.get(Person.class, "aaa")
                        .addOnSuccessListener(new OnSuccessListener<Person>() {
                            @Override
                            public void onSuccess(Person person) {
                                System.out.println(person);
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
                                System.out.println(people);
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