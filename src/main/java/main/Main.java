package main;

import IO.FileHandler;
import generators.CodeGenerator;
import generators.IdGenerator;
import runnables.CodeLinkRunnable;

import static IO.Connector.*;

public class Main {

    private static SystemTrayHandler systemTrayHandler = new SystemTrayHandler();

    public static void main(String[] args) {




        setup();
        systemTrayHandler.start();
        Thread codeLinkThread = new Thread(new CodeLinkRunnable());
        codeLinkThread.start();

    }

    private static void setup() {

        FileHandler.configFileCreator();
        boolean failedToReadId = false;
        try {
            id = FileHandler.getValueFromXMLForKey("id");
        } catch (Exception ignored) {
            failedToReadId = true;
        }

        if (id.equals("0") || id.equals("") || !IdGenerator.validateId(id) || failedToReadId) {
            id = IdGenerator.generateNewId();
            CodeGenerator.addActivationCode();
        }

        System.out.println("Logged in: " + id);

    }

    public static SystemTrayHandler getSystemTrayHandler() {
        return systemTrayHandler;
    }
}
