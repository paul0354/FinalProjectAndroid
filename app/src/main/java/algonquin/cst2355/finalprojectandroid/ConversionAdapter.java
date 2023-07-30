package algonquin.cst2355.finalprojectandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ConversionAdapter extends RecyclerView.Adapter<ConversionAdapter.ConversionViewHolder> {

    private final ArrayList<Conversion> conversions;
    private final OnItemClickListener itemClickListener;

    public ConversionAdapter(ArrayList<Conversion> conversions, OnItemClickListener itemClickListener) {
        this.conversions = conversions;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversion, parent, false);
        return new ConversionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        Conversion conversion = conversions.get(position);
        holder.amountTextView.setText(conversion.getConversionResult());
        holder.dateTextView.setText(conversion.getTimeSemt());
        holder.currencyTextView.setText(conversion.getCurrencyConversion());
    }

    @Override
    public int getItemCount() {
        return conversions.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView;
        TextView dateTextView;
        TextView currencyTextView;

        public ConversionViewHolder(View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.textViewAmount);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            currencyTextView = itemView.findViewById(R.id.currencyTextView);
            // Initialize other views if needed

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(conversions.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Conversion selectedItem);
    }
}
