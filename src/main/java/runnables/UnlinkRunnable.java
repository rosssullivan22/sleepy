package runnables;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import main.Main;

import static IO.Connector.*;
import static constants.Constants.LINKED;
import static constants.Constants.PHONES;
import static constants.Constants.USERS;

public class UnlinkRunnable implements Runnable {

    @Override
    public void run() {

        db.child(USERS).child(id).child(PHONES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() instanceof String) {
                    String phoneId = (String) dataSnapshot.getValue();
                    db.child(PHONES).child(phoneId).setValue(null);
                    db.child(USERS).child(id).child(PHONES).setValue(null);
                    db.child(USERS).child(id).child(LINKED).setValue(false);

                    Main.getMonitorThread().interrupt();
                    new Thread(new CodeLinkRunnable()).start();

                    Thread.currentThread().interrupt();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }


}
