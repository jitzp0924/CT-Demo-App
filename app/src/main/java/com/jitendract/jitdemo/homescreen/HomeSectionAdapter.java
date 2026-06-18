package com.jitendract.jitdemo.homescreen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jitendract.jitdemo.CarouselModel.SliderAdapter;
import com.jitendract.jitdemo.CarouselModel.SliderData;
import com.jitendract.jitdemo.R;
import com.jitendract.jitdemo.Reco.RecommendationAdapter;
import com.jitendract.jitdemo.Reco.RecommendationCard;
import com.smarteist.autoimageslider.SliderView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeSectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // --- Payload types ---

    public static class RecoPayload {
        public final List<RecommendationCard> cards;
        public RecoPayload(List<RecommendationCard> cards) { this.cards = cards; }
    }

    public static class QuickLinksPayload {
        public final List<QuickLinkItem> items;
        public final int columns;
        public QuickLinksPayload(List<QuickLinkItem> items, int columns) {
            this.items = items;
            this.columns = columns;
        }
    }

    public static class PayBillsPayload {
        public final List<PayBillItem> items;
        public PayBillsPayload(List<PayBillItem> items) { this.items = items; }
    }

    public static class CarouselPayload {
        public final ArrayList<SliderData> slides;
        public final HashMap<String, Object> slidermap;
        public final HashMap<String, Object> homeEvt;
        public CarouselPayload(ArrayList<SliderData> slides,
                               HashMap<String, Object> slidermap,
                               HashMap<String, Object> homeEvt) {
            this.slides = slides;
            this.slidermap = slidermap;
            this.homeEvt = homeEvt;
        }
    }

    // --- Interaction callbacks ---

    public interface SectionInteractionListener {
        void onQuickLinkClicked(QuickLinkItem item);
        void onPayBillClicked(PayBillItem item);
    }

    // --- Adapter core ---

    private final Context context;
    private final List<HomeSection> sections;
    private final SectionInteractionListener listener;

    public HomeSectionAdapter(Context context, List<HomeSection> sections,
                              SectionInteractionListener listener) {
        this.context = context;
        this.sections = sections;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return sections.get(position).type.ordinal();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(context);
        switch (HomeSection.SectionType.values()[viewType]) {
            case RECO:
                return new RecoViewHolder(inf.inflate(R.layout.section_reco, parent, false));
            case QUICK_LINKS:
                return new QuickLinksViewHolder(inf.inflate(R.layout.section_quick_links, parent, false));
            case PAY_BILLS:
                return new PayBillsViewHolder(inf.inflate(R.layout.section_pay_bills, parent, false));
            case CAROUSEL:
            default:
                return new CarouselViewHolder(inf.inflate(R.layout.section_carousel, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomeSection section = sections.get(position);
        switch (section.type) {
            case RECO:
                ((RecoViewHolder) holder).bind((RecoPayload) section.data, context);
                break;
            case QUICK_LINKS:
                ((QuickLinksViewHolder) holder).bind((QuickLinksPayload) section.data, context, listener);
                break;
            case PAY_BILLS:
                ((PayBillsViewHolder) holder).bind((PayBillsPayload) section.data, context, listener);
                break;
            case CAROUSEL:
                ((CarouselViewHolder) holder).bind((CarouselPayload) section.data, context);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    // --- ViewHolders ---

    static class RecoViewHolder extends RecyclerView.ViewHolder {
        final RecyclerView recycler;

        RecoViewHolder(View v) {
            super(v);
            recycler = v.findViewById(R.id.reco_recycler);
        }

        void bind(RecoPayload payload, Context ctx) {
            recycler.setLayoutManager(new LinearLayoutManager(ctx));
            recycler.setAdapter(new RecommendationAdapter(ctx, payload.cards));
        }
    }

    static class QuickLinksViewHolder extends RecyclerView.ViewHolder {
        final RecyclerView recycler;

        QuickLinksViewHolder(View v) {
            super(v);
            recycler = v.findViewById(R.id.quick_links_recycler);
        }

        void bind(QuickLinksPayload payload, Context ctx, SectionInteractionListener listener) {
            recycler.setLayoutManager(new GridLayoutManager(ctx, payload.columns));
            recycler.setAdapter(new QuickLinksAdapter(payload.items, listener::onQuickLinkClicked));
        }
    }

    static class PayBillsViewHolder extends RecyclerView.ViewHolder {
        final RecyclerView recycler;

        PayBillsViewHolder(View v) {
            super(v);
            recycler = v.findViewById(R.id.pay_bills_recycler);
        }

        void bind(PayBillsPayload payload, Context ctx, SectionInteractionListener listener) {
            recycler.setLayoutManager(new LinearLayoutManager(ctx));
            recycler.setAdapter(new PayBillsAdapter(payload.items, listener::onPayBillClicked));
        }
    }

    static class CarouselViewHolder extends RecyclerView.ViewHolder {
        final SliderView sliderView;

        CarouselViewHolder(View v) {
            super(v);
            sliderView = v.findViewById(R.id.carousel_slider);
        }

        void bind(CarouselPayload payload, Context ctx) {
            SliderAdapter adapter = new SliderAdapter(ctx, payload.slides, payload.slidermap, payload.homeEvt);
            sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
            sliderView.setSliderAdapter(adapter);
            sliderView.setScrollTimeInSec(3);
            sliderView.setAutoCycle(true);
            sliderView.startAutoCycle();
        }
    }
}
