package sheva.singapp.mvp.ui.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

import sheva.singapp.R;

/**
 * Created by shevc on 07.07.2017.
 * Let's GO!
 */

public class PagerMenuPicsAdapter extends PagerAdapter {
    int[] pics = {
            R.drawable.pic_1,
            R.drawable.pic_2,
            R.drawable.pic_3,
            R.drawable.pic_4,
            R.drawable.pic_5
    };
    private LayoutInflater inflater;
    private Context context;

    public PagerMenuPicsAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View item = inflater.inflate(R.layout.item_menu_photo, container, false);
        ImageView imageView = (ImageView) item.findViewById(R.id.iv_item_pic);
        Picasso.with(context)
                .load(pics[position])
                .into(imageView);
        if(imageView.getParent() != null) {
            ((ViewGroup)imageView.getParent()).removeView(imageView);
        }
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeViewAt(position);
    }
}
