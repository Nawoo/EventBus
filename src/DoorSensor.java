import EventPackage.Event;
import EventPackage.EventManagerInterface;
import EventPackage.EventQueue;
import InstrumentationPackage.MessageWindow;

public class DoorSensor {
	public static void main(String args[]) {
		String EvtMgrIP; // Event Manager IP address
		Event Evt = null; // Event object
		EventQueue eq = null; // Message Queue
		int EvtId = 0; // User specified event ID
		EventManagerInterface em = null;// Interface object to the event manager
		boolean CurrentDoor = false;// Windows state: false == 정상, true == 비정상
		int Delay = 3000; // The loop delay (2.5 seconds)
		boolean Done = false; // Loop termination flag

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the event manager
		/////////////////////////////////////////////////////////////////////////////////

		if (args.length == 0) {
			// event manager is on the local system

			System.out.println("\n\nAttempting to register on the local machine...");

			try {
				// Here we create an event manager interface object. This assumes
				// that the event manager is on the local machine

				em = new EventManagerInterface();
			}

			catch (Exception e) {
				System.out.println("Error instantiating event manager interface: " + e);

			} // catch

		} else {

			// event manager is not on the local system

			EvtMgrIP = args[0];

			System.out.println("\n\nAttempting to register on the machine:: " + EvtMgrIP);

			try {
				// Here we create an event manager interface object. This assumes
				// that the event manager is NOT on the local machine

				em = new EventManagerInterface(EvtMgrIP);
			}

			catch (Exception e) {
				System.out.println("Error instantiating event manager interface: " + e);

			} // catch

		} // if

		// Here we check to see if registration worked. If ef is null then the
		// event manager interface was not properly created.

		if (em != null) {

			// We create a message window. Note that we place this panel about 1/2 across
			// and 1/3 down the screen

			float WinPosX = 5.3f; // This is the X position of the message window in terms
									// of a percentage of the screen height
			float WinPosY = 5.3f; // This is the Y position of the message window in terms
									// of a percentage of the screen height

			MessageWindow mw = new MessageWindow("Door Sensor", WinPosX, WinPosY);

			mw.WriteMessage("Registered with the event manager.");

			try {
				mw.WriteMessage("   Participant id: " + em.GetMyId());
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime());

			} // try

			catch (Exception e) {
				mw.WriteMessage("Error:: " + e);

			} // catch

			mw.WriteMessage("\nInitializing Window Simulation::");

			CurrentDoor = false;

			mw.WriteMessage("   Initial Window Set:: " + CurrentDoor);
			/********************************************************************
			 ** Here we start the main simulation loop
			 *********************************************************************/

			mw.WriteMessage("Beginning Simulation... ");

			while (!Done) {
				// Post the current window

				// 이벤트 메니저에게 이벤트 보내기
				PostDoorStatus(em, CurrentDoor);

				mw.WriteMessage(" DoorSatus::  " + CurrentDoor + "not break!");

				// Get the message queue

				try {
					eq = em.GetEventQueue();

				} // try

				catch (Exception e) {
					mw.WriteMessage("Error getting event queue::" + e);

				} // catch

			} // if

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

			} // if

		} // for

	} // main

	/***************************************************************************
	 * CONCRETE METHOD:: PostTemperature Purpose: This method posts the specified
	 * temperature value to the specified event manager. This method assumes an
	 * event ID of 1.
	 *
	 * Arguments: EventManagerInterface ei - this is the eventmanger interface where
	 * the event will be posted.
	 *
	 * float temperature - this is the temp value.
	 *
	 * Returns: none
	 *
	 * Exceptions: None
	 *
	 ***************************************************************************/

	static private void PostDoorStatus(EventManagerInterface ei, boolean doorbreak) {
		// Here we create the event.

		Event evt = new Event((int) 4, String.valueOf(doorbreak));

		// Here we send the event to the event manager.

		try {
			ei.SendEvent(evt);// System.out.println( "Sent Door Event" );
		} // try
		catch (Exception e) {
			System.out.println("Error Posting doorbreak:: " + e);

		} // catch

	} // PostDoor

} // DoorSensor
