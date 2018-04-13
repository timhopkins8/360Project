// handle expressions
class Expression {
    protected static SimMemory memory = null;
    protected static CPU386 cpu = null;

    protected String curExpr = null;			// current expression
    protected int curPos = 0;					// current location within the expression

    protected int result;						// result of the expression
    public int getResult() { return result; }

    protected int operandValue;					// value of the most recent operand that was found

    protected char lastOperator = '+';			// last operator we found
    // constructor
    Expression( SimMemory m, CPU386 c ) {
        memory = m;
        cpu = c;
    }

    // get one operand from the expression and return its value
    protected boolean getOperand() {
        // skip leading spaces
        while ( curPos < curExpr.length() && Character.isWhitespace( curExpr.charAt(curPos) ) )
            curPos++;

        // nothing left?
        if ( curPos >= curExpr.length() )
            return false;

        // get the operand
        int startOperand = curPos;
        // skip leading spaces
        while ( curPos < curExpr.length() && Character.isJavaIdentifierPart( curExpr.charAt(curPos) ) )
            curPos++;

        String operand = curExpr.substring( startOperand, curPos );

        // do we have a register?
        for( int i=0; i< cpu.regNames.length; i++ ) {
            // is this the register we want?
            if ( operand.equals( cpu.regNames[i] ) ) {
                // if so, have the value
                operandValue = cpu.getRegister(i);
                return true;
            }
        }

        // now check symbols
        Integer symValue = memory.symbolTable.get( operand );
        if ( symValue != null ) {
            // have a value
            operandValue = (int) symValue;
            return true;
        }

        // is it a valid hex value?
        try {
            operandValue = Integer.parseInt( operand, 16 );
            // if that succeeds, we have a valid int
            return true;
        } catch ( Exception e ) {
            // error - not a valid hex integer
            System.out.println("'" + operand + "' is not a valid register, symbol, or hex integer");
            return false;
        }
    }

    // evaluate an expression of the form x+y-zero
    // only + and - are supported
    // operands may be symbols, registers, or hex values
    // return true if a valid expression is found
    //		if so, save result for later
    public boolean eval( String expr ) {
        final int END_POS = 1000;

        // handle symbols, etc., in uppercase
        curExpr = expr.toUpperCase();
        curPos = 0;

        // Pretend the real expression is "0+..."
        result = 0;
        lastOperator = '?';

        // get next operator ( '+' or '-' )
        while ( getOperand() ) {
            // compute this part of the expression
            if ( lastOperator == '-' )
                result = result - operandValue;
            else
                // first time or '+'
                result = result + operandValue;

            // find the next '+' or '-' if we have one
            // use '1000' if it wasn't found to avoid problems with -1 and min()
            int plusPos = curExpr.indexOf( '+', curPos );
            if ( plusPos < 0 )
                plusPos = END_POS;
            int minusPos = curExpr.indexOf( '-', curPos );
            if ( minusPos < 0 )
                minusPos = END_POS;

            // first operand position
            int opPos = Math.min( plusPos, minusPos );

            // if no '+' or '-', assume we have the end of the expression
            if ( opPos == END_POS )
                return true;

            // save this operator
            lastOperator = curExpr.charAt( opPos );

            // look for another operator
            curPos = opPos+1;
        }

        // missing an operand
        System.out.println("Missing value in the expression");
        return false;
    }
}