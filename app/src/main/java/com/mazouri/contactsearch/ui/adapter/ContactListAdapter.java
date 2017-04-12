package com.mazouri.contactsearch.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.mazouri.contactsearch.helper.QuickContactHelper;
import com.mazouri.contactsearch.R;
import com.mazouri.contactsearch.model.ContactBean;

import java.util.ArrayList;

/**
 * Created by wangdongdong on 17-4-12.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ItemVH> {

    private ArrayList<ContactBean> contactList;
    private Context context;

    public ContactListAdapter(Context context, ArrayList<ContactBean> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    public ArrayList<ContactBean> getContactList() {
        return contactList;
    }

    @Override
    public ContactListAdapter.ItemVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact_item, parent, false);
        return new ContactListAdapter.ItemVH(view);
    }

    @Override
    public void onBindViewHolder(ContactListAdapter.ItemVH holder, int position) {
        ContactBean contactBean = contactList.get(position);
        String name = contactBean.getDesplayName();
        String number = contactBean.getPhoneNum();
        holder.name.setText(name);
        holder.number.setText(number);

        holder.quickContactBadge.assignContactUri(ContactsContract.Contacts.getLookupUri(
                contactBean.getContactId(), contactBean.getLookUpKey()));

        if (0 == contactBean.getPhotoId()) {
            Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(R.mipmap.ic_launcher)).getBitmap();
            holder.quickContactBadge.setImageBitmap(QuickContactHelper.frameBitmapInCircle(bitmap));
        } else {
            QuickContactHelper.addThumbnail(context, holder.quickContactBadge, number, true);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ItemVH extends RecyclerView.ViewHolder {
        QuickContactBadge quickContactBadge;
        TextView name;
        TextView number;

        public ItemVH(View itemView) {
            super(itemView);
            quickContactBadge = (QuickContactBadge) itemView.findViewById(R.id.quickContactBadge);
            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);

            QuickContactHelper.removeOverlay(quickContactBadge);
        }
    }
}
