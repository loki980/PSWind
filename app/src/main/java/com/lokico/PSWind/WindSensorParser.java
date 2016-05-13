package com.lokico.PSWind;

import java.util.List;

public class WindSensorParser {
    static public void parseRawSensorData(String rawSensorData, List<WindSensor> windSensors) {
        // TODO Test data
        /*
        <markers>
<marker id="WOTW07" label="Hat Island" lat="48.0194" lng="-122.334" wind="1" gust="3" angle="225" timestamp="1462988635"/>
<marker id="WA010" label="Locust Beach" lat="48.7767" lng="-122.562" wind="3" gust="5" angle="337" timestamp="1462988340"/>
<marker id="PBFW1" label="Padilla Bay" lat="48.4639" lng="-122.468" wind="2" gust="4" angle="315" timestamp="1462984200"/>
<marker id="KNUW" label="Whidbey Island" lat="48.3492" lng="-122.651" wind="7" gust="0" angle="135" timestamp="1462985760"/>
<marker id="WA001" label="Jetty Island" lat="48.0035" lng="-122.228" wind="6" gust="3" angle="90" timestamp="1409602200"/>

<marker id="KWAFREEL8" label="Useless Bay" lat="47.99" lng="-122.51" wind="3" gust="7" angle="180" timestamp="1441839360"/>
<marker id="KWAFREEL6" label="Mutiny Bay" lat="48.01" lng="-122.56" wind="15" gust="15" angle="315" timestamp="1436731836"/>
<marker id="KUIL" label="Quillayute Airport" lat="47.9375" lng="-124.555" wind="8" gust="0" angle="45" timestamp="1463093580"/>
<marker id="KPAE" label="Everett: Snohomish County Airport" lat="47.9231" lng="-122.283" wind="6" gust="0" angle="112" timestamp="1463093580"/>
<marker id="WA002" label="Point No Point" lat="47.9167" lng="-122.533" wind="3" gust="0" angle="0" timestamp="1459895122"/>

        *
         */
        //WindSensor windSensor = new WindSensor(lat, lon, title, dir, speed)
        windSensors.add(new WindSensor((float) 48.0194,(float)-122.334, "Hat Island", 225, 10));
        windSensors.add(new WindSensor((float) 48.7767,(float)-122.562, "Locust Beach", 337, 15));
        windSensors.add(new WindSensor((float) 48.4639,(float)-122.468, "Padilla Bay", 315, 20));
        windSensors.add(new WindSensor((float) 48.3492,(float)-122.651, "Whidbey Island", 135,25));
        windSensors.add(new WindSensor((float) 48.0035,(float)-122.228, "Jetty Island", 90, 30));

        windSensors.add(new WindSensor((float) 47.99,(float)-122.51, "Useless Bay", 180, 22));
        windSensors.add(new WindSensor((float) 48.01,(float)-122.56, "Mutiny Bay", 315, 24));
        windSensors.add(new WindSensor((float) 47.9375,(float)-124.555, "Quillayute Airport", 45, 26));
        windSensors.add(new WindSensor((float) 47.9231,(float)-122.283, "Everett: Snohomish County Airport", 0, 28));
        windSensors.add(new WindSensor((float) 47.9167,(float)-122.533, "Point No Point", 0, 30));

    }

}
