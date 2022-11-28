import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents the parser for the grammar of the FORTRESS language.
 */
public class Parser {
    private ArrayList<Symbol> tokens;
    private ArrayList<Integer> leftMostDerivationArray;
    private ParseTree parseTree;

    /**
     * This is the constructor of the parser class.
     * 
     * @param tokens The list of tokens to parse.
     */
    public Parser(ArrayList<Symbol> tokens) {
        this.tokens = tokens;
        this.leftMostDerivationArray = new ArrayList<>();
        this.parseTree = null;
    }

    /**
     * This is the getter of the parse tree generated.
     * 
     * @return the parse tree.
     */
    public ParseTree getParseTree() {
        return parseTree;
    }

    /**
     * This method returns the next token to handle. If the list of tokens is empty,
     * it returns a null.
     * 
     * @return the next token.
     */
    Symbol next_token() {
        Symbol currToken;

        if (tokens.size() != 0) {
            currToken = tokens.get(0);
        } else {
            currToken = null;
        }

        return currToken;
    }

    /**
     * This method checks if the next token to handle correspond to the expected
     * lexical unit given in parameter.
     * 
     * @param lu the expected lexical unit
     * @return a node for the parse tree if there is a match.
     * @throws Exception throw an exception if there is a syntax error.
     */
    ParseTree match(LexicalUnit lu) throws Exception {
        Symbol s = null;
        if (lu == tokens.get(0).getType()) {
            s = new Symbol(lu, tokens.get(0).getValue().toString());
            tokens.remove(0);
        } else {
            syntax_error(Arrays.asList(lu));
        }
        return new ParseTree(s);
    }

    /**
     * This method is called when a syntax error is encountered. It throws an
     * exception and displays the syntax elements that were expected.
     * 
     * @param expected The list of lexical units that could be expected.
     * @throws Exception Throws an exception which shows the unexpected symbol and
     *                   the list of lexical units that were expected.
     */
    void syntax_error(List<LexicalUnit> expected) throws Exception {
        int line = tokens.get(0).getLine();
        int column = tokens.get(0).getColumn();
        String expectedString = "";

        if (expected.size() == 1) {
            expectedString += expected.get(0).toString();
        } else {
            for (int i = 0; i < expected.size() - 2; i++) {
                expectedString += expected.get(i).toString() + ", ";
            }
            expectedString += expected.get(expected.size() - 1).toString();
        }

        throw new Exception("Syntax Error ! Unexpected symbol at line: " + line + ", col: " + column + ". "
                + "Found '" + tokens.get(0).getValue() + "' as " + tokens.get(0).getType() + ". "
                + "(Expecting: " + expectedString + ")");
    }

    /**
     * This method converts the list of integer (which represents the number of the
     * applied rules) of the attribute "leftMostDerivationArray" into a String with
     * comma between each rule to be easily print.
     * 
     * @return The String representing the list of rules of the left-most
     *         derivation.
     */
    String getLeftMostDerivation() {
        String derivation = "";

        for (int i = 0; i < this.leftMostDerivationArray.size(); i++) {
            derivation += this.leftMostDerivationArray.get(i) + " ";
        }

        return derivation;
    }

    /**
     * This method starts the parsing and print the left-most derivation if the
     * parsing did not meet any syntax error.
     * 
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    void parse() throws Exception {
        this.parseTree = program();
        match(LexicalUnit.EOS);
        System.out.println(getLeftMostDerivation());
    }

    /**
     * This method handles the parsing of the rules comming from <Program> as
     * left-hand side. This method throws an exception if it meet an unexpected
     * lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree program() throws Exception {
        leftMostDerivationArray.add(1);
        ParseTree pt1 = match(LexicalUnit.BEGIN);
        ParseTree pt2 = match(LexicalUnit.PROGNAME);
        ParseTree pt3 = code();
        ParseTree pt4 = match(LexicalUnit.END);
        return new ParseTree(new Symbol(LexicalUnit.PROGRAM_, "<Program>"),
                Arrays.asList(pt1, pt2, pt3, pt4));

    }

    /**
     * This method handles the parsing of the rules comming from <Code> as left-hand
     * side. This method throws an exception if it meet an unexpected lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree code() throws Exception {
        Symbol tok = next_token();
        switch (tok.getType()) {
            case ELSE:
            case END:
                leftMostDerivationArray.add(3);
                return new ParseTree(new Symbol(LexicalUnit.CODE_, "<Code>"),
                        Arrays.asList(new ParseTree(new Symbol(LexicalUnit.EPSILON, "E"))));
            default:
        }
        leftMostDerivationArray.add(2);
        ParseTree pt1 = instruction();
        ParseTree pt2 = match(LexicalUnit.COMMA);
        ParseTree pt3 = code();
        return new ParseTree(new Symbol(LexicalUnit.CODE_, "<Code>"),
                Arrays.asList(pt1, pt2, pt3));
    }

    /**
     * This method handles the parsing of the rules comming from <Instruction> as
     * left-hand side. This method throws an exception if it meet an unexpected
     * lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree instruction() throws Exception {
        Symbol tok = next_token();
        ParseTree pt = null;
        switch (tok.getType()) {
            case VARNAME:
                leftMostDerivationArray.add(4);
                pt = assign();
                break;
            case IF:
                leftMostDerivationArray.add(5);
                pt = if_();
                break;
            case WHILE:
                leftMostDerivationArray.add(6);
                pt = while_();
                break;
            case PRINT:
                leftMostDerivationArray.add(7);
                pt = print();
                break;
            case READ:
                leftMostDerivationArray.add(8);
                pt = read();
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.VARNAME, LexicalUnit.IF,
                        LexicalUnit.WHILE, LexicalUnit.PRINT, LexicalUnit.READ));
        }
        return new ParseTree(new Symbol(LexicalUnit.INSTRUCTION_, "<Instruction>"), Arrays.asList(pt));
    }

    /**
     * This method handles the parsing of the rules comming from <Assign> as
     * left-hand side. This method throws an exception if it meet an unexpected
     * lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree assign() throws Exception {
        leftMostDerivationArray.add(9);
        ParseTree pt1 = match(LexicalUnit.VARNAME);
        ParseTree pt2 = match(LexicalUnit.ASSIGN);
        ParseTree pt3 = exprArith();
        return new ParseTree(new Symbol(LexicalUnit.ASSIGN_, "<Assign>"), Arrays.asList(pt1, pt2, pt3));
    }

    /**
     * This method handles the parsing of the rules comming from <ExprArith> as
     * left-hand side. This method throws an exception if it meet an unexpected
     * lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree exprArith() throws Exception {
        leftMostDerivationArray.add(10);
        ParseTree pt1 = mulDiv();
        ParseTree pt2 = exprArithQuote();
        return new ParseTree(new Symbol(LexicalUnit.EXPRARITH_, "<ExprArith>"), Arrays.asList(pt1, pt2));
    }

    /**
     * This method handles the parsing of the rules comming from <ExprArith'> as
     * left-hand side. This method throws an exception if it meet an unexpected
     * lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree exprArithQuote() throws Exception {
        Symbol tok = next_token();
        ParseTree pt1 = null;
        switch (tok.getType()) {
            case COMMA:
            case RPAREN:
            case EQUAL:
            case SMALLER:
            case GREATER:
                leftMostDerivationArray.add(13);
                return new ParseTree(new Symbol(LexicalUnit.EXPRARITHQUOTE_, "<ExprArith'>"),
                        Arrays.asList(new ParseTree(new Symbol(LexicalUnit.EPSILON, "E"))));
            case PLUS:
                leftMostDerivationArray.add(11);
                pt1 = match(LexicalUnit.PLUS);
                break;
            case MINUS:
                leftMostDerivationArray.add(12);
                pt1 = match(LexicalUnit.MINUS);
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.COMMA, LexicalUnit.RPAREN,
                        LexicalUnit.EQUAL, LexicalUnit.SMALLER, LexicalUnit.GREATER,
                        LexicalUnit.PLUS, LexicalUnit.MINUS));
        }
        ParseTree pt2 = mulDiv();
        ParseTree pt3 = exprArithQuote();
        return new ParseTree(new Symbol(LexicalUnit.EXPRARITHQUOTE_, "ExprArith'"), Arrays.asList(pt1, pt2, pt3));
    }

    /**
     * This method handles the parsing of the rules comming from <MulDiv> as
     * left-hand side. This method throws an exception if it meet an unexpected
     * lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree mulDiv() throws Exception {
        leftMostDerivationArray.add(14);
        ParseTree pt1 = atom();
        ParseTree pt2 = mulDivQuote();
        return new ParseTree(new Symbol(LexicalUnit.MULDIV_, "<MulDiv'>"), Arrays.asList(pt1, pt2));
    }

    /**
     * This method handles the parsing of the rules comming from <MulDiv'> as
     * left-hand side. This method throws an exception if it meet an unexpected
     * lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree mulDivQuote() throws Exception {
        Symbol tok = next_token();
        ParseTree pt1 = null;
        switch (tok.getType()) {
            case COMMA:
            case RPAREN:
            case EQUAL:
            case SMALLER:
            case GREATER:
            case PLUS:
            case MINUS:
                leftMostDerivationArray.add(17);
                return new ParseTree(new Symbol(LexicalUnit.MULDIVQUOTE_, "<MulDiv'>"),
                        Arrays.asList(new ParseTree(new Symbol(LexicalUnit.EPSILON, "E"))));
            case TIMES:
                leftMostDerivationArray.add(15);
                pt1 = match(LexicalUnit.TIMES);
                break;
            case DIVIDE:
                leftMostDerivationArray.add(16);
                pt1 = match(LexicalUnit.DIVIDE);
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.COMMA, LexicalUnit.RPAREN,
                        LexicalUnit.EQUAL, LexicalUnit.SMALLER, LexicalUnit.GREATER,
                        LexicalUnit.PLUS, LexicalUnit.MINUS, LexicalUnit.TIMES,
                        LexicalUnit.DIVIDE));
        }
        ParseTree pt2 = atom();
        ParseTree pt3 = mulDivQuote();
        return new ParseTree(new Symbol(LexicalUnit.MULDIVQUOTE_, "<MulDiv'>"), Arrays.asList(pt1, pt2, pt3));
    }

    /**
     * This method handles the parsing of the rules comming from <Atom> as left-hand
     * side. This method throws an exception if it meet an unexpected lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree atom() throws Exception {
        Symbol tok = next_token();
        ParseTree pt1 = null;
        ParseTree pt2 = null;
        ParseTree pt3 = null;
        switch (tok.getType()) {
            case MINUS:
                leftMostDerivationArray.add(18);
                pt1 = match(LexicalUnit.MINUS);
                pt2 = atom();
                break;
            case VARNAME:
                leftMostDerivationArray.add(19);
                pt1 = match(LexicalUnit.VARNAME);
                break;
            case NUMBER:
                leftMostDerivationArray.add(20);
                pt1 = match(LexicalUnit.NUMBER);
                break;
            case LPAREN:
                leftMostDerivationArray.add(21);
                pt1 = match(LexicalUnit.LPAREN);
                pt2 = exprArith();
                pt3 = match(LexicalUnit.RPAREN);
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.VARNAME, LexicalUnit.NUMBER,
                        LexicalUnit.LPAREN));
        }
        ArrayList<ParseTree> array = new ArrayList<>();
        array.add(pt1);
        if (pt2 != null) {
            array.add(pt2);
            if (pt3 != null) {
                array.add(pt3);
            }
        }
        return new ParseTree(new Symbol(LexicalUnit.ATOM_, "<Atom>"), array);
    }

    /**
     * This method handles the parsing of the rules comming from <If> as left-hand
     * side. This method throws an exception if it meet an unexpected lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree if_() throws Exception {
        leftMostDerivationArray.add(22);
        ParseTree pt1 = match(LexicalUnit.IF);
        ParseTree pt2 = match(LexicalUnit.LPAREN);
        ParseTree pt3 = cond();
        ParseTree pt4 = match(LexicalUnit.RPAREN);
        ParseTree pt5 = match(LexicalUnit.THEN);
        ParseTree pt6 = code();
        ParseTree pt7 = ifSeq();
        return new ParseTree(new Symbol(LexicalUnit.IF_, "<If>"), Arrays.asList(pt1, pt2, pt3, pt4, pt5, pt6, pt7));
    }

    /**
     * This method handles the parsing of the rules comming from <ifSeq> as
     * left-hand side. This method throws an exception if it meet an unexpected
     * lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree ifSeq() throws Exception {
        Symbol tok = next_token();
        ParseTree pt1 = null;
        ParseTree pt2 = null;
        ParseTree pt3 = null;
        switch (tok.getType()) {
            case END:
                leftMostDerivationArray.add(23);
                pt1 = match(LexicalUnit.END);
                break;
            case ELSE:
                leftMostDerivationArray.add(24);
                pt1 = match(LexicalUnit.ELSE);
                pt2 = code();
                pt3 = match(LexicalUnit.END);
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.ELSE, LexicalUnit.END));
        }
        ArrayList<ParseTree> array = new ArrayList<>();
        array.add(pt1);
        if (pt2 != null) {
            array.add(pt2);
            array.add(pt3);
        }
        return new ParseTree(new Symbol(LexicalUnit.IFSEQ_, "<IfSeq>"), array);
    }

    /**
     * This method handles the parsing of the rules comming from <Cond> as left-hand
     * side. This method throws an exception if it meet an unexpected lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree cond() throws Exception {
        leftMostDerivationArray.add(25);
        ParseTree pt1 = exprArith();
        ParseTree pt2 = comp();
        ParseTree pt3 = exprArith();
        return new ParseTree(new Symbol(LexicalUnit.IFSEQ_, "<Cond>"), Arrays.asList(pt1, pt2, pt3));
    }

    /**
     * This method handles the parsing of the rules comming from <Comp> as left-hand
     * side. This method throws an exception if it meet an unexpected lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree comp() throws Exception {
        Symbol tok = next_token();
        ParseTree pt1 = null;
        switch (tok.getType()) {
            case EQUAL:
                leftMostDerivationArray.add(26);
                pt1 = match(LexicalUnit.EQUAL);
                break;
            case SMALLER:
                leftMostDerivationArray.add(28);
                pt1 = match(LexicalUnit.SMALLER);
                break;
            case GREATER:
                leftMostDerivationArray.add(27);
                pt1 = match(LexicalUnit.GREATER);
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.EQUAL, LexicalUnit.SMALLER,
                        LexicalUnit.GREATER));
        }
        return new ParseTree(new Symbol(LexicalUnit.IFSEQ_, "<Comp>"), Arrays.asList(pt1));
    }

    /**
     * This method handles the parsing of the rules comming from <While> as
     * left-hand side. This method throws an exception if it meet an unexpected
     * lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree while_() throws Exception {
        leftMostDerivationArray.add(29);
        ParseTree pt1 = match(LexicalUnit.WHILE);
        ParseTree pt2 = match(LexicalUnit.LPAREN);
        ParseTree pt3 = cond();
        ParseTree pt4 = match(LexicalUnit.RPAREN);
        ParseTree pt5 = match(LexicalUnit.DO);
        ParseTree pt6 = code();
        ParseTree pt7 = match(LexicalUnit.END);
        return new ParseTree(new Symbol(LexicalUnit.WHILE_, "<While>"),
                Arrays.asList(pt1, pt2, pt3, pt4, pt5, pt6, pt7));
    }

    /**
     * This method handles the parsing of the rules comming from <Print> as
     * left-hand side. This method throws an exception if it meet an unexpected
     * lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree print() throws Exception {
        leftMostDerivationArray.add(30);
        ParseTree pt1 = match(LexicalUnit.PRINT);
        ParseTree pt2 = match(LexicalUnit.LPAREN);
        ParseTree pt3 = match(LexicalUnit.VARNAME);
        ParseTree pt4 = match(LexicalUnit.RPAREN);
        return new ParseTree(new Symbol(LexicalUnit.PRINT_, "<Print>"), Arrays.asList(pt1, pt2, pt3, pt4));
    }

    /**
     * This method handles the parsing of the rules comming from <Read> as left-hand
     * side. This method throws an exception if it meet an unexpected lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree read() throws Exception {
        leftMostDerivationArray.add(31);
        ParseTree pt1 = match(LexicalUnit.READ);
        ParseTree pt2 = match(LexicalUnit.LPAREN);
        ParseTree pt3 = match(LexicalUnit.VARNAME);
        ParseTree pt4 = match(LexicalUnit.RPAREN);
        return new ParseTree(new Symbol(LexicalUnit.PRINT_, "<Read>"), Arrays.asList(pt1, pt2, pt3, pt4));
    }
}
