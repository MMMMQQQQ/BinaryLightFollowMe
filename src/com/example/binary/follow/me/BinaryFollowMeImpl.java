package com.example.binary.follow.me;

import fr.liglab.adele.icasa.device.DeviceListener;
import fr.liglab.adele.icasa.device.GenericDevice;
import fr.liglab.adele.icasa.device.presence.PresenceSensor;
import fr.liglab.adele.icasa.device.light.BinaryLight;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BinaryFollowMeImpl implements DeviceListener {

	/** Field for presenceSensors dependency */
	private PresenceSensor[] presenceSensors;
	/** Field for binaryLights dependency */
	private BinaryLight[] binaryLights;

	/** Bind Method for presenceSensors dependency */
	public synchronized void bindPresenceSensor(PresenceSensor presenceSensor, Map properties) {
		/*System.out.println("bind presence sensor "+ presenceSensor.getSerialNumber());*/
		//Add the listener to the presence sensor
		presenceSensor.addListener(this);
	}

	/** Unbind Method for presenceSensors dependency */
	public synchronized void unbindPresenceSensor(PresenceSensor presenceSensor,
			Map properties) {
		/*System.out.println("Unbind presence sensor "+ presenceSensor.getSerialNumber());*/
		//Remove the listener from the presence sensor
		presenceSensor.removeListener(this);
	}

	/** Bind Method for binaryLights dependency */
	public void bindBinaryLight(BinaryLight binaryLight, Map<Object,Object> properties) {
		  System.out.println("bind binary light " + binaryLight.getSerialNumber());
	}

	/** Unbind Method for binaryLights dependency */
	public void unbindBinaryLight(BinaryLight binaryLight, Map<Object,Object> properties) {
		  System.out.println("unbind binary light " + binaryLight.getSerialNumber());
	}

	/** Component Lifecycle Method */
	public synchronized void stop() {
		/*System.out.println("Component is stopping...");*/
		for (PresenceSensor sensor:presenceSensors){
			sensor.removeListener(this);
		}
	}

	/** Component Lifecycle Method */
	public void start() {
		
	}

	@Override
	public void deviceAdded(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deviceEvent(GenericDevice arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void devicePropertyAdded(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
	/*the name of the LOCATION property*/
	public static final String LOCATION_PROPERTY_NAME="Location";
	
	/*the name of the location for unknown value*/
	public static final String LOCATION_UNKNOWN="unknown";
	
	@Override
	public void devicePropertyModified(GenericDevice device, String propertyName,
			Object oldValue, Object newValue) {
		//we assume that we listen only to presence sensor events (otherwise there is a bug)
		assert device instanceof PresenceSensor: "device must be a presence sensors only";
	
		//based on that assumption we can cast the generic device without checking via intanceof
		PresenceSensor changingSensor=(PresenceSensor)device;
		
		//check the change is related to presence sensing
		if (propertyName.equals(PresenceSensor.PRESENCE_SENSOR_SENSED_PRESENCE)){
			//get the location of the changing sensor:
			String detectorLocation=(String) changingSensor.getPropertyValue(LOCATION_PROPERTY_NAME);
			System.out.println("The device with the serial number "+changingSensor.getSerialNumber()+" has changed");
			System.out.println("This sensor is in the room: "+detectorLocation);
			
			//if the location is known:
			if(!detectorLocation.equals(LOCATION_UNKNOWN)){
				//get the related binary lights
				List<BinaryLight>sameLocationLights=getBinaryLightFromLocation(detectorLocation);
				for(BinaryLight binaryLight:sameLocationLights){
					//and swtich them on/off depending on the sensed presence
					if(changingSensor.getSensedPresence()){
						binaryLight.turnOn();
					}else{
						binaryLight.turnOff();
					}
				}
			}
		}
	}

	@Override
	public void devicePropertyRemoved(GenericDevice arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deviceRemoved(GenericDevice arg0) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Return all BinaryLight from the given location
	 * 
	 * @param location:this given location
	 * @return the list of matching BinaryLights
	 * */ 
	private synchronized List<BinaryLight> getBinaryLightFromLocation(String location){
		List<BinaryLight>binaryLightsLocation=new ArrayList<BinaryLight>();
		for(BinaryLight binLight:binaryLights){
			if(binLight.getPropertyValue(LOCATION_PROPERTY_NAME).equals(location)){
				binaryLightsLocation.add(binLight);
			}
		}
		return binaryLightsLocation;
	}
}
