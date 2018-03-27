// IBookManager.aidl
package com.aidl.youlong.project.aidl;

// Declare any non-default types here with import statements
import com.aidl.youlong.project.aidl.Book;
import com.aidl.youlong.project.aidl.IOnNewBookArrivedListener;
interface IBookManager {
    void addBook(in Book book);
    List<Book> getBookList();
    void registerListener(IOnNewBookArrivedListener listener);
    void unRegisterListener(IOnNewBookArrivedListener listener);
}
