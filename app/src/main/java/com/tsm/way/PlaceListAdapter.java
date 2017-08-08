package com.tsm.way;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tsm.way.model.PlaceBean;

import java.util.List;

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.PlaceListAdapterViewHolder> {

    private final PlaceListAdapterViewHolder mClickHandler;
    Context mContext;
    List<PlaceBean> placeBeanList;

    public PlaceListAdapter(Context mContext, List<PlaceBean> placeBeanList, PlaceListAdapterViewHolder mClickHandler) {
        this.mContext = mContext;
        this.placeBeanList = placeBeanList;
        this.mClickHandler = mClickHandler;
    }

    public void setData(List<PlaceBean> placeBeanList) {
        this.placeBeanList = placeBeanList;
        notifyDataSetChanged();
    }

    @Override
    public PlaceListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.single_place_in_list, parent, false);
        return new PlaceListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaceListAdapterViewHolder holder, int position) {
        PlaceBean pb = placeBeanList.get(position);
        holder.nameTextView.setText(pb.getName());
        holder.openTextView.setText(pb.getVicinity());
    }

    @Override
    public int getItemCount() {
        return placeBeanList.size();
    }

    public class PlaceListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTextView;
        TextView openTextView;

        public PlaceListAdapterViewHolder(View itemView) {
            super(itemView);
            openTextView = (TextView) itemView.findViewById(R.id.open_now_in_list);
            nameTextView = (TextView) itemView.findViewById(R.id.place_name_now_in_list);

        }

        @Override
        public void onClick(View v) {

        }
    }
}