//package com.jitendract.jitdemo.homescreen;
//
//import android.os.Bundle;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.jitendract.jitdemo.R;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//public class home extends AppCompatActivity {
//
//    RecyclerView homeRecycler;
//    List<HomeSection> homeSections = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
//
//        homeRecycler = findViewById(R.id.homeRecycler);
//
//        buildSectionsFromConfig();
//
//        Collections.sort(homeSections, Comparator.comparingInt(s -> s.order));
//
//        HomeAdapter adapter = new HomeAdapter(homeSections, this);
//        homeRecycler.setLayoutManager(new LinearLayoutManager(this));
//        homeRecycler.setAdapter(adapter);
//    }
//}