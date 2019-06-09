package me.vektory79.plantuml.stubs;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;
import jdk.vm.ci.meta.MetaAccessProvider;
import jdk.vm.ci.meta.ResolvedJavaField;
import sun.awt.X11.XToolkit;

import java.awt.*;
import java.security.AccessController;
import java.security.PrivilegedAction;

@TargetClass(XToolkit.class)
public final class XToolkitStub {
    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Custom, declClass = XToolkitStub_Thread_Recalc.class)
    static Thread toolkitThread;

    public static final class XToolkitStub_Thread_Recalc implements RecomputeFieldValue.CustomFieldValueComputer {
        public static Thread toolkitThread;

        @Override
        public Object compute(MetaAccessProvider metaAccess, ResolvedJavaField original, ResolvedJavaField annotated, Object receiver) {
            toolkitThread = AccessController.doPrivileged((PrivilegedAction<Thread>) () -> {
                Thread thread = new Thread(sun.misc.ThreadGroupUtils.getRootThreadGroup(), (XToolkit) Toolkit.getDefaultToolkit(), "AWT-XAWT");
                thread.setContextClassLoader(null);
                thread.setPriority(Thread.NORM_PRIORITY + 1);
                thread.setDaemon(true);
                return thread;
            });
            return toolkitThread;
        }

        public static void start() {
            toolkitThread.start();
        }
    }
}
