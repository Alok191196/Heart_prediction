package divy.heart_prediction;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class MainActivity extends AppCompatActivity{

    public static final String TAG = MainActivity.class.getName();

    ListView conversationList;
    ImageButton sendButton;
    EditText userInput;
    AIConfiguration aiConfiguration;
    AIDataService aiDataService;
    public static ArrayList<ChatData> chatlist;
    public static ChatAdapter chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AIConfiguration.SupportedLanguages lang =
                AIConfiguration.SupportedLanguages.fromLanguageTag("en");

        aiConfiguration = new AIConfiguration("cfad20a73540482c9d58792ebe9e0387",lang);
        aiDataService = new AIDataService(aiConfiguration);

        sendButton = (ImageButton)findViewById(R.id.imageButton);
        userInput = (EditText)findViewById(R.id.inputMessage);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = userInput.getText().toString();
                if(!text.trim().equals("")) {
                    addMessage(text,true);
                    userInput.setText("");
                    sendRequest(text);
                }
            }
        });
        conversationList = (ListView) findViewById(R.id.ConversationList);
        conversationList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        conversationList.setStackFromBottom(true);

        chatlist = new ArrayList<ChatData>();
        chatAdapter = new ChatAdapter(this, chatlist);
        conversationList.setAdapter(chatAdapter);
    }

    private void addMessage(String message, boolean isMine) {

        final ChatData chatMessage = new ChatData("", "","", isMine);
        chatMessage.body = message;
        chatAdapter.add(chatMessage);
        chatAdapter.notifyDataSetChanged();
    }

    private void sendRequest(String userText) {

        final String contextString = String.valueOf(userText);

        final AsyncTask<String, Void, AIResponse> task = new AsyncTask<String, Void, AIResponse>() {

            private AIError aiError;

            @Override
            protected AIResponse doInBackground(final String... params) {
                final AIRequest request = new AIRequest();
                String query = params[0];

                if (!TextUtils.isEmpty(query))
                    request.setQuery(query);

                try {
                    return aiDataService.request(request);
                } catch (final AIServiceException e) {
                    aiError = new AIError(e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final AIResponse response) {
                if (response != null) {
                    onResult(response);
                } else {
                    onError(aiError);
                }
            }
        };

        task.execute(contextString);
    }

    private void onError(AIError aiError) {
    }

    private void onResult(final AIResponse response) {

      /*  Toast.makeText(getApplicationContext(),
                response.getResult().getFulfillment().getSpeech().toString(),
                Toast.LENGTH_LONG).show();*/

      addMessage(response.getResult().getFulfillment().getSpeech().toString(),false);



    }
    }

