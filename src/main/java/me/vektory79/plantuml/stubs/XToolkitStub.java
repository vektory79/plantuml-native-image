package me.vektory79.plantuml.stubs;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.InjectAccessors;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;
import sun.awt.X11.XToolkit;

import java.awt.*;
import java.security.AccessController;
import java.security.PrivilegedAction;

@TargetClass(XToolkit.class)
public final class XToolkitStub {
    @Alias
    @InjectAccessors(ThreadWrapper.class)
    static Thread toolkitThread;

    public static final class ThreadWrapper {
        static Thread toolkitThread;


        public static Thread getToolkitThread() {
            return toolkitThread;
        }

        public static void setToolkitThread(Thread value) {
            value.setDaemon(false);
        }

        public static void start() {
            toolkitThread = AccessController.doPrivileged((PrivilegedAction<Thread>) () -> {
                Thread thread = new Thread(sun.misc.ThreadGroupUtils.getRootThreadGroup(), (XToolkit) Toolkit.getDefaultToolkit(), "AWT-XAWT");
                thread.setContextClassLoader(null);
                thread.setPriority(Thread.NORM_PRIORITY + 1);
                thread.setDaemon(true);
                return thread;
            });
            toolkitThread.start();
        }
    }
}
