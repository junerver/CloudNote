package com.junerver.cloudnote.observable;

import com.junerver.cloudnote.CloudNoteApp;
import com.junerver.cloudnote.db.NoteUtils;
import com.junerver.cloudnote.db.dao.NoteEntityDao;
import com.junerver.cloudnote.db.entity.Note;
import com.junerver.cloudnote.db.entity.NoteEntity;
import com.junerver.cloudnote.utils.NetUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Junerver on 2016/10/12.
 * 用于同步本地数据到bmob云
 * 两种情况，本地有云端无（基于isSync标记位），云端有本地无。
 * 2016.10.13 0:34
 *          —— 测试通过，可以将本地数据同步到云端
 *
 */
public class NotesSyncToBmobObservable {
    public static Observable sync() {
        return Observable.from(NoteUtils.listNotSync())
                .map(new Func1<NoteEntity, Note>() {
                    @Override
                    public Note call(final NoteEntity noteEntity) {
                        boolean isConnected = NetUtils.isConnected(CloudNoteApp.getContext());
                        Note note = new Note();
                        note.setTitle(noteEntity.getTitle());
                        note.setContent(noteEntity.getContent());
                        note.setSummary(noteEntity.getSummary());
                        note.setImage(noteEntity.getImage());
                        note.setVideo(noteEntity.getVideo());
                        if (isConnected) {
                            note.save(new SaveListener<String>() {
                                @Override
                                public void done(String objectId, BmobException e) {
                                    if (e == null) {
                                        //网络数据保存成功 保存objId 修改同步位
                                        noteEntity.setObjId(objectId);
                                        noteEntity.setSync(true);
                                    } else {
                                        noteEntity.setSync(false);  //同步不成功
                                        Logger.d("bmob保存失败：" + e.getMessage() + "," + e.getErrorCode());
                                    }
                                    //需要更新本地数据状态
                                    CloudNoteApp.getNoteEntityDao().update(noteEntity);
                                }
                            });
                        }
                        return note;
                    }
                })
                .subscribeOn(Schedulers.io())                     //io线程执行
                .observeOn(AndroidSchedulers.mainThread());      //主线程观察
    }
}
