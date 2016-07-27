package vav.cyberspace.viettel.vva.listview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import vav.cyberspace.viettel.vva.R;
import vav.cyberspace.viettel.vva.contacts.ContactItem;

/**
 * Created by thanhtn10 on 5/27/16.
 */
public class ContactAdapter extends ArrayAdapter<ContactItem> {
    private final Context context;
    private final ArrayList<ContactItem> mContactList = new ArrayList<>();
    private int mSelectedItem = -1;
    public ContactAdapter(Context context, ArrayList<ContactItem> m) {
        super(context, R.layout.contact_item, m);
        this.context = context;
        this.mContactList.clear();
        this.mContactList.addAll(m);
    }
    public void setmSelectedItem(int m){
        mSelectedItem = m;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.contact_item, parent, false);
        TextView txtContactName = (TextView) rowView.findViewById(R.id.contactnameid);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.iconcontactid);
        txtContactName.setText(mContactList.get(position).getmContactName());

      /*  TextView txtPhoneNumber = (TextView) rowView.findViewById(R.id.phonenumberid);
        txtPhoneNumber.setText(mContactList.get(position).getmPhoneNumber());*/
        imageView.setImageResource(R.drawable.contact);

        if(position == mSelectedItem){
            rowView.setBackgroundColor(Color.parseColor("#FF946005"));
            imageView.setBackgroundColor(Color.parseColor("#FF946005"));
            txtContactName.setBackgroundColor(Color.parseColor("#FF946005"));
        }
        return rowView;
    }
}
