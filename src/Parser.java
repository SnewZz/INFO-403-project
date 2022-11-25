import java.util.ArrayList;

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
            tokens.remove(0);
        } else{
            currToken = null;
        }
        
        return currToken;
    }

    void program(){
        Symbol tok = next_token();
    }
}
