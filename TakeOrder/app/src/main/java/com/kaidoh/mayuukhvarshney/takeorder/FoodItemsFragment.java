package com.kaidoh.mayuukhvarshney.takeorder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridView;
/**
 * Created by mayuukhvarshney on 31/05/16.
 */

public class FoodItemsFragment extends Fragment {
    public static String[] CONTENT={"Dosa","Idly","Sambar","Masala Dosa"};
    public static int[] ICONS={R.mipmap.black_image,R.mipmap.black_image,R.mipmap.black_image,R.mipmap.black_image};
    private ImageAdapter adapter;
    GridView menuGrid;
    protected int mPhotoSize, mPhotoSpacing;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View view=inflater.inflate(R.layout.food_menu_items,container,false);
        Log.d("FoodItems","has come here ");
        mPhotoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        mPhotoSpacing = getResources().getDimensionPixelSize(R.dimen.photo_spacing);
        adapter= new ImageAdapter(getActivity(),ICONS,CONTENT);
        menuGrid= (GridView) view.findViewById(R.id.grid_view1);
        menuGrid.setAdapter(adapter);
        menuGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (adapter.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(menuGrid.getWidth() / (mPhotoSize + mPhotoSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (menuGrid.getWidth() / numColumns);
                        adapter.setNumColumns(numColumns);
                        adapter.setItemHeight(columnWidth);


                    }
                }
            }
        });
        return view;
    }
}
