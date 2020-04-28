package de.phxlxp_mxyxr.arrive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

//used to display a ArrayList of Contacts in a ListView
public class ContactAdapter extends ArrayAdapter<Contact> {
    public ContactAdapter(Context context, ArrayList<Contact> arraylistContacts) {
        super(context,0,arraylistContacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        // Get the data item for this position
        Contact contact=getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if(convertView==null){
            convertView=LayoutInflater.from(getContext()).inflate(R.layout.listview_contacts_item, parent, false);
        }
        // Lookup view for data population
        TextView contact_name = (TextView) convertView.findViewById(R.id.contact_name);
        TextView contact_phonenumber = (TextView) convertView.findViewById(R.id.contact_phonenumber);
        // Populate the data into the template view using the data object
        contact_name.setText(contact.getContact_name());
        contact_phonenumber.setText(contact.getContact_number());
        // Return the completed view to render on screen
        return convertView;
    }
}
