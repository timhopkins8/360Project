class ALU {
    // flags
    boolean carry = false;
    boolean overflow = false;
    boolean sign = false;
    boolean zero = false;

    // clear all flags
    public void clearFlags() {
        carry = false;
        overflow = false;
        sign = false;
        zero = false;
    }

    // add values, setting flags as appropriate
    public int add( long op1, long op2 ) {
        // do this as 64 bit values so we can easily handle carry and overflow
        long extRes = op1 + op2;

        // extract the flags
        carry = (extRes & 0x100000000L) != 0;			// bit 32 is set (one bit beyond register)
        sign = (extRes & 0x800000000L) != 0;			// bit 31 is set (sign bit)
        overflow = (extRes >= 0x080000000L) || (extRes < -0x080000000L);
        zero = extRes == 0;

        // return 32-bit result
        return (int) extRes;
    }

    // subtract values, setting flags as appropriate
    public int sub( long op1, long op2 ) {
        // do this as 64 bit values so we can easily handle carry and overflow
        long extRes = op1 - op2;

        // extract the flags
        carry = (extRes & 0x100000000L) != 0;			// set if there is a borrow
        sign = (extRes & 0x800000000L) != 0;			// bit 31 is set (sign bit)
        overflow = (extRes >= 0x080000000L) || (extRes < -0x080000000L);
        zero = extRes == 0;

        // return 32-bit result
        return (int) extRes;
    }

    // compare values, setting flags as appropriate
    public void compare( long op1, long op2 ) {
        // do this as 64 bit values so we can easily handle carry and overflow
        long extRes = op1 - op2;

        // extract the flags
        carry = (extRes & 0x100000000L) != 0;			// set if there is a borrow
        sign = (extRes & 0x800000000L) != 0;			// bit 31 is set (sign bit)
        overflow = (extRes >= 0x080000000L) || (extRes < -0x080000000L);
        zero = extRes == 0;
        //System.out.println("ALU Compare: " + op1 + " - " + op2);
    }

    // logical and, setting flags as appropriate
    public int and( int op1, int op2 ) {
        // extend to 64 bit so we can determine carry and overflow
        int res = op1 & op2;

        // extract the flags
        carry = false;
        sign = (res & 0x800000000L) != 0;			// bit 31 is set (sign bit)
        overflow = false;
        zero = res == 0;

        // return 32-bit result
        return res;
    }

    // logical or, setting flags as appropriate
    public int or( int op1, int op2 ) {
        // extend to 64 bit so we can determine carry and overflow
        int res = op1 | op2;

        // extract the flags
        carry = false;
        sign = (res & 0x800000000L) != 0;			// bit 31 is set (sign bit)
        overflow = false;
        zero = res == 0;

        // return 32-bit result
        return res;
    }

    // logical exclusive-or, setting flags as appropriate
    public int xor( int op1, int op2 ) {
        // extend to 64 bit so we can determine carry and overflow
        int res = op1 ^ op2;

        // extract the flags
        carry = false;
        sign = (res & 0x800000000L) != 0;			// bit 31 is set (sign bit)
        overflow = false;
        zero = res == 0;

        // return 32-bit result
        return res;
    }

    // shift operations
    public int shiftArithmeticLeft( int val, int shiftAmt ) {
        // limit shift amount to 0..31
        shiftAmt &= 0x1f;

        long extVal = val;
        long extRes =  extVal << shiftAmt;

        // extract the flags
        carry = (extRes & 0x100000000L) != 0;			// look at last bit shifted out of the register
        sign = (extRes & 0x800000000L) != 0;			// bit 31 is set (sign bit)
        overflow = (extRes >= 0x080000000L) || (extRes < -0x080000000L);
        zero = extRes == 0;

        return (int) extRes;
    }

    public int shiftArithmeticRight( int val, int shiftAmt ) {
        // limit shift amount to 0..31
        shiftAmt &= 0x1f;

        long extVal = val;
        long extRes =  extVal >> shiftAmt;

        // extract the flags
        if ( shiftAmt > 0 )
            carry = ((extVal >> (shiftAmt-1)) % 1) != 0;	// look at last bit to be lost
        else
            carry = false;
        sign = (extRes & 0x800000000L) != 0;			// bit 31 is set (sign bit)
        overflow = false;
        zero = extRes == 0;

        return (int) extRes;
    }

    public int shiftLogicalRight( int val, int shiftAmt ) {
        // limit shift amount to 0..31
        shiftAmt &= 0x1f;

        long extVal =  0x0FFFFFFFFL & (long)val;		// zero-extend the value
        long extRes =  extVal >> shiftAmt;

        // extract the flags
        if ( shiftAmt > 0 )
            carry = ((extVal >> (shiftAmt-1)) % 1) != 0;	// look at last bit to be lost
        else
            carry = false;
        sign = (extRes & 0x800000000L) != 0;			// bit 31 is set (sign bit)
        overflow = false;
        zero = extRes == 0;

        return (int) extRes;
    }
} // class ALU