package com.app.doctair;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class OnboardAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    public OnboardAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
            R.drawable.doctor,
            R.drawable.appoint,
            R.drawable.call
    };
    public String[] slide_desc = {
            "Now finding doctors are just a tap away",
            "Get connected with other students like you",
            "Consult with a doctors on call and get best consultations"
    };

    @Override
    public int getCount() {
        return slide_desc.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View view = layoutInflater.inflate(R.layout.onboard_frag,container,false);

        ImageView slideImage = view.findViewById(R.id.slideImage);
        TextView slideDesc = view.findViewById(R.id.slideDesc);

        slideImage.setImageResource(slide_images[position]);
        slideDesc.setText(slide_desc[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
