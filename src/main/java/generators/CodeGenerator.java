package generators;

import IO.FileHandler;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static IO.Connector.db;
import static IO.Connector.id;
import static constants.Constants.*;

public class CodeGenerator {

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

}
