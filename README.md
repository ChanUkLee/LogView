# LogcatLayout [ ![Download](https://api.bintray.com/packages/chanuklee0227/maven/logcatlayout-lib/images/download.svg) ](https://bintray.com/chanuklee0227/maven/logcatlayout-lib/_latestVersion)

## Description

## Adding to project

Add these dependencies to the `build.gradle:project` of the module:

```groovy
repositories {
  jencter()
}
```

Add these dependencies to the `build.gradle:app` of the module:

```groovy
dependencies {
  implementation 'com.vader87.logcatlayout:logcatlayout-lib:{version}'
}
```

## Simple usage

Add logcatlayout in your activity.xml:

```xml
...
<com.vader87.locatlayout.LogcatLayout
        android:id="@+id/logcatLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        .../>
```

Call logcatlayout in your activity.java:

```	java
protected void onCreate(Bundle savedInstanceState) {
  ...
  com.vader87.locatlayout.LogcatLayout logcatLayout = (com.vader87.locatlayout.LogcatLayout)findViewById(R.id.logcatLayout);
  logcatLayout.d("MainActivity", "Debug");
  logcatLayout.w("MainActivity", "Warn");
  logcatLayout.e("MainActivity", "Error");
  ...
}
```

Using LogcatLayout.d(string:TAG, string:msg) instead of Log.d(string:TAG, string:msg)
Using LogcatLayout.w(string:TAG, string:msg) instead of Log.w(string:TAG, string:msg)
Using LogcatLayout.e(string:TAG, string:msg) instead of Log.e(string:TAG, string:msg)

## Links

Here are a list of useful links:

 * We always welcome people to contribute new features or bug fixes, [here is how](https://github.com/novoda/novoda/blob/master/CONTRIBUTING.md)
 * If you have a problem check the [Issues Page](https://github.com/chanuklee/logcatlayout/issues) first to see if we are working on it
 * For further usage or to delve more deeply checkout the [Project Wiki](https://github.com/chanuklee/logcatlayout/wiki)
 * [Demo project](https://github.com/ChanUkLee/LogcatLayout-Demo)
