package com.canada.cardelar.application.Models;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.canada.cardelar.application.R;

import java.util.List;

public class DealerAdapter extends RecyclerView.Adapter<DealerAdapter.CustomViewHolder> {

    List<AdapterData> adapterData;
    Context context;
    private  onItemClickListener mListener;
      public  interface onItemClickListener{
        void  editClick(int position);

        }


     public  void setOnItemClickListener(onItemClickListener listener){
          mListener=listener;
     }
   public static class  CustomViewHolder extends RecyclerView.ViewHolder{
        TextView dealerName,kmAway,ratting,totalRatting,seeMore;
        ImageView dealerImage;
       ImageView[] star = new ImageView[5];


        public CustomViewHolder(View itemView, final onItemClickListener listener) {
            super(itemView);
             dealerName=itemView.findViewById(R.id.textViewName);
             kmAway=itemView.findViewById(R.id.textViewDistance);
             ratting=itemView.findViewById(R.id.textViewRatting);
             totalRatting=itemView.findViewById(R.id.textViewTotal);
             seeMore=itemView.findViewById(R.id.textViewSeeDetails);
            dealerImage=itemView.findViewById(R.id.imageViewDealerImage);
            star[0]=itemView.findViewById(R.id.imageViewStar1);
            star[1]=itemView.findViewById(R.id.imageViewStar2);
            star[2]=itemView.findViewById(R.id.imageViewStar3);
            star[3]=itemView.findViewById(R.id.imageViewStar4);
            star[4]=itemView.findViewById(R.id.imageViewStar5);
            seeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int position=getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            listener.editClick(position);
                        }
                    }
                }
            });

        }
    }
    public DealerAdapter(List<AdapterData> adapterData, Context context) {
        this.adapterData = adapterData;
        this.context = context;
    }
    @Override
    public int getItemViewType(int position) {

            return R.layout.recycler_view_item_layout;
    }
    @Override
    public int getItemCount() {
        return  adapterData.size();
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false),mListener);
    }
    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        holder.dealerName.setText(adapterData.get(position).getDealerName());
        holder.kmAway.setText(adapterData.get(position).getDistanceAway()+" KM");
        holder.ratting.setText(adapterData.get(position).getRatting());

        holder.dealerImage.setImageBitmap(adapterData.get(position).getDealerImage());
        if (adapterData.get(position).getRatting().equals("")) {
            holder.ratting.setText("Ratting");
            holder.totalRatting.setText("NA");
        }
         else {
            holder.ratting.setText(adapterData.get(position).ratting);
            holder.totalRatting.setText("(" + adapterData.get(position).totalRatting + ")");
            Double dRate = Double.parseDouble(adapterData.get(position).ratting);
            int Irate = dRate.intValue();
            double point = dRate - Irate;

            int totalStar = 5;
            int i = 0;
            for (; i < Irate; i++) {
                holder.star[i].setImageResource(R.drawable.ic_full_star);
                totalStar--;
            }
            if (totalStar != 0) {
                if (point >= 0.3) {
                    holder.star[i].setImageResource(R.drawable.ic_half_star);
                    i++;
                    totalStar--;
                }
            }
            for (; i < 5; i++) {
                holder.star[i].setImageResource(R.drawable.ic_empty_star);
            }
        }







      }
}
