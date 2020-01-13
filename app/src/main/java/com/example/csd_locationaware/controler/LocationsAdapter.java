package com.example.csd_locationaware.controler;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.csd_locationaware.R;
import com.example.csd_locationaware.model.Bar;
import com.example.csd_locationaware.model.Bars;
import com.example.csd_locationaware.util.FetchURL;
import com.example.csd_locationaware.view.MapsActivity;

public class LocationsAdapter extends RecyclerView.Adapter<LocationsAdapter.LocationsViewHolder> {

    private Context context;
    private static final String TAG = "@LOCATIONSADAPTER";
    private Activity loc;

    public LocationsAdapter(Context context,Activity activity) {
        this.context = context;
        loc = activity;
    }

    @NonNull
    @Override
    public LocationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LocationsViewHolder(LayoutInflater.from(context).inflate(R.layout.locations_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LocationsViewHolder holder, int position) {
        Bar bar = Bars.bars.get(position);
        holder.name.setText(bar.getName());
        holder.address.setText(bar.getAdress());
    }


    @Override
    public int getItemCount() {
        return Bars.bars.size();
    }


    class LocationsViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView address;
        Button button;


        public LocationsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textView_Name);
            address = itemView.findViewById(R.id.textView_address);
            button = itemView.findViewById(R.id.button_calculate_route);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Bar selectedBar = Bars.bars.get(pos);
                    new FetchURL(MapsActivity.context).execute(Bars.getDirectionsUrl(MapsActivity.currentLocation, selectedBar.getLocation()), "walking");
                    loc.onBackPressed();
                    Log.d(TAG, "onClick: " + selectedBar.toString());
                }
            });
        }
    }
}
