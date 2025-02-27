package com.jitendract.jitdemo.CarouselModel;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jitendract.jitdemo.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.jitendract.jitdemo.CleveTapUtils;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {

    // list for storing urls of images.
    private final List<SliderData> mSliderItems;
    HashMap<String,Object> homescreenEvt,sliderMap;
    CleveTapUtils cleveTapUtils;

    // Constructor
    public SliderAdapter(Context context, ArrayList<SliderData> sliderDataArrayList, HashMap<String,Object> slidermap, HashMap<String,Object> homescreenEvt) {
        this.mSliderItems = sliderDataArrayList;
        this.sliderMap = slidermap;
        this.homescreenEvt = homescreenEvt;
        cleveTapUtils = new CleveTapUtils(context);
    }

    // We are inflating the slider_layout
    // inside on Create View Holder method.
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, null);
        return new SliderAdapterViewHolder(inflate);
    }

    // Inside on bind view holder we will
    // set data to item of Slider View.
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        final SliderData sliderItem = mSliderItems.get(position);

        // Glide is use to load image
        // from url in your imageview.
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getImgUrl()).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
//                .fitCenter()
                .into(viewHolder.imageViewBackground);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homescreenEvt.put("Clicked Position",String.valueOf(position + 1));
                homescreenEvt.put("Image Url",sliderMap.get(String.valueOf(position + 1)));
                cleveTapUtils.raiseEvent(String.valueOf(sliderMap.get("eventName")),homescreenEvt);
                Log.d("homescreenEvt", String.valueOf(homescreenEvt));

            }
        });
    }

    // this method will return
    // the count of our list.
    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        // Adapter class for initializing
        // the views of our slider view.
        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.slideimage);
            this.itemView = itemView;
        }
    }
}
