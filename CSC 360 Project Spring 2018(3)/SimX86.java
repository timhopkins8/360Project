import java.util.Scanner;
import java.lang.Exception;

public class SimX86 {
	static SimMemory memory = null;
	static CPU386 cpu = null;
	static Expression expr = null;

	public static void main(String [] args)
	{
		boolean haveProgram = false;		// loaded a program yet?
		String lastProgramLoaded = null;
		
		// last instruction that we started
		int lastEIP = 0;
		
		memory = new SimMemory();
		Scanner in = new Scanner( System.in );
		
		// create the simulated CPU class
		cpu = new CPU386( memory, in );
		
		// and our expression evaluator
		expr = new Expression( memory, cpu );

		while( true ) {
			String cmd;
			if ( ! haveProgram ) {
				// get a program to load
				System.out.print("Enter name of program to load: ");
				cmd = "L " + in.nextLine();
			} else {
				System.out.print("Command: ");
				cmd = in.nextLine();
				
				// empty line?
				if ( cmd.length() == 0 ) {
					// change to help request
					cmd = "h";
				}
			}
			
			try {
				switch( cmd.charAt(0) ) {
					case 'q':
					case 'Q':
						return;			// quit
					
					case 'm':			// display memory
					case 'M':
						if ( expr.eval( cmd.substring(1) ) )
							memory.display( expr.getResult() );
						break;
						
					case 'e':			// evaluate expression
					case 'E':
						if ( expr.eval( cmd.substring(1) ) )
							System.out.printf("Result: %08x\n", expr.getResult() );
						break;
						
					case 's':
					case 'S':			// step
						lastEIP = cpu.getEIP();					
						memory.disassemble( lastEIP );
						cpu.emulateInstruction();
						cpu.displayRegisters();
						break;
						
					case 'g':
					case 'G':			// Go
						cpu.setRunning();
						while( cpu.isRunning() ) {
							lastEIP = cpu.getEIP();
							cpu.emulateInstruction();
						}
						break;
					
					case 'r':
					case 'R':
						cpu.displayRegisters();
						break;
						
					case 'u':
					case 'U':
						// what address?
						int curAddr = 0;
						if ( cmd.length() == 1 ) {
							// no address, use the program counter
							curAddr = cpu.getEIP();
						} else {
							// use an explicit address
						if ( expr.eval( cmd.substring(1) ) )
							curAddr = expr.getResult();
						}
						
						for( int i=0; i<20; i++ ) {
							curAddr = memory.disassemble( curAddr );
						}
						break;
						
					case 'l':				// load/reload executable
					case 'L':
						try {
							memory.clear();
							cpu.reset();
							
							String fileName;
							
							// reload the old program?
							if ( cmd.length() == 1 ) {
								// reload the program
								fileName = lastProgramLoaded;
							} else {
								// have a program specified
								int startPos = 1;
								
								// skip spaces
								while ( Character.isWhitespace( cmd.charAt(startPos) ) )
									startPos++;	

								// isolate the name
								fileName = cmd.substring( startPos );
								
								// make sure we have the .exe file
								if ( fileName.indexOf(".") == -1 ) {
									// no extension given
									fileName = fileName + ".exe";
								} else if ( ! fileName.substring( fileName.length()-4 ).equals(".exe") ) {
									fileName = fileName.substring( 0, fileName.length()-4 ) + ".exe";
								}
								
								// save the filename for later
								lastProgramLoaded = fileName;
							}

							int startAddr = memory.loadExe(fileName);
							cpu.setEIP( startAddr );
							cpu.setESP( memory.getMaxAddress() );
							
							// have something to run
							haveProgram = true;
						} catch ( Exception e ) {
							System.out.println( e.getMessage() );
						}
						break;
		
					default:
						System.out.println("Command List:");
						System.out.println("  G - Go (run)");
						System.out.println("  E xxx - Evaluate expression");
						System.out.println("  M xxx - Display memory at the specified address");
						System.out.println("  S - Step (do one instruction and stop)");
						System.out.println("  R - Display registers");
						System.out.println("  U - Unassemble 20 instructions starting at EIP");
						System.out.println("  L - Load (reload) executable");
						System.out.println("  Q - Quit");
				}
			} catch ( Exception e ) {
				System.out.printf("Error in instruction at %08x\n", lastEIP);
				System.out.println( e.getMessage() );
			}
		}		
	}
}