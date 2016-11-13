package lessmeaning.easymessage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by 1 on 11.11.2016.
 */

public class ConversationActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mCreate;
    private ArrayAdapter<String> adapter;
    private ListView mListView;
    //private LocalCore localCore = new LocalCore();
    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_conversation);
        mCreate = (Button) findViewById(R.id.create_button);
        mListView = (ListView) findViewById(R.id.conversations);
        mCreate.setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case (R.id.create_button):
                hideKeayboard();
                //localCore.createConversation(String username);
                success();
                break;
        }
    }

    public void reloadList(String rows[]) {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, rows);
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
}
