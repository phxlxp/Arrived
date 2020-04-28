package de.phxlxp_mxyxr.arrive;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.android.gms.maps.model.LatLng;

public class Globals extends Application {

    //ArrayList, that contains all contacts
    private ArrayList<Contact> arraylistContacts=new ArrayList();
    public void set_arraylistContacts(ArrayList<Contact> arraylistContacts){
        this.arraylistContacts=arraylistContacts;
    }
    public ArrayList<Contact> get_arraylistContacts(){return arraylistContacts;}

    //ArrayList, that contains all selected contacts
    private ArrayList<Contact> arraylistSelectedContacts=new ArrayList();
    public ArrayList<Contact> get_arraylistSelectedContacts(){return arraylistSelectedContacts;}
    public void set_arraylistSelectedContacts(ArrayList<Contact>contacts){this.arraylistSelectedContacts=contacts;}
    public void addTo_arraylistSelectedContacts(Contact contact){this.arraylistSelectedContacts.add(contact);}
    public void removeFrom_arraylistSelectedContacts(int index){this.arraylistSelectedContacts.remove(index);}
    public boolean contains_arraylistSelectedContacts(Contact contact){
        return this.arraylistSelectedContacts.contains(contact);
    }

    //ArrayList, that contains all saved templates
    private ArrayList<String> arraylistTemplates;
    public void start_arraylistTemplates(){
        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString("templates", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        this.arraylistTemplates = gson.fromJson(json, type);

        if (arraylistTemplates==null) {
            this.arraylistTemplates=new ArrayList<>();
        }
    }
    public void set_arraylistTemplates(ArrayList<String> arraylistTemplates){
        this.arraylistTemplates=arraylistTemplates;
    }
    public ArrayList<String> get_arraylistTemplates(){return arraylistTemplates;}

    //String, that equals the selected template
    private String selectedTemplate=null;
    public void set_selectedTemplate(String template){this.selectedTemplate=template;}
    public String get_selectedTemplate(){
        String output=selectedTemplate;
        return output;
    }

    //Radius
    private int radius=100;
    public int get_radius(){return radius;}

    //Coordinates
    private LatLng latlngDestination=null;
    public void set_latlngDestination(LatLng point){this.latlngDestination=point;}
    public LatLng get_latlngDestination() {
        return latlngDestination;
    }

    //Address
    private String address=null;
    public void set_address(String address){this.address=address;}
    public String get_address(){return address;}

////////////////////////////////////////////////////////////////////////////////////////////////////

    //ADD TO STRING FILE

    //NOTIFICATION
    public static final String CHANNEL_ID="JourneyChannel";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //rename NAME
            NotificationChannel servicechannel = new NotificationChannel(CHANNEL_ID,"Journey Channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(servicechannel);
        }
    }
}
