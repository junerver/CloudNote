package com.junerver.cloudnote.observable;


import com.junerver.cloudnote.CloudNoteApp;
import com.junerver.cloudnote.db.entity.Note;
import com.junerver.cloudnote.db.entity.NoteEntity;
import com.junerver.cloudnote.ui.activity.MainActivity;
import com.junerver.cloudnote.utils.NetUtils;
import com.orhanobut.logger.Logger;

import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Junerver on 2016/10/12.
 * 这个类用于将从activity传来的笔记数据存储到数据库与bmob
 * 判断网络是否连接，有网络的情况同时保存，无网络的情况只保存数据库
 * 处理完毕后像activity返回一个处理完毕的数据，用于向NoteDetailActivity提供更新视图的数据
 */
public class NotesSaveToDbAndBmobObservable {
    public static Observable<NoteEntity> save(final NoteEntity entity, final boolean isNew) {
        //传入一个笔记的实例，在Observable中再次封装，并决定需不需要上传至Bmob
        return Observable.create(new Observable.OnSubscribe<NoteEntity>() {
            /**
             * @param subscriber
             */
            @Override
            public void call(Subscriber<? super NoteEntity> subscriber) {
                //判断是否存在网络连接
                boolean isConnected = NetUtils.isConnected(CloudNoteApp.getContext());
                if (isNew) {
                    //为新数据设置id
                    entity.setId(new Date().getTime());
                    if (isConnected) {
                        //新数据且有网络
                        saveNewBmobByEntity(entity, isNew,subscriber);
                    } else {
                        entity.setSync(false);
                        CloudNoteApp.getNoteEntityDao().insert(entity);
                        subscriber.onNext(entity);
                        subscriber.onCompleted();
                    }
                } else {
                    if (isConnected) {
                        //旧数据且有网络，更新bmob对象
                        if (entity.getObjId() != null) {
                            //获取到这个旧数据的objectId
                            updateBmobByEntity(entity, entity.getObjId(), isNew,subscriber);
                        } else {
                            //这个旧数据并没有上传过
                            saveNewBmobByEntity(entity, isNew, subscriber);
                        }
                    } else {
                        entity.setSync(false);
                        CloudNoteApp.getNoteEntityDao().update(entity);
                        subscriber.onNext(entity);
                        subscriber.onCompleted();
                    }
                }
            }
        })
                .subscribeOn(Schedulers.io())                     //io线程执行
                .observeOn(AndroidSchedulers.mainThread());      //主线程观察
    }

    /**
     * 这个方法用于保存bmob云实例
     *
     * @param entity 数据实例
     * @param subscriber
     */
    private static void saveNewBmobByEntity(final NoteEntity entity, final boolean isNew, final Subscriber<? super NoteEntity> subscriber) {
        Note note = createNewBmobByEntity(entity);
        note.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    //网络数据保存成功 保存objId 修改同步位
                    entity.setObjId(objectId);
                    entity.setSync(true);
                } else {
                    entity.setSync(false);  //同步不成功
                    Logger.d("bmob保存失败：" + e.getMessage() + "," + e.getErrorCode());
                }
                //如果是新建的则需要插入，如果是编辑则需要更新
                if (isNew) {
                    CloudNoteApp.getNoteEntityDao().insert(entity);
                } else {
                    CloudNoteApp.getNoteEntityDao().update(entity);
                }
                subscriber.onNext(entity);
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 这个方法用于根据objectId来更新云实例
     *  @param entity   数据实例
     * @param objectId bmob用的objectId
     * @param subscriber
     */
    private static void updateBmobByEntity(final NoteEntity entity, String objectId, final boolean isNew, final Subscriber<? super NoteEntity> subscriber) {
        Note note = createNewBmobByEntity(entity);
        note.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //数据更新成功
                    entity.setSync(true);
                } else {
                    entity.setSync(false);  //同步不成功
                    Logger.d("bmob更新失败：" + e.getMessage() + "," + e.getErrorCode());
                }
                CloudNoteApp.getNoteEntityDao().update(entity);
                subscriber.onNext(entity);
                subscriber.onCompleted();
            }
        });
    }


    private static Note createNewBmobByEntity(NoteEntity entity) {
        //封装bmob实例
        Note note = entity.toBmob();
        note.setUserObjId(BmobUser.getCurrentUser().getUsername());
        return note;
    }
}
