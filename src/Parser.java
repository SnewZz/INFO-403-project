import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private ArrayList<Symbol> tokens;
    private ArrayList<Symbol> variables;
    private ArrayList<Integer> leftMostDerivationArray;

    public Parser(ArrayList<Symbol> tokens, ArrayList<Symbol> variables) {
        this.tokens = tokens;
        this.variables = variables;
        this.leftMostDerivationArray = new ArrayList<>();
    }

    Symbol next_token() {
        Symbol currToken;

        if (tokens.size() != 0) {
            currToken = tokens.get(0);
        } else {
            currToken = null;
        }

        return currToken;
    }

    void match(LexicalUnit lu) throws Exception {
        if (lu == tokens.get(0).getType()) {
            System.out.println("Matched " + tokens.get(0).getType());
            tokens.remove(0);
        } else {
            syntax_error(Arrays.asList(lu));
        }
    }

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

    String getLeftMostDerivation(ArrayList<Integer> array){
        String derivation = "";

        for (int i = 0; i < array.size(); i++) {
            derivation += array.get(i) + " ";
        }

        return derivation;
    }

    void program() throws Exception {
        leftMostDerivationArray.add(1);
        match(LexicalUnit.BEGIN);
        match(LexicalUnit.PROGNAME);
        code();
        match(LexicalUnit.END);
        System.out.println("Program is syntactically correct!");
        System.out.println("Left most derivation:\n" + getLeftMostDerivation(leftMostDerivationArray));
    }

    void code() throws Exception {
        leftMostDerivationArray.add(2);
        Symbol tok = next_token();
        switch (tok.getType()) {
            case ELSE:
            case END:
                leftMostDerivationArray.add(3);
                return;
            default:
        }
        instruction();
        match(LexicalUnit.COMMA);
        code();

    }

    void instruction() throws Exception {
        Symbol tok = next_token();
        switch (tok.getType()) {
            case VARNAME:
                leftMostDerivationArray.add(4);
                assign();
                break;
            case IF:
                leftMostDerivationArray.add(5);
                if_();
                break;
            case WHILE:
                leftMostDerivationArray.add(6);
                while_();
                break;
            case PRINT:
                leftMostDerivationArray.add(7);
                print();
                break;
            case READ:
                leftMostDerivationArray.add(8);
                read();
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.VARNAME, LexicalUnit.IF,
                        LexicalUnit.WHILE, LexicalUnit.PRINT, LexicalUnit.READ));
        }
    }

    void assign() throws Exception {
        leftMostDerivationArray.add(9);
        match(LexicalUnit.VARNAME);
        match(LexicalUnit.ASSIGN);
        exprArith();
    }

    void exprArith() throws Exception {
        leftMostDerivationArray.add(10);
        mulDiv();
        exprArithQuote();
    }

    void exprArithQuote() throws Exception {
        Symbol tok = next_token();
        switch (tok.getType()) {
            case COMMA:
            case RPAREN:
            case EQUAL:
            case SMALLER:
            case GREATER:
                leftMostDerivationArray.add(13);
                return;
            case PLUS:
                leftMostDerivationArray.add(11);
                match(LexicalUnit.PLUS);
                break;
            case MINUS:
                leftMostDerivationArray.add(12);
                match(LexicalUnit.MINUS);
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.COMMA, LexicalUnit.RPAREN,
                        LexicalUnit.EQUAL, LexicalUnit.SMALLER, LexicalUnit.GREATER,
                        LexicalUnit.PLUS, LexicalUnit.MINUS));
                return;
        }
        mulDiv();
        exprArithQuote();
    }

    void mulDiv() throws Exception {
        leftMostDerivationArray.add(14);
        atom();
        mulDivQuote();
    }

    void mulDivQuote() throws Exception {
        Symbol tok = next_token();
        switch (tok.getType()) {
            case COMMA:
            case RPAREN:
            case EQUAL:
            case SMALLER:
            case GREATER:
            case PLUS:
            case MINUS:
                leftMostDerivationArray.add(17);
                return;
            case TIMES:
                leftMostDerivationArray.add(15);
                match(LexicalUnit.TIMES);
                break;
            case DIVIDE:
                leftMostDerivationArray.add(16);
                match(LexicalUnit.DIVIDE);
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.COMMA, LexicalUnit.RPAREN,
                        LexicalUnit.EQUAL, LexicalUnit.SMALLER, LexicalUnit.GREATER,
                        LexicalUnit.PLUS, LexicalUnit.MINUS, LexicalUnit.TIMES,
                        LexicalUnit.DIVIDE));
                return;
        }
        atom();
        mulDivQuote();
    }

    void atom() throws Exception {
        Symbol tok = next_token();
        switch (tok.getType()) {
            case MINUS:
                leftMostDerivationArray.add(18);
                match(LexicalUnit.MINUS);
                atom();
                break;
            case VARNAME:
                leftMostDerivationArray.add(19);
                match(LexicalUnit.VARNAME);
                break;
            case NUMBER:
                leftMostDerivationArray.add(20);
                match(LexicalUnit.NUMBER);
                break;
            case LPAREN:
                leftMostDerivationArray.add(21);
                match(LexicalUnit.LPAREN);
                exprArith();
                match(LexicalUnit.RPAREN);
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.VARNAME, LexicalUnit.NUMBER,
                        LexicalUnit.LPAREN));
        }
    }

    void if_() throws Exception {
        leftMostDerivationArray.add(22);
        match(LexicalUnit.IF);
        match(LexicalUnit.LPAREN);
        cond();
        match(LexicalUnit.RPAREN);
        match(LexicalUnit.THEN);
        code();
        ifSeq();
    }

    void ifSeq() throws Exception {
        Symbol tok = next_token();
        switch (tok.getType()) {
            case END:
                leftMostDerivationArray.add(23);
                match(LexicalUnit.END);
                break;
            case ELSE:
                leftMostDerivationArray.add(24);
                match(LexicalUnit.ELSE);
                code();
                match(LexicalUnit.END);
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.ELSE, LexicalUnit.END));
        }

    }

    void cond() throws Exception {
        leftMostDerivationArray.add(25);
        exprArith();
        comp();
        exprArith();

    }

    void comp() throws Exception {
        Symbol tok = next_token();
        switch (tok.getType()) {
            case EQUAL:
                leftMostDerivationArray.add(26);
                match(LexicalUnit.EQUAL);
                break;
            case SMALLER:
                leftMostDerivationArray.add(28);
                match(LexicalUnit.SMALLER);
                break;
            case GREATER:
                leftMostDerivationArray.add(27);
                match(LexicalUnit.GREATER);
                break;
            default:
                syntax_error(Arrays.asList(LexicalUnit.EQUAL, LexicalUnit.SMALLER,
                        LexicalUnit.GREATER));
        }

    }

    void while_() throws Exception {
        leftMostDerivationArray.add(29);
        match(LexicalUnit.WHILE);
        match(LexicalUnit.LPAREN);
        cond();
        match(LexicalUnit.RPAREN);
        match(LexicalUnit.DO);
        code();
        match(LexicalUnit.END);
    }

    void print() throws Exception {
        leftMostDerivationArray.add(30);
        match(LexicalUnit.PRINT);
        match(LexicalUnit.LPAREN);
        match(LexicalUnit.VARNAME);
        match(LexicalUnit.RPAREN);

    }

    void read() throws Exception {
        leftMostDerivationArray.add(31);
        match(LexicalUnit.READ);
        match(LexicalUnit.LPAREN);
        match(LexicalUnit.VARNAME);
        match(LexicalUnit.RPAREN);
    }
}
