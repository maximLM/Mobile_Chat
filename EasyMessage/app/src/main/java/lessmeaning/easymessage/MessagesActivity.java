package lessmeaning.easymessage;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Button mButton;
    private ListView mListView;
    private LocalCore localCore;
    private EditText mEditText;
    private MessageAdapter adapter;
    private String username;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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
        Log.d("supertesting", "onCreate: debug available");
        setContentView(R.layout.drawer_message);
        mButton = (Button) findViewById(R.id.button);
        navigationView = (NavigationView) findViewById(R.id.drawer_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_message);
        mEditText = (EditText) findViewById(R.id.editText);
        mListView = (ListView) findViewById(R.id.list_view);
        mButton.setOnClickListener(this);
        localCore = new LocalCore(this, getIntent().getIntExtra(ConversationActivity.CONVERSATION_ID, 0));
        localCore.sendApproved();
        mEditText.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        drawerLayout.openDrawer(Gravity.LEFT);
        return true;
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
        localCore.sendApproved();
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
    public void onClick(View v) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home: {
                Intent intent = new Intent(this, ConversationActivity.class);
                startActivity(intent);

            }
            break;
            case R.id.nav_log_out: {
                localCore.logOut();
                Intent intent = new Intent(this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            break;
            case R.id.nav_create: {
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Creating conversation");
                final EditText text = new EditText(this);
                text.setLayoutParams(new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
                text.setHint("Input username");
                dialog.setView(text);

                dialog.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        localCore.createConversation(text.getText().toString());
                        dialog.cancel();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.create().show();
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}