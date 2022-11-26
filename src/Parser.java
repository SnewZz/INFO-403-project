import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private ArrayList<Symbol> tokens;
    private ArrayList<Symbol> variables;

    public Parser(ArrayList<Symbol> tokens, ArrayList<Symbol> variables){
        this.tokens = tokens;
        this.variables = variables;
    }

    Symbol next_token(){
        Symbol currToken;

        if(tokens.size() != 0){
            currToken = tokens.get(0);
        } else{
            currToken = null;
        }
        
        return currToken;
    }

    void match(LexicalUnit lu) throws Exception{
        if(lu == tokens.get(0).getType()){
            System.out.println("Matched " + tokens.get(0).getType());
            tokens.remove(0);
        }else{
            syntax_error(Arrays.asList(lu));
        }
    }

    void syntax_error(List<LexicalUnit> expected) throws Exception{
        int line = tokens.get(0).getLine();
        int column = tokens.get(0).getColumn();

        String expectedString = "";
        for(LexicalUnit lu : expected){
            expectedString += lu.toString() + ", ";
        }
        throw new Exception("Syntax Error, unexpected symbol at line: "+line+", col: "+column+"."
                            +"Found "+tokens.get(0).getType()+". (Expecting: "+ expectedString +")");
    }

    void program() throws Exception{;
        match(LexicalUnit.BEGIN);
        match(LexicalUnit.PROGNAME);
        code();
        match(LexicalUnit.END);
        System.out.println("Program is syntactically correct!");
    }

    void code() throws Exception{
        Symbol tok = next_token();
        switch(tok.getType()){
            case ELSE:
            case END: return;
            default:
        }
        instruction(); match(LexicalUnit.COMMA); code();

    }

    void instruction() throws Exception{
        Symbol tok = next_token();
        switch(tok.getType()){
            case VARNAME: assign();
            break;
            case IF: if_();
            break;
            case WHILE: while_();
            break;
            case PRINT: print();
            break;
            case READ: read();
            break;
            default: syntax_error(Arrays.asList(LexicalUnit.VARNAME, LexicalUnit.IF,
                            LexicalUnit.WHILE, LexicalUnit.PRINT, LexicalUnit.READ));
        }
    }

    void assign() throws Exception{
        match(LexicalUnit.VARNAME);
        match(LexicalUnit.ASSIGN);
        exprArith();
    }

    void exprArith() throws Exception{
        mulDiv(); exprArithQuote();
    }

    void exprArithQuote() throws Exception{
        Symbol tok = next_token();
        switch(tok.getType()){
            case COMMA:
            case RPAREN:
            case EQUAL:
            case SMALLER:
            case GREATER: return;
            case PLUS:
                match(LexicalUnit.PLUS);
            break;
            case MINUS:
                match(LexicalUnit.MINUS);
            break;
            default : syntax_error(Arrays.asList(LexicalUnit.COMMA, LexicalUnit.RPAREN,
                            LexicalUnit.EQUAL, LexicalUnit.SMALLER, LexicalUnit.GREATER,
                            LexicalUnit.PLUS, LexicalUnit.MINUS));
            return;
        }
        mulDiv(); exprArithQuote();
    }

    void mulDiv() throws Exception{
        atom(); mulDivQuote();
    }

    void mulDivQuote() throws Exception{
        Symbol tok = next_token();
        switch(tok.getType()){
            case COMMA:
            case RPAREN:
            case EQUAL:
            case SMALLER:
            case GREATER:
            case PLUS:
            case MINUS: return;
            case TIMES:
                match(LexicalUnit.TIMES);
            break;
            case DIVIDE:
                match(LexicalUnit.DIVIDE);
            break;
            default : syntax_error(Arrays.asList(LexicalUnit.COMMA, LexicalUnit.RPAREN,
                            LexicalUnit.EQUAL, LexicalUnit.SMALLER, LexicalUnit.GREATER,
                            LexicalUnit.PLUS, LexicalUnit.MINUS, LexicalUnit.TIMES,
                            LexicalUnit.DIVIDE));
            return;
        }
        atom(); mulDivQuote();
    }

    void atom() throws Exception{
        Symbol tok = next_token();
        switch(tok.getType()){
            case MINUS:
                match(LexicalUnit.MINUS);
                atom();
            break;
            case VARNAME:
                match(LexicalUnit.VARNAME);
            break;
            case NUMBER:
                match(LexicalUnit.NUMBER);
            break;
            case LPAREN:
                match(LexicalUnit.LPAREN);
                exprArith();
                match(LexicalUnit.RPAREN);
            break;
            default : syntax_error(Arrays.asList(LexicalUnit.VARNAME, LexicalUnit.NUMBER,
                            LexicalUnit.LPAREN));
        }
    }

    void if_() throws Exception{
        match(LexicalUnit.IF);
        match(LexicalUnit.LPAREN);
        cond();
        match(LexicalUnit.RPAREN);
        match(LexicalUnit.THEN);
        code();
        ifSeq();
    }

    void ifSeq() throws Exception{
        Symbol tok = next_token();
        switch(tok.getType()){
            case END:
                match(LexicalUnit.END);
            break;
            case ELSE:
                match(LexicalUnit.ELSE);
                code();
                match(LexicalUnit.END);
            break;
            default : syntax_error(Arrays.asList(LexicalUnit.ELSE, LexicalUnit.END));
        }

    }

    void cond() throws Exception{
        exprArith();
        comp();
        exprArith();

    }

    void comp() throws Exception{
        Symbol tok = next_token();
        switch(tok.getType()){
            case EQUAL:
                match(LexicalUnit.EQUAL);
            break;
            case SMALLER:
                match(LexicalUnit.SMALLER);
            break;
            case GREATER:
                match(LexicalUnit.GREATER);
            break;
            default : syntax_error(Arrays.asList(LexicalUnit.EQUAL, LexicalUnit.SMALLER,
                            LexicalUnit.GREATER));
        }

    }

    void while_() throws Exception{
        match(LexicalUnit.WHILE);
        match(LexicalUnit.LPAREN);
        cond();
        match(LexicalUnit.RPAREN);
        match(LexicalUnit.DO);
        code();
        match(LexicalUnit.END);
    }

    void print() throws Exception{
        match(LexicalUnit.PRINT);
        match(LexicalUnit.LPAREN);
        match(LexicalUnit.VARNAME);
        match(LexicalUnit.RPAREN);

    }

    void read() throws Exception{
        match(LexicalUnit.READ);
        match(LexicalUnit.LPAREN);
        match(LexicalUnit.VARNAME);
        match(LexicalUnit.RPAREN);
    }
}
