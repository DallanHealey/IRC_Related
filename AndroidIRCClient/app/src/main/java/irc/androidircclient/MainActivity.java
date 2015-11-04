package irc.androidircclient;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    Socket socket;
    BufferedReader in;
    PrintWriter out;
    boolean connected = false;
    String messageText;
    public TextView messages;
    public EditText ipAddress;
    public EditText userName;
    Intent intent;
    Intent i;
    String[] ipName = new String[] {"IP", "Name"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        ipAddress = (EditText) findViewById(R.id.ipAddress);
        userName = (EditText) findViewById(R.id.userName);
        intent = new Intent(this, MessageBoard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i = new Intent(this, MessageBoard.class);
        ipName = i.getStringArrayExtra("ip/Name");
        try {
            ipAddress.setText(ipName[0].toString());
            userName.setText(ipName[1].toString());
        }
        catch (Exception e)
        {
            Log.d("Error", "Couldn't set ip and name");
        }

        try
        {
            Log.d("Started", "Started Activity");
            Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Thread cThread  = new Thread(new ClientThread());
                        cThread.start();
                    }
                    catch (Exception e1)
                    {
                        Log.d("Error", "Broke");
                        e1.printStackTrace();
                    }
                }
            });


        }
        catch (Exception e)
        {
            Log.d("Error1", "Broke");
            e.printStackTrace();
        }
    }

    public class ClientThread implements Runnable
    {
        public void run() {
            try {
                Log.d("Started", "Thread Started");
                Log.d("Started", ipAddress.getText().toString());
                /*
                socket = new Socket(ipAddress.getText().toString(), 444);
                connected = true;
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                out.println("Test");
                out.println("Hello User");
                */
                intent.putExtra("IP/Name", new String[]{ipAddress.getText().toString(), userName.getText().toString()});
                MainActivity.this.startActivity(intent);
/*
                while(connected)
                {
                    Log.d("While", "Looping");
                    messageText = in.readLine();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            MainActivity.this.messages.setText(messageText);
                        }
                    });
                }
*/
            }
            catch (Exception e)
            {
                Log.d("Stopped", "Something Happened");
                e.printStackTrace();
            }
            finally
            {
                /*
                try
                {
                    out.close();
                    in.close();
                    socket.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                */
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
