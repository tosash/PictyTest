package com.kido.pictytest;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kido.board.model.Ad;
import com.kido.board.network.VolleySingleton;
import com.kido.board.ui.FragmentDetailAd;
import com.kido.board.util.CircularNetworkImageView;

import java.util.List;

public class AdsAdapter extends RecyclerView.Adapter<AdsAdapter.AdsViewHolder> {
    public long currID;
    private List<Ad> mItems;
    private FragmentManager fm;

    public AdsAdapter(FragmentManager f, List<Ad> ads) {
        fm = f;
        mItems = ads;
    }

    @Override
    public AdsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ads_row, viewGroup, false);
        return new AdsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdsViewHolder adsViewHolder, int i) {
        Ad ad = mItems.get(i);

        adsViewHolder.imgThumbnail.setCircled(false);
        adsViewHolder.imgThumbnail.setImageUrl(ad.getAdUrl(), VolleySingleton.getInstance(null).getImageLoader());
        adsViewHolder.imgThumbnail.setDefaultImageResId(R.drawable.ic_no_image);
        adsViewHolder.imgThumbnail.setErrorImageResId(R.drawable.ic_error_image);

        adsViewHolder.txtName.setText(ad.getAdName());
        adsViewHolder.txtDesc.setText(ad.getAdDesc());
        adsViewHolder.txtDate.setText(ad.getAdDate().toString());
        adsViewHolder.txtDateExp.setText(ad.getAdDateExp().toString());
        adsViewHolder.txtPrice.setText(String.format("%.2f", ad.getAdPrice()) + (((ad.getAdCurrency() == null) || (ad.getAdCurrency() == "")) ? "USD" : ad.getAdCurrency()));
        adsViewHolder.txtCat.setText(ad.getAdCategory());
        adsViewHolder.txtReg.setText(ad.getAdRegion());
        adsViewHolder.txtDisplay.setText(Integer.toString(ad.getAdDisplay()));
    }

    @Override
    public int getItemCount() {
        if (mItems == null) {
            return 0;
        } else {
            return mItems.size();
        }
    }

    public Ad getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getAdNumer();
    }

    class AdsViewHolder extends RecyclerView.ViewHolder {

        protected CircularNetworkImageView imgThumbnail;
        protected TextView txtName;
        protected TextView txtDesc;
        protected TextView txtDateExp;
        protected TextView txtDate;
        protected TextView txtPrice;
        protected TextView txtCat;
        protected TextView txtReg;
        protected TextView txtDisplay;
        protected TabLayout tabL;

        public AdsViewHolder(final View itemView) {
            super(itemView);
            imgThumbnail = (CircularNetworkImageView) itemView.findViewById(R.id.img_thumbnail);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtDesc = (TextView) itemView.findViewById(R.id.txt_desc);
            txtDate = (TextView) itemView.findViewById(R.id.txt_date);
            txtDateExp = (TextView) itemView.findViewById(R.id.txt_dateExp);
            txtPrice = (TextView) itemView.findViewById(R.id.txtPrice);
            txtCat = (TextView) itemView.findViewById(R.id.txt_Cat);
            txtReg = (TextView) itemView.findViewById(R.id.txt_Region);
            txtDisplay = (TextView) itemView.findViewById(R.id.txt_Display);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        Bundle bundle = new Bundle();
                        int pos = getLayoutPosition();
//                        bundle.putLong("AdId", getItem(pos).getAdNumer());
                        Fragment fragAd = new FragmentDetailAd().newInstance(getItem(pos).getAdNumer());
//                        fragAd.setArguments(bundle);
                        fm.beginTransaction()
                                .add(R.id.containerMain, fragAd)
//                                .replace(R.id.containerMain, fragAd)
                                .addToBackStack(FragmentDetailAd.class.toString())
                                .commit();
                    } catch (Exception e) {
                        Log.e("Ad edit", e.toString());
                    }
                }
            });
        }
    }

}
