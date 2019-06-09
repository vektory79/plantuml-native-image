package me.vektory79.plantuml.stubs;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.RecomputeFieldValue;
import com.oracle.svm.core.annotate.TargetClass;
import sun.java2d.opengl.OGLRenderQueue;

@TargetClass(OGLRenderQueue.class)
public final class OGLRenderQueueStub {

    @Alias
    @RecomputeFieldValue(kind = RecomputeFieldValue.Kind.Reset)
    private OGLRenderQueueStub_QueueFlusher flusher;
}

@TargetClass(value = OGLRenderQueue.class, innerClass = "QueueFlusher")
final class OGLRenderQueueStub_QueueFlusher {
}

