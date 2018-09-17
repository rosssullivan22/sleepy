package runnables;

import IO.FileHandler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import generators.CodeGenerator;
import main.SystemTrayHandler;

import static constants.Constants.*;
import static IO.Connector.*;

public class CodeLinkRunnable implements Runnable {


    public void run() {
        db.child(USERS)
                .child(id)
                .child(LINKED)
                .addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getValue() instanceof Boolean) {
                                if ((Boolean) snapshot.getValue()) {
                                    System.out.println("code linked");
                                    pcWasLinked();
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

    private void checkIfCodeExistsInDB() {

        db.child(USERS)
                .child(id)
                .child(CODES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            System.out.println("code not in db, adding it");
                            CodeGenerator.addActivationCode();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });


    }


    private void pcWasLinked() {
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


    /**
     * @return true if linked to a phone, false if not linked to a phone
     */
    private boolean checkIfLinked() {

        final boolean[] dataAccessed = {false};
        final boolean[] data = {false};

        db.child(USERS)
                .child(id)
                .child(LINKED)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getValue() instanceof Boolean) {
                                data[0] = (boolean) snapshot.getValue();
                            }
                        }
                        dataAccessed[0] = true;
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

        while (!dataAccessed[0]) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return data[0];


    }

}
