package lessmeaning.easymessage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by 1 on 11.11.2016.
 */

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "newITIS";
    private Button mCreate;
    private ConversationAdapter adapter;
    private ListView mListView;
    private EditText userName;
    private LocalCore localCore;
    private AlertDialog.Builder dialog;
    public static final String CONVERSATION_ID = "CONVERSATION_ID";


    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_conversation);
        mCreate = (Button) findViewById(R.id.create_button);
        mListView = (ListView) findViewById(R.id.conversations);
        userName = (EditText) findViewById(R.id.find_username);
        mCreate.setOnClickListener(this);
        localCore = new LocalCore(this);
        if (!localCore.checkAuthorization()) {
            goToSignInActivity();
        }
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goTo((int)adapter.getItem(position).getConversationID());
            }
        });
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
        localCore.sendConversations();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.create_button):
                hideKeayboard();
                localCore.createConversation(userName.getText().toString());
                break;
        }
    }

    public void reloadList(ArrayList<Conversation> convs) {
        adapter = new ConversationAdapter(this, convs);
        mListView.setAdapter(adapter);
    }

    public void fail(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new AlertDialog.Builder(ConversationActivity.this);
                dialog.setTitle("Error! reason is " + reason);
                dialog.setCancelable(true);
                dialog.setNegativeButton("Try again", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.create();
                dialog.show();
            }
        });
    }

    public void success() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ConversationActivity.this, MessagesActivity.class);
                startActivity(intent);
            }
        });
    }

    public void hideKeayboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void goTo(final int conversationID) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ConversationActivity.this, MessagesActivity.class);
                intent.putExtra(CONVERSATION_ID, conversationID);
                startActivity(intent);
            }
        });
    }

    public void goToSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }
}
