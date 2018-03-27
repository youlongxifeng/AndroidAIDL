package com.aidl.youlong.project.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.aidl.youlong.project.aidl.Book;
import com.aidl.youlong.project.aidl.IBookManager;
import com.aidl.youlong.project.aidl.IOnNewBookArrivedListener;
import com.aidl.youlong.project.aidl.ListenerManagerImpl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Administrator
 * @name AndroidAIDL
 * @class name：com.aidl.youlong.project.service
 * @class describe
 * @time 2018/3/27 9:55
 * @change
 * @class describe
 */

public class RemoteService extends Service {
    CopyOnWriteArrayList<Book> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
    /**
     * 用于存储远程监听Listener的集合
     */
    RemoteCallbackList<IOnNewBookArrivedListener> remoteCallbackList = new RemoteCallbackList<>();
    ListenerManagerImpl mListenerManager;
    /**
     * 自定义权限
     */
    private static final String PERMISSION = "com.aidl.youlong.project.service.remoteservice";
    /**
     * 返回Binder
     * <p>
     * 这里进行了权限验证，如果没有声明自定义的权限就拿不到Binder
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        int value = checkCallingOrSelfPermission(PERMISSION);
        if (value == PackageManager.PERMISSION_DENIED) {
            return null;
        }
        return remote;
    }



    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("result", "onUnbind");
        return super.onUnbind(intent);
    }
    private Binder remote = new IBookManager.Stub() {
        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            //如果没有声明自定义的权限，就在调用远程方法的时候直接返回false
            int value = checkCallingOrSelfPermission(PERMISSION);
            if (value == PackageManager.PERMISSION_DENIED) {
                return false;
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mListenerManager.addBook(book);
            Log.i("TAG","bookList==addBook="+book);
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mListenerManager.getBookList();
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerManager.registerListener(listener);
        }

        @Override
        public void unRegisterListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerManager.unRegisterListener(listener);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mListenerManager=new ListenerManagerImpl();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}
