package main;

import IO.FileHandler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import generators.CodeGenerator;
import generators.IdGenerator;
import runnables.CodeLinkRunnable;
import runnables.MonitorRunnable;
import runnables.NewCodeRunnable;

import static IO.Connector.*;
import static constants.Constants.CODES;
import static constants.Constants.LINKED;
import static constants.Constants.USERS;

public class Main {

    private static SystemTrayHandler systemTrayHandler = new SystemTrayHandler();

    public static void main(String[] args) {

        setup();
        systemTrayHandler.start();

//        Thread codeLinkThread = new Thread(new CodeLinkRunnable());
//        codeLinkThread.start();

        test();

        Thread monitorThread = new Thread(new MonitorRunnable());
        monitorThread.start();

        Thread newCodeThread = new Thread(new NewCodeRunnable());
        newCodeThread.start();

    }

    private static void test(){
        db.child(USERS)
                .child(id)
                .child(LINKED)
                .addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getValue() instanceof Boolean) {
                                if ((Boolean) snapshot.getValue()) {
                                    System.out.println("code linked");
                                    pcWasLinkedTest();
                                } else {
                                    System.out.println("code not linked, waiting...");
                                    //Check if we have a code and make one if we dont
                                    //checkIfCodeExistsInDB();
                                }
                            }
                        }
                    }

                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });




    }

    private static void pcWasLinkedTest() {

        System.out.println("pc was linked");

        try {
            String code = FileHandler.getValueFromXMLForKey("code");
            db.child(CODES).child(code).removeValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            db.child(USERS).child(id).child(CODES).removeValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

//        FileHandler.removeValueForKey("code");
//        SystemTrayHandler.removeCodeOptionFromMenu();
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
