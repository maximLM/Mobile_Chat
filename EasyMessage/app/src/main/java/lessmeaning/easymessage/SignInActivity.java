package lessmeaning.easymessage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by 1 on 09.11.2016.
 */

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    EditText userName;
    EditText password;
    Button signIn;
    Button signUp;
    //LocalCore localCore = new LocalCore(this);

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_signin);
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
                break;
            case (R.id.signup):
                //localCore.signup(userName.getText().toString(), password.getText().toString());
                break;
        }
    }
}
