package de.phxlxp_mxyxr.arrive;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        actionbar=getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        //actionbar.setLogo(R.drawable.toolbar_icon);
        //actionbar.setDisplayUseLogoEnabled(true);
        //actionbar.setDisplayHomeAsUpEnabled(true);

        display_contacts();

        ListView listviewContacts=(ListView)findViewById(R.id.listview_contacts);
        listviewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Globals global=(Globals)getApplication();
                final Contact contact=global.get_arraylistContacts().get(i);
                if(!checkHasMultipleNumbers(contact)){
                    if(global.contains_arraylistSelectedContacts(contact)){
                        Toast.makeText(ContactsActivity.this,R.string.select_contact_false_message,Toast.LENGTH_LONG).show();
                    }
                    else{
                        global.addTo_arraylistSelectedContacts(contact);
                        seperateMultipleNumbers(contact);
                        Toast.makeText(ContactsActivity.this,R.string.select_contact_true_message,Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    //MULTIPLE NUMBERS
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactsActivity.this);
                    builder.setTitle(contact.getContact_name());
                    final String[] seperatednumbers=seperateMultipleNumbers(contact);
                    builder.setItems(seperatednumbers, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            handleMultipleNumbers(contact,seperatednumbers[i]);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    //displays contacts in ListView
    public void display_contacts(){
        Globals global=(Globals)getApplication();
        ArrayList<Contact> arraylistContacts=global.get_arraylistContacts();
        ContactAdapter contactAdapter=new ContactAdapter(this,arraylistContacts);
        ListView listview_contacts=(ListView)findViewById(R.id.listview_contacts);
        listview_contacts.setAdapter(contactAdapter);
    }

    public boolean checkHasMultipleNumbers(Contact contact){
        return contact.getContact_number().contains("\n");
    }

    public String[] seperateMultipleNumbers(Contact contact){
        String contactnumbers=contact.getContact_number().replaceAll("\n"," ");
        ArrayList<String> seperatednumbers=new ArrayList();
        String singlenumber="";
        for(int index=0;index<contactnumbers.length();index++){
            if(String.valueOf(contactnumbers.charAt(index)).equals(" ")){
                seperatednumbers.add(singlenumber);
                singlenumber="";
            }
            else if(index==contactnumbers.length()-1){
                singlenumber=singlenumber+String.valueOf(contactnumbers.charAt(index));
                seperatednumbers.add(singlenumber);
            }
            else{
                singlenumber=singlenumber+String.valueOf(contactnumbers.charAt(index));
            }
        }
        int size=seperatednumbers.size();
        String output[]=new String[size];
        for(int index=0;index<size;index++){
            output[index]=seperatednumbers.get(index);
        }
        return output;
    }

    //checks if number already was chosen, adds it if false
    public void handleMultipleNumbers(Contact contact,String selectednumber){
        Contact newcontact=new Contact();
        newcontact.setContact_name(contact.getContact_name());
        newcontact.setContact_id(contact.getContact_id());
        newcontact.setContact_number(selectednumber);
        Globals global=(Globals)getApplication();
        ArrayList<Contact> selectedcontacts=global.get_arraylistSelectedContacts();

        int check=0;
        for(int index=0;index<selectedcontacts.size();index++){
            if(newcontact.getContact_name().equals(selectedcontacts.get(index).getContact_name())&&selectedcontacts.get(index).getContact_number().equals(selectednumber)){
                Toast.makeText(ContactsActivity.this,R.string.select_contact_false_message,Toast.LENGTH_LONG).show();
                check=1;
                break;
            }
        }
        if(check==0){
            global.addTo_arraylistSelectedContacts(newcontact);
            Toast.makeText(ContactsActivity.this,R.string.select_contact_true_message,Toast.LENGTH_LONG).show();
        }
    }

}
