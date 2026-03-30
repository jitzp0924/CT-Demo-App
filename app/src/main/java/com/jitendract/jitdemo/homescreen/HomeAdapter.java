//package com.jitendract.jitdemo.homescreen;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.*;
//
//public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    Context context;
//    List<HomeSection> sections;
//
//    public static final int RECOMMENDED = 1;
//
//    public HomeAdapter(List<HomeSection> sections, Context context) {
//        this.sections = sections;
//        this.context = context;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//
//        if(sections.get(position).sectionName.equals("RecommendedForU")){
//            return RECOMMENDED;
//        }
//
//        return 0;
//    }
//
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//
//        if(viewType == RECOMMENDED){
//
//            View view = LayoutInflater.from(context)
//                    .inflate(R.layout.section_recommended,parent,false);
//
//            return new RecommendedHolder(view);
//        }
//
//        return null;
//    }
//
//
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//
//        HomeSection section = sections.get(position);
//
//        if(holder instanceof RecommendedHolder){
//
//            ((RecommendedHolder)holder)
//                    .bind((Map<String,Object>)section.data);
//        }
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return sections.size();
//    }
//
//}
