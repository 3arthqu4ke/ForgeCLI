package net.kunmc.lab.forgecli;

import net.kunmc.lab.forgecli.pre1_13.ClientInstallPre1_13;
import net.kunmc.lab.forgecli.pre1_13.Pre1_13Transformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * {@link URLClassLoader} which can transform {@link ClientInstallPre1_13#CLASS_NAME}
 * via the {@link Pre1_13Transformer}.
 *
 * @author 3arthqu4ke
 */
public class InstallerClassLoader extends URLClassLoader {
    public InstallerClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (ClientInstallPre1_13.CLASS_NAME.equals(name)) {
            String path = name.replace('.', '/').concat(".class");
            try (InputStream is = this.getResourceAsStream(path)) {
                if (is == null) {
                    throw new ClassNotFoundException(name);
                }

                System.err.println("Transforming " + name);

                ClassReader cr = new ClassReader(is);
                ClassNode cn = new ClassNode();
                cr.accept(cn, 0);

                new Pre1_13Transformer().transform(cn);

                ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES) {
                    @Override
                    protected ClassLoader getClassLoader() {
                        return InstallerClassLoader.this;
                    }
                };

                cn.accept(cw);
                byte[] clazzBytes = cw.toByteArray();
                return this.defineClass(name, clazzBytes, 0, clazzBytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name, e);
            }
        }

        return super.findClass(name);
    }

}
