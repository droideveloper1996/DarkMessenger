package messenger.dark.com.darkmessenger;

/**
 * Created by Abhishek on 02/09/2017.
 */

public class ChatMessage {

    private String message;
    private String date;
    private String time;
    private String pictureUrl;
    private String myMessage;
    private String imageUrl;



    public ChatMessage(String message, String myMessage) {
        this.message = message;
        this.myMessage = myMessage;
        imageUrl=null;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ChatMessage(String message, String myMessage, String imageUrl) {
        this.message = message;
        this.myMessage = myMessage;
        this.imageUrl=imageUrl;

    }


    public ChatMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(String myMessage) {
        this.myMessage = myMessage;
    }
}
