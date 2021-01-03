package ca.cmetcalfe.xposed.disablebatterywarnings;

import android.os.Build;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

/*
Links to relevant AOSP source code:

https://android.googlesource.com/platform/frameworks/base/+/jb-dev/packages/SystemUI/src/com/android/systemui/power/PowerUI.java
https://android.googlesource.com/platform/frameworks/base/+/kitkat-dev/packages/SystemUI/src/com/android/systemui/power/PowerUI.java
https://android.googlesource.com/platform/frameworks/base/+/lollipop-dev/packages/SystemUI/src/com/android/systemui/power/PowerNotificationWarnings.java
https://android.googlesource.com/platform/frameworks/base/+/marshmallow-dev/packages/SystemUI/src/com/android/systemui/power/PowerNotificationWarnings.java
https://android.googlesource.com/platform/frameworks/base/+/nougat-dev/packages/SystemUI/src/com/android/systemui/power/PowerNotificationWarnings.java
https://android.googlesource.com/platform/frameworks/base/+/oreo-dev/packages/SystemUI/src/com/android/systemui/power/PowerNotificationWarnings.java
*/


public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(LoadPackageParam loadPackageParam) throws Throwable {

        if (!loadPackageParam.packageName.equals("com.android.systemui"))
            return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ||
                Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            XposedBridge.log("The DisableBatteryWarnings Xposed module will only work on " +
                    "versions Jelly Bean to Oreo - exiting now");
            return;
        }

        try {
            applyHooks(loadPackageParam);
            XposedBridge.log("DisableBatteryWarnings hooks applied!");
        }
        catch (Throwable t){
            XposedBridge.log("DisableBatteryWarnings failed to apply hooks: " + t.toString());
        }
    }

    private void applyHooks(LoadPackageParam loadPackageParam) throws Throwable {
        // Jelly Bean, KitKat
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Class<?> powerUI = findClass("com.android.systemui.power.PowerUI",
                    loadPackageParam.classLoader);

            findAndHookMethod(powerUI, "playLowBatterySound", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return null;
                }
            });

            findAndHookMethod(powerUI, "showLowBatteryWarning", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                    return null;
                }
            });
        }
        // Lollipop, Marshmallow, Nougat, Oreo
        else {
            Class<?> powerNotificationWarnings = findClass("com.android.systemui.power.PowerNotificationWarnings",
                    loadPackageParam.classLoader);

            findAndHookMethod(powerNotificationWarnings, "updateNotification", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedHelpers.setBooleanField(param.thisObject, "mWarning", false);
                }
            });
        }
    }
}
