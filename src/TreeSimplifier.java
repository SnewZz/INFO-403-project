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
        ParseTree pt1 = t.getChild(0);
        ParseTree pt2 = t.getChild(1);
        ParseTree pt3 = code(t.getChild(2));
        ParseTree pt4 = t.getChild(3);
        return new ParseTree(new Symbol(LexicalUnit.PROGRAM_, "<Program>"),
                Arrays.asList(pt1, pt2, pt3, pt4));

    }

    ParseTree code(ParseTree t) throws Exception {

        if (t.getChild(0).getLabel().getType().equals(LexicalUnit.EPSILON)) {
            return new ParseTree(new Symbol(LexicalUnit.CODE_, "<Code>"));
        } else {

            ArrayList<ParseTree> list = new ArrayList<>();
            ParseTree pt1 = instruction(t.getChild(0));
            list.add(pt1);

            while (t.getChild(2).getChildren().size() > 1) {
                t = t.getChild(2);

                ParseTree codeFollow = instruction(t.getChild(0));
                list.add(codeFollow);
            }
            return new ParseTree(new Symbol(LexicalUnit.CODE_, "<Code>"), list);
        }
    }

    ParseTree instruction(ParseTree t) throws Exception {

        ParseTree pt = t.getChild(0);
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
        // return new ParseTree(new Symbol(LexicalUnit.INSTRUCTION_, "<Instruction>"));
        return pt;
    }

    ParseTree assign(ParseTree t) throws Exception {
        ParseTree pt1 = t.getChild(0);
        ParseTree pt2 = exprArith(t.getChild(2));
        return new ParseTree(new Symbol(LexicalUnit.ASSIGN_, "<Assign>"), Arrays.asList(pt1, pt2));
    }

    ParseTree exprArith(ParseTree t) throws Exception {

        ParseTree exprArithQuoteRight = t.getChild(1);

        if (exprArithQuoteRight.getChildren().size() == 1) {
            // case only things in muldiv, thus we return muldiv
            return mulDiv(t.getChild(0));
        } else {
            ParseTree pt1 = mulDiv(t.getChild(0));
            ParseTree op = t.getChild(1).getChild(0);
            ParseTree pt2 = exprArithQuote(t.getChild(1));
            return new ParseTree(new Symbol(op.getLabel().getType(), op.getLabel().getValue()),
                    Arrays.asList(pt1, pt2));
        }
    }

    ParseTree exprArithQuote(ParseTree t) throws Exception {

        ParseTree exprArithQuoteRight = t.getChild(2);

        if (exprArithQuoteRight.getChildren().size() == 1) {
            // case only things in muldiv, thus we return muldiv
            return mulDiv(t.getChild(1));
        } else {
            ParseTree pt1 = mulDiv(t.getChild(1));
            ParseTree op = t.getChild(2).getChild(0);
            ParseTree pt2 = exprArithQuote(t.getChild(2));
            return new ParseTree(new Symbol(op.getLabel().getType(), op.getLabel().getValue()),
                    Arrays.asList(pt1, pt2));
        }
    }

    ParseTree mulDiv(ParseTree t) throws Exception {
        ParseTree mulDivQuoteRight = t.getChild(1);

        if (mulDivQuoteRight.getChildren().size() == 1) {
            // case only things in Atom, thus we return Atom
            return atom(t.getChild(0));
        } else {
            ParseTree pt1 = atom(t.getChild(0));
            ParseTree op = t.getChild(1).getChild(0);
            ParseTree pt2 = mulDivQuote(t.getChild(1));
            return new ParseTree(new Symbol(op.getLabel().getType(), op.getLabel().getValue()),
                    Arrays.asList(pt1, pt2));
        }
    }

    ParseTree mulDivQuote(ParseTree t) throws Exception {

        ParseTree exprArithQuoteRight = t.getChild(2);

        if (exprArithQuoteRight.getChildren().size() == 1) {
            // case only things in atom, thus we return Atom
            return atom(t.getChild(1));
        } else {
            ParseTree pt1 = atom(t.getChild(1));
            ParseTree op = t.getChild(2).getChild(0);
            ParseTree pt2 = mulDivQuote(t.getChild(2));
            return new ParseTree(new Symbol(op.getLabel().getType(), op.getLabel().getValue()),
                    Arrays.asList(pt1, pt2));
        }
    }

    ParseTree atom(ParseTree t) throws Exception {
        if (t.getChildren().size() == 1) {
            return t.getChild(0);
        } else if (t.getChildren().size() == 2) {
            ParseTree pt = atom(t.getChild(1));
            return new ParseTree(new Symbol(LexicalUnit.MINUS, "-"), Arrays.asList(pt));
        } else {
            return exprArith(t.getChild(1));
        }
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
        ParseTree ifSeq = t.getChild(6);
        /*
         * if(ifSeq.getChild(1).getChildren().size() == 1){
         * 
         * }
         */
        return t;
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
        /*
         * leftMostDerivationArray.add(29);
         * ParseTree pt1 = match(LexicalUnit.WHILE);
         * ParseTree pt2 = match(LexicalUnit.LPAREN);
         * ParseTree pt3 = cond();
         * ParseTree pt4 = match(LexicalUnit.RPAREN);
         * ParseTree pt5 = match(LexicalUnit.DO);
         * ParseTree pt6 = code();
         * ParseTree pt7 = match(LexicalUnit.END);
         * return new ParseTree(new Symbol(LexicalUnit.WHILE_, "<While>"),
         * Arrays.asList(pt1, pt2, pt3, pt4, pt5, pt6, pt7));
         */
        return t;
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
        ParseTree pt = t.getChild(2);
        return new ParseTree(new Symbol(LexicalUnit.PRINT_, "<Print>"), Arrays.asList(pt));
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
        ParseTree pt = t.getChild(2);
        return new ParseTree(new Symbol(LexicalUnit.READ_, "<Read>"), Arrays.asList(pt));
    }

}