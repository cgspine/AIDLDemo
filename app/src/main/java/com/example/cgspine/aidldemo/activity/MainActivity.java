package com.example.cgspine.aidldemo.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cgspine.aidldemo.IBookManager;
import com.example.cgspine.aidldemo.R;
import com.example.cgspine.aidldemo.model.Book;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private IBookManager mBookManager;
    private MyAdaptor mBookListAdapter;


    @Bind(R.id.book_list) ListView mBookList;
    @Bind(R.id.book_num) EditText mBookNumEditText;
    @Bind(R.id.book_name) EditText mBookNameEditText;
    @Bind(R.id.book_submit) Button mBookSubmitBtn;

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG,"onServiceConnected");
            mBookManager = IBookManager.Stub.asInterface(service);
            mBookListAdapter.setBookList();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG,"onServiceDisconnected");
            mBookManager = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "bindService");
        Intent intent = new Intent();
        intent.setAction("com.example.cgspine.aidldemo.bookservice");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mBookListAdapter = new MyAdaptor();
        mBookList.setAdapter(mBookListAdapter);

        mBookSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String numStr = mBookNumEditText.getText().toString();
                String nameStr = mBookNameEditText.getText().toString();
                if(numStr == "" || nameStr == ""){
                    Toast.makeText(MainActivity.this,"编号和书名不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                int num;
                try {
                    num = Integer.parseInt(numStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this,"编号必须为数字",Toast.LENGTH_SHORT).show();
                    return;
                }
                Book book = new Book(num,nameStr);
                try {
                    mBookManager.addBook(book);
                    mBookListAdapter.setBookList();
                } catch (RemoteException e) {
                    Toast.makeText(MainActivity.this,"添加过程出现错误",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConn);
        super.onDestroy();
    }

    class MyAdaptor extends BaseAdapter{
        List<Book> mBookList = new ArrayList<>();
        @Override
        public int getCount() {
            return mBookList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBookList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.book_list_item,null);
                holder = new ViewHolder();
                holder.bookNum = (TextView) convertView.findViewById(R.id.book_item_num);
                holder.bookName = (TextView) convertView.findViewById(R.id.book_item_name);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            Book book = (Book) getItem(position);
            holder.bookNum.setText(""+book.getId());
            holder.bookName.setText(book.getName());
            return convertView;
        }

        public void setBookList() {
            try {
                mBookList = mBookManager.getBookList();
                notifyDataSetChanged();
            } catch (RemoteException e) {
                Toast.makeText(MainActivity.this,"获取书籍列表失败",Toast.LENGTH_SHORT).show();
            }
        }

        class ViewHolder{
            TextView bookNum;
            TextView bookName;
        }
    }

}
