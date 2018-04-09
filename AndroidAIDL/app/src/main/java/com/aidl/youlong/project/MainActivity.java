package com.aidl.youlong.project;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.aidl.youlong.project.aidl.Book;
import com.aidl.youlong.project.aidl.IBookManager;
import com.aidl.youlong.project.aidl.IOnNewBookArrivedListener;
import com.aidl.youlong.project.bean.Person;
import com.aidl.youlong.project.bean.Person.Student;
import com.aidl.youlong.project.service.RemoteService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private IBookManager iBookManager;
    List<Book> bookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Person person=new Person();
        Student student=new  Person().new Student();
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBookManager = IBookManager.Stub.asInterface(service);
            try {
                //通过linkToDeath，可以给Binder设置一个死亡代理，当Binder死亡时，就会收到通知
                service.linkToDeath(deathRecipient, 0);
                iBookManager.registerListener(iOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        /**
         * onServiceDisconnected()方法在正常情况下是不被调用的，它的调用时机是当Service服务被异外销毁时，例如内存的资源不足时。
         * 所以正常解绑 unbindService 并不会走这个方法
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBookManager = null;
            //TODO 在Service意外死亡之后会回调这个方法，可以在这里进行重新绑定

        }
    };
    /**
     * Binder死亡代理
     */
    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (iBookManager == null) {
                return;
            }
            iBookManager.asBinder().unlinkToDeath(deathRecipient, 0);
            iBookManager = null;
            // TODO 重新绑定
        }
    };

    /**
     * 监听
     */
    private IOnNewBookArrivedListener.Stub iOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            Log.i("TAG", "新书到了：" + book.toString());
        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            Log.i("TAG","bookList=="+bookList);
            return bookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            bookList.add(book);
            Log.i("TAG","bookList==book="+book);
        }
    };

    /**
     * 绑定启动远程服务
     */
    public void remote1(View view) {
        bindService(new Intent(this, RemoteService.class), serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 解绑启动远程服务
     */
    public void unbind1(View view) {
        try {
            if (iBookManager != null && iBookManager.asBinder().isBinderAlive()) {
                iBookManager.unRegisterListener(iOnNewBookArrivedListener);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(serviceConnection);
    }

    /**
     * 调用远程服务的书籍列表
     */
    public void getBookList(View view) {
        if (iBookManager == null)
            return;
        try {
            List<Book> bookList = iBookManager.getBookList();
            for (int i = 0; i < bookList.size(); i++) {
                Log.e("result", bookList.get(i).toString());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加书籍
     */
    public void addBook(View view) {
        if (iBookManager == null)
            return;
        try {
            iBookManager.addBook(new Book("中华上下五千年", 2));
            Log.e("result", "添加了Book");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (iBookManager != null && iBookManager.asBinder().isBinderAlive()) {
                iBookManager.unRegisterListener(iOnNewBookArrivedListener);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(serviceConnection);
    }
}
