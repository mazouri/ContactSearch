package com.mazouri.contactsearch;

import android.content.AsyncQueryHandler;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mazouri.contactsearch.helper.ContactAsyncQueryHandler;
import com.mazouri.contactsearch.helper.LocalContactSearch;
import com.mazouri.contactsearch.model.ContactBean;
import com.mazouri.contactsearch.ui.adapter.ContactListAdapter;
import com.mazouri.contactsearch.ui.adapter.SearchListAdapter;
import com.mazouri.contactsearch.ui.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final boolean USE_TEST_DATA = false;

    private ContactListAdapter myAdapter;
    private RecyclerView recyclerView;
    private RecyclerView autoRecyclerView;

    private AsyncQueryHandler asyncQueryHandler; // 异步查询数据库类对象
    private EditText editText;
    private SearchListAdapter mSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.edit);

        initialEdit();

        recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(getResources().getColor(R.color.divider)).build());

        initContacts();
    }

    private void initialEdit() {
        mSearchAdapter = new SearchListAdapter(this);
        autoRecyclerView = (RecyclerView) findViewById(R.id.auto_list);
        autoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        autoRecyclerView.setItemAnimator(new DefaultItemAnimator());
        autoRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).color(getResources().getColor(R.color.divider)).build());
        autoRecyclerView.setAdapter(mSearchAdapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() > 0) {
                    ArrayList<ContactBean> listG = LocalContactSearch.searchContact(s, myAdapter.getContactList());
                    if (listG != null && listG.size() > 0) {
                    } else {
                        Log.d(TAG, "搜索为空");
                    }
                    mSearchAdapter.updateContactList(listG);
                    setSearchListVisibility(true);
                } else {
                    setSearchListVisibility(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setSearchListVisibility(boolean isVisible) {
        if (isVisible) {
            autoRecyclerView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            autoRecyclerView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

    }

    private String[] testData = new String[]{"张三", "李四", "王二", "刘五", "武大", "孙七"};
    private String[] testDataNum = new String[]{"13611111111", "17733332222", "15844445555", "13566664444", "18700007777", "15213130000"};

    private void initContacts() {
        if (USE_TEST_DATA) {
            ArrayList<ContactBean> contactList = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                ContactBean contactBean = new ContactBean();
                contactBean.setDesplayName(testData[i]);
                contactBean.setPhoneNum(testDataNum[i]);
                contactBean.setPhotoId(0L);
                contactList.add(contactBean);
            }
            setAdapter(contactList);
            return;
        }

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人Uri；
        // 查询的字段
        String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY };

        asyncQueryHandler = new ContactAsyncQueryHandler(getContentResolver(), new ContactAsyncQueryHandler.OnQueryListener() {
            @Override
            public void onQueryComplete(ArrayList<ContactBean> contactList) {
                setAdapter(contactList);
            }
        });

        asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");
    }

    private void setAdapter(ArrayList<ContactBean> list) {
        myAdapter = new ContactListAdapter(this, list);
        recyclerView.setAdapter(myAdapter);
    }
}
