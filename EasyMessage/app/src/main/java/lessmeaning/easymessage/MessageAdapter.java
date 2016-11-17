package lessmeaning.easymessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 1 on 14.11.2016.
 */

public class MessageAdapter extends BaseAdapter {

    Context context;
    ArrayList<Row> message;
    LayoutInflater inflater;

    public MessageAdapter(Context context, ArrayList<Row> message) {
        this.context = context;
        this.message = message;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item, parent, false);
        }

        Row row = getRow(position);
        ((TextView) view.findViewById(R.id.sender)).setText(row.getUserSender());
        ((TextView) view.findViewById(R.id.message)).setText(row.getContent());
        return view;
    }

    public Row getRow(int position) {
        return ((Row) getItem(position));
    }
}
