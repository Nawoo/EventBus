import java.util.Random;

import EventPackage.Event;
import EventPackage.EventManagerInterface;
import EventPackage.EventQueue;
import InstrumentationPackage.MessageWindow;

public class FireSensor {
	public static void main(String args[]) {
		String EvtMgrIP;				// Event Manager IP address
		Event Evt = null;				// Event object
		EventQueue eq = null;			// Message Queue
		int EvtId = 0;					// User specified event ID
		EventManagerInterface em = null;// Interface object to the event manager
		
		boolean sprinklerState = false;	// repairState : false = still break,true = fix it
		boolean CurrentStatus = false;// fire state: false == ����, true == ������
		int	Delay = 3000;				// The loop delay (2.5 seconds)
		boolean Done = false;			// Loop termination flag

		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the event manager
		/////////////////////////////////////////////////////////////////////////////////

 		if ( args.length == 0 ){
			// event manager is on the local system

			System.out.println("\n\nAttempting to register on the local machine..." );

			try{
				// Here we create an event manager interface object. This assumes
				// that the event manager is on the local machine

				em = new EventManagerInterface();
			}

			catch (Exception e){
				System.out.println("Error instantiating event manager interface: " + e);

			} // catch

		} else {

			// event manager is not on the local system

			EvtMgrIP = args[0];

			System.out.println("\n\nAttempting to register on the machine:: " + EvtMgrIP );

			try{
				// Here we create an event manager interface object. This assumes
				// that the event manager is NOT on the local machine

				em = new EventManagerInterface( EvtMgrIP );
			}

			catch (Exception e){
				System.out.println("Error instantiating event manager interface: " + e);

			} // catch

		} // if

		// Here we check to see if registration worked. If ef is null then the
		// event manager interface was not properly created.
	

		if (em != null){

			// We create a message window. Note that we place this panel about 1/2 across 
			// and 1/3 down the screen			
			
			float WinPosX = 5.3f; 	//This is the X position of the message window in terms 
								 	//of a percentage of the screen height
			float WinPosY = 5.3f; 	//This is the Y position of the message window in terms 
								 	//of a percentage of the screen height 
			
			MessageWindow mw = new MessageWindow("Fire Sensor", WinPosX, WinPosY );

			mw.WriteMessage("Registered with the event manager." );

	    	try{
				mw.WriteMessage("   Participant id: " + em.GetMyId() );
				mw.WriteMessage("   Registration Time: " + em.GetRegistrationTime() );

			} // try

	    	catch (Exception e){
				mw.WriteMessage("Error:: " + e);

			} // catch

			mw.WriteMessage("\nInitializing Fire Simulation::" );
			
			CurrentStatus = false;
			
			/********************************************************************
			** Here we start the main simulation loop
			*********************************************************************/

			mw.WriteMessage("Beginning Simulation... ");


			while ( !Done ) {
				// Post the current window
				
				//�̺�Ʈ �޴������� �̺�Ʈ ������
				PostFireStatus( em, CurrentStatus );

				mw.WriteMessage(" FireSatus::  " + CurrentStatus + "calm");

				// Get the message queue

				try	{
					eq = em.GetEventQueue();

				} // try

				catch( Exception e ){
					mw.WriteMessage("Error getting event queue::" + e );

				} // catch

				// If there are messages in the queue, we read through them.
				// We are looking for EventIDs = -5, this means the the heater
				// or chiller has been turned on/off. Note that we get all the messages
				// at once... there is a 2.5 second delay between samples,.. so
				// the assumption is that there should only be a message at most.
				// If there are more, it is the last message that will effect the
				// output of the temperature as it would in reality.

				int qlen = eq.GetSize();

				for ( int i = 0; i < qlen; i++ ){
					Evt = eq.GetEvent();

					if ( Evt.GetEventId() == -8)	{
						if (Evt.GetMessage().equalsIgnoreCase("F1")) //sprinkler on
						{
							sprinklerState = true;

						} // if

						if (Evt.GetMessage().equalsIgnoreCase("F0")) // sprinkler off
						{
							sprinklerState = false;

						} // if

					} // if

					// If the event ID == 99 then this is a signal that the simulation
					// is to end. At this point, the loop termination flag is set to
					// true and this process unregisters from the event manager.

					if ( Evt.GetEventId() == 99 )
					{
						Done = true;

						try
						{
							em.UnRegister();

				    	} // try

				    	catch (Exception e)
				    	{
							mw.WriteMessage("Error unregistering: " + e);

				    	} // catch

				    	mw.WriteMessage("\n\nSimulation Stopped. \n");

					} // if

				} // for

				// Now we trend the temperature according to the status of the
				// heater/chiller controller.

				if (sprinklerState){ //������Ŭ���� �۵������ʴ� ���
					CurrentStatus = true;//�ҳ�
				} 
				if (!sprinklerState){ //������Ŭ���� �۵��ϴ� ���
					CurrentStatus = false;//�Ҿȳ�
				}
				try	{
					Thread.sleep( Delay );

				} // try

				catch( Exception e )	{
					mw.WriteMessage("Sleep error:: " + e );

				} // catch

			} // while

		} else {

			System.out.println("Unable to register with the event manager.\n\n" );

		} // if

	} // main


	/***************************************************************************
	* CONCRETE METHOD:: PostTemperature
	* Purpose: This method posts the specified temperature value to the
	* specified event manager. This method assumes an event ID of 1.
	*
	* Arguments: EventManagerInterface ei - this is the eventmanger interface
	*			 where the event will be posted.
	*
	*			 float temperature - this is the temp value.
	*
	* Returns: none
	*
	* Exceptions: None
	*
	***************************************************************************/

	static private void PostFireStatus(EventManagerInterface ei, boolean fire ){
		// Here we create the event.

		Event evt = new Event( (int) 5, String.valueOf(fire) );

		// Here we send the event to the event manager.

		try{
			ei.SendEvent( evt );//System.out.println( "Sent Temp Event" );		
		} // try
		catch (Exception e)	{
			System.out.println( "Error Posting windowbreak:: " + e );

		} // catch

	} // PostWindow

} // WindowSensor
