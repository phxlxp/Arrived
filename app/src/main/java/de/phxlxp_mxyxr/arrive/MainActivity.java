package de.phxlxp_mxyxr.arrive;

//import packages
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//start of main-class
public class MainActivity extends AppCompatActivity implements SelectedContactAdapter.ItemClickListener{

    //gui-objects initialized
    private Button buttonSetDestination;
    private Button buttonContacts;
    private Button buttonLoadTemplate;
    private Button buttonSaveTemplate;
    private Button buttonStart;
    private TextView textviewDestination;
    private EditText edittextMessage;
    private RecyclerView recyclerview;
    private ActionBar actionbar;

    //other variables
    private LinearLayoutManager horizontallayoutmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Actionbar
        actionbar=getSupportActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setLogo(R.drawable.toolbar_icon);
        actionbar.setDisplayUseLogoEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        //SetDestination-Button
        buttonSetDestination=(Button)findViewById(R.id.button_setdestination);
        buttonSetDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapsActivity();
            }
        });

        //Contacts-Button
        buttonContacts=(Button)findViewById(R.id.button_contacts);
        buttonContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openContactsActivity();
            }
        });

        //LoadTemplates-Button
        buttonLoadTemplate=(Button)findViewById(R.id.button_loadtemplate);
        buttonLoadTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {openTemplatesActivity();
            }
        });

        //SaveTemplate-Button
        buttonSaveTemplate=(Button)findViewById(R.id.button_savetemplate);
        buttonSaveTemplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edittextMessage=(EditText)findViewById(R.id.edittextMessage);
                String message=edittextMessage.getText().toString();
                if(checkHasContent(message)){
                    saveTemplate(message);
                    Toast.makeText(MainActivity.this,R.string.template_saved_message,Toast.LENGTH_LONG).show();
                }
            }
        });

        //Start-Button
        buttonStart=(Button)findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if contacts, destination and message exists
                //run travel-mode + send sms
                openJourneyActivity();
            }
        });

        checkManufacturer();
    }

    @Override
    public void onResume() {
        super.onResume();
        displayDestination();
        displaySelectedContacts();
        setLayout();
        setTemplate();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Globals global=(Globals)getApplication();
        edittextMessage=(EditText)findViewById(R.id.edittextMessage);
        global.set_selectedTemplate(edittextMessage.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Globals global=(Globals)getApplication();
        edittextMessage=(EditText)findViewById(R.id.edittextMessage);
        global.set_selectedTemplate(edittextMessage.getText().toString());
    }

    //ClickHandler RecyclerView
    @Override
    public void onItemClick(View view, int position) {
        Globals global=(Globals)getApplication();
        Toast.makeText(this,R.string.toast_contact_removed, Toast.LENGTH_SHORT).show();
        global.removeFrom_arraylistSelectedContacts(position);
        displaySelectedContacts();
    }

    //START NEW ACTIVITY
    //start "ContactsActivity"
    private void openContactsActivity(){
        Globals global=(Globals)getApplication();
        global.set_selectedTemplate(edittextMessage.getText().toString());
        Intent intent=new Intent(this,ContactsActivity.class);
        startActivity(intent);
    }

    //start "TemplatesActivity"
    private void openTemplatesActivity(){
        Globals global=(Globals)getApplication();
        global.set_selectedTemplate(edittextMessage.getText().toString());
        Intent intent=new Intent(this, TemplatesActivity.class);
        startActivity(intent);
    }

    //start "MapsActivity"
    private void openMapsActivity(){
        Globals global=(Globals)getApplication();
        global.set_selectedTemplate(edittextMessage.getText().toString());
        Intent intent=new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    //Start "JourneyActivity"
    private void openJourneyActivity(){
        Globals global=(Globals)getApplication();
        global.set_selectedTemplate(edittextMessage.getText().toString());

        if(global.get_latlngDestination()==null){
            Toast.makeText(MainActivity.this,R.string.you_haven_t_set_a_destination_yet,Toast.LENGTH_LONG).show();
        }

        else if(global.get_arraylistSelectedContacts().size()==0){
            Toast.makeText(MainActivity.this,R.string.you_haven_t_selected_any_contacts_yet,Toast.LENGTH_LONG).show();
        }

        else if(!checkHasContent(edittextMessage.getText().toString())){
            Toast.makeText(MainActivity.this,R.string.you_haven_t_entered_a_message_yet,Toast.LENGTH_LONG).show();
        }

        else{
            SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
            if(!sharedpreferences.getBoolean("smswarning",false)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View view=getLayoutInflater().inflate(R.layout.dialog_sms_warning,null);
                final CheckBox checkbox=view.findViewById(R.id.checkboxSMSWarning);
                builder.setView(view);
                builder.setMessage(R.string.sms_warning_dialog_text)
                        .setPositiveButton(R.string.sms_warning_dialog_understood, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(checkbox.isChecked()){
                                    SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedpreferences.edit();
                                    editor.putBoolean("smswarning",true);
                                    editor.apply();
                                }
                                Intent intent=new Intent(MainActivity.this,JourneyActivity.class);
                                startService();
                                startActivity(intent);
                                finish();
                            }
                        });
                builder.create().show();
            }
            else{
                Intent intent=new Intent(this,JourneyActivity.class);
                startService();
                startActivity(intent);
                finish();
            }
        }
    }

    //OTHER FUNCTIONS AND METHODS
    //displays destination
    private void displayDestination(){
        Globals global=(Globals)getApplication();
        textviewDestination=findViewById(R.id.textview_destination);
        if(global.get_latlngDestination()!=null){
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses= null;
            try {
                addresses = geocoder.getFromLocation(global.get_latlngDestination().latitude,global.get_latlngDestination().longitude,1);
                String address=addresses.get(0).getAddressLine(0);
                textviewDestination.setText(address);
                global.set_address(address);
            } catch (IOException e) {
                textviewDestination.setText(R.string.unknown_destination);
                global.set_address(getString(R.string.unknown_destination));
            }
        }
        else{
            textviewDestination.setText(null);
        };
    }

    //displays global variable "arraylistSelectedContacts" in RecyclerView
    private void displaySelectedContacts(){
        recyclerview=(RecyclerView)findViewById(R.id.recyclerview_contacts);
        horizontallayoutmanager=new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
        recyclerview.setLayoutManager(horizontallayoutmanager);
        recyclerview.setClickable(true);
        Globals global=(Globals)getApplication();
        SelectedContactAdapter adapter=new SelectedContactAdapter(this,global.get_arraylistSelectedContacts());
        adapter.setClickListener(this);
        recyclerview.setAdapter(adapter);
    }

    //save text in "shared preferences" and update global variable "arraylistTemplates"
    private void saveTemplate(String newTemplate){
        Globals global=(Globals)getApplication();
        ArrayList<String> templates=global.get_arraylistTemplates();
        templates.add(newTemplate);
        global.set_arraylistTemplates(templates);
        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(templates);
        editor.putString("templates",json);
        editor.apply();
    }

    //set text in EditText to value of global variable "selectedTemplate"
    private void setTemplate(){
        Globals global=(Globals)getApplication();
        edittextMessage=(EditText)findViewById(R.id.edittextMessage);
        edittextMessage.setText(global.get_selectedTemplate());
    }

    //checks if a string has content
    public boolean checkHasContent(String text){
        if(text.length()==0 ||text==null){
            return false;
        }
        for(int index=0;index<text.length();index++){
            if(!text.substring(index,index+1).equals(" ")){
                return true;
            }
        }
        return false;
    }

    public void setLayout(){
        Globals global=(Globals)getApplication();
        textviewDestination=findViewById(R.id.textview_destination);
        recyclerview=findViewById(R.id.recyclerview_contacts);
        if(global.get_address()==null){
            textviewDestination.setVisibility(View.GONE);
        }
        else{
            textviewDestination.setVisibility(View.VISIBLE);
        }
        if(global.get_arraylistSelectedContacts().size()==0){
            recyclerview.setVisibility(View.GONE);
        }
        else{
            recyclerview.setVisibility(View.VISIBLE);
        }
    }

    public void startService(){
        Intent serviceintent=new Intent(this,LocationService.class);
        ContextCompat.startForegroundService(this,serviceintent);
    }

    public void checkManufacturer(){
        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        if(!sharedpreferences.getBoolean("backgroundpermission",false)){
            String manufacturer=Build.MANUFACTURER;

            //HUAWEI
            if(manufacturer.equals("HUAWEI") || manufacturer.equals("huawei") || manufacturer.equals("Huawei")){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.manufacturer_dialog_huawei)
                        .setPositiveButton(R.string.manufacturer_dialog_positive_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent=new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                                SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedpreferences.edit();
                                editor.putBoolean("backgroundpermission",true);
                                editor.apply();
                            }
                        })
                        .setNegativeButton(R.string.manufacturer_dialog_negative_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                builder.create().show();
            }

            //Honor
            else if(manufacturer.equals("HONOR") || manufacturer.equals("honor") || manufacturer.equals("Honor")){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.manufacturer_dialog_honor)
                        .setPositiveButton(R.string.manufacturer_dialog_positive_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent=new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                                SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedpreferences.edit();
                                editor.putBoolean("backgroundpermission",true);
                                editor.apply();
                            }
                        })
                        .setNegativeButton(R.string.manufacturer_dialog_negative_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                builder.create().show();
            }

            //Samsung
            else if(manufacturer.equals("SAMSUNG") || manufacturer.equals("samsung") || manufacturer.equals("Samsung")){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.manufacturer_dialog_samsung)
                        .setPositiveButton(R.string.manufacturer_dialog_positive_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent=new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                                SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedpreferences.edit();
                                editor.putBoolean("backgroundpermission",true);
                                editor.apply();
                            }
                        })
                        .setNegativeButton(R.string.manufacturer_dialog_negative_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                builder.create().show();
            }

            //Different Phone
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.manufacturer_dialog_other)
                        .setPositiveButton(R.string.manufacturer_dialog_positive_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent=new Intent(Settings.ACTION_SETTINGS);
                                startActivity(intent);
                                SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
                                SharedPreferences.Editor editor=sharedpreferences.edit();
                                editor.putBoolean("backgroundpermission",true);
                                editor.apply();
                            }
                        })
                        .setNegativeButton(R.string.manufacturer_dialog_negative_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                builder.create().show();
            }
        }
    }
}
