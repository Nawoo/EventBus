
/******************************************************************************************************************
* File:ECSMonitor.java
*
* Description:
*
* This class monitors the environmental control systems that control museum temperature and humidity. In addition to
* monitoring the temperature and humidity, the ECSMonitor also allows a user to set the humidity and temperature
* ranges to be maintained. If temperatures exceed those limits over/under alarm indicators are triggered.
*
* Parameters: IP address of the event manager (on command line). If blank, it is assumed that the event manager is
* on the local machine.
*
* Internal Methods:
*	static private void Heater(EventManagerInterface ei, boolean ON )
*	static private void Chiller(EventManagerInterface ei, boolean ON )
*	static private void Humidifier(EventManagerInterface ei, boolean ON )
*	static private void Dehumidifier(EventManagerInterface ei, boolean ON )
*
******************************************************************************************************************/
import InstrumentationPackage.*;
import EventPackage.*;

class ECSMonitor2 extends Thread {
	private EventManagerInterface em = null;// Interface object to the event manager
	private String EvtMgrIP = null; // Event Manager IP address
	private boolean windowState = false; // window
	private boolean doorState = false; // door
	private boolean MotionDetectionState = false;
	boolean Registered = true; // Signifies that this class is registered with an event manager.
	MessageWindow mw = null; // This is the message window
	Indicator windowAlarm; // window indicator
	Indicator doorAlram; // door indicator
	Indicator motionAlarm; // motion indicator

	public ECSMonitor2() {
		// event manager is on the local system
		try {
			// Here we create an event manager interface object. This assumes
			// that the event manager is on the local machine
			// new하면 프로세스간 통신
			em = new EventManagerInterface();
		} catch (Exception e) {
			System.out.println("ECSMonitor2::Error instantiating event manager interface: " + e);
			Registered = false;
		} // catch

	} // Constructor

	public ECSMonitor2(String EvmIpAddress) {
		// event manager is not on the local system
		EvtMgrIP = EvmIpAddress;

		try {
			// Here we create an event manager interface object. This assumes
			// that the event manager is NOT on the local machine
			em = new EventManagerInterface(EvtMgrIP);
		}

		catch (Exception e) {
			System.out.println("ECSMonitor::Error instantiating event manager interface: " + e);
			Registered = false;
		} // catch

	} // Constructor

	public void run() {
		Event Evt = null; // Event object
		EventQueue eq = null; // Message Queue
		int EvtId = 0; // User specified event ID
		boolean CurrentWindow = false; // Current relative window as reported by the window sensor
		boolean CurrentDoor = false; // Current relative door as reported by the door sensor
		boolean CurrentMotionDetection = false;
		int Delay = 3000; // The loop delay (1 second)
		boolean Done = false; // Loop termination flag

		if (em != null) {
			// Now we create the ECS status and message panel
			// Note that we set up two indicators that are initially yellow. This is
			// because we do not know if the temperature/humidity is high/low.
			// This panel is placed in the upper left hand corner and the status
			// indicators are placed directly to the right, one on top of the other

			mw = new MessageWindow("ECS Monitoring2 Console", 3, 3);
			windowAlarm = new Indicator("Window UNK", mw.GetX() + mw.Width(), 0);
			doorAlram = new Indicator("Door UNK", mw.GetX() + mw.Width(), 0);
			motionAlarm = new Indicator("Motion UNK", mw.GetX() + mw.Width(), 0);

			mw.WriteMessage("Registered with the event manager.");

			try {
				mw.WriteMessage("   Participant id: " + em.GetMyId());
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime());

			} // try

			catch (Exception e) {
				System.out.println("Error:: " + e);

			} // catch

			/********************************************************************
			 ** Here we start the main simulation loop
			 *********************************************************************/

			while (!Done) {
				// Here we get our event queue from the event manager
				try {
					eq = em.GetEventQueue();

				} // try

				catch (Exception e) {
					mw.WriteMessage("Error getting event queue::" + e);

				} // catch

				// If there are messages in the queue, we read through them.
				// We are looking for EventIDs = 1 or 2. Event IDs of 1 are temperature
				// readings from the temperature sensor; event IDs of 2 are humidity sensor
				// readings. Note that we get all the messages at once... there is a 1
				// second delay between samples,.. so the assumption is that there should
				// only be a message at most. If there are more, it is the last message
				// that will effect the status of the temperature and humidity controllers
				// as it would in reality.

				int qlen = eq.GetSize();

				for (int i = 0; i < qlen; i++) {
					// 이벤트를 받아온다
					Evt = eq.GetEvent();

					if (Evt.GetEventId() == 3) { // windowStatus reading
						try {
							CurrentWindow = Boolean.valueOf(Evt.GetMessage());
						} // try
						catch (Exception e) {
							mw.WriteMessage("Error reading window: " + e);
						} // catch

					} // if
					if (Evt.GetEventId() == 4) { // doorStatus reading
						try {
							CurrentDoor = Boolean.valueOf(Evt.GetMessage());
						} // try
						catch (Exception e) {
							mw.WriteMessage("Error reading door: " + e);
						} // catch
					}
					if (Evt.GetEventId() == 6) { // motionDetectionStatus reading
						try {
							CurrentMotionDetection = Boolean.valueOf(Evt.GetMessage());
						} catch (Exception e) {
						} // catch
				}

				// If the event ID == 99 then this is a signal that the simulation
				// is to end. At this point, the loop termination flag is set to
				// true and this process unregisters from the event manager.

				if (Evt.GetEventId() == 99) {
					Done = true;

					try {
						em.UnRegister();

					} // try

					catch (Exception e) {
						mw.WriteMessage("Error unregistering: " + e);

					} // catch

					mw.WriteMessage("\n\nSimulation Stopped. \n");

					// Get rid of the indicators. The message panel is left for the
					// user to exit so they can see the last message posted.

					windowAlarm.dispose();
					doorAlram.dispose();
				} // if

			} // for

			mw.WriteMessage("Window:: " + CurrentWindow + "Door::" + CurrentDoor+ "MotionDetection::" +CurrentMotionDetection);

			// Check temperature and effect control as necessary

			// window break
			if (CurrentWindow != windowState) { // 창문이 깨졌을 때
				windowAlarm.SetLampColorAndMessage("WINDOW BREAK", 3);
			} else { // 안깨졌을 때
				windowAlarm.SetLampColorAndMessage("WINDOW NOT BREAK", 1);
			} // if
			if (CurrentDoor != doorState) {// 문이 부셔졌을 때
				doorAlram.SetLampColorAndMessage("DOOR BREAK", 3);
			} else {// 안부셔졌을 때
				doorAlram.SetLampColorAndMessage("DOOR NOT BREAK", 1);
			}
			if(CurrentMotionDetection != MotionDetectionState) {
				motionAlarm.SetLampColorAndMessage("MotionDetection", 3);
			}else {
				motionAlarm.SetLampColorAndMessage("Nothing", 1);
			}
			// This delay slows down the sample rate to Delay milliseconds
			try {
				Thread.sleep(Delay);
			} // try

			catch (Exception e) {
				System.out.println("Sleep error:: " + e);
			} // catch
		} // while
	}else	{
		System.out.println("Unable to register with the event manager.\n\n");
	} // if
	} // main

	/***************************************************************************
	 * CONCRETE METHOD:: IsRegistered Purpose: This method returns the registered
	 * status
	 *
	 * Arguments: none
	 *
	 * Returns: boolean true if registered, false if not registered
	 *
	 * Exceptions: None
	 *
	 ***************************************************************************/

	public boolean IsRegistered() {
		return (Registered);

	}

	/***************************************************************************
	 * CONCRETE METHOD:: Halt Purpose: This method posts an event that stops the
	 * environmental control system.
	 *
	 * Arguments: none
	 *
	 * Returns: none
	 *
	 * Exceptions: Posting to event manager exception
	 *
	 ***************************************************************************/

	public void Halt() {
		mw.WriteMessage("***HALT MESSAGE RECEIVED - SHUTTING DOWN SYSTEM***");

		// Here we create the stop event.

		Event evt;

		evt = new Event((int) 99, "XXX");

		// Here we send the event to the event manager.

		try {
			em.SendEvent(evt);

		} // try

		catch (Exception e) {
			System.out.println("Error sending halt message:: " + e);

		} // catch

	} // Halt

	public void setWindowStatus(boolean checkAlarm) {
		windowState = checkAlarm;
		mw.WriteMessage("***Window status changed to::" + windowState);

	}

	public void setDoorStatus(boolean checkAlarm2) {
		doorState = checkAlarm2;
		mw.WriteMessage("***Door status changed to::" + doorState);
	}
	
	public void setMotionDetectionStatus(boolean checkAlarm3) {
		MotionDetectionState = checkAlarm3;
		mw.WriteMessage("***MotionDetection status changed to::" + MotionDetectionState);
	}

} // ECSMonitor