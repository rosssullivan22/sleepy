package runnables;

import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import generators.CodeGenerator;

import static IO.Connector.db;
import static IO.Connector.id;
import static constants.Constants.NEWCODE;
import static constants.Constants.USERS;

public class NewCodeRunnable implements Runnable{
    @Override
    public void run() {
        db.child(USERS)
                .child(id)
                .child(NEWCODE)
                .addValueEventListener(new ValueEventListener() {
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getValue() instanceof Boolean) {
                                if ((Boolean) snapshot.getValue()) {
                                    System.out.println("gen new code");
                                    CodeGenerator.addActivationCode();
                                }
                            }
                        }
                    }

                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
    }
}
