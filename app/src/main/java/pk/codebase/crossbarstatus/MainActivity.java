package pk.codebase.crossbarstatus;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;

public class MainActivity extends AppCompatActivity {

    ImageView status;
    TextView textView;
    Session wampSession;
    ListView listView;
    Button btn;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayAdapter adapter;
    ToggleButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = findViewById(R.id.status);
        listView = findViewById(R.id.listview);
        btn = findViewById(R.id.reset_btn);
        toggleButton = findViewById(R.id.toggleButton);

        if (AppGlobals.getDataFromSharedPreferences(AppGlobals.KEY_MUTE)) {
            toggleButton.setChecked(true);
        } else {
            toggleButton.setChecked(false);
        }

        toggleButton.setOnClickListener(view -> {
            if (AppGlobals.getDataFromSharedPreferences(AppGlobals.KEY_MUTE)) {
                AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_MUTE, false);
                toggleButton.setChecked(false);
            } else {
                AppGlobals.saveDataToSharedPreferences(AppGlobals.KEY_MUTE, true);
                toggleButton.setChecked(true);
            }
        });
        adapter = new ArrayAdapter<>(this, R.layout.list_item, arrayList);

        listView.setAdapter(adapter);
        subscribe();

        btn.setOnClickListener(v -> {
            arrayList.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "screen cleared", Toast.LENGTH_SHORT).show();
        });
    }


    public void subscribe() {
        wampSession = new Session();
        wampSession.addOnJoinListener(this::onJoinSubscribe);
        wampSession.addOnDisconnectListener(this::onDisconnect);


        Client client = new Client(wampSession, "ws://192.168.100.150:5020/ws", "deskconn");
//        client.setOptions(new TransportOptions().);
        client.connect().whenComplete((exitInfo, throwable) -> {
            System.out.println("Exit!");
        });

    }

    private void onJoinSubscribe(Session session, SessionDetails details) {
        System.out.println("Joined realm");

        status.setBackground(getResources().getDrawable(R.drawable.status_ok));
        if (AppGlobals.getDataFromSharedPreferences(AppGlobals.KEY_MUTE)) {
            ringtone();
        }

        CompletableFuture<Subscription> future = session.subscribe(
                "brightflix.logs", this::onData);

        future.thenAccept((Subscription subscription) -> {

        });

        future.exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });
    }

    private void onDisconnect(Session session, boolean wasClean) {
        status.setBackground(getResources().getDrawable(R.drawable.ic_status));
    }

    private void onData(List<Object> items) {
        System.out.println(items);
        if (AppGlobals.getDataFromSharedPreferences(AppGlobals.KEY_MUTE)) {
            ringtone();
        }
        arrayList.add(items.get(0).toString());
        adapter.notifyDataSetChanged();
    }

    public void ringtone() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        final Handler handler = new Handler();
        final int delay = 2000;

        handler.postDelayed(new Runnable() {
            public void run() {
                if (!wampSession.isConnected()) {
                    subscribe();
                }
                handler.postDelayed(this, delay);
            }
        }, delay);
    }
}