public class ControlUnit {
    private String listingLine;
    private String firstByteOpCode;
    private CPU386 cpu;
    private ALU alu;

    public ControlUnit(String listingLine, CPU386 cpu){
        this.listingLine = listingLine;
        setFirstByteOpCode();
        this.cpu = cpu;
        alu = new ALU();
    }

    private int getRegisterFromOpCode(String bits){
        switch(bits){
            case "000":
                return 0;
            case "011":
                return 1;
            case "001":
                return 2;
            case "010":
                return 3;
            case "110":
                return 4;
            case "111":
                return 5;
            case "101":
                return 6;
            case "100":
                return 7;
            default:
                return -1;
        }
    }

    public void setFirstByteOpCode(){
        String opCode = listingLine.substring(10,12);
        int num = Integer.parseInt(opCode,16);
        firstByteOpCode = Integer.toBinaryString(num);
    }

    public void determineAction(){
        String firstFiveBits = firstByteOpCode.substring(0,5);
        SimMemory memory = cpu.memory;
        int regValues[] = cpu.regValues;
        int EAX = cpu.EAX; int EBX = cpu.EBX; int ECX = cpu.ECX; int EDX = cpu.EDX; int EBP = cpu.EBP; int EDI = cpu.EDI; int ESP = cpu.ESP; int ESI = cpu.ESI;

        switch (firstFiveBits){
            case "10111": //move reg/ const
                int destinationRegisterIndex = getRegisterFromOpCode(firstByteOpCode.substring(5,8));
                String const32Hex = listingLine.substring(13,21);
                int const32Int = Integer.parseInt(const32Hex, 16);
                regValues[destinationRegisterIndex] = const32Int;
                break;
            case "01011": // NEEDS TESTING pop register
                regValues[ESP] -= 32; // decrement stack pointer
                destinationRegisterIndex = getRegisterFromOpCode(firstByteOpCode.substring(5,8)); // get the index of the destination register
                String value = memory.listingLines.get(regValues[ESP]); //get value off the stack
                regValues[destinationRegisterIndex] = Integer.parseInt(value, 16); //put the value in the correct register
                break;
            case "01010": //NEEDS TESTING push reg
                int sourceRegisterIndex = getRegisterFromOpCode(firstByteOpCode.substring(5,8)); // get the index of the source register
                String hex = Integer.toHexString(regValues[sourceRegisterIndex]);   //convert the value in the source register to a hex string
                memory.listingLines.put(regValues[ESP],  hex);      //put the hex string onto the stack
                regValues[ESP] += 32;                               //increment the stack pointer
                break;
            default :
                //
        }

        switch(firstByteOpCode){
            case "00000011":
                System.out.print("Add register / register");
                break;
            case "10000011":
                System.out.print("Add register constant");
                System.out.println("AND reg, Const");
                System.out.println("or reg, const");
                System.out.println("subtract reg, cont");
                System.out.println("xor reg, const");
                break;
            case "00100011":
                System.out.println("AND reg, reg");
                break;
            case "11101000":
                System.out.println("Call addr");
                break;
            case "00111011":
                System.out.println("cmpr reg, reg");
                break;
            case "10000001":
                System.out.println("cmpr reg, constant");
                break;
            case "11001101":
                System.out.println("int xx");
                break;
            case "01110100":
                System.out.println("je loc");
                break;
            case "0110101":
                System.out.println("jne loc");
                break;
            case "10111111":
                System.out.println("jg loc");
                break;
            case "01111101":
                System.out.println("jge loc");
                break;
            case "01111100":
                System.out.println("jl loc");
                break;
            case "01111110":
                System.out.println("jle loc");
                break;
            case "11101001":
                System.out.println("jmp loc rel 8");
                System.out.println("jmp loc rel 32");
                break;
            case "10001011":
                System.out.println("mov reg, reg");
                break;
            case "10100001":
                System.out.println("mov eax, addr");
                break;
            case "10001001":
                System.out.println("mov reg/mem32, reg");
                break;
            case "10100011":
                System.out.println("mov addr, eax");
                break;
            case "00001011":
                System.out.println("or reg, reg");
                break;
            case "01101010":
                System.out.println("push const8");
                break;
            case "01101000": //NEEDS REAL TEST push const 32
                String const32Hex = listingLine.substring(13,21);   //get 32 bit const value in hex as a string
                memory.listingLines.put(regValues[ESP], const32Hex); //put the string on the stack
                regValues[ESP] += 32;                               //increment the stack pointer
                break;
            case "11000011":
                System.out.println("return");
                break;
            case "11000001":
                System.out.println("Sal reg, const");
                System.out.println("Sar reg, const");
                break;
            case "00101011":
                System.out.println("sub reg, reg");
                break;
            case "00110011":
                System.out.println("xor, reg, reg");
                break;
        }
        cpu.regValues[cpu.EIP] = memory.listingLines.higherKey(cpu.regValues[cpu.EIP]);

    }
}
