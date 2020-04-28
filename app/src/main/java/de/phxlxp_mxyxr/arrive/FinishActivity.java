package de.phxlxp_mxyxr.arrive;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.ArrayList;

public class FinishActivity extends AppCompatActivity {

    private EditText edittextDestination;
    private EditText edittextMessage;
    private EditText edittextContatcs;
    private Button buttonReturn;
    private Button buttonClose;
    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        actionbar=getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setLogo(R.drawable.toolbar_icon);
        actionbar.setDisplayUseLogoEnabled(true);

        stopService();
        fillEditTexts();
        resetVariables();

        buttonReturn=(Button)findViewById(R.id.button_return);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        buttonClose=(Button)findViewById(R.id.button_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void stopService(){
        Intent serviceintent=new Intent(this,LocationService.class);
        stopService(serviceintent);
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

    public void resetVariables(){
        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();
        editor.remove("address");
        editor.remove("latlng");
        editor.remove("message");
        editor.remove("contacts");
        editor.remove("running");
        editor.apply();
    }

    public void openMainActivity(){
        Globals global=(Globals)getApplication();
        if(global.get_arraylistContacts().size()==0){
            Intent intent=new Intent(this,SplashscreenActivity.class);
            startActivity(intent);
        }
        else{
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }

        finish();
    }
}
