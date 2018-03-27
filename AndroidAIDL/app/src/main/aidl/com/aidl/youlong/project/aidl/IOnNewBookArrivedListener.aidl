// IOnNewBookArrivedListener.aidl
package com.aidl.youlong.project.aidl;

// Declare any non-default types here with import statements
import com.aidl.youlong.project.aidl.Book;
import java.util.List;
interface IOnNewBookArrivedListener {
     void onNewBookArrived(in Book book);
     List<Book> getBookList();
      void addBook(in Book book);
}
