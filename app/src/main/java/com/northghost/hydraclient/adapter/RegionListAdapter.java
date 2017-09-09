package com.northghost.hydraclient.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.anchorfree.hydrasdk.api.data.Country;
import com.northghost.hydraclient.R;
import java.util.ArrayList;
import java.util.List;

public class RegionListAdapter extends RecyclerView.Adapter<RegionListAdapter.ViewHolder> {

    private List<Country> regions;
    private RegionListAdapterInterface listAdapterInterface;

    public RegionListAdapter(RegionListAdapterInterface listAdapterInterface) {
        this.listAdapterInterface = listAdapterInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.region_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Country country = regions.get(position);
        if (country.getCountry() != null) {
            holder.regionTitle.setText(regions.get(position).getCountry());
        } else {
            holder.regionTitle.setText("unknown(null)");
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listAdapterInterface.onCountrySelected(regions.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return regions != null ? regions.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.region_title)
        TextView regionTitle;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }

    public void setRegions(List<Country> list) {
        regions = new ArrayList<>();
        regions.add(new Country());
        regions.addAll(list);
        notifyDataSetChanged();
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(Country item);
    }
}
