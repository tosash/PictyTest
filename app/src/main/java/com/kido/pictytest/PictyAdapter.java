package com.kido.pictytest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class PictyAdapter extends RecyclerView.Adapter<PictyAdapter.PictyViewHolder> {
    public long currID;
    private List<Pict> mItems;
    private Context fContext;

    public PictyAdapter(Context context, List<Pict> ads) {
        fContext = context;
        mItems = ads;
    }

    @Override
    public PictyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recieve_row, viewGroup, false);
        return new PictyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(PictyViewHolder pictyViewHolder, int i) {
        Pict pict = mItems.get(i);

        pictyViewHolder.imgThumbnail.setCircled(false);
        pictyViewHolder.imgThumbnail.setImageUrl(pict.getUrl(), VolleySingleton.getInstance(fContext).getImageLoader());
        pictyViewHolder.imgThumbnail.setDefaultImageResId(R.drawable.ic_no_image);
        pictyViewHolder.imgThumbnail.setErrorImageResId(R.drawable.ic_error_image);

        pictyViewHolder.txtCountry.setText(pict.getFlag());
        int fi = fContext.getResources().getIdentifier(pict.getFlag(), "drawable", fContext.getPackageName());
        pictyViewHolder.imgFlag.setImageResource(fi);
        pictyViewHolder.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fContext, "button Reply clicked", Toast.LENGTH_SHORT).show();
            }
        });
        pictyViewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(fContext, "button Detail clicked", LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mItems == null) {
            return 0;
        } else {
            return mItems.size();
        }
    }

    public Pict getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mItems.get(position).getId();
    }




    class PictyViewHolder extends RecyclerView.ViewHolder {

        protected CircularNetworkImageView imgThumbnail;
        protected ImageView imgFlag;
        protected TextView txtCountry;
        protected ImageButton btnReply;
        protected ImageButton btnDetail;
        protected ImageView imgOs;

        public PictyViewHolder(final View itemView) {
            super(itemView);
            imgThumbnail = (CircularNetworkImageView) itemView.findViewById(R.id.img_thumbnail);
            imgFlag = (ImageView) itemView.findViewById(R.id.img_flag);
            txtCountry = (TextView) itemView.findViewById(R.id.txt_Country);
            btnReply = (ImageButton) itemView.findViewById(R.id.btn_reply);
            btnDetail = (ImageButton) itemView.findViewById(R.id.btn_detail);
            imgOs = (ImageView) itemView.findViewById(R.id.img_os);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(fContext, "Shot clicked", LENGTH_SHORT).show();
                }
            });
        }
    }

}
