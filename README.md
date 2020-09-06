# LogView [ ![Download](https://api.bintray.com/packages/chanuklee0227/maven/logview-lib/images/download.svg) ](https://bintray.com/chanuklee0227/maven/logview-lib/_latestVersion)

## Description

I wanted to see logcat without a computer.

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
<com.vader87.view.LogView
        android:id="@+id/logview"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        .../>
```

Call logcatlayout in your activity.java:

```	java
import com.vader87.view.*;

protected void onCreate(Bundle savedInstanceState) {
  ...
  LogView logcatLayout = (LogView)findViewById(R.id.logview);
  ...
}
```

## Links

Here are a list of useful links:

 * If you have a problem check the [Issues Page](https://github.com/chanuklee/logview/issues) first to see if we are working on it
 * For further usage or to delve more deeply checkout the [Project Wiki](https://github.com/chanuklee/logview/wiki)
 * [Demo project](https://github.com/ChanUkLee/LogcatLayout-Demo)
