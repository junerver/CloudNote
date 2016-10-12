package com.junerver.cloudnote.observable;

import com.junerver.cloudnote.db.NoteUtils;
import com.junerver.cloudnote.db.entity.NoteEntity;
import com.orhanobut.logger.Logger;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by junerver on 2016/9/10.
 */
public class NotesFromDatabaseObservable {
    public static Observable<List<NoteEntity>> ofDate() {
        return Observable.create(new Observable.OnSubscribe<List<NoteEntity>>() {
            @Override
            public void call(Subscriber<? super List<NoteEntity>> subscriber) {
                List<NoteEntity> noteEntities = NoteUtils.list();
                if (noteEntities != null && noteEntities.size() != 0) {
                    Logger.d("数据库中有数据！向配适器传送");
                    subscriber.onNext(noteEntities);
                }

                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())                     //io线程执行
                .observeOn(AndroidSchedulers.mainThread());      //主线程观察
    }
}
