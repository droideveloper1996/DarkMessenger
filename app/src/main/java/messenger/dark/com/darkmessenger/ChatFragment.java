package messenger.dark.com.darkmessenger;

import android.Manifest.permission;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Abhishek on 02/09/2017.
 */

public class ChatFragment extends Fragment {

    private static final String LOG_TAG = "ChatFragment";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 25;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 26;
    private static final int REQUEST_MICROPHONE = 40;


    EditText message;
    private MessageAdapter messageAdapter;
    ArrayList<ChatMessage> chatMessageArrayList;
    String myMessage = "Anonymous";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ChildEventListener childEventListener;
    FirebaseAuth firebaseAuth;
    ImageView media;
    FirebaseStorage firebaseStorage;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    StorageReference storageReference;
    StorageReference audioStoragereference;
    ListView mListView;
    LinearLayoutManager linearLayoutManager;
    private Context mContext;
    ImageView microphone;
    MediaRecorder mRecorder;
    private String mFileName = null;
    ProgressDialog progressDialogue;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment2_layout, container, false);
        final LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.linearLayout);

        mListView = (ListView) v.findViewById(R.id.messageListView);
        chatMessageArrayList = new ArrayList<ChatMessage>();
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recorded_audio.3gp";
        progressDialogue = new ProgressDialog(getContext());
        mContext = getContext();
        audioStoragereference=FirebaseStorage.getInstance().getReference().child("Audio");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("messages");
        final ImageView send = (ImageView) v.findViewById(R.id.send);
        media = (ImageView) v.findViewById(R.id.add);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("chat_photos");
        message = (EditText) v.findViewById(R.id.message);
        microphone = (ImageView) v.findViewById(R.id.microphone);
        firebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    myMessage = firebaseAuth.getCurrentUser().getEmail().toString();
                }
            }
        };
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                send.setVisibility(View.VISIBLE);
                if (charSequence.length() == 0) {
                    send.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        message.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.
                            INPUT_METHOD_SERVICE);
                    try {
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                return false;
            }

        });
        linearLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                try {
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        getPermision();
       /* mListView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                try {
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });*/
        final ImageView imageView = (ImageView) v.findViewById(R.id.microphone);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(100);

            }
        });
        imageView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });

        send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                String msg = message.getText().toString();
                sendMessage(msg);

            }
        });
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                chatMessageArrayList.add(chatMessage);
                messageAdapter = new MessageAdapter(mContext, chatMessageArrayList);
                mListView.setAdapter(messageAdapter);
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseReference.addChildEventListener(childEventListener);

        media.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/jpeg");
                // intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent, 45);
            }
        });
        microphone.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    media.setVisibility(View.GONE);
                    send.setVisibility(View.GONE);
                    startRecording();
                    message.setText("Recording...");
                    linearLayout.setBackgroundColor(Color.parseColor("#7C0a02"));


                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    media.setVisibility(View.VISIBLE);
                    send.setVisibility(View.VISIBLE);
                    message.setText("Recording Stopped...");
                    linearLayout.setBackgroundColor(Color.parseColor("#F9F9F9"));
                    message.setBackgroundColor(Color.parseColor("#ffffff"));
                    progressDialogue.setMessage("Sending...");
                    stopRecording();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            message.setText("");
                        }
                    }, 500);

                }
                return false;
            }
        });
        if (ContextCompat.checkSelfPermission(getActivity(),
                permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_STORAGE);
        }

        return v;
    }

    void sendMessage(String abc) {
        if (!TextUtils.isEmpty(abc) && abc != null) {
            //   chatMessageArrayList.add(new ChatMessage(abc.trim(), myMessage));
            databaseReference.push().setValue(new ChatMessage(abc.trim(), myMessage));
        }
        message.setText("");
    }

    @Override
    public void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 45 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImage = data.getData();
            StorageReference photoRef = storageReference.child(selectedImage.getLastPathSegment() + System.currentTimeMillis());
            photoRef.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadedUri = taskSnapshot.getDownloadUrl();

                    databaseReference.push().setValue(new ChatMessage(null, myMessage, downloadedUri.toString()));
                }
            });
        }
    }

    private void startRecording() {
        if (VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.RECORD_AUDIO},
                        REQUEST_MICROPHONE);

            }
        }

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();

    }


    private void stopRecording() {

        try {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        uploadAudio();
    }

    void getPermision() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
        }


    }

    void uploadAudio() {
        progressDialogue.setMessage("Uploading...");
        progressDialogue.show();

        StorageReference storage = audioStoragereference.child("new_audio" + System.currentTimeMillis()+".3gp");
        Uri uri = Uri.fromFile(new File(mFileName));
        storage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progressDialogue.dismiss();
                // databaseReference.push().setValue(new ChatMessage(null, myMessage, downloadedUri.toString()));

            }
        });
    }
}

