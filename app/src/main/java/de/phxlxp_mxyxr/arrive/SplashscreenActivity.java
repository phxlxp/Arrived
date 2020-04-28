package de.phxlxp_mxyxr.arrive;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.ContactsContract;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;

public class SplashscreenActivity extends AppCompatActivity {

    PowerManager powermanger;
    private final int STORAGE_PERMISSION_CODE=1;
    private boolean contactpermission=false;
    private boolean locationpermission=false;
    private boolean smspermission=false;
    private boolean batterypermission=false;
    private boolean privacy=false;
    private final int SPLASH_TIME_OUT=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        //hides the ActionBar
        getSupportActionBar().hide();

        privacy();

        if(privacy){
            //checks permission to read contacts and asks user for permission if necessary
            if (ContextCompat.checkSelfPermission(SplashscreenActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashscreenActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, STORAGE_PERMISSION_CODE);
            } else {
                this.contactpermission=true;
            }
            //checks permission to gain the users location
            if (ContextCompat.checkSelfPermission(SplashscreenActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || (ContextCompat.checkSelfPermission(SplashscreenActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
                ActivityCompat.requestPermissions(SplashscreenActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, STORAGE_PERMISSION_CODE);
                ActivityCompat.requestPermissions(SplashscreenActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, STORAGE_PERMISSION_CODE);
            } else {
                this.locationpermission=true;
            }
            //checks permission to send text messages
            if (ContextCompat.checkSelfPermission(SplashscreenActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SplashscreenActivity.this, new String[]{Manifest.permission.SEND_SMS}, STORAGE_PERMISSION_CODE);
            } else {
                this.smspermission=true;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(contactpermission && locationpermission && smspermission){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                powermanger=(PowerManager)getSystemService(POWER_SERVICE);
                if(!powermanger.isIgnoringBatteryOptimizations(getPackageName())){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:"+getPackageName()));
                    startActivity(intent);
                }
                else{
                    this.batterypermission=true;
                }
            }
            else{
                this.batterypermission=true;
            }

            if(batterypermission){
                //checks if "Journey" is running
                if(!isRunning()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //sets arraylistContacts as a global variable
                            Globals global=(Globals)getApplication();
                            global.set_arraylistContacts(get_contacts());

                            //loads shared preferences and imports the saved templates
                            global.start_arraylistTemplates();

                            //starts MainActivity and finishes SplashscreenActivity
                            Intent intent=new Intent(SplashscreenActivity.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    },SPLASH_TIME_OUT);
                }

                else{
                    Intent intent=new Intent(this,JourneyActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_PERMISSION_CODE:
                if (grantResults.length>0)
                {
                    Intent intent=new Intent(this,SplashscreenActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    //imports contacts into an ArrayList
    public ArrayList<Contact> get_contacts(){
        ArrayList<Contact> arraylistContacts=new ArrayList();

        //get the contacts
        Cursor cursorContacts=null;
        ContentResolver contentResolver=getContentResolver();
        try{
            cursorContacts=contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        }
        catch(Exception ex){
            Log.e("Error on contact!",ex.getMessage());
        }

        //check contacts
        if(cursorContacts.getCount()>0){
            while(cursorContacts.moveToNext()){
                Contact contact=new Contact();
                String contact_id=cursorContacts.getString(cursorContacts.getColumnIndex(ContactsContract.Contacts._ID));
                String contact_name=cursorContacts.getString(cursorContacts.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                contact.setContact_name(contact_name);
                contact.setContact_id(Integer.parseInt(contact_id));

                int hasphonenumber=Integer.parseInt(cursorContacts.getString(cursorContacts.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if(hasphonenumber>0){
                    Cursor cursorPhone=contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                            , null
                            , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?"
                            , new String[]{contact_id}
                            , null);
                    ArrayList<String> phonenumbers=new ArrayList();
                    while(cursorPhone.moveToNext()){
                        String contact_number=removeSpaces(cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                        if(contact_number.equals("")==false){
                            if(phonenumbers.contains(contact_number)==false){
                                phonenumbers.add(contact_number);
                            }
                        }
                    }
                    if(phonenumbers.size()!=0){
                        for(int index=0;index<phonenumbers.size();index++){
                            if(contact.getContact_number().length()==0){
                                contact.setContact_number(phonenumbers.get(index));
                            }
                            else{
                                contact.setContact_number(contact.getContact_number()+"\n"+phonenumbers.get(index));
                            }
                        }
                    }
                    cursorPhone.close();
                }
                if(contact.getContact_number().equals("")==false){
                    arraylistContacts=insert_contact(arraylistContacts,contact);
                }
            }
        }
        return arraylistContacts;
    }

    //Functions, used to sort the contacts
    public ArrayList<Contact> insert_contact(ArrayList<Contact> arraylist,Contact contact){
        if(arraylist.size()==0){
            arraylist.add(contact);
            return arraylist;
        }
        for(int index=0;index<arraylist.size();index++){

            if(placeHere(contact.getContact_name(),arraylist.get(index).getContact_name())==true){
                arraylist.add(index,contact);
                return arraylist;
            }
        }
        arraylist.add(contact);
        return arraylist;
    }
    public boolean placeHere(String name1,String name2){
        if(name1.length()>name2.length()){
            for(int index=0;index<name2.length();index++){
                if(checkLetters(name1.substring(index,index+1),name2.substring(index,index+1))==0){
                    return true;
                }
                else if(checkLetters(name1.substring(index,index+1),name2.substring(index,index+1))==1){
                    return false;
                }
            }
            return false;
        }
        else{
            for(int index=0;index<name1.length();index++){
                if(checkLetters(name1.substring(index,index+1),name2.substring(index,index+1))==0){
                    return true;
                }
                else if(checkLetters(name1.substring(index,index+1),name2.substring(index,index+1))==1){
                    return false;
                }
            }
            return true;
        }
    }
    public int checkLetters(String letter1,String letter2){
        String small="aäbcdefghijklmnoöpqrstuüvwxyz";
        String large="AÄBCDEFGHIJKLMNOÖPQRSTUÜVWXYZ";
        String numbers="0123456789";

        if(letter1.equals(letter2)){
            return 2;
        }
        else if(numbers.contains(letter1)){
            if(numbers.contains(letter2)){
                if(numbers.indexOf(letter1)<numbers.indexOf(letter2)){
                    return 0;
                }
                else{
                    return 1;
                }
            }
            else{
                return 0;
            }
        }
        else if(numbers.contains(letter2)){
            return 1;
        }
        else if(small.contains(letter1)){
            if(small.contains(letter2)){
                if(small.indexOf(letter1)<small.indexOf(letter2)){
                    return 0;
                }
                else if(small.indexOf(letter1)>small.indexOf(letter2)){
                    return 1;
                }
                else{
                    return 2;
                }
            }
            else if(large.contains(letter2)){
                if(small.indexOf(letter1)<large.indexOf(letter2)){
                    return 0;
                }
                else if(small.indexOf(letter1)>large.indexOf(letter2)){
                    return 1;
                }
                else{
                    return 2;
                }
            }
        }
        else if(large.contains(letter1)){
            if(small.contains(letter2)){
                if(large.indexOf(letter1)<small.indexOf(letter2)){
                    return 0;
                }
                else if(large.indexOf(letter1)>small.indexOf(letter2)){
                    return 1;
                }
                else{
                    return 2;
                }
            }
            else if(large.contains(letter2)){
                if(large.indexOf(letter1)<large.indexOf(letter2)){
                    return 0;
                }
                else if(large.indexOf(letter1)>large.indexOf(letter2)){
                    return 1;
                }
                else{
                    return 2;
                }
            }
        }
        else if(letter2.equals(" ")){
            return 1;
        }

        return 0;
    }
    public String removeSpaces(String phonenumber){
        String output="";
        for(int index=0;index<phonenumber.length();index++){
            if(!phonenumber.substring(index,index+1).equals(" ") && !phonenumber.substring(index,index+1).equals("-")){
                output=output+phonenumber.substring(index,index+1);
            }
        }
        return output;
    }

    //checks if "Journey" is running
    public boolean isRunning(){
        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        return sharedpreferences.getBoolean("running",false);
    }

    public void privacy(){
        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        if(!sharedpreferences.getBoolean("privacy",false)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view=getLayoutInflater().inflate(R.layout.dialog_privacy,null);
            final CheckBox checkbox=view.findViewById(R.id.checkboxPrivacy);
            builder.setView(view);
            builder.setPositiveButton(R.string.privacy_policy_button, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if(checkbox.isChecked()){
                        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedpreferences.edit();
                        editor.putBoolean("privacy",true);
                        editor.apply();
                    }
                    else{
                        Toast.makeText(SplashscreenActivity.this,R.string.privacy_statement_toast,Toast.LENGTH_LONG).show();
                    }
                    Intent intent=new Intent(SplashscreenActivity.this,SplashscreenActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setCancelable(false);
            builder.create().show();
        }
        else{
            this.privacy=true;
        }
    }
}
