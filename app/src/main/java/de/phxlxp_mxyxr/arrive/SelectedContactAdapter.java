package de.phxlxp_mxyxr.arrive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SelectedContactAdapter extends RecyclerView.Adapter<SelectedContactAdapter.ViewHolder>{

    private ArrayList<Contact> arraylistSelectedContacts;
    private LayoutInflater layoutinflater;
    private ItemClickListener itemclicklistener;
    private Context context;

    SelectedContactAdapter(Context context,ArrayList<Contact>arraylistSelectedContacts){
        this.arraylistSelectedContacts=arraylistSelectedContacts;
        this.layoutinflater=LayoutInflater.from(context);
        this.context=context;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutinflater.inflate(R.layout.recyclerview_contacts_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewholder, final int position) {
        String contact_name = arraylistSelectedContacts.get(position).getContact_name();
        viewholder.textview_selectedcontact.setText(contact_name);
    }

    @Override
    public int getItemCount() {
        return arraylistSelectedContacts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textview_selectedcontact;
        LinearLayout linearlayout;

        ViewHolder(View itemview) {
            super(itemview);
            textview_selectedcontact=itemview.findViewById(R.id.textview_selectedcontact);
            linearlayout=itemview.findViewById(R.id.linearlayout_recyclerview_item);
            itemview.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemclicklistener != null) itemclicklistener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return arraylistSelectedContacts.get(id).getContact_name();
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemclicklistener) {
        this.itemclicklistener = itemclicklistener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
