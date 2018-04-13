import java.util.Random;
import java.util.Scanner;

class CPU386 {

    public static final int OS_CrLf 		= 0x20;
    public static final int OS_ReadInt 		= 0x21;
    public static final int OS_WriteInt 	= 0x22;
    public static final int OS_WriteString 	= 0x23;
    public static final int OS_ExitProcess 	= 0x24;
    public static final int OS_RandomRange	= 0x25;

    public SimMemory memory;
    Random rng = new Random();
    Scanner scanner = new Scanner( System.in );

    public boolean running;

    public String[] regNames = {"EAX", "EBX", "ECX", "EDX", "ESI", "EDI", "EBP", "ESP", "EIP"};
    int EAX = 0, EBX = 1, ECX = 2, EDX = 3, ESI = 4, EDI = 5, EBP = 6, ESP = 7, EIP = 8;
    public int[] regValues;

    // constructor
    public CPU386( SimMemory memory, Scanner in ) {
        this.memory = memory;
        regValues = new int[9];
        for(int i = 0; i < regValues.length; i++)
            regValues[i] = 0;

        running = false;
    }

    // set the program counter
    public void setEIP( int startAddr ) {
        regValues[EIP] = startAddr;
    }

    public int getEIP(){
        return regValues[EIP];
    }

    // set the stack pointer
    public void setESP(int stackAddr ) {
        regValues[ESP] = stackAddr;
    }

    // reset the CPU
    public void reset() {

    }

    // display the CPU registers
    public void displayRegisters() {
        for(int i = 0; i < regValues.length; i++){
            System.out.println(regNames[i] + ": " + regValues[i]);
        }
    }

    // emulate os call
    // for "INT xx", the value of xx should be passed as the parameter osCall
    // <EAX> and <EDX> should be replaced by an appropriate reference to the register
    protected void processOsCall( int osCall ) throws Exception {
        //System.out.printf("processOsCall %02x\n", osCall );
        switch( osCall ) {
            // write Cr/Lf
            case OS_CrLf:
                System.out.println();
                break;

            // get a 32-bit signed integer and return it in EAX
            case OS_ReadInt:
                regValues[EAX] = scanner.nextInt();
                // eat up the end-of-line
                scanner.nextLine();
                break;

            // display a 32-bit signed integer from EAX
            case OS_WriteInt:

                int outVal = regValues[EAX];
                System.out.print( (outVal>=0?"+":"") + outVal );
                if ( !running )
                    System.out.println();
                break;

            // display the string referenced by EDX
            case OS_WriteString:
                String outStr = memory.readString( regValues[EDX] );
                System.out.print( outStr );

                // single step and doesn't end in LF
                if ( !running && outStr.charAt(outStr.length()-1)!=10 )
                    System.out.println();
                break;

            case OS_ExitProcess:
                System.out.println("!! ExitProcess called !!");
                running = false;
                break;

            case OS_RandomRange:
                regValues[EAX] = rng.nextInt(regValues[EAX]);
                break;

            default:
                //System.out.println("Throwing exception");
                throw new Exception("Unknown interrupt call");
        }
    }

    // emulate one instruction
    public void emulateInstruction() throws Exception {

        String instruction = memory.listingLines.get(regValues[EIP]);
        OpCodeInterpreter op = new OpCodeInterpreter(instruction, this);
        op.determineAction();

    } // emulateInstruction

    public void setRunning(){
        running = true;
    }

    public boolean isRunning(){
        return running;
    }

    public int getRegister(int key){
        return regValues[key];
    }
} // class CPU386