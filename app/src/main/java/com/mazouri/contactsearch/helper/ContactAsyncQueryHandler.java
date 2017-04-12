package com.mazouri.contactsearch.helper;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;

import com.mazouri.contactsearch.model.ContactBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wangdongdong on 17-4-12.
 */

public class ContactAsyncQueryHandler extends AsyncQueryHandler {
    private static final String TAG = ContactAsyncQueryHandler.class.getSimpleName();

    private Map<Integer, ContactBean> contactIdMap = null;
    private ArrayList<ContactBean> list;
    private OnQueryListener mOnQueryListener;

    public ContactAsyncQueryHandler(ContentResolver cr, OnQueryListener listener) {
        super(cr);
        this.mOnQueryListener = listener;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        Log.d(TAG, "onQueryComplete called token :" + token + ", cookie :" + cookie + ",cursor :" +cursor);

        if (cursor != null && cursor.getCount() > 0) {
            contactIdMap = new HashMap<Integer, ContactBean>();
            list = new ArrayList<ContactBean>();
            cursor.moveToFirst(); // 游标移动到第一项
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                String name = cursor.getString(1);
                String number = cursor.getString(2);
                String sortKey = cursor.getString(3);
                int contactId = cursor.getInt(4);
                Long photoId = cursor.getLong(5);
                String lookUpKey = cursor.getString(6);

                if (contactIdMap.containsKey(contactId)) {
                    // 无操作
                } else {
                    // 创建联系人对象
                    ContactBean contact = new ContactBean();
                    contact.setDesplayName(name);
                    contact.setPhoneNum(number);
                    contact.setSortKey(sortKey);
                    contact.setPhotoId(photoId);
                    contact.setLookUpKey(lookUpKey);
                    list.add(contact);

                    contactIdMap.put(contactId, contact);
                }
            }
            if (list.size() > 0) {
                Log.d(TAG, "onQueryComplete list size :" +list.size());
                if (mOnQueryListener != null) {
                    mOnQueryListener.onQueryComplete(list);
                }

                for (ContactBean contactBean : list) {
                    Log.d(TAG, "onQueryComplete list item :" +contactBean.getDesplayName() + " --> " + contactBean.getPhoneNum());
                }
            }
        }

        super.onQueryComplete(token, cookie, cursor);
    }

    public interface OnQueryListener {
        void onQueryComplete(ArrayList<ContactBean> contactList);
    }
}
