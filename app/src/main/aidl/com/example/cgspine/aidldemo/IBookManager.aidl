// IBookManager.aidl
package com.example.cgspine.aidldemo;

import com.example.cgspine.aidldemo.model.Book;

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
}
