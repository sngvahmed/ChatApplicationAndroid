package my.chatapplication.Model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import my.chatapplication.Constant.VALIDATION;
import my.chatapplication.DataHolder.User;
import my.chatapplication.R;

/**
 * Created by nasser on 20/07/15.
 */
public class UMSModule{

    private Firebase myFirebaseRef;
    private Handler loginHandler;
    private Context context;

    public UMSModule(Handler handler , Context context){
        this.context = context;
        loginHandler = handler;
        Firebase.setAndroidContext(context);
        myFirebaseRef = new Firebase(context.getString(R.string.fireBaseUrl));
    }

    public void signUp(String email, String password){
        myFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                Message message = new Message();
                message.obj = VALIDATION.ACCEPTED;
                handleMessage(message);
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Message message = new Message();
                message.obj = VALIDATION.EMAIL_ALREADY_EXIST;
                handleMessage(message);
            }
        });
    }

    public void login(String email, String password){

        myFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // showToastMessage("get Accepted from fire base");
                Message message = new Message();
                message.obj = VALIDATION.ACCEPTED;
                handleMessage(message);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Message message = new Message();
                // showToastMessage("error with status code is " + firebaseError.getCode());
                switch (firebaseError.getCode()) {
                    case -16:
                        message.obj = VALIDATION.PASSWORD_INVALID;
                        break;
                    case -17:
                        message.obj = VALIDATION.EMAIL_INVALID;
                        break;
                }
                handleMessage(message);
            }
        });
    }

    private void showToastMessage(String s) {
        Toast.makeText(context , s , Toast.LENGTH_LONG).show();
    }

    public void saveUser(User user){
        Firebase usersave = myFirebaseRef.child("users").child(removeDot(user.getEmail()));
        usersave.setValue(user);

        usersave.setValue(user, new Firebase.CompletionListener() {

            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Message message = loginHandler.obtainMessage();
                try {
                    message.arg1 = firebaseError.getCode();
                    message.obj = firebaseError.getMessage();
                    loginHandler.sendMessage(message);
                } catch (Exception e) {
                    message.obj = 1;
                    message.obj = firebase.getKey();
                    loginHandler.sendMessage(message);
                }

            }
        });
    }

    public void updateUser(User user){
        Firebase userUpdate = myFirebaseRef.child("users").child(user.getEmail());
        Map<String, Object> userdata = user.convertToHashMap();
        userUpdate.updateChildren(userdata);
    }

    public void getUser(String email){

        Firebase _user = myFirebaseRef.child("users").child(removeDot(email));

        _user.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Message message = loginHandler.obtainMessage();
                message.arg1 = 1;
                message.obj = dataSnapshot.getValue();
                loginHandler.sendMessage(message);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Message message = loginHandler.obtainMessage();
                message.arg1 = -1;
                message.obj = firebaseError.getMessage();
                loginHandler.sendMessage(message);
            }
        });
    }

    public void handleMessage(Message msg){
        // showToastMessage("message handler in UMSModule " + msg.obj );
        loginHandler.handleMessage(msg);
    }

    public String removeDot(String msg){
        String newMsg = msg.replace('.','@');
        System.out.println(newMsg);
        return newMsg;
    }
}
