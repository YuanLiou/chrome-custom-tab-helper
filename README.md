# ChromeCustomTabHelper
Convenient helper class to launch web page through Google [Chrome Custom Tab](https://developer.chrome.com/multidevice/android/customtabs).

[ ![Download](https://api.bintray.com/packages/louis383/chrome-custom-tab-helper/chrome-custom-tab-helper/images/download.svg) ](https://bintray.com/louis383/chrome-custom-tab-helper/chrome-custom-tab-helper/_latestVersion)

### How to install

1. Add `jcenter()` to your the root `build.gradle` allprojects scope.
```groovy
allprojects {
    repositories {
        google()
        jcenter()    // <== add this one
    }
}
```
2. Add the dependency

#### Stable version  (Android X)
**minSdkVersion** is 21
```groovy
implementation "liou.rayyuan.chromecustomtabhelper:chrome-custom-tab-helper:1.1.1"
```

### How to use

1. Create a new object for `ChromeCustomTabHelper`
```kotlin
    private val chromeCustomTabHelper = ChromeCustomTabsHelper() 
```

2. Warm up the custom tab service in the `onResume()` state of Activity or Fragment <br/>
   And don't forget to cool down in the `onStop()` state.
   You can pick up your prefer browser in the second parameter.  <br/>
   Current support browsers: Chrome, Firefox and Samsung internet.
```kotlin
    override fun onResume() {
        super.onResume()
        chromeCustomTabHelper.bindCustomTabsServices(this, Browsers.CHROME, url)
    }

    override fun onStop() {
        super.onStop()
        chromeCustomTabHelper.unbindCustomTabsServices(this)
    }
```

3. When it need to open web tab.
```kotlin
    val builder = CustomTabsIntent.Builder()
    // here custom your tab styles and behavior
    val chromeCustomTabIntent = builder.build()
    ChromeCustomTabsHelper.openCustomTab(this, Browsers.CHROME, chromeCustomTabIntent, uri) { activity, uri ->
        // if chrome is not install, fallback to standard webview 
        // or just send an intent to someone can handle
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        startActivity(intent)
    }
```

That's it！









