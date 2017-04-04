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


public class Main implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(LoadPackageParam loadPackageParam) throws Throwable {

        if (!loadPackageParam.packageName.equals("com.android.systemui"))
            return;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN ||
                Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            XposedBridge.log("The DisableBatteryWarnings Xposed module will only work on " +
                    "Jelly Bean to Marshmallow - exiting now");
            return;
        }

        // Jelly Bean and KitKat
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Class<?> powerUI = findClass("com.android.systemui.power.PowerUI",
                    loadPackageParam.classLoader);

            findAndHookMethod(powerUI, "playLowBatterySound", new XC_MethodReplacement() {
                @Override
                protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
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
        // Lollipop and Marshmallow
        else {
            Class<?> powerNotificationWarnings = findClass("com.android.systemui.power.PowerNotificationWarnings",
                    loadPackageParam.classLoader);

            findAndHookMethod(powerNotificationWarnings, "updateNotification", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedHelpers.setBooleanField(param.thisObject, "mPlaySound", false);
                    XposedHelpers.setBooleanField(param.thisObject, "mWarning", false);
                }
            });
        }
    }
}
