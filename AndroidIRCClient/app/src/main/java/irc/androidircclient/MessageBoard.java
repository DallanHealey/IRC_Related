package irc.androidircclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class MessageBoard extends AppCompatActivity {

    String[] ipName;
    Socket socket;
    BufferedReader in;
    PrintWriter out;
    boolean connected = false;
    String messageText;
    EditText message;
    TextView messages;
    Intent i;
    Intent notificationIntent;
    PendingIntent pendingNotificationIntent;

    public NotificationCompat.Builder mBuilder;
    public NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mBuilder = new NotificationCompat.Builder(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationIntent = new Intent(this, MessageBoard.class);
        notificationIntent.setFlags(Notification.FLAG_AUTO_CANCEL);
        pendingNotificationIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        setContentView(R.layout.activity_message_board);
        message = (EditText) findViewById(R.id.message);
        final Intent ip = new Intent(this, MainActivity.class);
        ip.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_FROM_BACKGROUND | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i = getIntent();
        ipName = i.getStringArrayExtra("IP/Name");
        message.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if(message.getText().toString() == "")
                    {
                        Log.d("Error", "Nothing to send");
                    }
                    else {
                        out.println(ipName[1] + ": " + message.getText().toString());
                    }
                        message.setText("");
                    return true;
                } else if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_BACK)) {
                    try {
                        out.close();
                        in.close();
                        socket.close();
                    } catch (Exception e) {
                        Log.d("Error", "Didn't close socket");
                        e.printStackTrace();
                    }
                    finally
                    {
                        i.putExtra("ip/Name", ipName);
                        MessageBoard.this.startActivity(ip);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        messages = (TextView) findViewById(R.id.messages);
        Thread messageThread = new Thread(new MessageThread());
        messageThread.start();

    }

    public class MessageThread implements Runnable
    {
        public void run() {
            try
            {
                socket = new Socket(ipName[0], 444);
                connected = true;
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println(ipName[1]);

                while(connected)
                {
                    Log.d("While", "Looping");
                    messageText = in.readLine();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {

                            mBuilder.setContentTitle("New Message");
                            mBuilder.setSmallIcon(R.drawable.chat);
                            mBuilder.setAutoCancel(true);
                            mBuilder.setContentText(messageText);
                            mBuilder.setContentIntent(pendingNotificationIntent);
                            mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS);
                            mBuilder.setOnlyAlertOnce(true);
                            messages.append(messageText + "\n");
                            notificationManager.notify(1, mBuilder.build());
                        }
                    });
                }

            }
            catch (Exception e)
            {
                Log.d("Error", "Socket Issue");
            }
            finally
            {
                try
                {
                    out.close();
                    in.close();
                    socket.close();
                }
                catch (Exception e)
                {
                    Log.d("Error", "Didn't close socket");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
