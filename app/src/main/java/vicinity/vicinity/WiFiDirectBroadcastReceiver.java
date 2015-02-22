package vicinity.vicinity;

/**
 * Created by LAMO on 2/19/15.
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */

import java.util.ArrayList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.widget.TextView;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver{

    private WifiP2pManager mManager;
    private Channel mChannel;
    private WiFiActivity mActivity;
    private PeerListListener myPeerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peers) {
// here, we are putting the discovered peers into
//the list of peers of the activity .
            ArrayList<WifiP2pDevice> list= new ArrayList<WifiP2pDevice>(peers.getDeviceList());
            mActivity.setPeersList(list);
//just displaying how many devices were found
            TextView research = (TextView)mActivity.findViewById(R.id.research);
            int nbr = mActivity.getPeersList().size();
            research.setText("Found "+nbr+" devices :D");
        }
    };
    /**
     * Constructor
     * @param manager
     * @param channel
     * @param activity
     */
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, Channel channel,
                                       WiFiActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }
    /**
     * Method which gets the events and do stuff .
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
// Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
// Wifi P2P is enabled
            } else {
// Wi-Fi P2P is not enabled TODO enable it : retry :p
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
// Call WifiP2pManager.requestPeers() . It will call onPeersAvailable
//of the PeerListListener, which allows us to get back the list
//of peers .
            if (mManager != null) {
                mManager.requestPeers(mChannel, myPeerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
// Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
// Respond to this device's wifi state changing
        }
    }

}
