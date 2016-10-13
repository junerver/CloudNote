# 云笔记
> 这是一个模仿云笔记的项目，使用Bmob云后端提供诸如注册、登录、数据保存等服务，项目中联系使用了RxJava来处理异步任务，Logger作为调试日志工具，ORM框架使用GreenDAO。


##功能：

- 实现了登录、注册、保持登录。
- 实现了笔记的增删改查。
- 基于Bmob后端云，笔记可以在云端与本地之间同步。
- 使用RxJava处理诸如数据库操作、数据同步等异步任务。
- 使用GreenDAO处理数据的本地持久化操作。

---

##预览：

<br/>
<img 
    src="./Screenshot_1.png" 
    width = "180" 
    height = "320" 
    alt="Preview 1" 
    align=center />
<img 
    src="./Screenshot_2.png" 
    width = "180" 
    height = "320" 
    alt="Preview 1" 
    align=center />
<img 
    src="./Screenshot_3.png" 
    width = "180" 
    height = "320" 
    alt="Preview 1" 
    align=center />

---

##更新日志：

- 16/10/13：彻底修复了本地与云端的同步问题
- 16/10/12：完善了RxJava处理笔记保存、笔记同步到bmob的逻辑
- 16/10/11：简单实现了添加视频、图片的功能
- 16/09/20：增加了查看、编辑、删除等功能
- 16/09/10：测试了新增bmob实例
- 16/09/06：使用自定义view来实现tab渐变切换，完成了ViewPager+Fragment+TabView的切换，在Fragment中接入了LRecyclerView
- 16/09/02：测试了bmob与greendao的配合
- 16/09/01：接入bmob后端云，完成注册登录逻辑
- 16/08/31：完成了部分ui的设计


##依赖列表：

	//一些支持库
	compile 'com.android.support:appcompat-v7:24.2.0'
    compile 'com.android.support:design:24.2.0'
    compile 'com.android.support:recyclerview-v7:24.2.0'
    compile 'com.android.support:cardview-v7:24.2.0'
	compile 'com.android.support:support-v4:24.2.0'
	//一个好用的RecycleView封装
    compile 'com.github.jdsjlzx:LRecyclerView:1.0.9'
	//view注入框架
    compile 'com.jakewharton:butterknife:8.4.0'
	//ORM框架
    compile 'org.greenrobot:greendao:3.0.1'
	//日志
    compile 'com.orhanobut:logger:1.15'
	//实际没用使用的EventBus
    compile 'org.greenrobot:eventbus:3.0.0'
	//bmob后端云
    compile 'cn.bmob.android:bmob-sdk:3.5.0'
    