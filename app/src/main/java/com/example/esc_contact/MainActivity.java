package com.example.esc_contact;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Contact> datalist = new ArrayList<>();
    private ArrayList<Contact> filteredList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Adapter adapter;
    private EditText search;
    private Button add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add = findViewById(R.id.add);

        SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ArrayList<Contact> mDataset = new ArrayList<>();
                datalist = new ArrayList<>();
                datalist.clear();
                datalist = getContactList();
                adapter.notifyDataSetChanged();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
                startActivity(intent);
            }
        });

        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
                datalist = new ArrayList<>();
                datalist.clear();
                datalist = getContactList();
                adapter = new Adapter(MainActivity.this, datalist);
                recyclerView = findViewById(R.id.main_recy);
                search = findViewById(R.id.search_tv);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(adapter);

                search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        String searchText = search.getText().toString();
                        searchFilter(searchText);

                    }
                });

            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한 거부", Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("주소록을 띄우고 전화를 걸기 위해서는 주소록과 전화 접근 권한이 필요합니다.")
                .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있습니다.")
                .setPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
                .check();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {

            ArrayList<Contact> mDataset = new ArrayList<>();
            datalist = new ArrayList<>();
            datalist.clear();
            datalist = getContactList();

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ArrayList<Contact> mDataset = new ArrayList<>();
        datalist = new ArrayList<>();
        datalist.clear();
        datalist = getContactList();

    }


    public ArrayList<Contact> getContactList() {

        LinkedHashSet<Contact> hasList = new LinkedHashSet<>();
        ArrayList<Contact> contactsList;

        hasList.clear();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        Cursor cursor = getContentResolver().query(uri, projection, null, null, sortOrder);


        if (cursor.moveToFirst()) {
            do {
                Contact myContact = new Contact();
                myContact.phoneNumber = cursor.getString(0);
                myContact.name = cursor.getString(1);

                if (myContact.phoneNumber.startsWith("01")) {
                    hasList.add(myContact);
                }

            } while (cursor.moveToNext());
        }

        contactsList = new ArrayList<>(hasList);

        if (cursor != null) {
            cursor.close();
        }

        return contactsList;

    }

    public void searchFilter (String searchText){
        filteredList.clear();

        for (int i = 0; i < datalist.size(); i++) {

            if (datalist.get(i).getName().toLowerCase().contains(searchText.toLowerCase())
                    || datalist.get(i).getPhoneNumber().contains(searchText)) {
                filteredList.add(datalist.get(i));
            }
        }

        adapter.filterList(filteredList);
        recyclerView.setAdapter(adapter);
    }

}