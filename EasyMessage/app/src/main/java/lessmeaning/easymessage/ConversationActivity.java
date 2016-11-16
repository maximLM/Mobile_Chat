package lessmeaning.easymessage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * Created by 1 on 11.11.2016.
 */

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mCreate;
    private ConversationAdapter adapter;
    private ListView mListView;
    private LocalCore localCore;
    private AlertDialog.Builder dialog;
    public static final String CONVERSATION_ID = "CONVERSATION_ID";

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_conversation);
        mCreate = (Button) findViewById(R.id.create_button);
        mListView = (ListView) findViewById(R.id.conversations);
        mCreate.setOnClickListener(this);
        ArrayList<Conversation> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            list.add(new Conversation(i, "Maxim", i * i * i * i));
        }
        adapter = new ConversationAdapter(this, list);
        mListView.setAdapter(adapter);
        localCore = new LocalCore(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goTo((int)adapter.getItem(position).getConversationID());
            }
        });
        dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Error!");
        dialog.setCancelable(true);
        dialog.setNegativeButton("Try again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.create_button):
                hideKeayboard();
                localCore.createConversation("Pedaras");
                break;
        }
    }

    public void reloadList(ArrayList<Conversation> convs) {
        adapter = new ConversationAdapter(this, convs);
        mListView.setAdapter(adapter);
    }

    public void fail(String reason) {
        dialog.create();
        dialog.show();
    }

    public void success() {
        Intent intent = new Intent(this, MessagesActivity.class);
        startActivity(intent);
    }

    public void hideKeayboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void goTo(int conversationID) {
        Intent intent = new Intent(this, MessagesActivity.class);
        intent.putExtra(CONVERSATION_ID, conversationID);
        startActivity(intent);
    }
}
