import java.io.FileReader;
import java.io.Reader;

/** This class contains the main method. 
 * This checks if there is one argument and read the file before passing it to the lexer. */
public class Main{
    public static void main(String[] args) {
        try{
            if(args.length == 0){
                throw new IllegalArgumentException("At least one argument needed!");
            }
            Reader fileInputStream = new FileReader( args[0] );
            Lexer lexer = new Lexer(fileInputStream);
            lexer.yylex();
            System.out.println(lexer.getTokens().size());
            System.out.println(lexer.getTokens().get(lexer.getTokens().size()-1));

            Parser parser = new Parser(lexer.getTokens(), lexer.getVariables());
            parser.program();
        }catch(Exception e){
            System.err.println( "Exception in Main " + e.toString() );
            e.printStackTrace();
        }
    }

}