/******************************************************************************************************************
* File:ECSConsole.java
*
* Description: This class is the console for the museum environmental control system. This process consists of two
* threads. The ECSMonitor object is a thread that is started that is responsible for the monitoring and control of
* the museum environmental systems. The main thread provides a text interface for the user to change the temperature
* and humidity ranges, as well as shut down the system.
*
* Parameters: None
*
* Internal Methods: None
*
******************************************************************************************************************/
import TermioPackage.*;
import EventPackage.*;

public class ECSConsole2 {
	public static void main(String args[]) {
    	Termio UserInput = new Termio();	// Termio IO Object
		boolean Done = false;				// Main loop flag
		String Option = null;				// Menu choice from user
		Event Evt = null;					// Event object
		boolean Error = false;				// Error flag
		ECSMonitor3 Monitor3 = null;			// The environmental control system monitor
		
		boolean fire = false;
		/////////////////////////////////////////////////////////////////////////////////
		// Get the IP address of the event manager
		/////////////////////////////////////////////////////////////////////////////////

 		if ( args.length != 0 ) {
			// event manager is not on the local system

			Monitor3 = new ECSMonitor3( args[0] );

		} else {

			Monitor3 = new ECSMonitor3();

		} // if


		// Here we check to see if registration worked. If ef is null then the
		// event manager interface was not properly created.

		if (Monitor3.IsRegistered()){
			Monitor3.start(); // Here we start the monitoring and control thread
			while (!Done){
				// Here, the main thread continues and provides the main menu

				System.out.println( "\n\n\n\n" );
				System.out.println( "Environmental Control System (ECS) Command Console: \n" );

				if (args.length != 0)
					System.out.println( "Using event manger at: " + args[0] + "\n" );
				else
					System.out.println( "Using local event manger \n" );

				System.out.println( "Select an Option: \n" );
				System.out.println( "1: Alarm Fire" );
				System.out.println( "2: Kill Sprinkler" );
				System.out.println( "X: Stop System\n" );
				System.out.print( "\n>>>> " );
				Option = UserInput.KeyboardReadString();

				//////////// option 1 ////////////

				if ( Option.equals( "1" ) ){
					// Here we get the temperature ranges

					Error = true;

					while (Error){
						// Here we get the low temperature range

						while (Error){
							System.out.print( "\nDo fire???? yes or no!>>> " );
							Option = UserInput.KeyboardReadString();

							if (UserInput.IsNumber(Option)){
								Error = false;
								fire = true;
								Monitor3.setFireStatus(fire);
							} else {
								System.out.println( "Not a number, please try again..." );
							} // if
							
						} // while

					} // while

				} // if
				//////////// option 2 ////////////
				
				if ( Option.equals( "2" ) ){
					// Here we get the temperature ranges

					Error = true;

					while (Error){
						// Here we get the low temperature range

						while (Error){
							System.out.print( "\nDo you want fill Sprinkler>>> " );
							Option = UserInput.KeyboardReadString();

							if (UserInput.IsNumber(Option)){
								Error = false;
								fire = false;
								Monitor3.setFireStatus(fire);
							} else {
								System.out.println( "Not a number, please try again..." );
							} // if
							
						} // while

					} // while

				} // if
				//////////// option X ////////////

				if ( Option.equalsIgnoreCase( "X" ) )
				{
					// Here the user is done, so we set the Done flag and halt
					// the environmental control system. The monitor provides a method
					// to do this. Its important to have processes release their queues
					// with the event manager. If these queues are not released these
					// become dead queues and they collect events and will eventually
					// cause problems for the event manager.

					Monitor3.Halt();
					Done = true;
					System.out.println( "\nConsole Stopped... Exit monitor mindow to return to command prompt." );
					Monitor3.Halt();

				} // if

			} // while

		} else {

			System.out.println("\n\nUnable start the monitor.\n\n" );

		} // if

  	} // main

} // ECSConsole
