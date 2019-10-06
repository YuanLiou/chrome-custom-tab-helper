package rayyuan.demo.chromecustomtabhelper

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.edit
import butterknife.BindView
import butterknife.ButterKnife
import liou.rayyuan.chromecustomtabhelper.Browsers
import liou.rayyuan.chromecustomtabhelper.ChromeCustomTabsHelper

/**
 * Created by louis383 on 2018/9/26.
 */
class MainActivity : AppCompatActivity(R.layout.activity_main) {

    @BindView(R.id.demo_spinner)
    protected lateinit var spinner: Spinner

    @BindView(R.id.demo_button)
    protected lateinit var button: Button

    private val KEY_DEFAULT_BROWSER = "selected-browser"
    private val KEY_DEFAULT_PREFERENCE = "customTabSample"
    private val SAMPLE_URL = "https://ebook.yuer.tw"
    private val chromeCustomTabsHelper = ChromeCustomTabsHelper()
    private val VALUE_BROWSER_NAME = mapOf(
        "CHROME" to Browsers.CHROME,
        "FIREFOX" to Browsers.FIREFOX,
        "SAMSUNG" to Browsers.SAMSUNG)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        Log.i("MainActivity", "prefer browser = ${getBrowserPreference().name}")
        val arrayAdapter = ArrayAdapter.createFromResource(this, R.array.browser_names, android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val browsers = if (position == 0) {
                    Browsers.CHROME
                } else if (position == 1) {
                    Browsers.FIREFOX
                } else {
                    Browsers.SAMSUNG
                }
                saveBrowserPreference(browsers)
                chromeCustomTabsHelper.bindCustomTabsServices(this@MainActivity, browsers, SAMPLE_URL)
            }
        }

        button.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                openWeb("https://podcasts.apple.com/tw/podcast/%E6%87%B7%E5%93%B2%E7%9A%84podcast/id1439751818")
            }
        })
    }

    private fun openWeb(url: String) {
        val customTabIntent = CustomTabsIntent.Builder().build()
        ChromeCustomTabsHelper.openCustomTab(this, getBrowserPreference(), customTabIntent, Uri.parse(url), object : ChromeCustomTabsHelper.Fallback {
            override fun openWithWebView(activity: Activity, uri: Uri) {
                Toast.makeText(activity, "Fallback", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        chromeCustomTabsHelper.bindCustomTabsServices(this, getBrowserPreference(), SAMPLE_URL)
    }

    override fun onPause() {
        super.onPause()
        chromeCustomTabsHelper.unbindCustomTabsServices(this)
    }

    private fun saveBrowserPreference(browser: Browsers) {
        val sharedPreferences = getSharedPreferences(KEY_DEFAULT_PREFERENCE, Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putString(KEY_DEFAULT_BROWSER, browser.name)
        }
    }

    private fun getBrowserPreference(): Browsers {
        val sharedPreferences = getSharedPreferences(KEY_DEFAULT_PREFERENCE, Context.MODE_PRIVATE)
        val selectedBrowser = sharedPreferences.getString(KEY_DEFAULT_BROWSER, "") ?: ""
        return if (selectedBrowser.isNotBlank()) {
            VALUE_BROWSER_NAME[selectedBrowser] ?: Browsers.CHROME
        } else {
            Browsers.CHROME
        }
    }
}
