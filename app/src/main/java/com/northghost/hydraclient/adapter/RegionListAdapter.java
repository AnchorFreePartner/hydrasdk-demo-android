package com.northghost.hydraclient.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.northghost.hydraclient.databinding.RegionListItemBinding;

import java.util.ArrayList;
import java.util.List;

import unified.vpn.sdk.Country;
import unified.vpn.sdk.Location;


public class RegionListAdapter extends RecyclerView.Adapter<RegionListAdapter.ViewHolder> {
    public static class Region {
        final String name;

        public Region(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private List<Region> regions;
    private RegionListAdapterInterface listAdapterInterface;

    public RegionListAdapter(RegionListAdapterInterface listAdapterInterface) {
        this.listAdapterInterface = listAdapterInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        RegionListItemBinding binding = RegionListItemBinding.inflate(layoutInflater, parent, false);
        ViewHolder vh = new ViewHolder(binding);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Region country = regions.get(position);
        if (!country.name.isEmpty()) {
            holder.binding.regionTitle.setText(regions.get(position).name);
        } else {
            holder.binding.regionTitle.setText("unknown(null)");
        }
        holder.itemView.setOnClickListener(view -> listAdapterInterface.onCountrySelected(regions.get(holder.getAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return regions != null ? regions.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private com.northghost.hydraclient.databinding.RegionListItemBinding binding;

        public ViewHolder(RegionListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setRegions(List<Location> list) {
        regions = new ArrayList<>();
        regions.add(new Region(""));
        for (Location loc : list) {
            regions.add(new Region(loc.getName()));
        }
        notifyDataSetChanged();
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(Region item);
    }
}
