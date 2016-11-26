package lessmeaning.easymessage;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.support.design.widget.NavigationView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;

import static lessmeaning.easymessage.R.id.center;
import static lessmeaning.easymessage.R.id.username;

/**
 * Created by 1 on 11.11.2016.
 */

public class ConversationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "newITIS";
    private ConversationAdapter adapter;
    private ListView mListView;
    private LocalCore localCore;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AlertDialog.Builder dialog;
    public static final String CONVERSATION_ID = "CONVERSATION_ID";


    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.drawer);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mListView = (ListView) findViewById(R.id.conversations);
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
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                break;
            case R.id.nav_log_out:
                localCore.logOut();
                Intent intent = new Intent(this, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.nav_create:
                //toDO create Alert Dialog with creating
                dialog = new AlertDialog.Builder(this);
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
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
