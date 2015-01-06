package model;

import java.io.Serializable;

import android.os.ParcelUuid;

public class Device implements Serializable {
	private static final long serialVersionUID = -5961124837206924220L;

	private String address, name;
	private int bondState, rssi;
	private byte[] scanReadData;
	private ParcelUuid[] uuids;
	
	public Device() {};
	
	public Device(String address, String name, int bondState, int rssi, byte[] scanReadData, ParcelUuid[] uuids) {
		this.address = address;
		this.name = name;
		this.bondState = bondState;
		this.rssi = rssi;
		this.scanReadData = scanReadData;
		this.uuids = uuids;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBondState() {
		return bondState;
	}

	public void setBondState(int bondState) {
		this.bondState = bondState;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public byte[] getScanReadData() {
		return scanReadData;
	}

	public void setScanReadData(byte[] scanReadData) {
		this.scanReadData = scanReadData;
	}

	public ParcelUuid[] getUuids() {
		return uuids;
	}

	public void setUuids(ParcelUuid[] uuids) {
		this.uuids = uuids;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
