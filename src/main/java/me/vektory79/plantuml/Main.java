package me.vektory79.plantuml;

import me.vektory79.plantuml.stubs.XToolkitStub;
import net.sourceforge.plantuml.Run;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        XToolkitStub.XToolkitStub_Thread_Recalc.start();
        Run.main(args);
    }
}
