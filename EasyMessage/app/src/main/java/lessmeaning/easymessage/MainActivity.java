package lessmeaning.easymessage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mButton;
    ListView mListView;
    LocalCore localCore;
    EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localCore = new LocalCore(this);
        mButton = (Button) findViewById(R.id.button);
        mEditText = (EditText) findViewById(R.id.editText);
        mListView = (ListView) findViewById(R.id.list_view);
        mButton.setOnClickListener(this);
        localCore.sendApproved();
    }

    @Override
    public void onClick (View v) {
        if (v.getId() == mButton.getId()) {
            if (!mEditText.getText().toString().equals("")) {
                localCore.addTemp(mEditText.getText().toString());
                mEditText.setText("");
            }
        }
    }

    public void reloadList(String row[]) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, row);
        mListView.setAdapter(adapter);
    }
}
