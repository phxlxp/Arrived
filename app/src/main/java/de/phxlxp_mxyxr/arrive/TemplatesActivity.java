package de.phxlxp_mxyxr.arrive;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import java.util.ArrayList;

public class TemplatesActivity extends AppCompatActivity {

    private ListView listviewTemplates;
    private ActionBar actionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates);

        actionbar=getSupportActionBar();
        actionbar.setDisplayShowHomeEnabled(true);
        actionbar.setDisplayShowTitleEnabled(false);
        //actionbar.setLogo(R.drawable.toolbar_icon);
        //actionbar.setDisplayUseLogoEnabled(true);

        displayTemplates();

        //ListView-Setup
        listviewTemplates=(ListView)findViewById(R.id.listview_templates);
        listviewTemplates.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listviewTemplates.isLongClickable();
        listviewTemplates.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                shortClick(i);
            }
        });
        listviewTemplates.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                longClick(i);
                return true;
            }
        });
    }

    //selects a template and changes global variables
    private void shortClick(int i){
        Globals global=(Globals)getApplication();
        global.set_selectedTemplate(global.get_arraylistTemplates().get(i));
        finish();
    }

    //deletes a template and changes global variables
    private void longClick(int i){
        Globals global=(Globals)getApplication();
        ArrayList<String> currentlist=global.get_arraylistTemplates();
        currentlist.remove(i);
        global.set_arraylistTemplates(currentlist);
        saveTemplate();
        displayTemplates();
        Toast.makeText(TemplatesActivity.this,R.string.template_deleted_message,Toast.LENGTH_LONG).show();
    }

    //displays the ArrayList that contains the saved templates
    private void displayTemplates(){
        Globals global=(Globals)getApplication();
        ArrayList<String> arraylistTemplates=global.get_arraylistTemplates();
        ListView listviewTemplates=(ListView)findViewById(R.id.listview_templates);
        ArrayAdapter<String> adapter=new ArrayAdapter(this,R.layout.listview_templates_item,R.id.textview_template,arraylistTemplates);
        listviewTemplates.setAdapter(adapter);
    }

    //saves the ArrayList which contains the templates in shared preferences and updates global variables
    private void saveTemplate(){
        Globals global=(Globals)getApplication();
        ArrayList<String> templates=global.get_arraylistTemplates();
        SharedPreferences sharedpreferences=getSharedPreferences("sharedpreferences",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedpreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(templates);
        editor.putString("templates",json);
        editor.apply();
    }
}
