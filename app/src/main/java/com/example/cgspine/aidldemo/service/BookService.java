package com.example.cgspine.aidldemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.cgspine.aidldemo.IBookManager;
import com.example.cgspine.aidldemo.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cgspine on 15/11/9.
 */
public class BookService extends Service {

    private static final String TAG = "BookService";

    List<Book> mBookList = new ArrayList<>();

    private final IBookManager.Stub mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
           return mBookList;

        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        mBookList.add(new Book(0, "《javascript权威指南》"));
        mBookList.add(new Book(1,"《javascript高级程序设计》"));
        mBookList.add(new Book(2,"《javascript框架设计》"));
        mBookList.add(new Book(3,"《javascript忍着秘籍》"));
        mBookList.add(new Book(4, "《javascript设计模式》"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG,"onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
    }
}
