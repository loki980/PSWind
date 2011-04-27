package com.lokico.PSWind;

import java.util.Vector;

public class SensorDataSet {
	private Vector<SensorData> sensorVector = new Vector<SensorData>();

	public SensorData getSensorById(String id) {
		for (int i = 0; i < sensorVector.size(); i++) {
			if (sensorVector.get(i).id == id) {
				return sensorVector.get(i);
			}
		}
		return (SensorData) null;
	}
	
	public SensorData getSensorByIndex(int index) {
		return sensorVector.get(index);
	}

	public int getSensorDataSize() {
		return sensorVector.size();
	}

	public void addSensorData(String id, String label, float age, float lat, float lon,
			int wind, int gust, int angle) {
		sensorVector.addElement(new SensorData(id, label, age, lat, lon, wind, gust,
				angle));
	}

	public class SensorData {
		public String id = null;
		public String label = null;
		public float age = 0;
		public float lat = 0;
		public float lon = 0;
		public int wind = 0;
		public int gust = 0;
		public int angle = 0;
		public String angleImg = null;

		public SensorData(String id, String label, float age, float lat, float lon, int wind,
				int gust, int angle) {
			this.id = id;
			this.age = age;
			this.lat = lat;
			this.lon = lon;
			this.wind = wind;
			this.gust = gust;
			this.angle = angle;
			this.label = label;
		}
	}
}