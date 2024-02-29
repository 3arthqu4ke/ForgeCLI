package net.kunmc.lab.forgecli;

import net.kunmc.lab.forgecli.impl.ClientInstall;
import net.kunmc.lab.forgecli.impl.LegacyClientInstall;
import net.minecraftforge.installer.SimpleInstaller;
import net.minecraftforge.installer.actions.ActionCanceledException;
import net.minecraftforge.installer.actions.ProgressCallback;
import net.minecraftforge.installer.json.Install;
import net.minecraftforge.installer.json.Util;

import java.io.File;
import java.lang.reflect.Method;

public class InstallerUtil {
    public static boolean runClientInstall(ProgressCallback monitor, File target, File installerJar) {
        try {
            boolean v1;
            try {
                Class.forName("net.minecraftforge.installer.json.InstallV1");
                v1 = true;
            } catch (ClassNotFoundException e) {
                v1 = false;
            }

            SimpleInstaller.headless = true;

            if (v1) {
                Install profile = ClientInstall.loadInstallProfile();
                return new ClientInstall(profile, monitor).run(target, input -> true, installerJar);
            } else {
                Install profile = LegacyClientInstall.loadInstallProfile();
                return new LegacyClientInstall(profile, monitor).run(target, input -> true);
            }
        } catch (ActionCanceledException e) {
            throw new RuntimeException(e);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return tryNew2_2Install(monitor, target, installerJar);
        }
    }

    /**
     * There is a new 2.2. version of the Installer since November 2023
     */
    private static boolean tryNew2_2Install(ProgressCallback monitor, File target, File installerJar) {
        try {
            //noinspection JavaReflectionMemberAccess
            Method method = net.minecraftforge.installer.actions.ClientInstall.class.getDeclaredMethod("run", File.class, File.class);
            method.setAccessible(true);
            net.minecraftforge.installer.actions.ClientInstall clientInstall = new net.minecraftforge.installer.actions.ClientInstall(Util.loadInstallProfile(), monitor);
            return (boolean) method.invoke(clientInstall, target, installerJar);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unknown forge installer version.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
