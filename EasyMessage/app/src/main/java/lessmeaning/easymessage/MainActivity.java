package lessmeaning.easymessage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mButton;
    ListView mListView;
    LocalCore localCore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        localCore = new LocalCore(this);
        mButton = (Button) findViewById(R.id.button);
        mListView = (ListView) findViewById(R.id.list_view);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick (View v) {
        if (v.getId() == mButton.getId()) {
//            localCore.addTemp(inf);
        }
    }

    public void reloadList(String row[]) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, row);
        mListView.setAdapter(adapter);
    }
}
