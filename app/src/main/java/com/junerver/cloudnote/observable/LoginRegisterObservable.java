package com.junerver.cloudnote.observable;

import com.google.gson.Gson;
import com.junerver.cloudnote.Constants;
import com.junerver.cloudnote.db.entity.HttpResult;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.IOException;

import okhttp3.Response;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Junerver on 2016/9/1.
 */
public class LoginRegisterObservable {
    /**
     * @param baseUrl  登录或者注册的url
     * @param username 参数用户名
     * @param password 参数密码
     * @return
     */
    public static Observable<String> getObservable(final String baseUrl, final String username, final String password) {
        return Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        //明文get并不是一个好方法，此处为了简单
                        String url = baseUrl + "/?username=" + username + "&password=" + password;
                        try {
                            Response response = OkHttpUtils
                                    .get()
                                    .url(url)
                                    .build()
                                    .execute();
                            Gson gson = new Gson();
                            HttpResult<String> result = gson.fromJson(response.body().string(), HttpResult.class);

                            subscriber.onNext(result.getResultCode() + "");
                            subscriber.onCompleted();

                        } catch (IOException e) {
                            e.printStackTrace();
                            subscriber.onError(e);
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
