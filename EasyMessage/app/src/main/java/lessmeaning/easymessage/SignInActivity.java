package lessmeaning.easymessage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;

/**
 * Created by 1 on 09.11.2016.
 */

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "newITIS";
    private EditText userName;
    private EditText password;
    private Button signIn;
    private Button signUp;
    private AlertDialog.Builder dialog;
    private CheckBox mCheck;
    private ProgressBar loading;
    private LocalCore localCore;

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
        loading = (ProgressBar) findViewById(R.id.loading);
        signIn = (Button) findViewById(R.id.signin);
        localCore = new LocalCore(this);
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
    protected void onStop() {
        super.onStop();
        localCore.disconnectService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        localCore.connectToService();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.signin):
                hideKeyboard();
                if (authorizationErrorChecking()) {
                    fail("Your username or password is empty, please, try again");
                }
                else {
                    loading.setVisibility(View.VISIBLE);
                    localCore.signin(userName.getText().toString(), password.getText().toString());
                }
                break;
            case (R.id.signup):
                hideKeyboard();
                if (authorizationErrorChecking()) {
                    fail("Your username or password is empty, please, try again");
                }
                else {
                    loading.setVisibility(View.VISIBLE);
                    localCore.signup(userName.getText().toString(), password.getText().toString());
                }
                break;
        }
    }

    public void fail(final String reason) {
        Log.d(TAG, "fail : " + reason);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.INVISIBLE);
                dialog.setMessage(reason);
                dialog.create().show();
            }
        });
    }

    public void success() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(SignInActivity.this, ConversationActivity.class);
                startActivity(intent);
            }
        });
    }

    public boolean authorizationErrorChecking() {
        if ((userName.getText().toString().equals("")) ||
                (password.getText().toString().equals(""))) {
            return true;
        }
        else {
           return false;
        }
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
