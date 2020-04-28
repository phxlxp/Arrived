package de.phxlxp_mxyxr.arrive;

//represents a contact
public class Contact {
    private String contact_name="";
    private String contact_number="";
    private int contact_id=0;

    //constructor
    public Contact(){}

    public Contact(String contact_name){
        this.contact_name=contact_name;
    }

    //set-methods
    public void setContact_name(String contact_name){this.contact_name=contact_name;}
    public void setContact_number(String contact_number){this.contact_number=contact_number;}
    public void setContact_id(int contact_id){this.contact_id=contact_id;}

    //get-functions
    public String getContact_name(){return contact_name;}
    public String getContact_number(){return contact_number;}
    public int getContact_id(){return contact_id;}
}
