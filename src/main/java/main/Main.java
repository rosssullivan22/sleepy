package main;

import IO.FileHandler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import generators.CodeGenerator;
import generators.IdGenerator;
import runnables.CodeLinkRunnable;
import runnables.MonitorRunnable;

import static IO.Connector.db;
import static IO.Connector.id;
import static constants.Constants.*;

public class Main {

    private static SystemTrayHandler systemTrayHandler = new SystemTrayHandler();

    private static Thread codeLinkThread = new Thread(new CodeLinkRunnable());
    private static Thread monitorThread = new Thread(new MonitorRunnable());

    public static void main(String[] args) {

        setup();
        systemTrayHandler.start();
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
        }

        System.out.println("Logged in: " + id);

    }

    public static SystemTrayHandler getSystemTrayHandler() {
        return systemTrayHandler;
    }

    public static Thread getMonitorThread() {
        return monitorThread;
    }

}
