package de.phxlxp_mxyxr.arrive;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JourneyActivity extends AppCompatActivity {

    private EditText edittextDestination;
    private EditText edittextMessage;
    private EditText edittextContatcs;
    private Button buttonCancel;
    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        actionbar=getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setLogo(R.drawable.toolbar_icon);
        actionbar.setDisplayUseLogoEnabled(true);

        if(isRunning()){
            loadSharedPreferences();
        }
        else{
            saveSharedPreferences();
        }

        fillEditTexts();

        buttonCancel=(Button)findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    //BackButton pressed
    @Override
    public void onBackPressed() {
        cancel();
    }

    //HomeButton pressed
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        finish();
    }

    //stop "LocationService"
    public void stopService(){
        Intent serviceintent=new Intent(this,LocationService.class);
        stopService(serviceintent);
    }

    //check if "Journey" is running
    public boolean isRunning(){
        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        return sharedpreferences.getBoolean("running",false);
    }

    //load SharedPreferences
    public void loadSharedPreferences(){
        Globals global=(Globals)getApplication();
        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);

        //Load address
        global.set_address(sharedpreferences.getString("address",null));

        //Load LatLng
        Gson gsonLatlng = new Gson();
        String jsonLatlng = sharedpreferences.getString("latlng", null);
        Type typeLatlng = new TypeToken<ArrayList<Double>>() {}.getType();
        ArrayList<Double> latlng=gsonLatlng.fromJson(jsonLatlng,typeLatlng);
        global.set_latlngDestination(new LatLng(latlng.get(0),latlng.get((1))));

        //Load message
        global.set_selectedTemplate(sharedpreferences.getString("message",null));

        //Load ArrayList<Contact>
        Gson gsonContacts = new Gson();
        String jsonContacts = sharedpreferences.getString("contacts", null);
        Type typeContacts = new TypeToken<ArrayList<Contact>>() {}.getType();
        ArrayList<Contact> arraylistContacts=gsonContacts.fromJson(jsonContacts,typeContacts);
        global.set_arraylistSelectedContacts(arraylistContacts);

    }

    //save SharedPreferences
    public void saveSharedPreferences(){
        Globals global=(Globals)getApplication();

        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();

        ArrayList<Double> latlng=new ArrayList();
        latlng.add(global.get_latlngDestination().latitude);
        latlng.add(global.get_latlngDestination().longitude);
        Gson gsonLatlng = new Gson();
        String jsonLatlng = gsonLatlng.toJson(latlng);

        Gson gsonContacts = new Gson();
        String jsonContacts = gsonContacts.toJson(global.get_arraylistSelectedContacts());

        editor.putString("address",global.get_address());
        editor.putString("latlng",jsonLatlng);
        editor.putString("message",global.get_selectedTemplate());
        editor.putString("contacts",jsonContacts);
        editor.putBoolean("running",true);

        editor.apply();
    }

    //fills the EditText-Fields
    public void fillEditTexts(){
        Globals global=(Globals)getApplication();

        edittextDestination=findViewById(R.id.edittext_yourdestination);
        edittextDestination.setText(global.get_address());
        edittextDestination.setClickable(false);
        edittextDestination.setFocusable(false);
        edittextDestination.setFocusableInTouchMode(false);
        edittextDestination.setKeyListener(null);

        edittextMessage=findViewById(R.id.edittext_yourmessage);
        edittextMessage.setText(global.get_selectedTemplate());
        edittextMessage.setClickable(false);
        edittextMessage.setFocusable(false);
        edittextMessage.setFocusableInTouchMode(false);
        edittextMessage.setKeyListener(null);

        edittextContatcs=findViewById(R.id.edittext_yourcontacts);
        edittextContatcs.setText(returnContacts());
        edittextContatcs.setClickable(false);
        edittextContatcs.setFocusable(false);
        edittextContatcs.setFocusableInTouchMode(false);
        edittextContatcs.setKeyListener(null);
    }

    //transforms ArrayList<Contact> to String
    public String returnContacts(){
        Globals global=(Globals)getApplication();
        String output="";

        for(int index=0;index<global.get_arraylistSelectedContacts().size();index++){
            output=output+global.get_arraylistSelectedContacts().get(index).getContact_name()+" - "+global.get_arraylistSelectedContacts().get(index).getContact_number();
            if(index!=global.get_arraylistSelectedContacts().size()-1){
                output=output+"\n";
            }
        }

        return output;
    }

    //Dialog einfügen
    public void cancel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.cancel_dialog_question)
                .setPositiveButton(R.string.cancel_dialog_positive_button, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Globals global=(Globals)getApplication();
                        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedpreferences.edit();

                        stopService();

                        editor.remove("address");
                        editor.remove("latlng");
                        editor.remove("message");
                        editor.remove("contacts");
                        editor.remove("running");
                        editor.apply();

                        if(global.get_arraylistContacts().size()==0){
                            Intent intent=new Intent(JourneyActivity.this,SplashscreenActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Intent intent=new Intent(JourneyActivity.this,MainActivity.class);
                            startActivity(intent);
                        }

                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel_dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }
}
