import java.util.ArrayList;
import java.util.Arrays;

public class TreeSimplifier {
    ParseTree oldTree;
    ParseTree newTree;

    public TreeSimplifier(ParseTree oldTree) {
        this.oldTree = oldTree;
        this.newTree = new ParseTree(new Symbol(LexicalUnit.PROGRAM_, "<Program>"));
    }

    public ParseTree getNewTree() {
        return newTree;
    }

    public void simplify() throws Exception {
        newTree = program(oldTree);
    }

    ParseTree program(ParseTree t) throws Exception {
        ParseTree pt1 = t.getChildren().get(0);
        ParseTree pt2 = t.getChildren().get(1);
        ParseTree pt3 = code(t.getChildren().get(2));
        ParseTree pt4 = t.getChildren().get(3);
        return new ParseTree(new Symbol(LexicalUnit.PROGRAM_, "<Program>"),
                Arrays.asList(pt1, pt2, pt3, pt4));

    }

    ParseTree code(ParseTree t) throws Exception {

        ArrayList list = new ArrayList<ParseTree>();
        ParseTree pt1 = instruction(t.getChildren().get(0));
        list.add(pt1);

        while (t.getChildren().size() > 1) {
            t = t.getChildren().get(2);

            System.out.println(t.getLabel().getValue());
            if(t.getChildren().size() > 1){
                ParseTree codeFollow = instruction(t.getChildren().get(0));
                list.add(codeFollow);
            }
        }
        return new ParseTree(new Symbol(LexicalUnit.CODE_, "<Code>"),list);
    }

    ParseTree instruction(ParseTree t) throws Exception {

        ParseTree pt = t.getChildren().get(0);
        switch (pt.getLabel().getType()) {
            case ASSIGN_:
                pt = assign(pt);
                break;
            case IF_:
                pt = if_(pt);
                break;
            case WHILE_:
                pt = while_(pt);
                break;
            case PRINT_:
                pt = print(pt);
                break;
            case READ_:
                pt = read(pt);
                break;
            default:
        }
        //return new ParseTree(new Symbol(LexicalUnit.INSTRUCTION_, "<Instruction>"));
        return pt;
    }

    ParseTree assign(ParseTree t) throws Exception {
        ParseTree pt1 = t.getChildren().get(0);
        ParseTree pt2 = t.getChildren().get(2);
        return new ParseTree(new Symbol(LexicalUnit.ASSIGN_, "<Assign>"), Arrays.asList(pt1, pt2));
    }

    ParseTree exprArith(ParseTree t) throws Exception {
        /*leftMostDerivationArray.add(10);
        ParseTree pt1 = mulDiv();
        ParseTree pt2 = exprArithQuote();
        return new ParseTree(new Symbol(LexicalUnit.EXPRARITH_, "<ExprArith>"), Arrays.asList(pt1, pt2));*/
        return null;
    }


    /**
     * This method handles the parsing of the rules comming from <If> as left-hand
     * side. This method throws an exception if it meet an unexpected lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree if_(ParseTree t) throws Exception {
        /*leftMostDerivationArray.add(22);
        ParseTree pt1 = match(LexicalUnit.IF);
        ParseTree pt2 = match(LexicalUnit.LPAREN);
        ParseTree pt3 = cond();
        ParseTree pt4 = match(LexicalUnit.RPAREN);
        ParseTree pt5 = match(LexicalUnit.THEN);
        ParseTree pt6 = code();
        ParseTree pt7 = ifSeq();
        return new ParseTree(new Symbol(LexicalUnit.IF_, "<If>"), Arrays.asList(pt1, pt2, pt3, pt4, pt5, pt6, pt7));*/
        return null;
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
    ParseTree while_(ParseTree t) throws Exception {
        /*leftMostDerivationArray.add(29);
        ParseTree pt1 = match(LexicalUnit.WHILE);
        ParseTree pt2 = match(LexicalUnit.LPAREN);
        ParseTree pt3 = cond();
        ParseTree pt4 = match(LexicalUnit.RPAREN);
        ParseTree pt5 = match(LexicalUnit.DO);
        ParseTree pt6 = code();
        ParseTree pt7 = match(LexicalUnit.END);
        return new ParseTree(new Symbol(LexicalUnit.WHILE_, "<While>"),
                Arrays.asList(pt1, pt2, pt3, pt4, pt5, pt6, pt7));*/
        return null;
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
    ParseTree print(ParseTree t) throws Exception {
        /*leftMostDerivationArray.add(30);
        ParseTree pt1 = match(LexicalUnit.PRINT);
        ParseTree pt2 = match(LexicalUnit.LPAREN);
        ParseTree pt3 = match(LexicalUnit.VARNAME);
        ParseTree pt4 = match(LexicalUnit.RPAREN);
        return new ParseTree(new Symbol(LexicalUnit.PRINT_, "<Print>"), Arrays.asList(pt1, pt2, pt3, pt4));*/
        return null;
    }

    /**
     * This method handles the parsing of the rules comming from <Read> as left-hand
     * side. This method throws an exception if it meet an unexpected lexical unit.
     * 
     * @return The parse tree at the corresponding level of this rule.
     * @throws Exception Throws an exception if the lexical unit do not correspond
     *                   to what the parser was expecting.
     */
    ParseTree read(ParseTree t) throws Exception {
        /*leftMostDerivationArray.add(31);
        ParseTree pt1 = match(LexicalUnit.READ);
        ParseTree pt2 = match(LexicalUnit.LPAREN);
        ParseTree pt3 = match(LexicalUnit.VARNAME);
        ParseTree pt4 = match(LexicalUnit.RPAREN);
        return new ParseTree(new Symbol(LexicalUnit.PRINT_, "<Read>"), Arrays.asList(pt1, pt2, pt3, pt4));*/
        return null;
    }


}
