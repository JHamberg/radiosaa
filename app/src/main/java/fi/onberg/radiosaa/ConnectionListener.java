package fi.onberg.radiosaa;

import android.content.Context;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrength;
import android.telephony.TelephonyManager;

public class ConnectionListener {
    private TelephonyManager telephonyManager;
    private Handler updateHandler;
    private Store<Double> store;
    private long updateInterval;
    private boolean stop;

    private Runnable update = new Runnable() {
        @Override
        public void run() {
            if(!stop){
                double signalStrength = getSignalStrength();
                if(signalStrength != Integer.MIN_VALUE){
                    store.dispatch("UPDATE_CONNECTION", signalStrength);
                }
                updateHandler.postDelayed(this, updateInterval);
            }
        }
    };

    public ConnectionListener(Context context, final Store<Double> store, final long updateInterval){
        this.updateHandler = new Handler();
        this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        this.store = store;
        this.updateInterval = updateInterval;

        // Start updating
        this.stop = false;
        updateHandler.post(update);
    }

    public void destroy(){
        this.stop = true;
        updateHandler.removeCallbacks(update);
    }

    private double getSignalStrength(){
        double maxStrength = Integer.MIN_VALUE;

        for(CellInfo info : telephonyManager.getAllCellInfo()){
            if(info == null) continue;

            CellSignalStrength signalStrength = null;
            if(info instanceof CellInfoGsm){
                signalStrength = ((CellInfoGsm) info).getCellSignalStrength();
            } else if(info instanceof CellInfoCdma){
                signalStrength = ((CellInfoCdma) info).getCellSignalStrength();
            } else if(info instanceof CellInfoLte){
                signalStrength = ((CellInfoLte) info).getCellSignalStrength();
            }

            if(signalStrength == null) continue;
            maxStrength = Math.max(signalStrength.getDbm(), maxStrength);
        }
        return maxStrength;
    }
}
