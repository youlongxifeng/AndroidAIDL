package com.aidl.youlong.project.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
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
        public void join(IBinder token, String name) throws RemoteException {
            CustomerClient cl = new CustomerClient(token, name);
            mClientsList.add(cl); 
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
    //这个list 就是用来存储当前餐厅有多少顾客 注意我们为什么没有用顾客的名字来存储？
    //而是用了这个CustomerClient的类 看这个类的注释即可明白
    private List<CustomerClient> mClientsList = new ArrayList<>();
   //http://developer.android.com/intl/zh-cn/reference/android/os/Binder.html#linkToDeath(android.os.IBinder.DeathRecipient, int)
    //实际上 这个接口 就是用来 当客户端自己发生崩溃时， 我们的服务端也能收到这个崩溃的消息
    //并且会调用binderDied 这个回调方法，所以你看这个内部类的代码 就明白了 无非就是保证当客户端异常销毁的时候
    //我们服务端也要保证收到这个消息 然后做出相应的应对
    final class CustomerClient implements IBinder.DeathRecipient {

        public final IBinder mToken;

        public CustomerClient(IBinder mToken, String mCustomerName) {
            this.mToken = mToken;
            this.mCustomerName = mCustomerName;
        }

        public final String mCustomerName;

        @Override
        public void binderDied() {//粘结剂死亡
            //我们的应对方法就是当客户端 也就是顾客异常消失的时候 我们要把这个list里面 的对象也移出掉
            if (mClientsList.indexOf(this) >= 0) {
                mClientsList.remove(this);
            }

        }
    }
}
