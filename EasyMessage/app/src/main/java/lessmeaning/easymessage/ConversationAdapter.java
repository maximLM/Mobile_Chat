package lessmeaning.easymessage;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 1 on 14.11.2016.
 */

public class ConversationAdapter extends BaseAdapter {

    public static final String TAG = "Adaptertest";

    Context context;
    LayoutInflater inflater;
    ArrayList<Conversation> conversations;

    public ConversationAdapter(Context context, ArrayList<Conversation> conversations) {
        this.context = context;
        this.conversations = conversations;
        inflater =  (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public Conversation getItem(int position) {
        return conversations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView: position is " + position);
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.convitem, parent, false);
        }
        Conversation conversation = getItem(position);
        ((TextView) view.findViewById(R.id.conv)).setText(conversation.getFriend());
        String text = "";
        if (conversation.getRow() != null)
            text = conversation.getRow().getContent();
        ((TextView) view.findViewById(R.id.message)).setText(text);
        return view;
    }
}
