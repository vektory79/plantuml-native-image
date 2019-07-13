package me.vektory79.plantuml;

import net.sourceforge.plantuml.Run;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String currentDir = System.getProperty("user.dir");
        System.out.println("user.dir=" + currentDir);
        System.setProperty("java.awt.headless", "true");

        if (args.length == 0) {
            Run.main(new String[]{"/media/data/java/plantuml-graal/examples/example1.txt"});
        } else {
            Run.main(args);
        }
    }
}
