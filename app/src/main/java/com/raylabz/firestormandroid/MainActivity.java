package com.raylabz.firestormandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button createButton;
    Button getButton;
    Button deleteButton;
    Button getManyButton;
    Button existsButton;
    Button updateButton;
    Button createManyButton;
    Button listButton;
    Button listAllButton;
    Button filterButton;

    TextView resultTextview;

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
        createManyButton = findViewById(R.id.buttonCreateMany);
        listButton = findViewById(R.id.buttonList);
        listAllButton = findViewById(R.id.buttonListAll);
        filterButton = findViewById(R.id.buttonFilter);

        resultTextview = findViewById(R.id.resultTextview);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Person p = new Person("aaa", "Nicos", 29);
                Firestorm.create(p, p.getId());
                resultTextview.setText(p.getId());
            }
        });

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long time = System.currentTimeMillis();
                Firestorm.get(Person.class, "aaa")
                        .addOnSuccessListener(new OnSuccessListener<Person>() {
                            @Override
                            public void onSuccess(Person person) {
                                resultTextview.setText(person.toString());
                                System.out.println("Time: " + (System.currentTimeMillis()  - time) + "ms");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                resultTextview.setText("Failed to get object");
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
                                resultTextview.setText(people.toString());
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
                                resultTextview.setText(aBoolean.toString());
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
                                resultTextview.setText("updated");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                resultTextview.setText("Failed to update");
                            }
                        });
            }
        });

        createManyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 5; i++) {
                    Person p = new Person(UUID.randomUUID().toString().substring(0, 5), "BK-Randy-" + i, i + 10);
                    Firestorm.create(p);
                }
            }
        });

        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firestorm.list(Person.class, 3)
                        .addOnSuccessListener(new OnSuccessListener<List<Person>>() {
                            @Override
                            public void onSuccess(List<Person> people) {
                                resultTextview.setText(people.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                resultTextview.setText("Failed to list");
                            }
                        });
            }
        });

        listAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firestorm.listAll(Person.class)
                        .addOnSuccessListener(new OnSuccessListener<List<Person>>() {
                            @Override
                            public void onSuccess(List<Person> people) {
                                resultTextview.setText(people.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                resultTextview.setText("Failed to list all");
                            }
                        });
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firestorm.filter(Person.class)
                        .whereGreaterThan("age", 12)
                        .fetch()
                        .addOnSuccessListener(new OnSuccessListener<QueryResult<Person>>() {
                            @Override
                            public void onSuccess(QueryResult<Person> personQueryResult) {
                                List<Person> items = personQueryResult.getItems();
                                resultTextview.setText(items.toString() + "\nlastID:" + personQueryResult.getLastDocumentID());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                resultTextview.setText("Error filtering");
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