package messenger.dark.com.darkmessenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Abhishek on 02/09/2017.
 */

public class MessageAdapter extends ArrayAdapter<ChatMessage> {


    public MessageAdapter(Context context, ArrayList<ChatMessage> objects) {
        super(context, 0, objects);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        int layoutResource = 0; // determined by view type
        ChatMessage ChatBubble = getItem(position);
        int viewType = getItemViewType(position);

        if (ChatBubble.getMyMessage().equals("droid.developer1996@gmail.com")) {
            layoutResource = R.layout.right_chat_bubble;
        } else {
            layoutResource = R.layout.left_chat_bubble;
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layoutResource, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.txt_msg);
        ImageView imageView = convertView.findViewById(R.id.image);
        boolean isPhoto = ChatBubble.getImageUrl() != null;
        if (isPhoto) {
            imageView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
            Picasso.with(getContext()).load(ChatBubble.getImageUrl()).into(imageView);

        } else {
            imageView.setVisibility(View.GONE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(ChatBubble.getMessage());
        }
        return convertView;
    }


}

