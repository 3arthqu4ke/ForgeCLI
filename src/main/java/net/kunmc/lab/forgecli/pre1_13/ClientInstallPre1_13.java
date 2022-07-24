package net.kunmc.lab.forgecli.pre1_13;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * net.minecraftforge.installer.ClientInstall is used by Installers before 1.13
 *
 * @author 3arthqu4ke
 */
public class ClientInstallPre1_13 {
    public static final String CLASS_NAME =
        "net.minecraftforge.installer.ClientInstall";

    public static void install(File target) throws Throwable {
        System.err.println("Installing using pre 1.13 installer!");
        Class<?> serverInstall = Class.forName("net.minecraftforge.installer.ServerInstall");
        // this makes DownloadUtils create a headless IMonitor
        Field headless = serverInstall.getField("headless");
        headless.set(null, true);

        Class<?> clientInstall = Class.forName(CLASS_NAME);
        Method install = clientInstall.getMethod("run", File.class, Predicates.getPredicateClass());
        Object instance = clientInstall.getConstructor().newInstance();
        install.invoke(instance, target, Predicates.getPredicate());
    }

    public static boolean hasPre1_13_Install() {
        try {
            Class.forName(CLASS_NAME);
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }

}
