import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.TreeMap;

class SimMemory {
    // simulated memory size
    public static final int REAL_MEM_SIZE = 0x00100000;			// 1 MB memory actually present

    // symbols defined in the program
    TreeMap<String,Integer> symbolTable;
    // listing for each instruction address
    TreeMap<Integer,String> listingLines;

    // set up a base-bound limits on memory
    int virtualBaseAddress = 0;			// virtual starting address
    int virtualAddressLimit = 0;		// highest valid address

    // first byte after the code
    int codeSectionEnd = 0;

    // simulated memory contents
    protected ByteBuffer mem;

    // return the highest address available to the program
    public int getMaxAddress() {
        return virtualAddressLimit;
    }

    // constructor
    SimMemory () {
        // allocate memory
        mem = ByteBuffer.allocate( REAL_MEM_SIZE );
        // note x86 is little-endian
        mem.order( ByteOrder.LITTLE_ENDIAN );
    }

    // set the virtual base address
    public void setVirtualBaseAddress( int newBase ) {
        // set starting address
        virtualBaseAddress = newBase;
        // and ending address
        virtualAddressLimit = newBase + REAL_MEM_SIZE;
    }

    // clear memory
    public void clear() {
        mem.clear();
    }

    // display one word of memory
    public void display( int addr ) {
        try {
            int val = readDWord( addr );
            System.out.printf("Mem[%08x]: %08x (%d)\n", addr, val, val );
        } catch ( Exception e ) {
            System.out.println("Error: " + e.getMessage() );
        }
    }

    // read a 32-bit integer from memory
    public int readDWord( int addr ) throws Exception {
        // beyond end of memory?
        if ( addr < virtualBaseAddress || addr >= virtualAddressLimit-3 ) {
            throw new Exception("Error: Reading beyond the end of memory, address: "
                    + String.format( "%08x", addr ) );
        }

        // do read
        return mem.getInt( addr-virtualBaseAddress );
    }

    // write a 32-bit integer to memory
    public void writeDWord( int addr, int val ) throws Exception {
        // beyond end of memory?
        if ( addr < virtualBaseAddress || addr >= virtualAddressLimit-3 ) {
            throw new Exception("Error: Writing beyond the end of memory, address: "
                    + String.format( "%08x", addr ) );
        }

        // do write
        mem.putInt( addr-virtualBaseAddress, val );
    }

    // read a 16-bit integer from memory
    public int readWord( int addr ) throws Exception {
        // beyond end of memory?
        if ( addr < virtualBaseAddress || addr >= virtualAddressLimit-1 ) {
            throw new Exception("Error: Reading beyond the end of memory, address: "
                    + String.format( "%08x", addr ) );
        }

        // do read
        return mem.getShort( addr-virtualBaseAddress );
    }

    // read unsigned word
    public int readUnsignedWord( int addr ) throws Exception {
        return readWord(addr) & 0x0ffff;
    }

    // write a 16-bit integer to memory
    public void writeWord( int addr, int val ) throws Exception {
        // beyond end of memory?
        if ( addr < virtualBaseAddress || addr >= virtualAddressLimit-1 ) {
            throw new Exception("Error: Writing beyond the end of memory, address: "
                    + String.format( "%08x", addr ) );
        }

        // do write
        mem.putShort( addr-virtualBaseAddress, (short)val );
    }

    // read a byte from memory
    public int readByte( int addr ) throws Exception {
        // beyond end of memory?
        if ( addr < virtualBaseAddress || addr >= virtualAddressLimit ) {
            throw new Exception("Error: Reading beyond the end of memory, address: "
                    + String.format( "%08x", addr ) );
        }

        // do read
        return mem.get( addr-virtualBaseAddress );
    }

    // read unsigned byte
    public int readUnsignedByte( int addr ) throws Exception {
        return readByte(addr) & 0x0ff;
    }

    // write a byte to memory
    public void writeByte( int addr, int val ) throws Exception {
        // beyond end of memory?
        if ( addr < virtualBaseAddress || addr >= virtualAddressLimit ) {
            throw new Exception("Error: Writing beyond the end of memory, address: "
                    + String.format( "%08x", addr ) );
        }

        // do write
        mem.put( addr-virtualBaseAddress, (byte)val );
    }

    // write a block of memory
    public void writeByteArray( int addr, byte[] data ) throws Exception {
        // beyond end of memory?
        if ( addr < virtualBaseAddress || addr+data.length >= virtualAddressLimit ) {
            throw new Exception("Error: Writing block beyond the end of memory, address: "
                    + String.format( "%08x", addr ) );
        }

        // do write
        mem.position(addr - virtualBaseAddress);
        mem.put( data );
    }

    // read a null-terminated string from memory
    public String readString( int addr ) throws Exception {
        String res = "";

        while( true ) {
            // beyond end of memory?
            if ( addr < virtualBaseAddress || addr >= virtualAddressLimit ) {
                System.out.printf("Address error %08x\n", addr );
                throw new Exception("Error: Reading block beyond the end of memory, address: "
                        + String.format( "%08x", addr ) );
            }

            char c = (char) mem.get(addr - virtualBaseAddress);

            // null terminator?
            if ( c == 0 ) {
                return res;
            }

            // add to string and move to next byte
            res += c;
            addr++;
        }
    }

    // take the given string and expand tabs (assume tab = 8 characters)
    protected String expandTabs( String inLine ) {
        String resString = "";

        for( int i=0; i<inLine.length(); i++ ) {
            // get the current character
            char curChar = inLine.charAt(i);

            // is it a tab?
            if ( curChar == '\t' ) {
                do {
                    // expand with spaces until the string length is a multiple of 8
                    resString += " ";
                } while( (resString.length() % 8) != 0 );
            } else {
                // not a tab, copy to the string
                resString += curChar;
            }
        }

        return resString;
    }

    final String hexDigits = "0123456789ABCDEF";

    // does this look like a code listing line
    // " 00000000 00 "
    protected boolean isCodeListingLine( String line ) {
        // too short to have address and opcode?
        if ( line.length() < 14 )
            return false;

        String uline = line.toUpperCase();

        if ( ! Character.isWhitespace( line.charAt(0) ) ) {
            return false;
        }

        // does it have a hex address?
        for( int i=1; i<9; i++ ) {
            if ( hexDigits.indexOf( line.charAt(i) ) < 0 ) {
                return false;
            }
        }
        // then two spaces
        if ( ! Character.isWhitespace( line.charAt(9) ) ) {
            return false;
        }
        if ( ! Character.isWhitespace( line.charAt(10) ) ) {
            return false;
        }

        // opcode (two hex digits)
        if ( hexDigits.indexOf( line.charAt(11) ) < 0 ) {
            return false;
        }
        if ( hexDigits.indexOf( line.charAt(12) ) < 0 ) {
            return false;
        }

        // and another space/tab
        if ( ! Character.isWhitespace( line.charAt(13) ) ) {
            return false;
        }

        // if we get here, everything fits
        return true;
    }

    // process a code symbol
    // if that instruction doesn't have a label, add it to the line
    // add it to the symbol table
    protected void processCodeSymbol( String symbolName, int address ) {
        // add to the symbol table
        symbolTable.put( symbolName.toUpperCase(), address );

        // do we have an entry in the listing?
        String listLine = listingLines.get( address );

        if ( listLine == null ) {
            System.out.printf("No listing line found for address %08x\n", address );
            return;					// no, ignore this symbol (should be rare)
        }

        // does this line already have a symbol?
        if ( listLine.charAt(32) != ' ' ) {
            //System.out.println("Found character " + listLine.charAt(32) + " at position 32 of line " + listLine);
            return;					// already have a symbol, so probably a duplicate
        }

        // how many spaces do we have before the opcode?
        int pos=32;
        while( listLine.charAt(pos) == ' ' )
            pos++;

        // insert label and pad (if necessary) with spaces
        String newListLine = listLine.substring(0,32)+symbolName + ':';

        // pad with a minimum of one space up to specified length
        do {
            newListLine += " ";
        } while ( newListLine.length() < pos );

        // copy the opcode
        while( listLine.charAt(pos) != ' ' ) {
            newListLine += listLine.charAt(pos);
            pos++;
        }
        // copy one space
        newListLine += listLine.charAt(pos);
        pos++;

        // remove spaces between opcode and operand to get operands to line up
        int removeCount = symbolName.length() - 5;
        while( removeCount>0 && listLine.charAt(pos) == ' ' ) {
            // move past this space
            pos++;
            // track # of spaces left to remove
            removeCount--;
        }

        // update the listing
        listingLines.replace( address, newListLine+listLine.substring(pos) );
    }

    // read an executable into memory
    // return the initial value for the program counter
    // information from https://msdn.microsoft.com/en-us/library/windows/desktop/ms680547(v=vs.85).aspx
    public int loadExe( String filename ) throws Exception {
        int preferredImageAddress = 0;
        int entryPointAddress = 0;

        // start of the code section relative the the .exe in memory
        int codeSectionOffset=0;

        // start of the data section relative the the .exe in memory
        int dataSectionOffset=0;

        // allocate buffer for reading the file
        ByteBuffer progData = null;

        // read the executable
        FileInputStream inprog = new FileInputStream( filename );

        // create a buffer to hold the data
        progData = ByteBuffer.allocate( inprog.available() );
        // note x86 is little-endian
        progData.order( ByteOrder.LITTLE_ENDIAN );

        // read it in one big gulp
        inprog.read( progData.array() );

        // close the file
        inprog.close();

        // look for location of PE header
        int peHeaderStart = progData.getInt( 0x3c );
        //System.out.printf("pe Header Start: %08x\n", peHeaderStart);

        // check for PE (0) (0) signature
        if ( progData.get(peHeaderStart) != 'P' || progData.get(peHeaderStart+1) != 'E'
                || progData.get(peHeaderStart+2) != 0 || progData.get(peHeaderStart+3) != 0 ) {
            System.out.printf("Failed to locate PE00 header at location %08x, found %02x %02x %02x %02x ",
                    peHeaderStart, progData.get(peHeaderStart), progData.get(peHeaderStart+1),
                    progData.get(peHeaderStart+2), progData.get(peHeaderStart+3) );
            System.exit(1);
        }

        // check machine type
        int machineType = progData.getShort( peHeaderStart+4 );
        if ( machineType != 0x14c ) {
            System.out.println("Incorrect/unknown machine type" );
            System.exit(1);
        }

        int sectionCount = progData.getShort( peHeaderStart + 6 );
        //System.out.println("Section Count: " + sectionCount );

        // check "optional" header size (required for executables)
        int optionalHeaderSize = progData.getShort( peHeaderStart + 20 );
        if ( optionalHeaderSize <=0 || optionalHeaderSize >= progData.limit() ) {
            System.out.println("Incorrect optional header size " + optionalHeaderSize );
            System.exit(1);
        }

        //System.out.println("Code section size: " + progData.getInt(peHeaderStart+24+4) );
        //System.out.println("Initialized data section size: " + progData.getInt(peHeaderStart+24+8) );
        //System.out.println("Uninitialized data section size: " + progData.getInt(peHeaderStart+24+12) );
        entryPointAddress = progData.getInt(peHeaderStart+24+16);
        //System.out.printf("Entry Point Address: %08x\n", entryPointAddress );
        //System.out.printf("Code Base Address: %08x\n", progData.getInt(peHeaderStart+24+20) );
        //System.out.printf("Data Base Address: %08x\n", progData.getInt(peHeaderStart+24+24) );
        preferredImageAddress = progData.getInt(peHeaderStart+24+28);
        //System.out.printf("Preferred Image Address: %08x\n", preferredImageAddress );
        //System.out.printf("Stack size reserved: %08x\n", progData.getInt(peHeaderStart+24+72) );
        //System.out.printf("Stack size committed: %08x\n", progData.getInt(peHeaderStart+24+76) );

        // set our virtual memory location
        setVirtualBaseAddress( preferredImageAddress );

        // read sections
        int optionPosition = peHeaderStart+24+224;

        for( int sectNo=0; sectNo<sectionCount; sectNo++) {
/***
 // get information about this section
 String label = "";
 for( int i=0; i<8; i++ ) {
 char c = (char) progData.get(optionPosition+i);
 // null character?
 if ( c==0 )
 break;

 // no, add this character to the label
 label += c;
 }
 System.out.println("Section label: " + label );
 ***/
            int virtualSize = progData.getInt(optionPosition+8);
            //System.out.printf("Virtual Size: %08x\n", virtualSize );
            int virtualAddress = progData.getInt(optionPosition+12);
            //System.out.printf("Virtual Address: %08x\n", virtualAddress );
            int dataSize = progData.getInt(optionPosition+16);
            //System.out.printf("RawData Size: %08x\n", dataSize );
            int dataPointer = progData.getInt(optionPosition+20);
            //System.out.printf("RawData Pointer: %08x\n", dataPointer );
            int characteristics = progData.getInt(optionPosition+36);
            //System.out.printf("Characteristics: %08x\n", characteristics );

            // can it be safely discarded?
            if ( (characteristics & 0x02000000) != 0 ) {
                //System.out.println("Discarding this segment");
                continue;
            }

            // do we need to note the location of this section?
            if ( (characteristics & 0x00000020) != 0 ) {
                codeSectionOffset = virtualAddress;
                // also note the end of the code section
                codeSectionEnd = virtualSize + preferredImageAddress + virtualAddress;
                //System.out.printf("code offset=%08x, end=%08x\n", codeSectionOffset, codeSectionEnd);
            } if ( (characteristics & 0x00000040) != 0 ) {
                dataSectionOffset = virtualAddress;
            }

            // get section data from the .exe file
            byte[] sectionData = new byte[dataSize];
            progData.position( dataPointer );
            progData.get( sectionData );

            // write to virtual memory
            writeByteArray( preferredImageAddress+virtualAddress, sectionData );

            // update location
            optionPosition += 40;
        }

        // create TreeMaps for info we will want later
        symbolTable = new TreeMap<>();
        listingLines = new TreeMap<>();

        // read the listing - will ignore errors here
        try {
            String listFileName = filename.substring( 0, filename.length()-3 ) + "lst";
            // open the listing file
            FileInputStream inlist = new FileInputStream( listFileName );

            // Create a scanner
            BufferedReader inListing = new BufferedReader( new InputStreamReader(inlist) );

            while ( true ) {
                // read one line of the .LST file
                String listLine = inListing.readLine();

                // nothing to read?
                if ( listLine == null )
                    break;				// no, done here

                // expand tabs so everything is consistent
                listLine = expandTabs( listLine );

                // is it something we care about?
                if ( isCodeListingLine( listLine ) ) {
                    // convert the instruction address
                    int lineAddress = Integer.parseInt( listLine.substring(1,9), 16 )
                            + preferredImageAddress + codeSectionOffset;

                    // handle any variable addresses
                    int relocPos = listLine.indexOf(" R");

                    if ( relocPos==22 || relocPos==25 ) {
                        // have a relocation entry at the expected address
                        // assume it is a data variable
                        int dataAddress = Integer.parseInt( listLine.substring(relocPos-8,relocPos), 16 )
                                + preferredImageAddress + dataSectionOffset;

                        // reassemble with the actual addresses
                        listLine = String.format("%08X",lineAddress)
                                + listLine.substring(9,relocPos-8) + String.format("%08X  ",dataAddress)
                                + listLine.substring(relocPos+2);
                    } else {
                        // update instruction address only
                        listLine = String.format("%08X",lineAddress) + listLine.substring(9);
                    }

                    // remove any 'C' for include files or '1' for macro expansions
                    //listLine = listLine.substring(0,23) + listLine.substring(26);
                    if ( listLine.indexOf(" C ") == 28 ) {
                        // remove from display
                        listLine = listLine.substring( 0, 27 ) + "   " + listLine.substring( 30 );
                    } else if ( listLine.indexOf(" 1 ") == 27 ) {
                        // remove from display
                        listLine = listLine.substring( 0, 26 ) + "   " + listLine.substring( 29 );
                    }

                    // truncate to a max length of 79
                    int trimLength = Math.min( 79, listLine.length() );

                    // also drop comments since they will probably be truncated anyway
                    int commentStart = listLine.indexOf(";");
                    if ( commentStart >= 0 ) {
                        trimLength = Math.min( trimLength, commentStart );
                    }

                    // and remove trailing spaces
                    while( trimLength > 0 && listLine.charAt(trimLength-1) == ' ' )
                        trimLength--;

                    // add to our listing
                    listingLines.put( lineAddress, listLine.substring(0,trimLength) );
                    //System.out.println("code:" + listLine);
                } else if ( listLine.indexOf("P Near") == 40 ) {
                    // is a subroutine name
                    int symEnd = listLine.indexOf(" ");
                    String symbolName = listLine.substring( 0, symEnd );
                    //String strAddr = listLine.substring(41,49);
                    int symbolAddr = Integer.parseInt( listLine.substring(49,57), 16 );
                    //System.out.printf("Subroutine name %s at offset %08x\n", symbolName, symbolAddr );

                    // add to the symbol table, update listing if necessary
                    processCodeSymbol( symbolName, preferredImageAddress+codeSectionOffset+symbolAddr );
                } else if ( listLine.indexOf("L Near") == 40 ) {
                    // is an instruction label
                    int symEnd = listLine.indexOf(" ", 2);
                    String symbolName = listLine.substring( 2, symEnd );

                    int symbolAddr = Integer.parseInt( listLine.substring(49,57), 16 );
                    //System.out.printf("Instruction label %s at offset %08x\n", symbolName, symbolAddr );

                    // add to the symbol table, update listing if necessary
                    processCodeSymbol( symbolName, preferredImageAddress+codeSectionOffset+symbolAddr );
                } else if ( listLine.indexOf("_DATA") == 58 ) {
                    // is an instruction label
                    int symEnd = listLine.indexOf(" ");
                    String symbolName = listLine.substring( 0, symEnd );
                    int symbolAddr = Integer.parseInt( listLine.substring(49,57), 16 );
                    //System.out.printf("Data label %s at offset %08x\n", symbolName, symbolAddr );

                    // add to the symbol table
                    symbolTable.put( symbolName, preferredImageAddress+dataSectionOffset+symbolAddr );
                }
            }

            // close the file
            inlist.close();
        } catch ( Exception e ) {
            // problem - just set symbol table to empty
            symbolTable.clear();
        }

        // return starting address
        return preferredImageAddress + entryPointAddress;
    } // readExe


    // Display a set of instructions and corresponding source code
    // addr = address of the line to display
    // return the address of the next instruction (or -1 if no such entry can be found)
    public int disassemble( int addr ) throws Exception {
        // do we have an entry in the listing?
        String listLine = listingLines.get( addr );

        // did we find one?
        if ( listLine != null ) {
            // display this line
            System.out.println( listLine );

            // get the address of the next line
            Integer nextAddr = listingLines.higherKey( addr );
            // no entrY/
            if ( nextAddr == null )
                return codeSectionEnd;

            // return next address
            return (int) nextAddr;
        } else {
            // no entry, display one byte
            System.out.printf("%08X  %02X                           ???\n", addr, readUnsignedByte(addr) );
            // only display one byte
            return addr+1;
        }
    }
} // class SimMemory