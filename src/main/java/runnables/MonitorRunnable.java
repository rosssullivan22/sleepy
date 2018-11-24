package runnables;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.IOException;

import static IO.Connector.db;
import static IO.Connector.id;
import static constants.Constants.IS_LEAVING_REGION;
import static constants.Constants.USERS;

public class MonitorRunnable implements Runnable {

    public void run() {
        db.child(USERS)
                .child(id)
                .child(IS_LEAVING_REGION)
                .addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        if ((Boolean) snapshot.getValue()) {
                            System.out.println("sleep");
                            putComputerToSleep();
                        } else {
                            System.out.println("user is in range of PC");
                        }
                    }

                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
    }

    private static void putComputerToSleep() {

        //run sleep bat
        try {
            String path = "C:\\Users\\Ross\\Desktop\\Computer_Sleep_Program\\src\\main\\resources\\";
            String file = "test.bat";

            Runtime.getRuntime().exec("cmd /c start \"\" \"" + path + file + "\"");
        } catch (IOException e) {
            e.printStackTrace();
        }


        //update database
        db.child(USERS).child(id).child(IS_LEAVING_REGION).setValue(false);

    }

}
