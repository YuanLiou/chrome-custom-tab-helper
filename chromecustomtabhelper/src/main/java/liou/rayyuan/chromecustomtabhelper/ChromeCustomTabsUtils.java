package liou.rayyuan.chromecustomtabhelper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.browser.customtabs.CustomTabsService;

/**
 * Created by louis383 on 2017/1/20.
 */

public class ChromeCustomTabsUtils {

    private static final String CHROME_STABLE_PACKAGE = "com.android.chrome";
    private static final String CHROME_BETA_PACKAGE = "com.chrome.beta";
    private static final String CHROME_DEV_PACKAGE = "com.chrome.dev";
    private static final String CHROME_CANARY_PACKAGE = "com.chrome.canary";
    private static final String CHROME_LOCAL_PACKAGE = "com.google.android.apps.chrome";

    private static final String FIREFOX_STABLE_PACKAGE = "org.mozilla.firefox";
    private static final String FIREFOX_BETA_PACKAGE = "org.mozilla.firefox_beta";
    private static final String FIREFOX_AURORA_PACKAGE = "org.mozilla.fennec_aurora";

    private static final String SAMSUNG_STABLE_PACKAGE = "com.sec.android.app.sbrowser";
    private static final String SAMSUNG_BETA_PACKAGE = "com.sec.android.app.sbrowser.beta";

    private static final String ACTION_CUSTOM_TABS_CONNECTION = CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION;

    public static String getPackageNameToUse(Context context, Browsers browsers, String urlString) {
        String packageNameToUse;
        PackageManager packageManager = context.getPackageManager();
        // get default VIEW intent handler
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
        ResolveInfo defaultViewHandlerInfo = packageManager.resolveActivity(intent, 0);
        String defaultViewHandlerPackageName = null;
        if (defaultViewHandlerInfo != null) {
            defaultViewHandlerPackageName = defaultViewHandlerInfo.activityInfo.packageName;
        }

        // get all apps that can handle VIEW intent
        List<ResolveInfo> resolvedActivityList = packageManager.queryIntentActivities(intent, 0);
        List<String> packagesSupportingCustomTabs = new ArrayList<>();
        for (ResolveInfo resolveInfo : resolvedActivityList) {
            Intent serviceIntent = new Intent();
            serviceIntent.setAction(ACTION_CUSTOM_TABS_CONNECTION);
            serviceIntent.setPackage(resolveInfo.activityInfo.packageName);
            if (packageManager.resolveService(serviceIntent, 0) != null) {
                packagesSupportingCustomTabs.add(resolveInfo.activityInfo.packageName);
            }
        }

        // packagesSupportingCustomTabs contains all apps that can handle both VIEW intent
        // and service call
        if (packagesSupportingCustomTabs.isEmpty()) {
            return null;
        }

        if (packagesSupportingCustomTabs.size() == 1) {
            packageNameToUse = packagesSupportingCustomTabs.get(0);
        } else if (!TextUtils.isEmpty(defaultViewHandlerPackageName) &&
                !hasSpecializeHandlerIntent(context, intent) &&
                packagesSupportingCustomTabs.contains(defaultViewHandlerPackageName)) {
            packageNameToUse = defaultViewHandlerPackageName;
        } else {
            packageNameToUse = "";
            Map<Browsers, List<String>> browserPackageNames = getBrowsersPackageName();
            List<String> packageNames = browserPackageNames.get(browsers);
            if (packageNames != null) {
                for (String packageName : packageNames) {
                    if (packagesSupportingCustomTabs.contains(packageName)) {
                        packageNameToUse = packageName;
                        break;
                    }
                }
            }
        }

        return packageNameToUse;
    }

    private static boolean hasSpecializeHandlerIntent(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> handlers = packageManager.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
        if (handlers == null || handlers.size() == 0) {
            return false;
        }

        for (ResolveInfo info : handlers) {
            IntentFilter filter = info.filter;
            if (filter == null) {
                continue;
            }

            if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) {
                continue;
            }

            if (info.activityInfo == null) {
                continue;
            }

            return true;
        }

        return false;
    }

    private static Map<Browsers, List<String>> getBrowsersPackageName() {
        Map<Browsers, List<String>> results = new HashMap<>();
        List<String> chromePackages = new ArrayList<>();
        chromePackages.add(CHROME_STABLE_PACKAGE);
        chromePackages.add(CHROME_BETA_PACKAGE);
        chromePackages.add(CHROME_DEV_PACKAGE);
        chromePackages.add(CHROME_CANARY_PACKAGE);
        chromePackages.add(CHROME_LOCAL_PACKAGE);
        results.put(Browsers.CHROME, chromePackages);

        List<String> firefoxPackages = new ArrayList<>();
        firefoxPackages.add(FIREFOX_STABLE_PACKAGE);
        firefoxPackages.add(FIREFOX_BETA_PACKAGE);
        firefoxPackages.add(FIREFOX_AURORA_PACKAGE);
        results.put(Browsers.FIREFOX, firefoxPackages);

        List<String> samsungPackages = new ArrayList<>();
        samsungPackages.add(SAMSUNG_STABLE_PACKAGE);
        samsungPackages.add(SAMSUNG_BETA_PACKAGE);
        results.put(Browsers.SAMSUNG, samsungPackages);

        return results;
    }
}
