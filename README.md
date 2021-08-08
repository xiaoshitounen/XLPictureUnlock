# XLPictureUnlock 手势解锁

详细内容博客地址:

[自定义View-XLPictureUnlock](http://www.fanandjiu.com/article/b4a475cc.html)

[Android项目10：手势解锁](http://www.fanandjiu.com/article/29270e87.html)


简介：

简单的手势解锁封装。

app模块是使用例子，其运行效果：

![](https://android-1300729795.cos.ap-chengdu.myqcloud.com/project/Self_View/XLPictureUnlock/XLPictureUnlock.gif)

### 1. 添加依赖

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
~~~
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
~~~

Step 2. Add the dependency
~~~
dependencies {
    implementation 'com.github.xiaoshitounen:XLPictureUnlock:1.0.0'
}
~~~

### 2. Xml文件中静态添加使用

~~~xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:background="@drawable/bg">

    <TextView
        android:id="@+id/text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="请输入密码"
        android:textSize="30sp"
        android:textColor="#ffffff"
        android:textAlignment="center"
        android:layout_marginTop="80dp"
        />

    <Button
        android:id="@+id/btn"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@null"
        android:text="忘记密码？"
        android:textColor="#ff99cc"
        android:layout_centerHorizontal="true"
        android:layout_below="@id/text"
        />

    <RelativeLayout
        android:id="@+id/layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_below="@+id/text"
        >
        <swu.xl.pictureunlock_draw.XLPictureUnlock
            android:id="@+id/unlock"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:can_select_again="true"
            app:dot_size="65"
            app:line_width="8"
            />
    </RelativeLayout>

</RelativeLayout>
~~~

~~~java
//XLPictureUnlock
XLPictureUnlock pictureUnlock = findViewById(R.id.unlock);
//找到添加点的布局
RelativeLayout layout = findViewById(R.id.layout);
//添加点---不管什么方式初始化，此方法必须执行
pictureUnlock.addDotView(layout);
//监听密码
pictureUnlock.setCallBackPasswordListener(new XLPictureUnlock.CallBackPasswordListener() {
    @Override
    public void picturePassword(String pwd) {
        //回调密码
    }
});
~~~

#### ① 属性

- normal_image_id：正常状态的图片资源
- select_image_id：选中状态的图片资源
- dot_size：点的大小
- line_color：线条的颜色
- line_width：线条的大小
- can_select_again：选中的点是否能够再次选择


#### ② 回调密码

回调的`currentPage`是页面的索引值而不是序列值。

~~~java
//XLPictureUnlock
XLPictureUnlock pictureUnlock = findViewById(R.id.unlock);
//找到添加点的布局
RelativeLayout layout = findViewById(R.id.layout);
//添加点
pictureUnlock.addDotView(layout);
//监听密码
pictureUnlock.setCallBackPasswordListener(new XLPictureUnlock.CallBackPasswordListener() {
    @Override
    public void picturePassword(String pwd) {
        //回调密码
    }
});
~~~

### 3. Java代码中动态添加

本人没有测试，理论上也可以。
