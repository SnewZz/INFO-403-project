import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.io.Reader;

class Main{
    public static void main(String[] args) {
        try{
            if(args.length == 0){
                throw new IllegalArgumentException("At least one argument needed!");
            }
            Reader fileInputStream = new FileReader( args[0] );
            Lexer lexer = new Lexer(fileInputStream);
            lexer.yylex();
        }catch(Exception e){
            System.err.println( "Exception in Main " + e.toString() );
            e.printStackTrace();
        }
    }

}