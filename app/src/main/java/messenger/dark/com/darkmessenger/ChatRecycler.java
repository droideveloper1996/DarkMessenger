package messenger.dark.com.darkmessenger;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Abhishek on 04/09/2017.
 */

public class ChatRecycler extends RecyclerView.Adapter<ChatRecycler.ChatHolder> {
    private Context mContext;
    private ArrayList<ChatMessage> messages = new ArrayList<>();
    int i = 0;
    private ChatMessage ChatBubble;

    public ChatRecycler(Context context, ArrayList<ChatMessage> messageArrayList) {
        this.mContext = context;
        messages = new ArrayList<>();
        this.messages = messageArrayList;

    }


    @Override
    public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutResource = -1;
        if(i<=messages.size()) {
            ChatBubble =messages.get(i);
            i++;
        }
        if (ChatBubble.getMyMessage().equals("kshitizagarwal27@gmail.com")) {
            layoutResource = R.layout.right_chat_bubble;
        } else {
            layoutResource = R.layout.left_chat_bubble;
        }
        View v = LayoutInflater.from(mContext).inflate(layoutResource, parent, false);
        ChatHolder chatHolder = new ChatHolder(v);
        return chatHolder;
    }

    @Override
    public void onBindViewHolder(ChatHolder holder, int position) {
        ChatMessage chatMessage = messages.get(position);
        boolean isPhoto = chatMessage.getImageUrl() != null;
        if (isPhoto) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textView.setVisibility(View.GONE);
            Picasso.with(mContext).load(chatMessage.getImageUrl()).into(holder.imageView);

        } else {
            holder.imageView.setVisibility(View.GONE);
            holder.textView.setVisibility(View.VISIBLE);
            holder.textView.setText(chatMessage.getMessage());
        }



    }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ChatHolder(View itemView) {
            super(itemView);
            View v = itemView;
            int position = getAdapterPosition();
            imageView = (ImageView) itemView.findViewById(R.id.image);
            textView = (TextView) itemView.findViewById(R.id.txt_msg);

        }
    }
}
