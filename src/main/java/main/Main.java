package main;

import IO.FileHandler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import runnables.CodeLinkRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import static constants.Constants.*;
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

        if (id.equals("0") || id.equals("") || !validateId(id) || failedToReadId) {

            final String[] uniqueID = {UUID.randomUUID().toString()};
            final boolean[] gotNewID = {false};

            while (!gotNewID[0]) {

                db.child(USERS).child(uniqueID[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        System.out.println("Getting new Id");
                        if (snapshot.exists()) {
                            uniqueID[0] = UUID.randomUUID().toString();
                        } else {
                            gotNewID[0] = true;
                        }
                    }

                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });

                //Sleep thread so code so while is not ran to many times
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                id = uniqueID[0];
                db.child(USERS).child(id).child(IS_LEAVING_REGION).setValue(false);

                addActivationCode();

                //Save Id
                try {

                    Map<String, String> values = new HashMap<>();
                    values.put("id", String.valueOf(id));
                    FileHandler.writeToXML(values);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        System.out.println(id + " Logged in");

    }

    private static boolean validateId(String id) {
        final boolean[] exists = {false};

        db.child(USERS).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    exists[0] = true;
                } else {
                    System.out.println("Invalid Id");
                }

            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return exists[0];
    }

    public static void addActivationCode() {

        final String[] code = {generateActivationCode()};
        final boolean[] gotNewCode = {false};

        while (!gotNewCode[0]) {
            db.child(CODES).child(code[0]).addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    System.out.println("Creating Activation Code");
                    if (snapshot.exists()) {
                        code[0] = generateActivationCode();
                    } else {
                        gotNewCode[0] = true;

                    }
                }

                public void onCancelled(FirebaseError firebaseError) {
                }
            });

            //Sleep thread so code so while is not ran to many times
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            db.child(CODES).child(code[0]).child(PC_ID).setValue(id);
            db.child(USERS).child(id).child(CODES).setValue(code[0]);
            db.child(USERS).child(id).child(LINKED).setValue(false);
            try {
                Map<String, String> map = new HashMap<>();
                map.put("code", code[0]);
                FileHandler.writeToXML(map);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    private static String generateActivationCode() {
        Random r = new Random();
        int low = 0;
        int high = 9999;
        int result = r.nextInt(high - low) + low;

        StringBuilder codeBuilder = new StringBuilder(String.valueOf(result));

        while (codeBuilder.length() < 4)
            codeBuilder.insert(0, "0");
        return codeBuilder.toString();
    }

    public static SystemTrayHandler getSystemTrayHandler() {
        return systemTrayHandler;
    }
}
