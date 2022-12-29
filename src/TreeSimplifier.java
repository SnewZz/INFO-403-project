import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class contains the methods to simplify the parse tree created by the parser.
 * It is used to remove the useless nodes.
 */
public class TreeSimplifier {
    ParseTree oldTree;
    ParseTree newTree;

    /**
     * Constructor of the class.
     * It creates a new parse tree with the same root as the old one.
     * @param oldTree the old parse tree
     */
    public TreeSimplifier(ParseTree oldTree) {
        this.oldTree = oldTree;
        this.newTree = new ParseTree(new Symbol(LexicalUnit.PROGRAM_, "<Program>"));
    }

    /**
     * Getter of the new parse tree.
     * @return the new parse tree
     */
    public ParseTree getNewTree() {
        return newTree;
    }

    /**
     * This method calls the program method to simplify the parse tree recursively.
     */
    public void simplify() {
        newTree = program(oldTree);
    }

    /**
     * This method simplifies the parse tree recursively from the program node.
     * Calls the corresponding method for the children of the current node.
     * @param t the current node
     * @return the simplified node
     */
    ParseTree program(ParseTree t) {
        ParseTree pt1 = t.getChild(0);
        ParseTree pt2 = t.getChild(1);
        ParseTree pt3 = code(t.getChild(2));
        ParseTree pt4 = t.getChild(3);
        return new ParseTree(new Symbol(LexicalUnit.PROGRAM_, "<Program>"),
                Arrays.asList(pt1, pt2, pt3, pt4));

    }

    /**
     * This method simplifies the parse tree recursively from the code node.
     * Calls the corresponding method for the children of the current node.
     * This one is a bit more complicated because it has to handle the epsilon case.
     * It puts the instructions in a list and then creates a new node with the list as children.
     * @param t the current node
     * @return the simplified node
     */
    ParseTree code(ParseTree t) {

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

    /**
     * This method simplifies the parse tree recursively from the instruction node.
     * Calls the corresponding method for the children of the current node.
     * We return directly the child of the current node because this node is useless.
     * @param t the current node
     * @return the simplified node
     */
    ParseTree instruction(ParseTree t) {

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
        return pt;
    }

    /**
     * This method simplifies the parse tree recursively from the assign node.
     * Calls the corresponding method for the children of the current node.
     * We remove ":=".
     * @param t the current node
     * @return the simplified node
     */
    ParseTree assign(ParseTree t) {
        ParseTree pt1 = t.getChild(0);
        ParseTree pt2 = exprArith(t.getChild(2));
        return new ParseTree(new Symbol(LexicalUnit.ASSIGN_, "<Assign>"), Arrays.asList(pt1, pt2));
    }

    /**
     * This method simplifies the parse tree recursively from the exprArith node.
     * Calls the corresponding method for the children of the current node.
     * We search the operator in the second child of the current node,
     * and put it between the two children of the current node.
     * If there is no operator, we call the muldiv method.
     * @param t the current node
     * @return the simplified node
     */
    ParseTree exprArith(ParseTree t) {

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

    /**
     * This method simplifies the parse tree recursively from the exprArithQuote node.
     * Calls the corresponding method for the children of the current node.
     * We search the operator in the second child of the current node,
     * and put it between the two children of the current node.
     * If there is no operator, we call the muldiv method.
     * @param t the current node
     * @return the simplified node
     */
    ParseTree exprArithQuote(ParseTree t) {

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

    /**
     * This method simplifies the parse tree recursively from the mulDiv node.
     * Calls the corresponding method for the children of the current node.
     * We search the operator in the second child of the current node,
     * and put it between the two children of the current node.
     * If there is no operator, we call the atom method.
     * @param t the current node
     * @return the simplified node
     */
    ParseTree mulDiv(ParseTree t) {
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

    /**
     * This method simplifies the parse tree recursively from the mulDivQuote node.
     * Calls the corresponding method for the children of the current node.
     * We search the operator in the second child of the current node,
     * and put it between the two children of the current node.
     * If there is no operator, we call the atom method.
     * @param t the current node
     * @return the simplified node
     */
    ParseTree mulDivQuote(ParseTree t) {

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

    /**
     * This method simplifies the parse tree recursively from the atom node.
     * Calls the corresponding method for the children of the current node.
     * If there is only one child, we return it.
     * If there are two children, we return the second child with a minus operator.
     * If there are three children, we simplify by removing the parenthesis and
     * call the exprArith method in between.
     * @param t the current node
     * @return the simplified node
     */
    ParseTree atom(ParseTree t) {
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
     * This method simplifies the parse tree recursively from the if node.
     * Calls the corresponding method for the children of the current node.
     * If there is only one child in the ifSeq node, we put the condition in the
     * first child, the code in the second child and return the node.
     * If there are two children in the ifSeq node, we put the condition in the
     * first child, the then code in the second child, the else code in the third child
     * @param t the current node
     * @return the simplified node
     */
    ParseTree if_(ParseTree t) {
        ParseTree ifSeq = t.getChild(6);
        if(ifSeq.getChildren().size() == 1){
            ParseTree cond = cond(t.getChild(2));
            ParseTree then = code(t.getChild(5));
            return new ParseTree(new Symbol(LexicalUnit.IF_, "<If>"), Arrays.asList(cond, then));
        }else{
            ParseTree cond = cond(t.getChild(2));
            ParseTree then = code(t.getChild(5));
            ParseTree else_ = code(ifSeq.getChild(1));
            return new ParseTree(new Symbol(LexicalUnit.IF_, "<If>"), Arrays.asList(cond, then, else_));
        }
    }

    /**
     * This method simplifies the parse tree recursively from the cond node.
     * Calls the corresponding method for the children of the current node.
     * We search the operator in the second child of the current node,
     * and put it between the two children of the current node.
     * @param t the current node
     * @return the simplified node
     */
    private ParseTree cond(ParseTree t) {
        ParseTree expL = exprArith(t.getChild(0));
        ParseTree op = t.getChild(1).getChild(0);
        ParseTree expR = exprArith(t.getChild(2));
        return new ParseTree(new Symbol(op.getLabel().getType(), op.getLabel().getValue()),
                Arrays.asList(expL, expR));
    }


    /**
     * This method simplifies the parse tree recursively from the while node.
     * Calls the corresponding method for the children of the current node.
     * We put the condition in the first child, the code in the second child
     * @param t the current node
     * @return the simplified node
     */
    ParseTree while_(ParseTree t) {
        ParseTree cond = cond(t.getChild(2));
        ParseTree then = code(t.getChild(5));
        return new ParseTree(new Symbol(LexicalUnit.WHILE_, "<While>"), Arrays.asList(cond, then));
    }

    /**
     * This method simplifies the parse tree recursively from the print node.
     * Calls the corresponding method for the children of the current node.
     * We remove the parenthesis and the useless stuff.
     * @param t the current node
     * @return the simplified node
     */
    ParseTree print(ParseTree t) {
        ParseTree pt = t.getChild(2);
        return new ParseTree(new Symbol(LexicalUnit.PRINT_, "<Print>"), Arrays.asList(pt));
    }

    /**
     * This method simplifies the parse tree recursively from the read node.
     * Calls the corresponding method for the children of the current node.
     * We remove the parenthesis and the useless stuff.
     * @param t the current node
     * @return the simplified node
     */
    ParseTree read(ParseTree t) {
        ParseTree pt = t.getChild(2);
        return new ParseTree(new Symbol(LexicalUnit.READ_, "<Read>"), Arrays.asList(pt));
    }

}