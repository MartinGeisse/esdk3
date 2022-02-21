package name.martingeisse.esdk.core.library.signal.getter;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

public final class GeneratedClassLoader extends ClassLoader {

    public static final GeneratedClassLoader INSTANCE = new GeneratedClassLoader();

    private GeneratedClassLoader() {
    }

    public Class<?> defineClass(ClassNode classNode) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(classWriter);
        byte[] data = classWriter.toByteArray();
        return defineClass(null, data, 0, data.length);
    }

}
