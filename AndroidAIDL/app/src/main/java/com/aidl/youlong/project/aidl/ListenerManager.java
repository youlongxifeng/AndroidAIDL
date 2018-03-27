package com.aidl.youlong.project.aidl;

import java.util.List;

/**
 * @author Administrator
 * @name AndroidAIDL
 * @class name：com.aidl.youlong.project.aidl
 * @class describe
 * @time 2018/3/27 10:00
 * @change
 * @class describe
 */

public interface ListenerManager {


    void registerListener(IOnNewBookArrivedListener listener);

    void unRegisterListener(IOnNewBookArrivedListener listener);

    void onNewBookArrived(  Book book);
    List<Book> getBookList();
    void addBook(Book book);
}
