package generators;

import IO.FileHandler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static IO.Connector.db;
import static constants.Constants.IS_LEAVING_REGION;
import static constants.Constants.USERS;

public class IdGenerator {

    public static String generateNewId() {
        final String[] uniqueID = {UUID.randomUUID().toString()};
        final boolean[] gotNewID = {false};
        String id = null;

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

            //Save Id
            try {

                Map<String, String> values = new HashMap<>();
                values.put("id", String.valueOf(id));
                FileHandler.writeToXML(values);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return id;
    }

    public static boolean validateId(String id) {
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

}
