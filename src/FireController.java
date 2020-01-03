/******************************************************************************************************************
* File:TemperatureController.java
*
* Description:
*
* This class simulates a device that controls a heater and chiller. It polls the event manager for event ids = 5
* and reacts to them by turning on or off the heater or chiller. The following command are valid strings for con
* trolling the heater and chiller:
*
*	H1 = heater on
*	H0 = heater off
*	C1 = chillerer on
*	C0 = chiller off
*
* The state (on/off) is graphically displayed on the terminal in the indicator. Command messages are displayed in
* the message window. Once a valid command is recieved a confirmation event is sent with the id of -5 and the command in
* the command string.
*
* Parameters: IP address of the event manager (on command line). If blank, it is assumed that the event manager is
* on the local machine.
*
* Internal Methods:
*	static private void ConfirmMessage(EventManagerInterface ei, String m )
*
******************************************************************************************************************/
import InstrumentationPackage.*;
import EventPackage.*;

class FireController{
	public static void main(String args[])	{
		String EvtMgrIP;				// Event Manager IP address
		Event Evt = null;				// Event object
		EventQueue eq = null;			// Message Queue
		int EvtId = 0;					// User specified event ID
		EventManagerInterface em = null;// Interface object to the event manager
		boolean sprinklerState = false;
		int	Delay = 3000;				// The loop delay (2.5 seconds)
		boolean Done = false;			// Loop termination flag

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the event manager
		/////////////////////////////////////////////////////////////////////////////////

 		if ( args.length == 0 )	{
			// event manager is on the local system
			System.out.println("\n\nAttempting to register on the local machine..." );

			try	{
				// Here we create an event manager interface object. This assumes
				// that the event manager is on the local machine
				em = new EventManagerInterface();
			}

			catch (Exception e)		{
				System.out.println("Error instantiating event manager interface: " + e);
			} // catch
		} else {
			// event manager is not on the local system
			EvtMgrIP = args[0];

			System.out.println("\n\nAttempting to register on the machine:: " + EvtMgrIP );

			try	{
				// Here we create an event manager interface object. This assumes
				// that the event manager is NOT on the local machine
				em = new EventManagerInterface( EvtMgrIP );
			}
			catch (Exception e)	{
				System.out.println("Error instantiating event manager interface: " + e);
			} // catch

		} // if

		// Here we check to see if registration worked. If ef is null then the
		// event manager interface was not properly created.

		if (em != null)
		{
			System.out.println("Registered with the event manager." );

			/* Now we create the temperature control status and message panel
			** We put this panel about 1/3 the way down the terminal, aligned to the left
			** of the terminal. The status indicators are placed directly under this panel
			*/

			float WinPosX = 0.0f; 	//This is the X position of the message window in terms 
								 	//of a percentage of the screen height
			float WinPosY = 0.6f; 	//This is the Y position of the message window in terms 
								 	//of a percentage of the screen height 
			
			MessageWindow mw = new MessageWindow("Fire Controller Status Console", WinPosX, WinPosY);
			
			// Put the status indicators under the panel...
			//화면빨강
			Indicator sprinkler = new Indicator ("Fire Status", mw.GetX(), mw.GetY()+mw.Height());

			mw.WriteMessage("Registered with the event manager." );

	    	try  {
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );

			} // try

	    	catch (Exception e)	{
				System.out.println("Error:: " + e);

			} // catch

			/********************************************************************
			** Here we start the main simulation loop
			*********************************************************************/

			while ( !Done )	{

				try	{
					eq = em.GetEventQueue();
				} // try
				catch( Exception e ){
					mw.WriteMessage("Error getting event queue::" + e );
				} // catch

				// If there are messages in the queue, we read through them.
				// We are looking for EventIDs = 6, this is a request to turn the
				// heater or chiller on. Note that we get all the messages
				// at once... there is a 2.5 second delay between samples,.. so
				// the assumption is that there should only be a message at most.
				// If there are more, it is the last message that will effect the
				// output of the temperature as it would in reality.

				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ )	{
					Evt = eq.GetEvent();

					if ( Evt.GetEventId() == 8 )	{
						if (Evt.GetMessage().equalsIgnoreCase("F1")) //springCooler on
						{
							sprinklerState = true;//sprinkler on
							mw.WriteMessage("Received sprinklerState on event" );

							// Confirm that the message was received and acted on

							ConfirmMessage( em, "F1" );

						} // if

						if (Evt.GetMessage().equalsIgnoreCase("F0")) // alarm off
						{
							sprinklerState = false;//sprinkler Off
							mw.WriteMessage("Received sprinklerState off event" );

							// Confirm that the message was received and acted on

							ConfirmMessage( em, "F0" );

						} // if

					} // if

					// If the event ID == 99 then this is a signal that the simulation
					// is to end. At this point, the loop termination flag is set to
					// true and this process unregisters from the event manager.

					if ( Evt.GetEventId() == 99 )	{
						Done = true;

						try	{
							em.UnRegister();

				    	} // try

				    	catch (Exception e)   	{
							mw.WriteMessage("Error unregistering: " + e);

				    	} // catch

				    	mw.WriteMessage( "\n\nSimulation Stopped. \n");

						// Get rid of the indicators. The message panel is left for the
						// user to exit so they can see the last message posted.

				    	sprinkler.dispose();

					} // if

				} // for

				// Update the lamp status

				if (sprinklerState){ //false = 작동안함
					// Set to black, sprinkler is off
					sprinkler.SetLampColorAndMessage("SPRINKLER OFF", 0);

				} else {
					// Set to green, sprinkler is on
					sprinkler.SetLampColorAndMessage("SPRINKLER ON", 1);

				} // if
				try		{
					Thread.sleep( Delay );
				} // try

				catch( Exception e )	{
					System.out.println( "Sleep error:: " + e );
				} // catch

			} // while

		} else {

			System.out.println("Unable to register with the event manager.\n\n" );

		} // if

	} // main

	/***************************************************************************
	* CONCRETE METHOD:: ConfirmMessage
	* Purpose: This method posts the specified message to the specified event
	* manager. This method assumes an event ID of -5 which indicates a confirma-
	* tion of a command.
	*
	* Arguments: EventManagerInterface ei - this is the eventmanger interface
	*			 where the event will be posted.
	*
	*			 string m - this is the received command.
	*
	* Returns: none
	*
	* Exceptions: None
	*
	***************************************************************************/

	static private void ConfirmMessage(EventManagerInterface ei, String m )	{
		// Here we create the event.

		Event evt = new Event( (int) -8, m );

		// Here we send the event to the event manager.

		try	{
			ei.SendEvent( evt );

		} // try

		catch (Exception e)	{
			System.out.println("Error Confirming Message:: " + e);

		} // catch

	} // PostMessage

} // WindowController