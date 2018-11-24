package runnables;

import IO.FileHandler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import generators.CodeGenerator;
import main.Main;
import main.SystemTrayHandler;

import java.util.HashMap;
import java.util.Map;

import static IO.Connector.db;
import static IO.Connector.id;
import static constants.Constants.*;

public class CodeLinkRunnable implements Runnable {


    public void run() {
        checkIfCodeExistsInDB();
    }

    private void checkIfCodeExistsInDB() {
        db.child(USERS)
                .child(id)
                .child(CODES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            System.out.println("code not in db, adding it");
                            addActivationCode();
                        } else {
                            System.out.println("code in db but not linked, waiting...");
                            listenForLink();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
    }

    private static void addActivationCode() {
        final String[] code = {CodeGenerator.generateActivationCode()};

        db.child(CODES).child(code[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("Creating Activation Code");
                if (snapshot.exists()) {
                    code[0] = CodeGenerator.generateActivationCode();
                    addActivationCode();
                } else {
                    db.child(CODES).child(code[0]).child(PC_ID).setValue(id);
                    db.child(USERS).child(id).child(CODES).setValue(code[0]);
                    db.child(USERS).child(id).child(NEWCODE).removeValue();

                    try {
                        Map<String, String> map = new HashMap<>();
                        map.put(XML_CODE, code[0]);
                        FileHandler.writeToXML(map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    listenForLink();
                }
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private static void listenForLink() {
        db.child(USERS)
                .child(id)
                .child(LINKED)
                .addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getValue() instanceof Boolean) {
                                if ((Boolean) snapshot.getValue()) {
                                    pcIsLinked();
                                    db.removeEventListener(this);
                                }
                            }
                        }
                    }

                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
    }

    private static void pcIsLinked() {
        System.out.println("pc is linked");

        try {
            String code = FileHandler.getValueFromXMLForKey(XML_CODE);
            db.child(CODES).child(code).removeValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            db.child(USERS).child(id).child(CODES).removeValue();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileHandler.removeValueForKey(XML_CODE);
        SystemTrayHandler.removeCodeOptionFromMenu();

        Main.getMonitorThread().start();
        Thread.currentThread().interrupt();
    }

}
