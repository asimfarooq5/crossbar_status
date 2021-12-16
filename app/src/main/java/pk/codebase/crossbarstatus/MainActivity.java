package pk.codebase.crossbarstatus;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;


import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.crossbar.autobahn.wamp.Client;
import io.crossbar.autobahn.wamp.Session;
import io.crossbar.autobahn.wamp.types.SessionDetails;
import io.crossbar.autobahn.wamp.types.Subscription;

public class MainActivity extends AppCompatActivity {

    ImageView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        status = findViewById(R.id.status);
        subscribe();
    }

    public void subscribe() {
        Session wampSession = new Session();
        wampSession.addOnJoinListener(this::onJoinSubscribe);
        wampSession.addOnDisconnectListener(this::onDisconnect);


        Client client = new Client(wampSession, "ws://192.168.100.2:8080/ws", "realm1");
//        client.setOptions(new TransportOptions().);
        client.connect().whenComplete((exitInfo, throwable) -> {
            System.out.println("Exit!");
        });

    }

    private void onJoinSubscribe(Session session, SessionDetails details) {
        System.out.println("Joined realm");

        status.setBackground(getResources().getDrawable(R.drawable.status_ok));
        ringtone();

        CompletableFuture<Subscription> future = session.subscribe(
                "pk.codebase.profile", this::onData);

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
        Toast.makeText(this, "" + items, Toast.LENGTH_SHORT).show();
        ringtone();
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

}