package model;

public class Pin {
	
	private int pin, value, mode;
	private int[] modes;
	
	public Pin() {};
	
	public Pin(int pin, int[] modes, int value, int mode) {
		this.pin = pin;
		this.modes = modes;
		this.value = value;
		this.mode = mode;
	}

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int[] getModes() {
		return modes;
	}

	public void setModes(int[] modes) {
		this.modes = modes;
	}
}
