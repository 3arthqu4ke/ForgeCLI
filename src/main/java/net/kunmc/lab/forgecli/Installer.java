package net.kunmc.lab.forgecli;

import net.kunmc.lab.forgecli.pre1_13.ClientInstallPre1_13;
import net.minecraftforge.installer.actions.ProgressCallback;

import java.io.File;

public class Installer {
    @SuppressWarnings("unused")
    public static boolean install(File target, File installerJar) {
        try {
            if (ClientInstallPre1_13.hasPre1_13_Install()) {
                ClientInstallPre1_13.install(target);
                return true;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            if (Boolean.parseBoolean(System.getProperty("forgecli.retry",
                                                        "false"))) {
                return false;
            }
        }

        return installPost1_13(target, installerJar);
    }

    private static boolean installPost1_13(File target, File installerJar) {
        ProgressCallback monitor = ProgressCallback.withOutputs(System.out);
        if (System.getProperty("java.net.preferIPv4Stack") == null) {
            System.setProperty("java.net.preferIPv4Stack", "true");
        }
        String vendor = System.getProperty("java.vendor", "missing vendor");
        String javaVersion = System.getProperty("java.version",
                                                "missing java version");
        String jvmVersion = System.getProperty("java.vm.version",
                                               "missing jvm version");
        monitor.message(
            String.format("JVM info: %s - %s - %s", vendor, javaVersion,
                          jvmVersion));
        monitor.message("java.net.preferIPv4Stack=" + System.getProperty(
            "java.net.preferIPv4Stack"));
        return InstallerUtil.runClientInstall(monitor, target, installerJar);
    }

}
