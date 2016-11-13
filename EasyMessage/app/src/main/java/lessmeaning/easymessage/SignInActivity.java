package lessmeaning.easymessage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

/**
 * Created by 1 on 09.11.2016.
 */

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText userName;
    private EditText password;
    private Button signIn;
    private Button signUp;
    private AlertDialog.Builder dialog;
    private CheckBox mCheck;
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
        mCheck = (CheckBox) findViewById(R.id.checkBox);
        signIn = (Button) findViewById(R.id.signin);
        signUp = (Button) findViewById(R.id.signup);
        mCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    password.setTransformationMethod(new PasswordTransformationMethod());
                }
                else {
                    password.setTransformationMethod(null);
                }
                password.setSelection(password.length());
            }
        });
        signIn.setOnClickListener(this);
        signUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.signin):
                //localCore.signin(userName.getText().toString(), password.getText().toString());
                success();
                userName.setText("");
                password.setText("");
                break;
            case (R.id.signup):
                //localCore.signup(userName.getText().toString(), password.getText().toString());
                userName.setText("");
                password.setText("");
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
