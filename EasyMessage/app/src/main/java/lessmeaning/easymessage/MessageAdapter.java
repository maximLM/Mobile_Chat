package lessmeaning.easymessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 1 on 14.11.2016.
 */

public class MessageAdapter extends BaseAdapter {

    MessagesActivity activity;
    String username;
    ArrayList<Row> message;
    LayoutInflater inflater;

    public MessageAdapter(MessagesActivity context, ArrayList<Row> message) {
        this.activity = context;
        username = activity.getUsername();
        this.message = message;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return message.size();
    }

    @Override
    public Object getItem(int position) {
        return message.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Row row = (Row) getItem(position);
        View view = convertView;
        if (view == null) {
            if (username.equals(row.getUserSender())) {
                view = inflater.inflate(R.layout.my_item, parent, false);
            } else view = inflater.inflate(R.layout.item, parent, false);
        }

        ((TextView) view.findViewById(R.id.sender)).setText(row.getUserSender());
        ((TextView) view.findViewById(R.id.message)).setText(row.getContent());
        return view;
    }

    public Row getRow(int position) {
        return ((Row) getItem(position));
    }
}
