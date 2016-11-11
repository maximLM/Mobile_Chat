package lessmeaning.easymessage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by 1 on 09.11.2016.
 */

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName;
    private EditText password;
    private Button signIn;
    private Button signUp;
    AlertDialog.Builder dialog;
    //LocalCore localCore = new LocalCore(this);

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_signin);
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Authorization error");
        dialog.setCancelable(true);
        dialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signIn = (Button) findViewById(R.id.signin);
        signUp = (Button) findViewById(R.id.signup);
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.signin):
                //localCore.signin(userName.getText().toString(), password.getText().toString());
                success();
                break;
            case (R.id.signup):
                //localCore.signup(userName.getText().toString(), password.getText().toString());
                break;
        }
    }

    public void fail(String reason) {
        dialog.setMessage(reason);
        dialog.create();
        dialog.show();
    }

    public void success() {
        Intent intent = new Intent(this, ConversationActivity.class);
        startActivity(intent);
    }
}
