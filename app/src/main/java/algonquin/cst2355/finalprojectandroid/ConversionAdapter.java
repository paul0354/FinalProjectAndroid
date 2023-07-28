package algonquin.cst2355.finalprojectandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import algonquin.cst2355.finalprojectandroid.Conversion;
import algonquin.cst2355.finalprojectandroid.R;

public class ConversionAdapter extends RecyclerView.Adapter<ConversionAdapter.ConversionViewHolder> {

    private List<Conversion> conversions;

    public ConversionAdapter(List<Conversion> conversions) {
        this.conversions = conversions;
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

    static class ConversionViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView;
        TextView dateTextView;
        TextView currencyTextView;

        public ConversionViewHolder(View itemView) {
            super(itemView);
            amountTextView = itemView.findViewById(R.id.textViewAmount);
            dateTextView = itemView.findViewById(R.id.textViewDate);
            currencyTextView = itemView.findViewById(R.id.currencyTextView);
            // Initialize other views if needed
        }
    }
}
