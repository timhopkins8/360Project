public class OpCodeInterpreter {
    private String listingLine;
    private String firstByteOpCode;
    private CPU386 cpu;

    public OpCodeInterpreter(String listingLine, CPU386 cpu){
        this.listingLine = listingLine;
        setFirstByteOpCode();
        this.cpu = cpu;
    }

    public void setFirstByteOpCode(){
        String opCode = listingLine.substring(10,12);
        int num = Integer.parseInt(opCode,16);
        firstByteOpCode = Integer.toBinaryString(num);
    }

    public void determineAction(){
        String firstFiveBits = firstByteOpCode.substring(0,5);

        switch (firstFiveBits){
            case "10111":
                System.out.println("mov reg/const");
                return;
            case "01011":
                System.out.println("pop register");
                return;
            case "01010":
                System.out.println("Push reg");
                return;
            default :
                System.out.println("Go to next Switch");
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
            case "01101000":
                System.out.println("push const32");
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
    }

    public void add(int val1, int val2, int destination){
        cpu.regValues[destination] = val1 + val2;
    }

    public void subtract(int val1, int val2, int destination){
        cpu.regValues[destination] = val1 - val2;
    }

    public void and(int val1, int val2, int destination){
        cpu.regValues[destination] = val1 & val2;
    }

    public void or(int val1, int val2, int destination){
        cpu.regValues[destination] = val1 | val2;
    }

    public void xor(int val1, int val2, int destination){
        cpu.regValues[destination] = val1 ^ val2;
    }

    public void move(int val, int destination){
        cpu.regValues[destination] = val;
    }


}
