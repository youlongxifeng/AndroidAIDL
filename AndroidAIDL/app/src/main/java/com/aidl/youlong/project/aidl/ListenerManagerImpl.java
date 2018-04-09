package com.aidl.youlong.project.aidl;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

/**
 * @author Administrator
 * @name AndroidAIDL
 * @class name：com.aidl.youlong.project.aidl
 * @class describe
 * @time 2018/3/27 9:57
 * @change
 * @class describe
 */

public class ListenerManagerImpl implements ListenerManager {
    private RemoteCallbackList<IOnNewBookArrivedListener> mOnPlayChangedListener;

    public ListenerManagerImpl() {
        mOnPlayChangedListener = new RemoteCallbackList<>();
    }


    @Override
    public void registerListener(IOnNewBookArrivedListener listener) {
        mOnPlayChangedListener.register(listener);
    }

    @Override
    public void unRegisterListener(IOnNewBookArrivedListener listener) {
        mOnPlayChangedListener.unregister(listener);
    }

    @Override
    public void onNewBookArrived(Book book) {
        int count = 0;
        List<Book> bookList = null;
        try {
            count = mOnPlayChangedListener.beginBroadcast();
        } catch (Exception e) {
            Log.i("TAG", "E-=" + e);
        }
        try {
            for (int i = 0; i < count; i++) {
                mOnPlayChangedListener.getBroadcastItem(i).onNewBookArrived(book);
                ;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            if (count > 0) {
                mOnPlayChangedListener.finishBroadcast();
            }
        }
    }

    @Override
    public List<Book> getBookList() {
        int count = 0;
        List<Book> bookList = null;
        try {
            count = mOnPlayChangedListener.beginBroadcast();
        } catch (Exception e) {
            Log.i("TAG", "E-=" + e);
        }
        try {
            for (int i = 0; i < count; i++) {
                bookList = mOnPlayChangedListener.getBroadcastItem(i).getBookList();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            if (count > 0) {
                mOnPlayChangedListener.finishBroadcast();
            }
        }
        return bookList;
    }

    @Override
    public void addBook(Book book) {
        int count = 0;

        try {
            count = mOnPlayChangedListener.beginBroadcast();
        } catch (Exception e) {
            Log.i("TAG", "E-=" + e);
        }
        try {
            for (int i = 0; i < count; i++) {
                mOnPlayChangedListener.getBroadcastItem(i).addBook(book);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            if (count > 0) {
                mOnPlayChangedListener.finishBroadcast();
            }
        }
    }
}
