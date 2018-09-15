package runnables;

import IO.FileHandler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import main.Main;
import main.SystemTrayHandler;
import static constants.Constants.*;

public class CodeLinkRunnable implements Runnable {

    public void run() {
        Main.db.child(USERS)
                .child(Main.id)
                .child(LINKED)
                .addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        if ((Boolean) snapshot.getValue()) {
                            System.out.println("code linked");
                            startMonitoringLocation();
                        } else {
                            System.out.println("code not linked, waiting...");
                            //Check if we have a code and make one if we dont
                            checkIfCodeExistsInDB();
                        }
                    }

                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
    }

    private void checkIfCodeExistsInDB() {

        Main.db.child(USERS)
                .child(Main.id)
                .child(CODES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Main.addActivationCode();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    private void startMonitoringLocation() {
        SystemTrayHandler.remomveCodeOptionFromMenu();
        FileHandler.removeValueForKey("code");
        Main.db.child(USERS).child(Main.id).child(CODES).removeValue();
        Thread monitorThread = new Thread(new MonitorRunnable());
        monitorThread.start();
    }

}
