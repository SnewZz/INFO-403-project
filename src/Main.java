import java.io.FileReader;
import java.io.Reader;

/**
 * This class contains the main method.
 * This checks if there is one argument and read the file before passing it to
 * the lexer.
 * After the lexer, it calls the parser to ensure that the input data respect
 * the syntax specified by a grammar.
 * If specified in the second argument, it creates a latex file containing the
 * parse tree with the name specified in the third argument.
 */
public class Main {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw new IllegalArgumentException("At least one argument needed!");
            }

            Reader fileInputStream = new FileReader(args[0]);

            Lexer lexer = new Lexer(fileInputStream);
            lexer.yylex();

            Parser parser = new Parser(lexer.getTokens());
            parser.parse();

            TreeSimplifier treeSimplifier = new TreeSimplifier(parser.getParseTree());
            treeSimplifier.simplify();

            if (args.length > 2 && args[1].equals("-wt")) {
                ParseTree pt = parser.getParseTree();
                TexHandler.createTreeTex(args[2], pt.toLaTeX());
            }

            if (args.length > 2 && args[1].equals("-wt")) {
                ParseTree npt = treeSimplifier.getNewTree();
                TexHandler.createTreeTex("simple_"+args[2], npt.toLaTeX());
            }
        } catch (Exception e) {
            System.err.println("Exception in parsing :" + e.toString());
            e.printStackTrace();
        } catch (Error e) {
            System.err.println("Exception in lexing :" + e.toString());
            e.printStackTrace();
        }
    }
}