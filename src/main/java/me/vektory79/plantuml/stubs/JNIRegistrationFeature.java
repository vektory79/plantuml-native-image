package me.vektory79.plantuml.stubs;

/*-
 * #%L
 * plantuml-graal
 * %%
 * Copyright (C) 2019 - 2020 vektory79.me
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.jni.JNIRuntimeAccess;
import com.oracle.svm.hosted.FeatureImpl;
import com.oracle.svm.hosted.classinitialization.ClassInitializationSupport;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageWriter;
import org.graalvm.nativeimage.ImageSingletons;
import org.graalvm.nativeimage.hosted.Feature;
import org.graalvm.nativeimage.impl.RuntimeReflectionSupport;
import sun.awt.*;
import sun.awt.X11.*;
import sun.awt.datatransfer.DataTransferer;
import sun.awt.im.InputMethodContext;
import sun.awt.image.*;
import sun.font.*;
import sun.java2d.Disposer;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.*;
import sun.java2d.opengl.OGLRenderQueue;
import sun.java2d.pipe.Region;
import sun.java2d.pipe.RegionIterator;
import sun.java2d.pipe.ShapeSpanIterator;
import sun.java2d.pipe.SpanClipRenderer;
import sun.swing.SwingUtilities2;

import javax.imageio.ImageIO;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.metal.MetalTheme;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Path2D;
import java.awt.geom.RectangularShape;
import java.awt.image.*;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

// https://github.com/oracle/graal/blob/master/substratevm/JNI.md#reflection
@AutomaticFeature
@SuppressWarnings({"squid:S1192", "squid:S00112", "unused"})
class JNIRegistrationFeature implements Feature {

    private static final String AWT_SUPPORT = "AWT Support";
    private static final String AWT_SUPPORT_SUPERCLASS = "AWT Support needs superclass to initialize at runtime";
    private Set<Class<?>> runtimeClasses = new HashSet<>();
    private Set<String> runtimeClassesSimple = new HashSet<>();
    private Set<Class<?>> reflectionClasses = new HashSet<>();

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        try {
            FeatureImpl.BeforeAnalysisAccessImpl a = (FeatureImpl.BeforeAnalysisAccessImpl) access;
            ClassInitializationSupport classInitializationSupport = a.getHostVM().getClassInitializationSupport();

            registerReflection("sun.awt.X11GraphicsEnvironment");
            registerReflection("sun.awt.X11.XToolkit");
            registerReflection("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            registerReflection("java.awt.EventQueue");
            registerReflection("sun.java2d.loops.GraphicsPrimitive");
            registerReflection("sun.awt.X11FontManager");
            registerReflection("sun.font.FreetypeFontScaler");
            registerReflection("net.sourceforge.plantuml.brotli.DictionaryData");
            registerReflection("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
//            registerReflection("org.apache.batik.apps.rasterizer.SVGConverter");
//            registerReflection("org.apache.batik.apps.rasterizer.DestinationType");

            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XDragAndDropProtocols", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.MotifDnDConstants", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(WindowPropertyGetter.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XWM", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.dnd.SunDropTargetContextPeer$EventDispatcher", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XRootWindow$LazyHolder", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XSelection", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XDnDConstants", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XDataTransferer", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(StringSelection.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(StrikeCache.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(PhysicalStrike.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.font.FontDesignMetrics$KeyReference", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(ColorConvertOp.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.java2d.cmm.lcms.LCMSTransform", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(ScrollPaneAdjustable.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(RepaintManager.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(ImageIO.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("java.awt.LightweightDispatcher", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(InputMethodContext.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(ImagingLib.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(RenderLoops.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(RenderingHints.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(SwingUtilities2.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XTrayIconPeer$XTrayIconEmbeddedFrame", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(SpanClipRenderer.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(BorderFactory.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("java.awt.GradientPaintContext", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("java.awt.ColorPaintContext", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11GraphicsEnvironment", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.java2d.jules.JulesPathBuf", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(SunLayoutEngine.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(FontManagerNativeLibrary.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(XMSelection.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(XSystemTrayPeer.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XToolkitThreadBlockedHandler", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(BasicGraphicsUtils.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(AreaAveragingScaleFilter.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(GifImageDecoder.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XlibWrapper", AWT_SUPPORT); // Hand founded
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.MotifDnDConstants", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.Native", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.UnsafeXDisposerRecord", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(WindowPropertyGetter.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(XAtom.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(XCustomCursor.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XDropTargetContextPeer", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(XGlobalCursorManager.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(XKeysym.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XNETProtocol", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(XPropertyCache.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(XQueryTree.class, AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XSelection", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime("sun.awt.X11.XSelection$IncrementalDataProvider", AWT_SUPPORT);
            classInitializationSupport.initializeAtRunTime(XTranslateCoordinates.class, AWT_SUPPORT);

            initAtRuntimeSimple(a, Image.class);
            initAtRuntimeSimple(a, DataFlavor.class);
            initAtRuntimeSimple(a, DataTransferer.class);
            initAtRuntimeSimple(a, DataTransferer.IndexedComparator.class);
            initAtRuntimeSimple(a, "sun.awt.datatransfer.DataTransferer$StandardEncodingsHolder");
            initAtRuntimeSimple(a, SunFontManager.class);
            initAtRuntimeSimple(a, FontMetrics.class);
            initAtRuntimeSimple(a, Toolkit.class);
            initAtRuntimeSimple(a, Dimension2D.class);
            initAtRuntimeSimple(a, RectangularShape.class);
            initAtRuntimeSimple(a, FileSystemView.class);
            initAtRuntimeSimple(a, MetalTheme.class);
            initAtRuntimeSimple(a, "sun.awt.shell.ShellFolder");
            initAtRuntimeSimple(a, DataBuffer.class);
            initAtRuntimeSimple(a, KeyboardFocusManager.class);
            initAtRuntimeSimple(a, AWTKeyStroke.class);
            initAtRuntimeSimple(a, ImageIcon.class);
            initAtRuntimeSimple(a, Graphics.class);
            initAtRuntimeSimple(a, AbstractBorder.class);
            initAtRuntimeSimple(a, RenderingHints.Key.class);
            initAtRuntimeSimple(a, KeyboardFocusManagerPeerImpl.class);
            initAtRuntimeSimple(a, GraphicsConfiguration.class);
            initAtRuntimeSimple(a, GraphicsDevice.class);
            initAtRuntimeSimple(a, Font2D.class);
            initAtRuntimeSimple(a, DropTargetEvent.class);
            initAtRuntimeSimple(a, "java.awt.TexturePaintContext");
            initAtRuntimeSimple(a, "java.awt.MultipleGradientPaintContext");
            initAtRuntimeSimple(a, "sun.awt.X11.XScrollbar");

            initAtRuntimeSimple(a, "sun.awt.X11.XWrapperBase");
            initAtRuntimeSimple(a, "sun.awt.X11.XDragSourceProtocol");
            initAtRuntimeSimple(a, "sun.awt.X11.XDropTargetProtocol");
            initAtRuntimeSimple(a, "sun.awt.X11.XCanvasPeer");
            initAtRuntimeSimple(a, XEmbedHelper.class);
            initAtRuntimeSimple(a, "sun.awt.X11.XWindowPeer");
            initAtRuntimeSimple(a, "sun.awt.X11.XWM");
            initAtRuntimeSimple(a, "sun.awt.X11.XWrapperBase");


            JNIRuntimeAccess.register(System.class);
            JNIRuntimeAccess.register(System.class.getDeclaredMethod("setProperty", String.class, String.class));
            JNIRuntimeAccess.register(System.class.getDeclaredMethod("getProperty", String.class));
            JNIRuntimeAccess.register(System.class.getDeclaredMethod("load", String.class));

            JNIRuntimeAccess.register(boolean.class);
            JNIRuntimeAccess.register(byte.class);
            JNIRuntimeAccess.register(byte[].class);
            JNIRuntimeAccess.register(char.class);
            JNIRuntimeAccess.register(char[].class);
            JNIRuntimeAccess.register(int.class);
            JNIRuntimeAccess.register(int[].class);

            JNIRuntimeAccess.register(NullPointerException.class);
            JNIRuntimeAccess.register(FileNotFoundException.class);
            JNIRuntimeAccess.register(OutOfMemoryError.class);

            JNIRuntimeAccess.register(Array.class);
            JNIRuntimeAccess.register(Array.class.getDeclaredMethod("newInstance", Class.class, int.class));

            JNIRuntimeAccess.register(ArrayList.class);
            JNIRuntimeAccess.register(ArrayList.class.getDeclaredMethod("add", Object.class));

            JNIRuntimeAccess.register(String.class);
            JNIRuntimeAccess.register(String.class.getDeclaredConstructors());
            JNIRuntimeAccess.register(String.class.getDeclaredMethod("getBytes"));
            JNIRuntimeAccess.register(String.class.getDeclaredMethod("getBytes", String.class));

            JNIRuntimeAccess.register(Charset.class);
            JNIRuntimeAccess.register(Charset.class.getDeclaredMethod("isSupported", String.class));

            JNIRuntimeAccess.register(InternalError.class);
            JNIRuntimeAccess.register(InternalError.class.getDeclaredConstructor());
            JNIRuntimeAccess.register(InternalError.class.getDeclaredConstructor(String.class));
            JNIRuntimeAccess.register(InternalError.class.getDeclaredConstructor(String.class, Throwable.class));
            JNIRuntimeAccess.register(InternalError.class.getDeclaredConstructor(Throwable.class));

//            initAtRuntime(a, InputStream.class);
            JNIRuntimeAccess.register(InputStream.class);
            JNIRuntimeAccess.register(InputStream.class.getDeclaredMethod("read", byte[].class, int.class, int.class));
            JNIRuntimeAccess.register(InputStream.class.getDeclaredMethod("available"));

            initAtRuntime(a, Insets.class);
            JNIRuntimeAccess.register(Insets.class.getDeclaredField("top"));
            JNIRuntimeAccess.register(Insets.class.getDeclaredField("bottom"));
            JNIRuntimeAccess.register(Insets.class.getDeclaredField("left"));
            JNIRuntimeAccess.register(Insets.class.getDeclaredField("right"));

            classInitializationSupport.initializeAtRunTime(Event.class, AWT_SUPPORT);
            JNIRuntimeAccess.register(Event.class);
            JNIRuntimeAccess.register(Event.class.getDeclaredField("data"));
            JNIRuntimeAccess.register(Event.class.getDeclaredField("consumed"));
            JNIRuntimeAccess.register(Event.class.getDeclaredField("id"));

            classInitializationSupport.initializeAtRunTime(TrayIcon.class, AWT_SUPPORT);
            JNIRuntimeAccess.register(TrayIcon.class);
            JNIRuntimeAccess.register(TrayIcon.class.getDeclaredField("id"));
            JNIRuntimeAccess.register(TrayIcon.class.getDeclaredField("actionCommand"));

            initAtRuntime(a, Region.class);
            JNIRuntimeAccess.register(Region.class.getDeclaredField("endIndex"));
            JNIRuntimeAccess.register(Region.class.getDeclaredField("bands"));
            JNIRuntimeAccess.register(Region.class.getDeclaredField("lox"));
            JNIRuntimeAccess.register(Region.class.getDeclaredField("loy"));
            JNIRuntimeAccess.register(Region.class.getDeclaredField("hix"));
            JNIRuntimeAccess.register(Region.class.getDeclaredField("hiy"));

            initAtRuntime(a, X11GraphicsConfig.class);
            JNIRuntimeAccess.register(X11GraphicsConfig.class.getDeclaredField("aData"));
            JNIRuntimeAccess.register(X11GraphicsConfig.class.getDeclaredField("bitsPerPixel"));
            JNIRuntimeAccess.register(X11GraphicsConfig.class.getDeclaredField("screen"));

            initAtRuntime(a, X11GraphicsDevice.class);
            JNIRuntimeAccess.register(X11GraphicsDevice.class.getDeclaredField("screen"));
            JNIRuntimeAccess.register(X11GraphicsDevice.class.getDeclaredMethod("addDoubleBufferVisual", int.class));

            classInitializationSupport.initializeAtRunTime(JPEGImageReader.class, AWT_SUPPORT);
            JNIRuntimeAccess.register(JPEGImageReader.class);
            JNIRuntimeAccess.register(JPEGImageReader.class.getDeclaredMethod("readInputData", byte[].class, int.class, int.class));
            JNIRuntimeAccess.register(JPEGImageReader.class.getDeclaredMethod("skipInputBytes", long.class));
            JNIRuntimeAccess.register(JPEGImageReader.class.getDeclaredMethod("warningOccurred", int.class));
            JNIRuntimeAccess.register(JPEGImageReader.class.getDeclaredMethod("warningWithMessage", String.class));
            JNIRuntimeAccess.register(JPEGImageReader.class.getDeclaredMethod("setImageData", int.class, int.class, int.class, int.class, int.class, byte[].class));
            JNIRuntimeAccess.register(JPEGImageReader.class.getDeclaredMethod("acceptPixels", int.class, boolean.class));
            JNIRuntimeAccess.register(JPEGImageReader.class.getDeclaredMethod("passStarted", int.class));
            JNIRuntimeAccess.register(JPEGImageReader.class.getDeclaredMethod("passComplete"));
            JNIRuntimeAccess.register(JPEGImageReader.class.getDeclaredMethod("pushBack", int.class));
            JNIRuntimeAccess.register(JPEGImageReader.class.getDeclaredMethod("skipPastImage", int.class));

            classInitializationSupport.initializeAtRunTime(RegionIterator.class, AWT_SUPPORT);
            JNIRuntimeAccess.register(RegionIterator.class);
            JNIRuntimeAccess.register(RegionIterator.class.getDeclaredField("region"));
            JNIRuntimeAccess.register(RegionIterator.class.getDeclaredField("curIndex"));
            JNIRuntimeAccess.register(RegionIterator.class.getDeclaredField("numXbands"));

            Class<?> xRBackendNativeClass = initAtRuntime(a, "sun.java2d.xr.XRBackendNative");
            JNIRuntimeAccess.register(xRBackendNativeClass.getDeclaredField("FMTPTR_A8"));
            JNIRuntimeAccess.register(xRBackendNativeClass.getDeclaredField("FMTPTR_ARGB32"));
            JNIRuntimeAccess.register(xRBackendNativeClass.getDeclaredField("MASK_XIMG"));

            classInitializationSupport.initializeAtRunTime(ImageRepresentation.class, AWT_SUPPORT);
            JNIRuntimeAccess.register(ImageRepresentation.class);
            JNIRuntimeAccess.register(ImageRepresentation.class.getDeclaredField("numSrcLUT"));
            JNIRuntimeAccess.register(ImageRepresentation.class.getDeclaredField("srcLUTtransIndex"));

            classInitializationSupport.initializeAtRunTime(GlyphLayout.GVData.class, AWT_SUPPORT);
            JNIRuntimeAccess.register(GlyphLayout.GVData.class);
            JNIRuntimeAccess.register(GlyphLayout.GVData.class.getDeclaredField("_count"));
            JNIRuntimeAccess.register(GlyphLayout.GVData.class.getDeclaredField("_flags"));
            JNIRuntimeAccess.register(GlyphLayout.GVData.class.getDeclaredField("_glyphs"));
            JNIRuntimeAccess.register(GlyphLayout.GVData.class.getDeclaredField("_positions"));
            JNIRuntimeAccess.register(GlyphLayout.GVData.class.getDeclaredField("_indices"));

            classInitializationSupport.initializeAtRunTime(ShapeSpanIterator.class, AWT_SUPPORT);
            JNIRuntimeAccess.register(ShapeSpanIterator.class);
            JNIRuntimeAccess.register(ShapeSpanIterator.class.getDeclaredField("pData"));

            initAtRuntime(a, SunToolkit.class);
            JNIRuntimeAccess.register(SunToolkit.class.getDeclaredMethod("awtLock"));
            JNIRuntimeAccess.register(SunToolkit.class.getDeclaredMethod("awtUnlock"));
            JNIRuntimeAccess.register(SunToolkit.class.getDeclaredMethod("awtLockWait", long.class));
            JNIRuntimeAccess.register(SunToolkit.class.getDeclaredMethod("awtLockNotify"));
            JNIRuntimeAccess.register(SunToolkit.class.getDeclaredMethod("awtLockNotifyAll"));
            JNIRuntimeAccess.register(SunToolkit.class.getDeclaredMethod("isAWTLockHeldByCurrentThread"));

            initAtRuntime(a, XErrorHandlerUtil.class);
            JNIRuntimeAccess.register(XErrorHandlerUtil.class.getDeclaredMethod("init", long.class));
            JNIRuntimeAccess.register(XErrorHandlerUtil.class.getDeclaredMethod("globalErrorHandler", long.class, long.class));

            initAtRuntime(a, OGLRenderQueue.class);
            JNIRuntimeAccess.register(OGLRenderQueue.class.getDeclaredMethod("disposeGraphicsConfig", long.class));

            initAtRuntime(a, Rectangle.class);
            JNIRuntimeAccess.register(Rectangle.class.getDeclaredConstructor(int.class, int.class, int.class, int.class));

            initAtRuntime(a, DisplayMode.class);
            JNIRuntimeAccess.register(DisplayMode.class.getDeclaredConstructor(int.class, int.class, int.class, int.class));

            Class<?> xRSurfaceData = initAtRuntime(a, "sun.java2d.xr.XRSurfaceData");
            JNIRuntimeAccess.register(xRSurfaceData.getDeclaredField("picture"));
            JNIRuntimeAccess.register(xRSurfaceData.getDeclaredField("xid"));

            initAtRuntime(a, X11InputMethod.class);
            JNIRuntimeAccess.register(X11InputMethod.class.getDeclaredField("pData"));

            initAtRuntime(a, JPEGImageDecoder.class);
            JNIRuntimeAccess.register(JPEGImageDecoder.class.getDeclaredMethod("sendHeaderInfo", int.class, int.class, boolean.class, boolean.class, boolean.class));
            JNIRuntimeAccess.register(JPEGImageDecoder.class.getDeclaredMethod("sendPixels", byte[].class, int.class));
            JNIRuntimeAccess.register(JPEGImageDecoder.class.getDeclaredMethod("sendPixels", int[].class, int.class));


            // Register primitive types: jdk/src/share/native/sun/java2d/loops/GraphicsPrimitiveMgr.c:581
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.Blit").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.BlitBg").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.ScaledBlit").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.FillRect").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.FillSpans").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.FillParallelogram").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.DrawParallelogram").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.DrawLine").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.DrawRect").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.DrawPolygons").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.DrawPath").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.FillPath").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.MaskBlit").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.MaskFill").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.DrawGlyphList").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.DrawGlyphListAA").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.DrawGlyphListLCD").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));
            JNIRuntimeAccess.register(initAtRuntime(a, "sun.java2d.loops.TransformHelper").getDeclaredConstructor(long.class, SurfaceType.class, CompositeType.class, SurfaceType.class));


            registerXWindow(a);
            registerDisposer(classInitializationSupport);
            registerBufferedImage(a);
            registerRaster(a);
            registerColorModel(a);
            registerSampleModel(a);
            registerKernel(classInitializationSupport);
            registerColor(a);
            registerJPEGImageWriter(classInitializationSupport);
            registerJPEGQTable(classInitializationSupport);
            registerJPEGHuffmanTable(classInitializationSupport);
            registerSurfaceData(a);
            registerInvalidPipeException(classInitializationSupport);
            registerCursor(a);
            registerXToolkit(classInitializationSupport);
            registerComponent(a);
            registerMenuComponent(a);
            registerGraphicsPrimitiveMgr(classInitializationSupport);
            registerGraphicsPrimitive(a);
            registerSurfaceType(classInitializationSupport);
            registerCompositeType(classInitializationSupport);
            registerSunGraphics2D(classInitializationSupport);
            registerAffineTransform(classInitializationSupport);
            registerXORComposite(classInitializationSupport);
            registerAlphaComposite(classInitializationSupport);
            registerPath2D(a);
            registerSunHints(a);
            registerFont(a);
            registerFontDescriptor(classInitializationSupport);
            registerPlatformFont(a);
            registerPoint(classInitializationSupport);
            registerAWTEvent(a);
            registerBufImgSurfaceData(classInitializationSupport);
            registerFonts(a);
            registerImage(a);

            registerChildClasses(a);

            a.getHostVM().registerClassReachabilityListener((duringAnalysisAccess, clazz) -> {
                FeatureImpl.DuringAnalysisAccessImpl a2 = (FeatureImpl.DuringAnalysisAccessImpl) duringAnalysisAccess;

                Class<?> superClass = clazz.getSuperclass();
                boolean isFullChild = false;
                boolean isSimpleChild = false;
                boolean isReflection = false;
                while (superClass != null) {
                    if (runtimeClasses.contains(superClass)) {
                        isFullChild = true;
                        break;
                    }
                    if (runtimeClassesSimple.contains(superClass.getName())) {
                        isSimpleChild = true;
                        break;
                    }
                    superClass = superClass.getSuperclass();
                }
                superClass = clazz;
                while (superClass != null) {
                    if (reflectionClasses.contains(superClass)) {
                        isReflection = true;
                        break;
                    }
                    superClass = superClass.getSuperclass();
                }
                if (isFullChild) {
                    superClass = clazz;
                    while (superClass != null) {
                        if (runtimeClasses.contains(superClass)) {
                            break;
                        }
                        System.out.println("initAtRuntime(access, \"" + superClass.getName() + "\");");
                        runtimeClasses.add(superClass);
                        superClass = superClass.getSuperclass();
                    }
                }
                if (isSimpleChild) {
                    superClass = clazz;
                    while (superClass != null) {
                        if (runtimeClassesSimple.contains(superClass.getName())) {
                            break;
                        }
                        System.out.println("initAtRuntimeSimple(access, \"" + superClass.getName() + "\");");
                        runtimeClassesSimple.add(superClass.getName());
                        superClass = superClass.getSuperclass();
                    }
                }
                if (isReflection) {
                    superClass = clazz;
                    while (superClass != null) {
                        if (reflectionClasses.contains(superClass)) {
                            break;
                        }
                        System.out.println("registerReflection(\"" + superClass.getName() + "\");");
                        reflectionClasses.add(superClass);
                        superClass = superClass.getSuperclass();
                    }
                }
            });
        } catch (NoSuchMethodException | NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerImage(FeatureImpl.BeforeAnalysisAccessImpl a) throws NoSuchFieldException {
        initAtRuntime(a, ComponentSampleModel.class);
        JNIRuntimeAccess.register(ComponentSampleModel.class.getDeclaredField("pixelStride"));
        JNIRuntimeAccess.register(ComponentSampleModel.class.getDeclaredField("scanlineStride"));
        JNIRuntimeAccess.register(ComponentSampleModel.class.getDeclaredField("bandOffsets"));
    }

    private void registerFonts(FeatureImpl.BeforeAnalysisAccessImpl access) throws ClassNotFoundException, NoSuchMethodException, NoSuchFieldException {
        initAtRuntimeSimple(access, "sun.font.TrueTypeFont");
        initAtRuntimeSimple(access, "sun.font.Type1Font");
        initAtRuntimeSimple(access, "sun.font.FontManagerNativeLibrary");
        initAtRuntimeSimple(access, "sun.font.FontScaler");
        initAtRuntimeSimple(access, "sun.font.NullFontScaler");
        initAtRuntimeSimple(access, "sun.awt.FcFontManager");
        initAtRuntimeSimple(access, "sun.awt.X11FontManager");
        initAtRuntimeSimple(access, "sun.font.FileFontStrike");
        initAtRuntimeSimple(access, "sun.font.NativeStrike");
        initAtRuntimeSimple(access, "sun.font.DelegateStrike");

        Class<?> clazz = getClass("sun.font.TrueTypeFont");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("readBlock", ByteBuffer.class, int.class, int.class));
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("readBytes", int.class, int.class));

        clazz = getClass("sun.font.Type1Font");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("readFile", ByteBuffer.class));

        Class<?> point2DFloatClass = getClass("java.awt.geom.Point2D$Float");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(point2DFloatClass.getDeclaredConstructors());
        JNIRuntimeAccess.register(point2DFloatClass.getDeclaredFields());

        clazz = getClass("sun.font.StrikeMetrics");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredConstructors());

        clazz = getClass("java.awt.geom.Rectangle2D$Float");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredConstructors());
        JNIRuntimeAccess.register(clazz.getDeclaredFields());

        clazz = getClass("sun.font.Font2D");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("charToGlyph", int.class));
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("getMapper"));
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("getTableBytes", int.class));
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("canDisplay", char.class));

        clazz = initAtRuntime(access, "java.awt.geom.GeneralPath");
        JNIRuntimeAccess.register(clazz.getDeclaredConstructors());

        clazz = getClass("sun.font.CharToGlyphMapper");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("charToGlyph", int.class));

        clazz = getClass("sun.font.PhysicalStrike");
        initAtRuntimeSimple(access, clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("getGlyphPoint", int.class, int.class));
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("adjustPoint", point2DFloatClass));
        JNIRuntimeAccess.register(clazz.getDeclaredField("pScalerContext"));

        clazz = getClass("sun.font.FontStrike");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("getGlyphMetrics", int.class));

        clazz = getClass("sun.font.GlyphList");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredFields());

        clazz = getClass("sun.font.FontConfigManager$FontConfigInfo");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredField("fcVersion"));
        JNIRuntimeAccess.register(clazz.getDeclaredField("cacheDirs"));

        clazz = getClass("sun.font.FontConfigManager$FcCompFont");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredField("fcName"));

        clazz = getClass("sun.font.FontConfigManager$FcCompFont");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredField("fcName"));
        JNIRuntimeAccess.register(clazz.getDeclaredField("firstFont"));
        JNIRuntimeAccess.register(clazz.getDeclaredField("allFonts"));

        clazz = getClass("sun.font.FontConfigManager$FontConfigFont");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredConstructor());
        JNIRuntimeAccess.register(clazz.getDeclaredField("familyName"));
        JNIRuntimeAccess.register(clazz.getDeclaredField("styleStr"));
        JNIRuntimeAccess.register(clazz.getDeclaredField("fullName"));
        JNIRuntimeAccess.register(clazz.getDeclaredField("fontFile"));

        clazz = getClass("sun.font.FontUtilities");
        JNIRuntimeAccess.register(clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredField("isOpenJDK"));

        clazz = getClass("sun.font.FreetypeFontScaler");
        initAtRuntimeSimple(access, clazz);
        JNIRuntimeAccess.register(clazz.getDeclaredMethod("invalidateScaler"));
    }

    private void registerBufImgSurfaceData(ClassInitializationSupport classInitializationSupport) throws NoSuchMethodException, NoSuchFieldException {
        classInitializationSupport.initializeAtRunTime(BufImgSurfaceData.ICMColorData.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(BufImgSurfaceData.ICMColorData.class);
        JNIRuntimeAccess.register(BufImgSurfaceData.ICMColorData.class.getDeclaredConstructor(long.class));
        JNIRuntimeAccess.register(BufImgSurfaceData.ICMColorData.class.getDeclaredField("pData"));
    }

    private void registerAWTEvent(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException {
        initAtRuntime(access, AWTEvent.class);
        JNIRuntimeAccess.register(AWTEvent.class.getDeclaredField("bdata"));
        JNIRuntimeAccess.register(AWTEvent.class.getDeclaredField("id"));
        JNIRuntimeAccess.register(AWTEvent.class.getDeclaredField("consumed"));

        JNIRuntimeAccess.register(KeyEvent.class.getDeclaredField("isProxyActive"));
    }

    private void registerPoint(ClassInitializationSupport classInitializationSupport) throws NoSuchFieldException {
        classInitializationSupport.initializeAtRunTime(Point.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(Point.class);
        JNIRuntimeAccess.register(Point.class.getDeclaredField("x"));
        JNIRuntimeAccess.register(Point.class.getDeclaredField("y"));
    }

    private void registerPlatformFont(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException, NoSuchMethodException {
        initAtRuntime(access, PlatformFont.class);
        JNIRuntimeAccess.register(PlatformFont.class.getDeclaredField("componentFonts"));
        JNIRuntimeAccess.register(PlatformFont.class.getDeclaredField("fontConfig"));
        JNIRuntimeAccess.register(PlatformFont.class.getDeclaredMethod("makeConvertedMultiFontString", String.class));
        JNIRuntimeAccess.register(PlatformFont.class.getDeclaredMethod("makeConvertedMultiFontChars", char[].class, int.class, int.class));

        JNIRuntimeAccess.register(XFontPeer.class.getDeclaredField("xfsname"));
    }

    private void registerFontDescriptor(ClassInitializationSupport classInitializationSupport) throws NoSuchFieldException {
        classInitializationSupport.initializeAtRunTime(FontDescriptor.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(FontDescriptor.class);
        JNIRuntimeAccess.register(FontDescriptor.class.getDeclaredField("nativeName"));
        JNIRuntimeAccess.register(FontDescriptor.class.getDeclaredField("charsetName"));
    }

    private void registerFont(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException, NoSuchMethodException {
        initAtRuntime(access, Font.class);
        JNIRuntimeAccess.register(Font.class.getDeclaredField("pData"));
        JNIRuntimeAccess.register(Font.class.getDeclaredField("style"));
        JNIRuntimeAccess.register(Font.class.getDeclaredField("size"));
        JNIRuntimeAccess.register(Font.class.getDeclaredMethod("getPeer_NoClientCode"));
        JNIRuntimeAccess.register(Font.class.getDeclaredMethod("getFamily_NoClientCode"));
    }

    private void registerSunHints(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException {
        initAtRuntimeSimple(access, SunHints.Key.class);
        initAtRuntimeSimple(access, SunHints.Value.class);
        initAtRuntimeSimple(access, SunHints.LCDContrastKey.class);

        initAtRuntime(access, SunHints.class);
        JNIRuntimeAccess.register(SunHints.class.getDeclaredField("INTVAL_STROKE_PURE"));
    }

    private void registerPath2D(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException {
        initAtRuntime(access, Path2D.class);
        JNIRuntimeAccess.register(Path2D.class.getDeclaredField("pointTypes"));
        JNIRuntimeAccess.register(Path2D.class.getDeclaredField("numTypes"));
        JNIRuntimeAccess.register(Path2D.class.getDeclaredField("windingRule"));

        JNIRuntimeAccess.register(Path2D.Float.class.getDeclaredField("floatCoords"));
    }

    private void registerAlphaComposite(ClassInitializationSupport classInitializationSupport) throws NoSuchFieldException {
        classInitializationSupport.initializeAtRunTime(AlphaComposite.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(AlphaComposite.class);
        JNIRuntimeAccess.register(AlphaComposite.class.getDeclaredField("rule"));
        JNIRuntimeAccess.register(AlphaComposite.class.getDeclaredField("extraAlpha"));
    }

    private void registerXORComposite(ClassInitializationSupport classInitializationSupport) throws NoSuchFieldException {
        classInitializationSupport.initializeAtRunTime(XORComposite.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(XORComposite.class);
        JNIRuntimeAccess.register(XORComposite.class.getDeclaredField("xorPixel"));
        JNIRuntimeAccess.register(XORComposite.class.getDeclaredField("xorColor"));
        JNIRuntimeAccess.register(XORComposite.class.getDeclaredField("alphaMask"));
    }

    private void registerAffineTransform(ClassInitializationSupport classInitializationSupport) throws NoSuchFieldException {
        classInitializationSupport.initializeAtRunTime(AffineTransform.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(AffineTransform.class);
        JNIRuntimeAccess.register(AffineTransform.class.getDeclaredField("m00"));
        JNIRuntimeAccess.register(AffineTransform.class.getDeclaredField("m01"));
        JNIRuntimeAccess.register(AffineTransform.class.getDeclaredField("m02"));
        JNIRuntimeAccess.register(AffineTransform.class.getDeclaredField("m10"));
        JNIRuntimeAccess.register(AffineTransform.class.getDeclaredField("m11"));
        JNIRuntimeAccess.register(AffineTransform.class.getDeclaredField("m12"));
    }

    private void registerSunGraphics2D(ClassInitializationSupport classInitializationSupport) throws NoSuchFieldException {
        classInitializationSupport.initializeAtRunTime(SunGraphics2D.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(SunGraphics2D.class);
        JNIRuntimeAccess.register(SunGraphics2D.class.getDeclaredField("pixel"));
        JNIRuntimeAccess.register(SunGraphics2D.class.getDeclaredField("eargb"));
        JNIRuntimeAccess.register(SunGraphics2D.class.getDeclaredField("clipRegion"));
        JNIRuntimeAccess.register(SunGraphics2D.class.getDeclaredField("composite"));
        JNIRuntimeAccess.register(SunGraphics2D.class.getDeclaredField("lcdTextContrast"));
        JNIRuntimeAccess.register(SunGraphics2D.class.getDeclaredField("strokeHint"));
    }

    private void registerCompositeType(ClassInitializationSupport classInitializationSupport) {
        classInitializationSupport.initializeAtRunTime(CompositeType.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(CompositeType.class);
        JNIRuntimeAccess.register(CompositeType.class.getDeclaredFields());
    }

    private void registerSurfaceType(ClassInitializationSupport classInitializationSupport) {
        classInitializationSupport.initializeAtRunTime(SurfaceType.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(SurfaceType.class);
        JNIRuntimeAccess.register(SurfaceType.class.getDeclaredFields());
    }

    private void registerGraphicsPrimitive(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException {
        initAtRuntime(access, GraphicsPrimitive.class);
        JNIRuntimeAccess.register(GraphicsPrimitive.class.getDeclaredField("pNativePrim"));
    }

    private void registerGraphicsPrimitiveMgr(ClassInitializationSupport classInitializationSupport) throws NoSuchMethodException {
        classInitializationSupport.initializeAtRunTime(GraphicsPrimitiveMgr.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(GraphicsPrimitiveMgr.class);
        JNIRuntimeAccess.register(GraphicsPrimitiveMgr.class.getDeclaredMethod("register", GraphicsPrimitive[].class));
    }

    private void registerMenuComponent(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException {
        initAtRuntime(access, MenuComponent.class);
        JNIRuntimeAccess.register(MenuComponent.class.getDeclaredField("appContext"));
    }

    private void registerComponent(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException, NoSuchMethodException {
        initAtRuntime(access, Component.class);
        JNIRuntimeAccess.register(Component.class.getDeclaredField("x"));
        JNIRuntimeAccess.register(Component.class.getDeclaredField("y"));
        JNIRuntimeAccess.register(Component.class.getDeclaredField("width"));
        JNIRuntimeAccess.register(Component.class.getDeclaredField("height"));
        JNIRuntimeAccess.register(Component.class.getDeclaredField("isPacked"));
        JNIRuntimeAccess.register(Component.class.getDeclaredField("peer"));
        JNIRuntimeAccess.register(Component.class.getDeclaredField("background"));
        JNIRuntimeAccess.register(Component.class.getDeclaredField("foreground"));
        JNIRuntimeAccess.register(Component.class.getDeclaredField("graphicsConfig"));
        JNIRuntimeAccess.register(Component.class.getDeclaredField("name"));
        JNIRuntimeAccess.register(Component.class.getDeclaredMethod("getParent_NoClientCode"));
        JNIRuntimeAccess.register(Component.class.getDeclaredMethod("getLocationOnScreen_NoTreeLock"));
        JNIRuntimeAccess.register(Component.class.getDeclaredField("appContext"));
    }

    private void registerXToolkit(ClassInitializationSupport classInitializationSupport) throws NoSuchFieldException {
        classInitializationSupport.initializeAtRunTime(XToolkit.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(XToolkit.class);
        JNIRuntimeAccess.register(XToolkit.class.getDeclaredField("numLockMask"));
        JNIRuntimeAccess.register(XToolkit.class.getDeclaredField("modLockIsShiftLock"));
    }

    private void registerCursor(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException, NoSuchMethodException {
        initAtRuntime(access, Cursor.class);
        JNIRuntimeAccess.register(Cursor.class.getDeclaredField("pData"));
        JNIRuntimeAccess.register(Cursor.class.getDeclaredField("type"));
        JNIRuntimeAccess.register(Cursor.class.getDeclaredMethod("setPData", long.class));
    }

    private void registerInvalidPipeException(ClassInitializationSupport classInitializationSupport) {
        classInitializationSupport.initializeAtRunTime(InvalidPipeException.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(InvalidPipeException.class);
    }

    private void registerSurfaceData(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException {
        initAtRuntime(access, SurfaceData.class);
        JNIRuntimeAccess.register(SurfaceData.class.getDeclaredField("pData"));
        JNIRuntimeAccess.register(SurfaceData.class.getDeclaredField("valid"));
    }

    private void registerJPEGHuffmanTable(ClassInitializationSupport classInitializationSupport) throws NoSuchFieldException {
        classInitializationSupport.initializeAtRunTime(JPEGHuffmanTable.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(JPEGHuffmanTable.class);
        JNIRuntimeAccess.register(JPEGHuffmanTable.class.getDeclaredField("lengths"));
        JNIRuntimeAccess.register(JPEGHuffmanTable.class.getDeclaredField("values"));
    }

    private void registerJPEGQTable(ClassInitializationSupport classInitializationSupport) throws NoSuchFieldException {
        classInitializationSupport.initializeAtRunTime(JPEGQTable.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(JPEGQTable.class);
        JNIRuntimeAccess.register(JPEGQTable.class.getDeclaredField("qTable"));
    }

    private void registerJPEGImageWriter(ClassInitializationSupport classInitializationSupport) throws NoSuchMethodException {
        classInitializationSupport.initializeAtRunTime(JPEGImageWriter.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(JPEGImageWriter.class);
        JNIRuntimeAccess.register(JPEGImageWriter.class.getDeclaredMethod("writeOutputData", byte[].class, int.class, int.class));
        JNIRuntimeAccess.register(JPEGImageWriter.class.getDeclaredMethod("warningOccurred", int.class));
        JNIRuntimeAccess.register(JPEGImageWriter.class.getDeclaredMethod("warningWithMessage", String.class));
        JNIRuntimeAccess.register(JPEGImageWriter.class.getDeclaredMethod("grabPixels", int.class));
    }

    private void registerColor(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException, NoSuchMethodException {
        initAtRuntime(access, Color.class);
        JNIRuntimeAccess.register(Color.class.getDeclaredField("value"));
        JNIRuntimeAccess.register(Color.class.getDeclaredMethod("getRGB"));
    }

    private void registerKernel(ClassInitializationSupport classInitializationSupport) throws NoSuchFieldException {
        classInitializationSupport.initializeAtRunTime(Kernel.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(Kernel.class);
        JNIRuntimeAccess.register(Kernel.class.getDeclaredField("width"));
        JNIRuntimeAccess.register(Kernel.class.getDeclaredField("height"));
        JNIRuntimeAccess.register(Kernel.class.getDeclaredField("data"));
    }

    private void registerSampleModel(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException, NoSuchMethodException {
        initAtRuntime(access, SampleModel.class);
        JNIRuntimeAccess.register(SampleModel.class.getDeclaredField("width"));
        JNIRuntimeAccess.register(SampleModel.class.getDeclaredField("height"));
        JNIRuntimeAccess.register(SampleModel.class.getDeclaredMethod("getPixels", int.class, int.class, int.class, int.class, int[].class, DataBuffer.class));
        JNIRuntimeAccess.register(SampleModel.class.getDeclaredMethod("setPixels", int.class, int.class, int.class, int.class, int[].class, DataBuffer.class));

        JNIRuntimeAccess.register(SinglePixelPackedSampleModel.class.getDeclaredField("bitMasks"));
        JNIRuntimeAccess.register(SinglePixelPackedSampleModel.class.getDeclaredField("bitOffsets"));
        JNIRuntimeAccess.register(SinglePixelPackedSampleModel.class.getDeclaredField("bitSizes"));
        JNIRuntimeAccess.register(SinglePixelPackedSampleModel.class.getDeclaredField("maxBitSize"));
    }

    private void registerColorModel(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchFieldException, NoSuchMethodException {
        initAtRuntime(access, ColorModel.class);
        JNIRuntimeAccess.register(ColorModel.class.getDeclaredField("pData"));
        JNIRuntimeAccess.register(ColorModel.class.getDeclaredField("nBits"));
        JNIRuntimeAccess.register(ColorModel.class.getDeclaredField("colorSpace"));
        JNIRuntimeAccess.register(ColorModel.class.getDeclaredField("numComponents"));
        JNIRuntimeAccess.register(ColorModel.class.getDeclaredField("supportsAlpha"));
        JNIRuntimeAccess.register(ColorModel.class.getDeclaredField("isAlphaPremultiplied"));
        JNIRuntimeAccess.register(ColorModel.class.getDeclaredField("transparency"));
        JNIRuntimeAccess.register(ColorModel.class.getDeclaredField("colorSpaceType"));
        JNIRuntimeAccess.register(ColorModel.class.getDeclaredField("is_sRGB"));
        JNIRuntimeAccess.register(ColorModel.class.getDeclaredMethod("getRGB", Object.class));
        JNIRuntimeAccess.register(ColorModel.class.getDeclaredMethod("getRGBdefault"));

        JNIRuntimeAccess.register(IndexColorModel.class.getDeclaredField("rgb"));
        JNIRuntimeAccess.register(IndexColorModel.class.getDeclaredField("allgrayopaque"));
        JNIRuntimeAccess.register(IndexColorModel.class.getDeclaredField("map_size"));
        JNIRuntimeAccess.register(IndexColorModel.class.getDeclaredField("colorData"));
        JNIRuntimeAccess.register(IndexColorModel.class.getDeclaredField("transparent_index"));
    }

    private void registerRaster(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchMethodException, NoSuchFieldException {
        initAtRuntime(access, Raster.class);
        JNIRuntimeAccess.register(Raster.class.getDeclaredMethod("getDataElements", int.class, int.class, int.class, int.class, Object.class));
        JNIRuntimeAccess.register(Raster.class.getDeclaredField("width"));
        JNIRuntimeAccess.register(Raster.class.getDeclaredField("height"));
        JNIRuntimeAccess.register(Raster.class.getDeclaredField("numBands"));
        JNIRuntimeAccess.register(Raster.class.getDeclaredField("minX"));
        JNIRuntimeAccess.register(Raster.class.getDeclaredField("minY"));
        JNIRuntimeAccess.register(Raster.class.getDeclaredField("sampleModelTranslateX"));
        JNIRuntimeAccess.register(Raster.class.getDeclaredField("sampleModelTranslateY"));
        JNIRuntimeAccess.register(Raster.class.getDeclaredField("sampleModel"));
        JNIRuntimeAccess.register(Raster.class.getDeclaredField("numDataElements"));
        JNIRuntimeAccess.register(Raster.class.getDeclaredField("numBands"));
        JNIRuntimeAccess.register(Raster.class.getDeclaredField("dataBuffer"));

        JNIRuntimeAccess.register(ByteComponentRaster.class.getDeclaredField("data"));
        JNIRuntimeAccess.register(ByteComponentRaster.class.getDeclaredField("scanlineStride"));
        JNIRuntimeAccess.register(ByteComponentRaster.class.getDeclaredField("pixelStride"));
        JNIRuntimeAccess.register(ByteComponentRaster.class.getDeclaredField("bandOffset"));
        JNIRuntimeAccess.register(ByteComponentRaster.class.getDeclaredField("dataOffsets"));
        JNIRuntimeAccess.register(ByteComponentRaster.class.getDeclaredField("type"));

        JNIRuntimeAccess.register(BytePackedRaster.class.getDeclaredField("data"));
        JNIRuntimeAccess.register(BytePackedRaster.class.getDeclaredField("scanlineStride"));
        JNIRuntimeAccess.register(BytePackedRaster.class.getDeclaredField("pixelBitStride"));
        JNIRuntimeAccess.register(BytePackedRaster.class.getDeclaredField("type"));
        JNIRuntimeAccess.register(BytePackedRaster.class.getDeclaredField("dataBitOffset"));

        JNIRuntimeAccess.register(ShortComponentRaster.class.getDeclaredField("data"));
        JNIRuntimeAccess.register(ShortComponentRaster.class.getDeclaredField("scanlineStride"));
        JNIRuntimeAccess.register(ShortComponentRaster.class.getDeclaredField("pixelStride"));
        JNIRuntimeAccess.register(ShortComponentRaster.class.getDeclaredField("bandOffset"));
        JNIRuntimeAccess.register(ShortComponentRaster.class.getDeclaredField("dataOffsets"));
        JNIRuntimeAccess.register(ShortComponentRaster.class.getDeclaredField("type"));

        JNIRuntimeAccess.register(IntegerComponentRaster.class.getDeclaredField("data"));
        JNIRuntimeAccess.register(IntegerComponentRaster.class.getDeclaredField("scanlineStride"));
        JNIRuntimeAccess.register(IntegerComponentRaster.class.getDeclaredField("pixelStride"));
        JNIRuntimeAccess.register(IntegerComponentRaster.class.getDeclaredField("dataOffsets"));
        JNIRuntimeAccess.register(IntegerComponentRaster.class.getDeclaredField("bandOffset"));
        JNIRuntimeAccess.register(IntegerComponentRaster.class.getDeclaredField("type"));
        JNIRuntimeAccess.register(IntegerComponentRaster.class.getDeclaredMethod("setDataElements", int.class, int.class, int.class, int.class, Object.class));
    }

    private void registerBufferedImage(FeatureImpl.BeforeAnalysisAccessImpl access) throws NoSuchMethodException, NoSuchFieldException {
        initAtRuntime(access, BufferedImage.class);
        JNIRuntimeAccess.register(BufferedImage.class.getDeclaredMethod("getRGB", int.class, int.class, int.class, int.class, int[].class, int.class, int.class));
        JNIRuntimeAccess.register(BufferedImage.class.getDeclaredMethod("setRGB", int.class, int.class, int.class, int.class, int[].class, int.class, int.class));
        JNIRuntimeAccess.register(BufferedImage.class.getDeclaredField("raster"));
        JNIRuntimeAccess.register(BufferedImage.class.getDeclaredField("imageType"));
        JNIRuntimeAccess.register(BufferedImage.class.getDeclaredField("colorModel"));
    }

    private void registerDisposer(ClassInitializationSupport classInitializationSupport) throws NoSuchMethodException {
        classInitializationSupport.initializeAtRunTime(Disposer.class, AWT_SUPPORT);
        JNIRuntimeAccess.register(Disposer.class);
        JNIRuntimeAccess.register(Disposer.class.getDeclaredMethod("addRecord", Object.class, long.class, long.class));
    }

    private void registerXWindow(FeatureImpl.BeforeAnalysisAccessImpl access) throws ClassNotFoundException, NoSuchFieldException {
        Class<?> clazz = initAtRuntime(access, "sun.awt.X11.XWindow");
        JNIRuntimeAccess.register(clazz.getDeclaredField("target"));
        JNIRuntimeAccess.register(clazz.getDeclaredField("graphicsConfig"));
        JNIRuntimeAccess.register(clazz.getDeclaredField("drawState"));

        initAtRuntime(access, XBaseWindow.class);
        JNIRuntimeAccess.register(XBaseWindow.class.getDeclaredField("window"));
    }

    private Class<?> initAtRuntime(FeatureImpl.BeforeAnalysisAccessImpl access, Class<?> clazz) {
        ClassInitializationSupport classInitializationSupport = access.getHostVM().getClassInitializationSupport();
        classInitializationSupport.initializeAtRunTime(clazz, AWT_SUPPORT);
        JNIRuntimeAccess.register(clazz);
        runtimeClasses.add(clazz);
        return clazz;
    }

    private Class<?> initAtRuntime(FeatureImpl.BeforeAnalysisAccessImpl access, String className) throws ClassNotFoundException {
        Class<?> clazz = getClass(className);
        return initAtRuntime(access, clazz);
    }

    private Class<?> getClass(String className) throws ClassNotFoundException {
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    private void initAtRuntimeSimple(FeatureImpl.BeforeAnalysisAccessImpl access, Class<?> clazz) {
        ClassInitializationSupport classInitializationSupport = access.getHostVM().getClassInitializationSupport();
        classInitializationSupport.initializeAtRunTime(clazz, AWT_SUPPORT);
        runtimeClassesSimple.add(clazz.getName());
    }

    private void initAtRuntimeSimple(FeatureImpl.BeforeAnalysisAccessImpl access, String className) {
        ClassInitializationSupport classInitializationSupport = access.getHostVM().getClassInitializationSupport();
        classInitializationSupport.initializeAtRunTime(className, AWT_SUPPORT);
        runtimeClassesSimple.add(className);
    }

    private void registerReflection(String className) throws ClassNotFoundException {
        RuntimeReflectionSupport reflectionSupport = ImageSingletons.lookup(RuntimeReflectionSupport.class);
        Class<?> clazz = getClass(className);
        reflectionClasses.add(clazz);
        reflectionSupport.register(clazz);
        reflectionSupport.register(clazz.getDeclaredConstructors());
    }

    private void registerChildClasses(FeatureImpl.BeforeAnalysisAccessImpl access) throws ClassNotFoundException {
        initAtRuntime(access, "java.awt.Container");
        initAtRuntime(access, "java.awt.Window");
        initAtRuntime(access, "java.awt.Frame");
        initAtRuntime(access, "javax.swing.JFrame");
        initAtRuntime(access, "net.sourceforge.plantuml.swing.MainWindow2");
        initAtRuntime(access, "net.sourceforge.plantuml.Splash");
        initAtRuntimeSimple(access, "java.awt.image.BufferedImage");
        initAtRuntimeSimple(access, "java.awt.Graphics2D");
        initAtRuntimeSimple(access, "java.awt.geom.Rectangle2D");
        initAtRuntime(access, "javax.swing.JComponent");
        initAtRuntime(access, "javax.swing.JList");
        initAtRuntime(access, "javax.swing.AbstractButton");
        initAtRuntime(access, "javax.swing.JButton");
        initAtRuntime(access, "javax.swing.text.JTextComponent");
        initAtRuntime(access, "javax.swing.JTextField");
        initAtRuntime(access, "java.awt.image.PackedColorModel");
        initAtRuntime(access, "java.awt.image.DirectColorModel");
        initAtRuntime(access, "java.awt.image.WritableRaster");
        initAtRuntime(access, "javax.swing.JScrollPane");
        initAtRuntime(access, "java.awt.image.IndexColorModel");
        initAtRuntime(access, "javax.swing.JPanel");
        initAtRuntime(access, "javax.swing.JLabel");
        initAtRuntimeSimple(access, "javax.swing.border.CompoundBorder");
        initAtRuntime(access, "java.awt.image.ComponentColorModel");
        initAtRuntime(access, "javax.swing.JMenuBar");
        initAtRuntime(access, "javax.swing.JMenuItem");
        initAtRuntime(access, "javax.swing.JMenu");
        initAtRuntimeSimple(access, "java.awt.Dimension");
        initAtRuntime(access, "java.awt.Dialog");
        initAtRuntimeSimple(access, "sun.awt.SunToolkit");
        initAtRuntimeSimple(access, "java.awt.image.DataBufferInt");
        initAtRuntimeSimple(access, "java.awt.image.DataBufferUShort");
        initAtRuntimeSimple(access, "java.awt.image.DataBufferByte");
        initAtRuntimeSimple(access, "javax.swing.border.EmptyBorder");
        initAtRuntime(access, "javax.swing.JRootPane");
        initAtRuntimeSimple(access, "java.awt.Rectangle");
        initAtRuntime(access, "java.awt.event.ComponentEvent");
        initAtRuntime(access, "java.awt.event.FocusEvent");
        initAtRuntime(access, "sun.awt.CausedFocusEvent");
        initAtRuntime(access, "java.awt.image.MultiPixelPackedSampleModel");
        initAtRuntime(access, "sun.awt.image.SunWritableRaster");
        initAtRuntime(access, "sun.awt.image.BytePackedRaster");
        initAtRuntime(access, "java.awt.image.SinglePixelPackedSampleModel");
        initAtRuntime(access, "sun.awt.image.IntegerComponentRaster");
        initAtRuntime(access, "sun.awt.image.IntegerInterleavedRaster");
        initAtRuntime(access, "sun.awt.image.ShortComponentRaster");
        initAtRuntime(access, "sun.awt.image.ShortInterleavedRaster");
        initAtRuntime(access, "sun.awt.image.ByteComponentRaster");
        initAtRuntime(access, "sun.awt.image.ByteInterleavedRaster");
        initAtRuntime(access, "sun.awt.EmbeddedFrame");
        initAtRuntime(access, "java.awt.Panel");
        initAtRuntime(access, "java.applet.Applet");
        initAtRuntime(access, "java.awt.event.PaintEvent");
        initAtRuntime(access, "java.awt.event.WindowEvent");
        initAtRuntime(access, "sun.awt.TimedWindowEvent");
        initAtRuntime(access, "java.awt.image.PixelInterleavedSampleModel");
        initAtRuntime(access, "java.awt.geom.Path2D$Float");
        initAtRuntimeSimple(access, "javax.swing.plaf.metal.DefaultMetalTheme");
        initAtRuntimeSimple(access, "javax.swing.plaf.metal.MetalHighContrastTheme");
        initAtRuntime(access, "java.awt.MenuItem");
        initAtRuntime(access, "javax.swing.JLayeredPane");
        initAtRuntime(access, "java.awt.Menu");
        initAtRuntime(access, "java.awt.PopupMenu");
        initAtRuntimeSimple(access, "javax.swing.plaf.metal.OceanTheme");
        initAtRuntime(access, "java.awt.event.HierarchyEvent");
        initAtRuntimeSimple(access, "java.awt.DefaultKeyboardFocusManager");
        initAtRuntime(access, "java.awt.event.ContainerEvent");
        initAtRuntime(access, "javax.swing.plaf.ColorUIResource");
        initAtRuntime(access, "javax.swing.plaf.InsetsUIResource");
        initAtRuntime(access, "java.awt.event.InputEvent");
        initAtRuntime(access, "java.awt.event.KeyEvent");
        initAtRuntime(access, "java.awt.event.MouseEvent");
        initAtRuntime(access, "sun.awt.dnd.SunDropTargetEvent");
        initAtRuntime(access, "java.awt.event.MouseWheelEvent");
        initAtRuntime(access, "java.awt.event.InputMethodEvent");
        initAtRuntime(access, "sun.awt.im.CompositionArea");
        initAtRuntime(access, "java.awt.event.InvocationEvent");
        initAtRuntimeSimple(access, "javax.swing.plaf.BorderUIResource$EmptyBorderUIResource");
        initAtRuntimeSimple(access, "javax.swing.plaf.DimensionUIResource");
        initAtRuntime(access, "javax.swing.JViewport");
        initAtRuntime(access, "javax.swing.JScrollBar");
        initAtRuntime(access, "javax.swing.JScrollPane$ScrollBar");
        initAtRuntime(access, "javax.swing.JPopupMenu");
        initAtRuntimeSimple(access, "javax.swing.KeyStroke");
        initAtRuntime(access, "sun.awt.PeerEvent");
        initAtRuntime(access, "javax.swing.JPasswordField");
        initAtRuntimeSimple(access, "javax.swing.FocusManager");
        initAtRuntime(access, "sun.awt.image.BufImgSurfaceData");
        initAtRuntimeSimple(access, "net.sourceforge.plantuml.Dimension2DDouble");
        initAtRuntime(access, "javax.swing.text.DefaultCaret");
        initAtRuntime(access, "javax.swing.text.JTextComponent$ComposedTextCaret");
        initAtRuntime(access, "java.awt.SequencedEvent");
        initAtRuntimeSimple(access, "javax.swing.border.LineBorder");
        initAtRuntimeSimple(access, "javax.swing.plaf.basic.BasicBorders$MarginBorder");
        initAtRuntime(access, "javax.swing.JInternalFrame");
        initAtRuntime(access, "javax.swing.JWindow");
        initAtRuntime(access, "javax.swing.Popup$HeavyWeightWindow");
        initAtRuntime(access, "javax.swing.JLayer");
        initAtRuntime(access, "javax.swing.CellRendererPane");
        initAtRuntime(access, "java.awt.event.ActionEvent");
        initAtRuntime(access, "java.awt.SentEvent");
        initAtRuntime(access, "java.awt.DefaultKeyboardFocusManager$DefaultKeyboardFocusManagerSentEvent");
        initAtRuntimeSimple(access, "java.awt.image.DataBufferDouble");
        initAtRuntimeSimple(access, "java.awt.image.DataBufferFloat");
        initAtRuntimeSimple(access, "java.awt.image.DataBufferShort");
        initAtRuntime(access, "java.awt.event.AdjustmentEvent");
        initAtRuntime(access, "java.awt.event.ItemEvent");
        initAtRuntime(access, "java.awt.List");
        initAtRuntime(access, "java.awt.Choice");
        initAtRuntime(access, "java.awt.Button");
        initAtRuntime(access, "net.sourceforge.plantuml.swing.ImageWindow2");
        initAtRuntimeSimple(access, "sun.font.FontDesignMetrics");
        initAtRuntimeSimple(access, "sun.font.CompositeFont");
        initAtRuntime(access, "javax.swing.plaf.FontUIResource");
        initAtRuntime(access, "javax.swing.JToggleButton");
        initAtRuntime(access, "javax.swing.JCheckBox");
        initAtRuntime(access, "net.sourceforge.plantuml.swing.ScrollablePicture");
        initAtRuntime(access, "javax.swing.JToolTip");
        initAtRuntime(access, "javax.swing.ImageIcon$3");
        initAtRuntime(access, "javax.swing.event.MenuKeyEvent");
        initAtRuntime(access, "javax.swing.JToolBar");
        initAtRuntimeSimple(access, "java.awt.geom.Rectangle2D$Double");
        initAtRuntimeSimple(access, "javax.swing.DebugGraphics");
        initAtRuntime(access, "javax.swing.JComboBox");
        initAtRuntimeSimple(access, "java.awt.image.VolatileImage");
        initAtRuntimeSimple(access, "sun.java2d.SunGraphics2D");
        initAtRuntime(access, "javax.swing.JDialog");
        initAtRuntime(access, "javax.swing.JApplet");
        initAtRuntime(access, "net.sourceforge.plantuml.swing.SpriteWindow");
        initAtRuntime(access, "net.sourceforge.plantuml.swing.AboutWindow");
        initAtRuntimeSimple(access, "sun.swing.ImageIconUIResource");
        initAtRuntime(access, "javax.swing.DefaultListCellRenderer");
        initAtRuntime(access, "javax.swing.DefaultListCellRenderer$UIResource");
        initAtRuntimeSimple(access, "javax.swing.plaf.metal.MetalBorders$DialogBorder");
        initAtRuntimeSimple(access, "javax.swing.plaf.metal.MetalBorders$QuestionDialogBorder");
        initAtRuntime(access, "javax.swing.JFileChooser");
        initAtRuntimeSimple(access, "javax.swing.plaf.metal.MetalBorders$FrameBorder");
        initAtRuntimeSimple(access, "javax.swing.plaf.metal.MetalBorders$ErrorDialogBorder");
        initAtRuntimeSimple(access, "javax.swing.plaf.metal.MetalBorders$WarningDialogBorder");
        initAtRuntimeSimple(access, "javax.swing.plaf.metal.MetalBorders$ButtonBorder");
        initAtRuntimeSimple(access, "javax.swing.plaf.metal.MetalBorders$RolloverMarginBorder");
        initAtRuntimeSimple(access, "javax.swing.plaf.metal.MetalBorders$RolloverButtonBorder");
        initAtRuntimeSimple(access, "java.awt.geom.RoundRectangle2D");
        initAtRuntimeSimple(access, "java.awt.geom.RoundRectangle2D$Float");
        initAtRuntimeSimple(access, "java.awt.geom.Rectangle2D$Float");
        initAtRuntime(access, "javax.swing.JTextArea");
        initAtRuntime(access, "sun.awt.im.InputMethodJFrame");
        initAtRuntime(access, "sun.awt.AWTAutoShutdown$1");
        initAtRuntime(access, "javax.swing.JOptionPane");
        initAtRuntime(access, "javax.swing.JEditorPane");
        initAtRuntimeSimple(access, "javax.swing.border.MatteBorder");
        initAtRuntimeSimple(access, "javax.swing.border.EtchedBorder");
        initAtRuntimeSimple(access, "javax.swing.filechooser.UnixFileSystemView");
        initAtRuntime(access, "javax.swing.SwingUtilities$SharedOwnerFrame");
        initAtRuntime(access, "javax.swing.JSeparator");
        initAtRuntimeSimple(access, "java.awt.geom.RoundRectangle2D$Double");
        initAtRuntimeSimple(access, "java.awt.geom.Ellipse2D");
        initAtRuntimeSimple(access, "java.awt.geom.Ellipse2D$Double");
        initAtRuntimeSimple(access, "java.awt.geom.Arc2D");
        initAtRuntimeSimple(access, "java.awt.geom.Arc2D$Double");
        initAtRuntimeSimple(access, "sun.awt.image.ToolkitImage");
        initAtRuntimeSimple(access, "sun.awt.image.MultiResolutionToolkitImage");
        initAtRuntime(access, "java.awt.Canvas");
        initAtRuntime(access, "java.awt.geom.Path2D$Double");
        initAtRuntime(access, "javax.swing.Popup$DefaultFrame");
        initAtRuntime(access, "net.sourceforge.plantuml.swing.LicenseWindow");
        initAtRuntime(access, "javax.swing.JPopupMenu$Separator");
        initAtRuntime(access, "java.awt.CheckboxMenuItem");
        initAtRuntime(access, "javax.swing.JCheckBoxMenuItem");
        initAtRuntime(access, "javax.swing.PopupFactory$MediumWeightPopup$MediumWeightComponent");
        initAtRuntime(access, "sun.java2d.loops.GraphicsPrimitiveProxy");
        initAtRuntimeSimple(access, "sun.font.PhysicalFont");
        initAtRuntimeSimple(access, "sun.font.NativeFont");
        initAtRuntimeSimple(access, "net.sourceforge.plantuml.activitydiagram3.ftile.FtileGeometry");
        initAtRuntimeSimple(access, "sun.font.FileFont");
        initAtRuntimeSimple(access, "sun.awt.image.BufferedImageGraphicsConfig");
        initAtRuntime(access, "sun.awt.UNIXToolkit");
        initAtRuntime(access, "sun.awt.X11.XToolkit");
        initAtRuntimeSimple(access, "sun.awt.X11GraphicsDevice");
        initAtRuntime(access, "sun.java2d.x11.XSurfaceData");
        initAtRuntimeSimple(access, "sun.awt.X11GraphicsConfig");
        initAtRuntime(access, "sun.awt.X11.XFontPeer");
        initAtRuntime(access, "sun.java2d.xr.XRGraphicsConfig");
        initAtRuntimeSimple(access, "javax.swing.border.BevelBorder");
        initAtRuntime(access, "sun.java2d.pipe.Region$ImmutableRegion");
        initAtRuntime(access, "sun.swing.PrintColorUIResource");
        initAtRuntimeSimple(access, "sun.awt.X11.XErrorEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XAnyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XKeyboardFocusManagerPeer");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbAnyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XModifierKeymap");
        initAtRuntimeSimple(access, "sun.awt.X11.XMotionEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XCrossingEvent");
        initAtRuntime(access, "sun.awt.X11.XComponentPeer");
        initAtRuntime(access, "sun.awt.X11.XCanvasPeer");
        initAtRuntime(access, "sun.awt.X11.XPanelPeer");
        initAtRuntime(access, "sun.awt.X11.XWindowPeer");
        initAtRuntime(access, "sun.java2d.loops.DrawLine$TraceDrawLine");
        initAtRuntime(access, "sun.java2d.loops.FillRect$TraceFillRect");
        initAtRuntime(access, "sun.java2d.loops.DrawRect$TraceDrawRect");
        initAtRuntime(access, "sun.java2d.loops.DrawPolygons$TraceDrawPolygons");
        initAtRuntime(access, "sun.java2d.loops.DrawPath$TraceDrawPath");
        initAtRuntime(access, "sun.java2d.loops.FillPath$TraceFillPath");
        initAtRuntime(access, "sun.java2d.loops.FillSpans$TraceFillSpans");
        initAtRuntime(access, "sun.java2d.loops.FillParallelogram$TraceFillParallelogram");
        initAtRuntime(access, "sun.java2d.loops.DrawParallelogram$TraceDrawParallelogram");
        initAtRuntime(access, "sun.java2d.loops.DrawGlyphList$TraceDrawGlyphList");
        initAtRuntime(access, "sun.java2d.loops.DrawGlyphListAA$TraceDrawGlyphListAA");
        initAtRuntime(access, "sun.java2d.loops.DrawGlyphListLCD$TraceDrawGlyphListLCD");
        initAtRuntime(access, "sun.java2d.loops.MaskFill$TraceMaskFill");
        initAtRuntime(access, "sun.java2d.loops.BlitBg$TraceBlitBg");
        initAtRuntime(access, "sun.java2d.loops.ScaledBlit$TraceScaledBlit");
        initAtRuntime(access, "sun.java2d.loops.MaskBlit$TraceMaskBlit");
        initAtRuntime(access, "sun.java2d.loops.Blit$TraceBlit");
        initAtRuntime(access, "sun.java2d.loops.TransformHelper$TraceTransformHelper");
        initAtRuntime(access, "sun.java2d.loops.FillRect$General");
        initAtRuntime(access, "sun.java2d.loops.DrawGlyphList$General");
        initAtRuntime(access, "sun.java2d.loops.DrawGlyphListAA$General");
        initAtRuntime(access, "sun.java2d.loops.MaskFill$General");
        initAtRuntime(access, "sun.java2d.loops.BlitBg$General");
        initAtRuntime(access, "sun.java2d.loops.MaskBlit$General");
        initAtRuntime(access, "sun.java2d.loops.Blit$GeneralXorBlit");
        initAtRuntime(access, "sun.java2d.loops.Blit$GeneralMaskBlit");
        initAtRuntime(access, "sun.java2d.loops.Blit$AnyBlit");
        initAtRuntimeSimple(access, "sun.awt.X11.XKeyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XReparentEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XConfigureEvent");
        initAtRuntime(access, "sun.awt.X11.XRootWindow");
        initAtRuntimeSimple(access, "sun.awt.X11.XButtonEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XClientMessageEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XPropertyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XExposeEvent");
        initAtRuntime(access, "sun.awt.event.IgnorePaintEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.AwtScreenData");
        initAtRuntimeSimple(access, "sun.awt.X11.XSetWindowAttributes");
        initAtRuntimeSimple(access, "sun.awt.HeadlessToolkit");
        initAtRuntimeSimple(access, "sun.awt.X11.XDestroyWindowEvent");
        initAtRuntime(access, "javax.swing.ImageIcon$2$1");
        initAtRuntimeSimple(access, "sun.awt.X11.XFocusChangeEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XGraphicsExposeEvent");
        initAtRuntime(access, "sun.awt.X11.XDecoratedPeer");
        initAtRuntime(access, "sun.awt.X11.XFramePeer");
        initAtRuntimeSimple(access, "sun.awt.X11.XNoExposeEvent");
        initAtRuntime(access, "sun.awt.X11.XEmbeddedFramePeer");
        initAtRuntimeSimple(access, "sun.awt.X11.XVisibilityEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XCreateWindowEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XUnmapEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XMapEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XMapRequestEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XGravityEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XResizeRequestEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XConfigureRequestEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XCirculateEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XCirculateRequestEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XSelectionClearEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XSelectionRequestEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XSelectionEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XColormapEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XMappingEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XKeymapEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbNewKeyboardNotifyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbMapNotifyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbStateNotifyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbControlsNotifyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbIndicatorNotifyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbNamesNotifyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbCompatMapNotifyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbBellNotifyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbActionMessageEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbAccessXNotifyEvent");
        initAtRuntimeSimple(access, "sun.awt.X11.XkbExtensionDeviceNotifyEvent");
        initAtRuntime(access, "sun.java2d.NullSurfaceData");
        initAtRuntime(access, "sun.java2d.opengl.GLXGraphicsConfig");
        initAtRuntimeSimple(access, "sun.awt.image.SunVolatileImage");
        initAtRuntimeSimple(access, "java.awt.geom.Ellipse2D$Float");
        initAtRuntimeSimple(access, "java.awt.TexturePaintContext$Int");
        initAtRuntimeSimple(access, "java.awt.TexturePaintContext$ByteFilter");
        initAtRuntimeSimple(access, "java.awt.TexturePaintContext$Byte");
        initAtRuntimeSimple(access, "java.awt.TexturePaintContext$Any");
        initAtRuntimeSimple(access, "sun.print.PrinterGraphicsConfig");
        initAtRuntime(access, "java.awt.image.BandedSampleModel");
        initAtRuntime(access, "java.awt.SystemColor");
        initAtRuntime(access, "sun.java2d.x11.X11SurfaceData");
        initAtRuntimeSimple(access, "sun.awt.X11.XDnDDragSourceProtocol");
        initAtRuntimeSimple(access, "sun.awt.X11.MotifDnDDragSourceProtocol");
        initAtRuntimeSimple(access, "sun.awt.X11.XDnDDropTargetProtocol");
        initAtRuntime(access, "sun.java2d.x11.X11PMBlitLoops");
        initAtRuntimeSimple(access, "sun.awt.X11.MotifDnDDropTargetProtocol");
        initAtRuntime(access, "sun.java2d.x11.X11PMBlitBgLoops");
        initAtRuntime(access, "sun.java2d.x11.X11PMBlitLoops$DelegateBlitLoop");
        initAtRuntime(access, "sun.java2d.xr.XRMaskFill");
        initAtRuntime(access, "sun.java2d.xr.XRMaskBlit");
        initAtRuntime(access, "sun.java2d.xr.XRPMBlit");
        initAtRuntime(access, "sun.java2d.xr.XRPMScaledBlit");
        initAtRuntime(access, "sun.java2d.loops.TransformBlit");
        initAtRuntime(access, "sun.java2d.xr.XRPMTransformedBlit");
        initAtRuntime(access, "sun.java2d.xr.XrSwToPMBlit");
        initAtRuntime(access, "sun.java2d.xr.XrSwToPMScaledBlit");
        initAtRuntime(access, "sun.java2d.xr.XrSwToPMTransformedBlit");
        initAtRuntimeSimple(access, "sun.awt.X11.XWindowAttributes");
        initAtRuntime(access, "sun.java2d.loops.TransformBlit$TraceTransformBlit");
        initAtRuntime(access, "sun.java2d.x11.X11SurfaceData$X11PixmapSurfaceData");
        initAtRuntime(access, "sun.java2d.opengl.OGLSurfaceData");
        initAtRuntime(access, "sun.java2d.opengl.GLXSurfaceData");
        initAtRuntime(access, "sun.java2d.opengl.GLXSurfaceData$GLXOffScreenSurfaceData");
        initAtRuntime(access, "sun.java2d.xr.XRSurfaceData$XRPixmapSurfaceData");
        initAtRuntime(access, "sun.java2d.opengl.GLXSurfaceData$GLXVSyncOffScreenSurfaceData");
        initAtRuntime(access, "sun.awt.X11.XEmbedCanvasPeer");
        initAtRuntimeSimple(access, "java.awt.dnd.DropTargetDragEvent");
        initAtRuntimeSimple(access, "java.awt.dnd.DropTargetDropEvent");
        initAtRuntime(access, "sun.java2d.xr.XRSurfaceData$XRInternalSurfaceData");
        initAtRuntime(access, "sun.java2d.pipe.BufferedMaskFill");
        initAtRuntime(access, "sun.java2d.opengl.OGLMaskFill");
        initAtRuntime(access, "sun.java2d.pipe.BufferedMaskBlit");
        initAtRuntime(access, "sun.java2d.opengl.OGLMaskBlit");
        initAtRuntime(access, "sun.java2d.opengl.OGLSwToSurfaceBlit");
        initAtRuntime(access, "sun.java2d.opengl.OGLSwToTextureBlit");
        initAtRuntime(access, "sun.java2d.opengl.OGLSwToSurfaceTransform");
        initAtRuntime(access, "sun.java2d.opengl.OGLSurfaceToSwBlit");
        initAtRuntime(access, "sun.java2d.opengl.OGLSurfaceToSurfaceBlit");
        initAtRuntime(access, "sun.java2d.opengl.OGLSurfaceToSurfaceScale");
        initAtRuntime(access, "sun.java2d.opengl.OGLSurfaceToSurfaceTransform");
        initAtRuntime(access, "sun.java2d.opengl.OGLRTTSurfaceToSurfaceBlit");
        initAtRuntime(access, "sun.java2d.opengl.OGLRTTSurfaceToSurfaceScale");
        initAtRuntime(access, "sun.java2d.opengl.OGLRTTSurfaceToSurfaceTransform");
        initAtRuntime(access, "sun.java2d.opengl.OGLGeneralBlit");
        initAtRuntime(access, "sun.java2d.opengl.OGLAnyCompositeBlit");
        initAtRuntime(access, "sun.java2d.opengl.OGLSwToSurfaceScale");
        initAtRuntime(access, "sun.java2d.opengl.OGLGeneralTransformedBlit");
        initAtRuntime(access, "sun.java2d.opengl.OGLTextureToSurfaceBlit");
        initAtRuntime(access, "sun.java2d.opengl.OGLTextureToSurfaceScale");
        initAtRuntime(access, "sun.java2d.opengl.OGLTextureToSurfaceTransform");
        initAtRuntimeSimple(access, "sun.awt.X11.XDataTransferer");
        initAtRuntime(access, "java.awt.MenuBar");
        initAtRuntimeSimple(access, "sun.awt.X11.XSizeHints");
        initAtRuntimeSimple(access, "sun.awt.X11.XWMHints");

        initAtRuntimeSimple(access, "sun.awt.X11.PropMwmHints");
        initAtRuntimeSimple(access, "sun.awt.X11.AwtGraphicsConfigData");
        initAtRuntime(access, "javax.swing.JToolBar$Separator");
        initAtRuntime(access, "sun.awt.X11.XFocusProxyWindow");
        initAtRuntime(access, "sun.awt.X11.XLightweightFramePeer");
        initAtRuntime(access, "sun.awt.UngrabEvent");
        initAtRuntime(access, "sun.awt.X11.XDialogPeer");
        initAtRuntimeSimple(access, "sun.awt.X11.XVisualInfo");
        initAtRuntimeSimple(access, "sun.awt.X11.XRenderPictFormat");
        initAtRuntimeSimple(access, "sun.awt.X11.XRenderDirectFormat");

        initAtRuntimeSimple(access, "sun.awt.X11.XEmbedCanvasPeer$XEmbedServer");
        initAtRuntime(access, "sun.awt.X11.XWarningWindow");
        initAtRuntime(access, "sun.awt.X11.XIconWindow");
        initAtRuntime(access, "sun.awt.X11.XContentWindow");
        initAtRuntime(access, "sun.awt.X11.XBaseMenuWindow");
        initAtRuntime(access, "sun.awt.X11.XMenuBarPeer");
        initAtRuntime(access, "sun.awt.X11.XMenuWindow");
        initAtRuntime(access, "sun.awt.X11.XPopupMenuPeer");
        initAtRuntime(access, "sun.awt.im.SimpleInputMethodWindow");
        initAtRuntime(access, "sun.awt.ModalityEvent");
        initAtRuntime(access, "sun.awt.X11.XChoicePeer");
        initAtRuntime(access, "sun.awt.X11.XButtonPeer");
        initAtRuntime(access, "sun.awt.X11.XListPeer");
        initAtRuntime(access, "java.awt.Scrollbar");
        initAtRuntime(access, "sun.awt.X11.InfoWindow");
        initAtRuntime(access, "sun.awt.X11.InfoWindow$Tooltip");
        initAtRuntime(access, "sun.awt.X11.XChoicePeer$UnfurledChoice");
        initAtRuntimeSimple(access, "sun.awt.X11.XVerticalScrollbar");
        initAtRuntimeSimple(access, "sun.awt.X11.XHorizontalScrollbar");
        initAtRuntimeSimple(access, "sun.awt.X11.awtImageData");
        initAtRuntimeSimple(access, "sun.awt.X11.XIconSize");
        initAtRuntime(access, "sun.awt.CustomCursor");
        initAtRuntime(access, "sun.awt.X11CustomCursor");
        initAtRuntime(access, "sun.awt.X11.XCustomCursor");
        initAtRuntimeSimple(access, "sun.awt.X11.ColorData");
        initAtRuntimeSimple(access, "sun.awt.X11.XPixmapFormatValues");
        initAtRuntimeSimple(access, "sun.print.ProxyGraphics");
        initAtRuntimeSimple(access, "sun.print.ProxyPrintGraphics");
        initAtRuntime(access, "javax.swing.plaf.basic.BasicSplitPaneDivider");
        initAtRuntime(access, "sun.awt.X11.XInputMethod");
        initAtRuntime(access, "javax.swing.event.InternalFrameEvent");
        initAtRuntime(access, "sun.java2d.x11.X11SurfaceData$X11WindowSurfaceData");
        initAtRuntime(access, "sun.java2d.opengl.GLXSurfaceData$GLXWindowSurfaceData");
        initAtRuntime(access, "sun.java2d.xr.XRSurfaceData$XRWindowSurfaceData");
        initAtRuntime(access, "sun.awt.image.OffScreenImage");
        initAtRuntime(access, "java.awt.Label");
        initAtRuntime(access, "sun.awt.X11.InfoWindow$Balloon");
        initAtRuntimeSimple(access, "sun.awt.datatransfer.DataTransferer$DataFlavorComparator");
        initAtRuntimeSimple(access, "sun.awt.X11.XColor");
        initAtRuntime(access, "sun.awt.X11.InfoWindow$1");
        initAtRuntimeSimple(access, "sun.awt.datatransfer.DataTransferer$IndexOrderComparator");
        initAtRuntimeSimple(access, "sun.awt.datatransfer.DataTransferer$CharsetComparator");
        initAtRuntime(access, "sun.awt.X11.XLabelPeer");
        initAtRuntimeSimple(access, "sun.awt.X11.XEmbeddingContainer");

        initAtRuntime(access, "sun.awt.X11.XScrollbarPeer");
        initAtRuntimeSimple(access, "com.sun.java.swing.plaf.motif.MotifBorders$BevelBorder");
        initAtRuntimeSimple(access, "com.sun.java.swing.plaf.motif.MotifBorders$FocusBorder");
        initAtRuntimeSimple(access, "javax.swing.plaf.BorderUIResource$CompoundBorderUIResource");
        initAtRuntimeSimple(access, "com.sun.java.swing.plaf.motif.MotifBorders$ButtonBorder");
        initAtRuntimeSimple(access, "com.sun.java.swing.plaf.motif.MotifBorders$ToggleButtonBorder");
        initAtRuntimeSimple(access, "com.sun.java.swing.plaf.motif.MotifBorders$MotifPopupMenuBorder");
        initAtRuntimeSimple(access, "com.sun.java.swing.plaf.motif.MotifBorders$MenuBarBorder");
        initAtRuntimeSimple(access, "javax.swing.plaf.BorderUIResource$LineBorderUIResource");
        initAtRuntime(access, "javax.swing.tree.DefaultTreeCellRenderer");
        initAtRuntime(access, "com.sun.java.swing.plaf.motif.MotifTreeCellRenderer");

        registerReflection("sun.java2d.loops.DrawLine");
        registerReflection("sun.java2d.loops.FillRect");
        registerReflection("sun.java2d.loops.DrawRect");
        registerReflection("sun.java2d.loops.DrawPolygons");
        registerReflection("sun.java2d.loops.DrawPath");
        registerReflection("sun.java2d.loops.FillPath");
        registerReflection("sun.java2d.loops.FillSpans");
        registerReflection("sun.java2d.loops.FillParallelogram");
        registerReflection("sun.java2d.loops.DrawParallelogram");
        registerReflection("sun.java2d.loops.DrawGlyphList");
        registerReflection("sun.java2d.loops.DrawGlyphListAA");
        registerReflection("sun.java2d.loops.DrawGlyphListLCD");
        registerReflection("sun.java2d.loops.GraphicsPrimitiveProxy");
        registerReflection("sun.java2d.loops.TransformBlit");
        registerReflection("sun.java2d.xr.XrSwToPMTransformedBlit");
        registerReflection("sun.java2d.loops.FillParallelogram$TraceFillParallelogram");
        registerReflection("sun.java2d.loops.TransformBlit$TraceTransformBlit");
        registerReflection("sun.java2d.opengl.OGLRTTSurfaceToSurfaceTransform");
        registerReflection("sun.java2d.loops.Blit");
        registerReflection("sun.java2d.xr.XRPMBlit");
        registerReflection("sun.java2d.loops.DrawPolygons$TraceDrawPolygons");
        registerReflection("sun.java2d.loops.TransformHelper");
        registerReflection("sun.java2d.loops.MaskFill");
        registerReflection("sun.java2d.xr.XRMaskFill");
        registerReflection("sun.java2d.loops.BlitBg");
        registerReflection("sun.java2d.x11.X11PMBlitBgLoops");
        registerReflection("sun.java2d.loops.ScaledBlit");
        registerReflection("sun.java2d.xr.XRPMScaledBlit");
        registerReflection("sun.java2d.loops.FillRect$General");
        registerReflection("sun.java2d.xr.XRPMTransformedBlit");
        registerReflection("sun.java2d.loops.MaskFill$General");
        registerReflection("sun.java2d.opengl.OGLGeneralTransformedBlit");
        registerReflection("sun.java2d.opengl.OGLSwToTextureBlit");
        registerReflection("sun.java2d.loops.MaskFill$TraceMaskFill");
        registerReflection("sun.java2d.opengl.OGLTextureToSurfaceBlit");
        registerReflection("sun.java2d.loops.MaskBlit");
        registerReflection("sun.java2d.pipe.BufferedMaskBlit");
        registerReflection("sun.java2d.loops.DrawRect$TraceDrawRect");
        registerReflection("sun.java2d.opengl.OGLSurfaceToSurfaceScale");
        registerReflection("sun.java2d.opengl.OGLSwToSurfaceBlit");
        registerReflection("sun.java2d.pipe.BufferedMaskFill");
        registerReflection("sun.java2d.opengl.OGLSurfaceToSurfaceBlit");
        registerReflection("sun.java2d.loops.DrawPath$TraceDrawPath");
        registerReflection("sun.java2d.opengl.OGLSwToSurfaceScale");
        registerReflection("sun.java2d.loops.TransformHelper$TraceTransformHelper");
        registerReflection("sun.java2d.opengl.OGLSurfaceToSwBlit");
        registerReflection("sun.java2d.opengl.OGLAnyCompositeBlit");
        registerReflection("sun.java2d.opengl.OGLMaskFill");
        registerReflection("sun.java2d.loops.ScaledBlit$TraceScaledBlit");
        registerReflection("sun.java2d.opengl.OGLRTTSurfaceToSurfaceScale");
        registerReflection("sun.java2d.loops.DrawGlyphList$General");
        registerReflection("sun.java2d.loops.DrawParallelogram$TraceDrawParallelogram");
        registerReflection("sun.java2d.opengl.OGLRTTSurfaceToSurfaceBlit");
        registerReflection("sun.java2d.loops.DrawLine$TraceDrawLine");
        registerReflection("sun.java2d.loops.DrawGlyphListAA$TraceDrawGlyphListAA");
        registerReflection("sun.java2d.loops.BlitBg$General");
        registerReflection("sun.java2d.opengl.OGLTextureToSurfaceScale");
        registerReflection("sun.java2d.opengl.OGLSurfaceToSurfaceTransform");
        registerReflection("sun.java2d.loops.DrawGlyphListLCD$TraceDrawGlyphListLCD");
        registerReflection("sun.java2d.x11.X11PMBlitLoops$DelegateBlitLoop");
        registerReflection("sun.java2d.x11.X11PMBlitLoops");
        registerReflection("sun.java2d.opengl.OGLGeneralBlit");
        registerReflection("sun.java2d.xr.XrSwToPMScaledBlit");
        registerReflection("sun.java2d.loops.MaskBlit$General");
        registerReflection("sun.java2d.loops.DrawGlyphListAA$General");
        registerReflection("sun.java2d.loops.BlitBg$TraceBlitBg");
        registerReflection("sun.java2d.loops.Blit$TraceBlit");
        registerReflection("sun.java2d.loops.Blit$AnyBlit");
        registerReflection("sun.java2d.xr.XrSwToPMBlit");
        registerReflection("sun.java2d.xr.XRMaskBlit");
        registerReflection("sun.java2d.loops.MaskBlit$TraceMaskBlit");
        registerReflection("sun.java2d.loops.DrawGlyphList$TraceDrawGlyphList");
        registerReflection("sun.java2d.opengl.OGLTextureToSurfaceTransform");
        registerReflection("sun.java2d.loops.FillRect$TraceFillRect");
        registerReflection("sun.java2d.loops.Blit$GeneralMaskBlit");
        registerReflection("sun.java2d.loops.FillPath$TraceFillPath");
        registerReflection("sun.java2d.opengl.OGLSwToSurfaceTransform");
        registerReflection("sun.java2d.loops.FillSpans$TraceFillSpans");
        registerReflection("sun.java2d.loops.Blit$GeneralXorBlit");
        registerReflection("sun.java2d.opengl.OGLMaskBlit");
    }
}
