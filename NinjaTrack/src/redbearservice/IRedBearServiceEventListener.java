package redbearservice;

import android.os.ParcelUuid;

public interface IRedBearServiceEventListener {
	void onDeviceFound(String address, String name, int rssi,
			int bondState, byte[] scanRecord, ParcelUuid[] uuids);

	void onDeviceRssiUpdate(String address, int rssi, int state);

	void onDeviceConnectStateChange(String address, int state);

	void onDeviceReadValue(int[] value);
	
	void onDeviceCharacteristicFound();
}