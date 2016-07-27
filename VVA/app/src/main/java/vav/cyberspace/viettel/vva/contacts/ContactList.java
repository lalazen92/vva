package vav.cyberspace.viettel.vva.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;

import vav.cyberspace.viettel.vva.utils.utils;

/**
 * Created by thanhtn10 on 5/27/16.
 */
public class ContactList {
    static public ArrayList<ContactItem> mContactList = new ArrayList<>();

    public ArrayList<ContactItem> getSimilarContact(String contactname){

        ArrayList<ContactItem> result = new ArrayList<>();
        contactname = contactname.toLowerCase().trim();
        for (int i = 0; i < mContactList.size(); i++){
            String temp = mContactList.get(i).getmContactName().toLowerCase().trim();
            temp = utils.removeAccent(temp);
            if(contactname.compareToIgnoreCase(temp) == 0){
                result.clear();
                result.add(mContactList.get(i));
                break;
            }
            String []listcontactname = contactname.split(" ");
            for (int k = 0; k < listcontactname.length; k++){
                if(temp.compareToIgnoreCase(listcontactname[k]) == 0){
                    result.add(mContactList.get(i));
                    break;
                }
            }

        }
        return  result;
    }
    public String getContactNameFromNumber(String numberphone){

        numberphone = numberphone.toLowerCase().trim();
        for (int i = 0; i < mContactList.size(); i++){
            String temp = mContactList.get(i).getmPhoneNumber().toLowerCase().trim();
            temp = temp.replace("+84", "0");
            temp = temp.replaceAll(" ", "");
            if(temp.contains(numberphone))
            {
                return  mContactList.get(i).getmContactName();
            }

        }
        return  "";
    }

    static public void getContactList(Context context){
        mContactList.clear();
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        ContactItem item = new ContactItem();
                        item.setmContactName(name);
                        item.setmPhoneNumber(phoneNo);
                        mContactList.add(item);
                    }
                    pCur.close();
                }
            }
        }
    }
}
