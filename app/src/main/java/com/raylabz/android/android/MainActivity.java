package com.raylabz.android.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.raylabz.android.ClassListener;
import com.raylabz.android.FilterableListener;
import com.raylabz.android.Firestorm;
import com.raylabz.android.FirestormFilterable;
import com.raylabz.android.ObjectChange;
import com.raylabz.android.Paginator;
import com.raylabz.android.QueryResult;
import com.raylabz.android.R;

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
    Button paginatorButton;

    TextView resultTextview;

    private String lastDocumentID = null;

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
        paginatorButton = findViewById(R.id.buttonPaginator);

        resultTextview = findViewById(R.id.resultTextview);

        final Person p = new Person("aaa", "Nicos", 29);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                System.out.println("Time: " + (System.currentTimeMillis() - time) + "ms");
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

        paginatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Pagination:
                Paginator<Person> next = Paginator.next(Person.class, lastDocumentID, 5);
                next.fetch().addOnSuccessListener(new OnSuccessListener<QueryResult<Person>>() {
                    @Override
                    public void onSuccess(QueryResult<Person> personQueryResult) {
                        String lastDocumentID = personQueryResult.getLastDocumentID();
                        System.out.println("lastDocumentID = " + lastDocumentID);
                        System.out.println("items = " + personQueryResult.getItems());
                        resultTextview.setText(personQueryResult.getItems().toString());
                        MainActivity.this.lastDocumentID = lastDocumentID;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.err.println(e.getMessage());
                    }
                });
            }
        });

        //Initialize:
        Firestorm.init();

        //Register classes:
        Firestorm.register(Person.class);


        //Test object reference with ID
//        {
//            DocumentReference aaa = Firestorm.getObjectReference(Person.class, "aaa");
//            aaa.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    System.out.println(documentSnapshot.toObject(Person.class));
//                }
//            });
//        }


        //Test object reference with POJO:
//        {
//            DocumentReference aaa1 = Firestorm.getObjectReference(p);
//            aaa1.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                @Override
//                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                    System.out.println(documentSnapshot.toObject(Person.class));
//                }
//            });
//        }

        //Test collection reference:
//        {
//            CollectionReference c1 = Firestorm.getCollectionReference(Person.class);
//            c1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                @Override
//                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                    List<Person> people = queryDocumentSnapshots.toObjects(Person.class);
//                    System.out.println(people);
//                }
//            });
//        }

        //Test attach listener to object:
//        {
//            Firestorm.attachListener(new OnObjectUpdateListener(p) {
//                @Override
//                public void onSuccess() {
//                    System.out.println("Updated object: " + p);
//                }
//
//                @Override
//                public void onFailure(String failureMessage) {
//                    System.err.println(failureMessage);
//                }
//            });
////            Firestorm.detachListener(p);
//        }

        //Test attach listener to reference:
//        {
//            ListenerRegistration listenerRegistration = Firestorm.attachListener(new OnReferenceUpdateListener(Person.class, "aaa") {
//                @Override
//                public void onSuccess(Object object) {
//                    System.out.println("Updated object: " + object);
//                }
//
//                @Override
//                public void onFailure(String failureMessage) {
//                    System.err.println(failureMessage);
//                }
//            });
//
////            Firestorm.detachListener(listenerRegistration);
//        }

        //Test real-time update on class:
//        {
//            Firestorm.attachListener(new ClassListener<Person>(Person.class) {
//                @Override
//                public void onSuccess(List<ObjectChange<Person>> objectChanges) {
//                    for (ObjectChange<Person> o : objectChanges) {
//                        System.out.println(o.getType() + " " + o.getObject().toString());
//                    }
//                }
//
//                @Override
//                public void onFailure(String failureMessage) {
//                    Toast.makeText(MainActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }

        //Test real-time update on filtered items:
//        {
//            FirestormFilterable<Person> filter = Firestorm.filter(Person.class)
//                    .whereGreaterThan("age", 12);
//
//            Firestorm.attachListener(new FilterableListener<Person>(filter) {
//                @Override
//                public void onSuccess(List<ObjectChange<Person>> objectChanges) {
//                    for (ObjectChange<Person> o : objectChanges) {
//                        System.out.println(o.getType() + " " + o.getObject().toString());
//                    }
//                }
//
//                @Override
//                public void onFailure(String failureMessage) {
//                    Toast.makeText(MainActivity.this, failureMessage, Toast.LENGTH_SHORT).show();
//                }
//            });
//        }

        //Test transactions:
//        Firestorm.runTransaction(new FirestormTransaction<Integer>() {
//            @Override
//            public Integer execute() {
//                get(Person.class, "aaa");
//                update(new Person("bbb", "B!", 233));
//                return 2;
//            }
//        }).addOnSuccessListener(new OnSuccessListener<Integer>() {
//            @Override
//            public void onSuccess(Integer unused) {
//                System.out.println("Transaction success!" + unused);
//            }
//        })
//        .addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                System.err.println("Transaction failed");
//            }
//        });

        //Test batch:
//        Firestorm.runBatch(new FirestormBatch() {
//            @Override
//            public Void execute() {
//                for (int i = 0; i < 10; i++) {
//                    Person c = new Person(i + "-id", "name-" + i, i + 20);
//                    create(c);
//                }
//                return null;
//            }
//        }).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                System.out.println("Batch write success!");
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                System.err.println(e.getMessage());
//            }
//        });


    }
}