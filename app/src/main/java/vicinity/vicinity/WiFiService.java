package vicinity.vicinity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class WiFiService extends Service {

    private WifiP2pManager mManager;//wifi manager
    //private WifiP2pManager.Channel mChannel;//this channel is used to connect the app to the wifi framework
    private Channel mChannel;
    private BroadcastReceiver mReceiver;//will catch event and do some stuff .
    private ArrayList<WifiP2pDevice> peers_list;//list of peers
    private IntentFilter mIntentFilter;//for saying to broadcastreceiver which events to check
    boolean flag = false;

    public WiFiService() {
    }

    private static final String TAG ="Service message";

    @Override
    public void onCreate() {

        //init variables
        super.onCreate();
        Log.i(TAG, "Service onCreate");
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        peers_list = new ArrayList<WifiP2pDevice>();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service onStartCommand");
        registerReceiver(mReceiver, mIntentFilter);
        Runnable r = new Runnable() {
            public void run() {
                Looper.prepare();

                    discoverPeers();
                    Toast.makeText(WiFiService.this, " Searching for peers...",
                            Toast.LENGTH_SHORT).show();

                connectPeers();
                    Log.i(TAG, "call connectPeers method, flag= "+flag);
                Looper.loop();
                stopSelf();
            }
        };

        Thread t = new Thread(r);
        t.start();
        return Service.START_REDELIVER_INTENT;//tells the system to restart the service after the crash and also redeliver the intents that were present at the time of crash.
    }

    /**
     * Discovering peers
     * If it succeed, the broadcastreceiver update the list of peers .
     * The Broadcast receiver will get the event WIFI_P2P_PEERS_CHANGED_ACTION
     */
    void discoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {//when successful
            flag=true;
                Toast.makeText(WiFiService.this, "Found peer",
                        Toast.LENGTH_SHORT).show();
                Log.i(TAG, "discoverPeers onSuccess method, flag= "+flag);
            }
            @Override
            public void onFailure(int reasonCode) {//when there was a problem
                flag=false;
                Log.i(TAG, "discoverPeers onFailure method, flag= "+flag);
            }
        });
    }
    /**
     * Connecting to peers .
     * For now, just connect to the first device of the list .
     */
    void connectPeers() {//connects to every peers around
        if(this.peers_list.size() == 0) {
            Toast.makeText(WiFiService.this, "Found 0 device",
                    Toast.LENGTH_SHORT).show();
            Log.i(TAG, "connectPeers peers size=0");
            return;
        }
        for(WifiP2pDevice device : this.peers_list) {
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
            final String s = config.deviceAddress;
            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {//if it connected, we display the info of the device on the screen
                    Toast.makeText(WiFiService.this, "Device connected "+s,
                            Toast.LENGTH_LONG).show();
                    Log.i(TAG, "connectPeers onSuccess method "+s);

                }
                @Override
                public void onFailure(int reason) {
                    Log.i(TAG, "connectPeers onFailure method");
                }
            });
        }
    }
    /**
     * Getters /Setters
     */
    ArrayList<WifiP2pDevice> getPeersList() {
        return this.peers_list;
    }
    void setPeersList(ArrayList<WifiP2pDevice> a) {
        this.peers_list = a;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        Log.i(TAG, "Service onDestroy");
    }


}
