# FullLineTextView
避免文字中出现英文或符号导致的未撑满一行的情况下的换行

---

### Usage
```xml
<com.altair441n.fulllinetextview.FullLineTextView
    android:id="@+id/tv_content"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@android:color/holo_green_light"
    android:lineSpacingExtra="5dp"
    android:text="@string/content"
    android:textColor="@color/black"
    android:textSize="15sp"
    app:expandableMaxLines="2"
    app:lastLineRightPadding="50dp" />
```
    
![yKwpad.png](https://s3.ax1x.com/2021/02/03/yKwpad.png)&nbsp;&nbsp;&nbsp; ![yKw9IA.png](https://s3.ax1x.com/2021/02/03/yKw9IA.png)

---

### Dependency

Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
    
Add the dependency
```groovy
dependencies {
    implementation 'com.github.altair441n:FullLineTextView:1.1'
}
```