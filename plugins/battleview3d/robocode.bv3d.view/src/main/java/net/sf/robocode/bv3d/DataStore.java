package net.sf.robocode.bv3d;


import java.util.ArrayList;


public class DataStore {
	
	private int inputSerial, outputSerial;
	private ArrayList<String> data;

	public DataStore() {
		data = new ArrayList<String>();
		inputSerial = 0;
		outputSerial = 0;
	}

	public String getData() {
		String res = null;

		if (outputSerial < inputSerial) {
			res = data.get(outputSerial);
			outputSerial++;
		}
		return res;
	}

	public void setData(String message) {
		this.data.add(message);
		inputSerial++;
	}
	
}
