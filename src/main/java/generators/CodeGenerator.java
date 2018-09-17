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


    private final static String[] code = {generateActivationCode()};

    public static void addActivationCode() {
        db.child(CODES).child(code[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println("Creating Activation Code");
                if (snapshot.exists()) {
                    code[0] = generateActivationCode();
                    addActivationCode();
                }else{
                    db.child(CODES).child(code[0]).child(PC_ID).setValue(id);
                    db.child(USERS).child(id).child(CODES).setValue(code[0]);
                    db.child(USERS).child(id).child(NEWCODE).removeValue();

                    try {
                        Map<String, String> map = new HashMap<>();
                        map.put("code", code[0]);
                        FileHandler.writeToXML(map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    private static String generateActivationCode() {
        Random random = new Random();
        int low = 0;
        int high = 9999;
        int result = random.nextInt(high - low) + low;

        StringBuilder codeBuilder = new StringBuilder(String.valueOf(result));

        while (codeBuilder.length() < 4)
            codeBuilder.insert(0, "0");
        return codeBuilder.toString();
    }

}
