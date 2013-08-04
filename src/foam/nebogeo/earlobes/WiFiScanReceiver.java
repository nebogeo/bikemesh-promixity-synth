package foam.nebogeo.earlobes;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class WiFiScanReceiver extends BroadcastReceiver {
    private static final String TAG = "WiFiScanReceiver";

    public WiFiScanReceiver() {
        super();
        Level=0;
    }

    static public int Level;

    @Override
    public void onReceive(Context c, Intent intent) {
        List<ScanResult> results = ((Earlobes)c).wifi.getScanResults();
        ScanResult bestSignal = null;

        for (ScanResult result : results) {
            if (result.SSID.equals("bikemesh")) {
                String message = String.format("bikemesh located: strength %d",
                                               result.level);
                Level = result.level;
            }
        }
        ((Earlobes)c).wifi.startScan();
    }

}
