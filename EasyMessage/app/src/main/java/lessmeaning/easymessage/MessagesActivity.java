package lessmeaning.easymessage;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButton;
    private ListView mListView;
    private LocalCore localCore;
    private EditText mEditText;
    private MessageAdapter adapter;

   void ask(final String permission) {
       if (ContextCompat.checkSelfPermission(this, permission
               )
               != PackageManager.PERMISSION_GRANTED) {

           // Should we show an explanation?
           if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                   permission)) {

               // Show an expanation to the user *asynchronously* -- don't block
               // this thread waiting for the user's response! After the user
               // sees the explanation, try again to request the permission.

           } else {

               // No explanation needed, we can request the permission.

               ActivityCompat.requestPermissions(this,
                       new String[]{permission}, 111);

               // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
               // app-defined int constant. The callback method gets the
               // result of the request.
           }
       }

   }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ask(Manifest.permission.INTERNET);
        ask(Manifest.permission.ACCESS_NETWORK_STATE);
        ask(Manifest.permission.CALL_PHONE);
        ask(Manifest.permission.READ_PHONE_STATE);
        Log.d("supertesting", "onCreate: debug available");
        setContentView(R.layout.activity_messages);
        localCore = new LocalCore(this, getIntent().getIntExtra(ConversationActivity.CONVERSATION_ID, 0));
        mButton = (Button) findViewById(R.id.button);
        mEditText = (EditText) findViewById(R.id.editText);
        mListView = (ListView) findViewById(R.id.list_view);
        mButton.setOnClickListener(this);
        localCore.sendApproved();
        mEditText.setOnClickListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        localCore.disconnectService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        localCore.connectService();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick (View v) {
        if (v.getId() == mButton.getId()) {
            if (!mEditText.getText().toString().equals("")) {
                localCore.addTemp(mEditText.getText().toString());
                mEditText.setText("");

            }
        } else if (v.getId() == mEditText.getId()) {
            scrollBottom(mListView);
        }
    }

    public void reloadList(ArrayList<Row> rows) {
        adapter = new MessageAdapter(this, rows);
        mListView.setAdapter(adapter);
        scrollBottom(mListView);
    }
    public void scrollBottom(final ListView listView) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(listView.getCount() - 1);
            }
        });
    }
}
