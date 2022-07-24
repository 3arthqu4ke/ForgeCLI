package net.kunmc.lab.forgecli.pre1_13;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.objectweb.asm.Opcodes.*;

/**
 * Quick and dirty, doesn't catch a bunch of edge cases which hopefully never
 * occur...
 * <p>
 * This redirects every call to {@link JOptionPane#showMessageDialog(Component,
 * Object, String, int)} which is used a lot by the {@link
 * ClientInstallPre1_13#CLASS_NAME} class.
 *
 * @author 3arthqu4ke
 */
public class Pre1_13Transformer {
    private final List<Runnable> methodCreators = new ArrayList<>();
    private int methodId;

    public void transform(ClassNode cn) {
        Map<String, String> methodsCreated = new HashMap<>();
        for (MethodNode mn : cn.methods) {
            AbstractInsnNode in = mn.instructions.getFirst();
            while (in != null) {
                if (in instanceof MethodInsnNode
                    && in.getOpcode() == INVOKESTATIC
                    && Type.getInternalName(JOptionPane.class)
                           .equals(((MethodInsnNode) in).owner)) {
                    MethodInsnNode min = (MethodInsnNode) in;
                    String name = methodsCreated.computeIfAbsent(
                        min.name + min.desc, v -> createMethod(cn, min));
                    mn.instructions.insertBefore(min, new MethodInsnNode(
                        INVOKESTATIC, cn.name, name, min.desc, false));
                    mn.instructions.remove(min);
                }

                in = in.getNext();
            }

            mn.visitMaxs(0, 0);
        }

        for (Runnable methodCreator : methodCreators) {
            methodCreator.run();
        }
    }

    private String createMethod(ClassNode cn, MethodInsnNode min) {
        String name = min.name + (methodId++);
        // run later to prevent Concurrent bla bla, AHHHHHHH
        methodCreators.add(() -> {
            MethodVisitor mv = cn.visitMethod(ACC_PUBLIC | ACC_STATIC, name,
                                              min.desc, null, new String[0]);
            // I'm just assuming we never call a non-void method
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        });

        return name;
    }

}
