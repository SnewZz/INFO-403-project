import java.util.ArrayList;

public class LLVMGenerator {

    private ParseTree pt; //parse tree to generate LLVM code from
    private String result; //result of the LLVM code generation
    private ArrayList<String> variablesName; //list of variables names
    private int generateNewVariableNameCounter; //counter for new variables
    private int ifCounter; //counter for if statements
    private int whileCounter; //counter for while statements

    /**
     * Constructor of the LLVMGenerator class.
     * @param pt the parse tree to generate LLVM code from
     */
    public LLVMGenerator(ParseTree pt){
        this.pt = pt;
        this.result = new String();
        this.generateNewVariableNameCounter = 0;
        this.ifCounter = 0;
        this.whileCounter = 0;
        this.variablesName = new ArrayList<String>();
    }

    /**
     * Getter for the parse tree.
     * @return the parse tree
     */
    public ParseTree getParseTree(){
        return this.pt;
    }

    /**
     * Getter for the result of the LLVM code generation.
     * @return the result of the LLVM code generation
     */
    public String getResult(){
        return this.result;
    }
    
    /**
     * This method start to generates the corresponding LLVM code recursively.
     * It first generates the read and print functions.
     * Then, it generates the main function.
     */
    public void generateCorrespondingLLVM(){
        this.result += "; This is the code corresponding to the "+this.pt.getChild(1).getLabel().getValue()+" program.\n";
        this.result += ";--------------------------------------------\n";
        this.result += ";First, we define read and print functions\n\n";
        this.result += generateReadFunction();
        this.result += generatePrintFunction();
        this.result += ";--------------------------------------------\n";
        this.result += ";Then, we define the main function\n";
        this.result += ";==============================================================================\n\n";
        this.result += "define i32 @main() {\n";
        this.result += code(pt.getChild(2));
        this.result += "ret i32 0\n";
        this.result += "}";
    }

    /**
     * This method generates all the instructions in a code block by calling the corresponding methods.
     * @return the LLVM code corresponding to the code block
     */
    private String code(ParseTree p){
        String result = new String();
        for (ParseTree child : p.getChildren()) {
            switch(child.getLabel().getType()){
                case ASSIGN_:
                    result += assign(child);
                    break;
                case IF_:
                    result += if_(child);
                    break;
                case WHILE_:
                    result += while_(child);
                    break;
                case PRINT_:
                    result += print(child);
                    break;
                case READ_:
                    result += read(child);
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * This method generates the LLVM code corresponding to an assignement.
     * And defines the variable if it does not exist.
     * Call the operationHandler method to generate the LLVM code corresponding to the operation in the assignement.
     * @return the LLVM code corresponding to the assignement.
     */
    private String assign(ParseTree p){
        String result = new String();
        result += ";ASSIGN ("+p.getChild(0).getLabel().getValue()+" := node("+p.getChild(1).getLabel().getValue()+"))\n";
        result += ";--------------------------------------------\n";

        String varName = "%"+p.getChild(0).getLabel().getValue().toString();
        // create variable if not exist
        if(!this.variablesName.contains(varName)){
            this.variablesName.add(varName);
            result += "\t"+varName+" = alloca i32\n";
        }

        ParseTree rightSubT = p.getChild(1);
        String valueC = new String();
        
        if(rightSubT.getLabel().getType().equals(LexicalUnit.NUMBER)){
            valueC = rightSubT.getLabel().getValue().toString();
        }else if(p.getChild(1).getLabel().getType().equals(LexicalUnit.VARNAME)){
            
            valueC = generateNewVariableName();
            String var = "%"+p.getChild(1).getLabel().getValue().toString();
            result += "\t"+valueC+" = load i32, i32* "+var+"\n";
        }else{
            valueC = generateNewVariableName();
            result += "\t"+operationHandler(rightSubT, valueC);
        }

        result += "\t"+"store i32 "+valueC+", i32* "+varName + "\n\n";

        return result;
    }

    /**
     * This method checks the type of the operation and calls the corresponding method.
     * @param p the parse tree of the operation
     * @param target the target variable where the result of the operation will be stored.
     * @return the LLVM code corresponding to the operation.
     */
    private String operationHandler(ParseTree p, String target){
        String result = new String();

        switch(p.getLabel().getType()){
            case PLUS:
                //PLUS
                result += add(p,target);
                break;
            case MINUS:
                //MINUS
                if(p.getChildren().size() == 1){
                    result += minusUnary(p,target);
                } else if(p.getChildren().size() == 2){
                    result += minus(p,target);
                }
                break;
            case TIMES:
                //TIMES
                result += times(p,target);
                break;
            case DIVIDE:
                //DIVIDE
                result += divide(p,target);
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * This method generates the LLVM code corresponding to a suboperation in a operation.
     * Call the operationHandler method to generate the LLVM code corresponding to the suboperation if
     * the suboperation is an operation.
     * Else it directly returns the value of the suboperation.
     * @param p the parse tree of the suboperation
     * @return an array containing the LLVM code corresponding to the suboperation and the variable where the result of the suboperation is stored.
     */
    private String[] subOperation(ParseTree p){
        String resultText = new String();
        String operation = new String();

        if(p.getLabel().getType().equals(LexicalUnit.NUMBER)){
            operation = p.getLabel().getValue().toString();
        }else if(p.getLabel().getType().equals(LexicalUnit.VARNAME)){
            operation = generateNewVariableName();
            String varName = "%"+p.getLabel().getValue().toString();
            resultText += "\t"+operation+" = load i32, i32* "+varName+"\n";
        }else{
            operation = generateNewVariableName();
            resultText += "\t"+operationHandler(p, operation);
        }

        String[] textAndOperation = {resultText, operation};
        return textAndOperation;

    }

    /**
     * This method generates the LLVM code corresponding to an addition.
     * Call the subOperation method to generate the LLVM code corresponding to the suboperations.
     * @param p the parse tree of the addition
     * @param target the target variable where the result of the addition will be stored.
     * @return the LLVM code corresponding to the addition.
     */
    private String add(ParseTree p, String target){
        String result = new String();
        result += ";ADDITION (" + target + " = " + p.getChild(0).getLabel().getValue().toString() + " + " + p.getChild(1).getLabel().getValue().toString() + ") \n";

        String leftValueC = new String();
        String rightValueC = new String();

        String[] textAndOperationL = subOperation(p.getChild(0));
        leftValueC = textAndOperationL[1];
        result += textAndOperationL[0];

        String[] textAndOperationR = subOperation(p.getChild(1));
        rightValueC = textAndOperationR[1];
        result += textAndOperationR[0];

        result += "\t"+target+" = add i32 "+leftValueC+", "+rightValueC+"\n";
        return result;
    }

    /**
     * This method generates the LLVM code corresponding to a substraction.
     * Call the subOperation method to generate the LLVM code corresponding to the suboperations.
     * @param p the parse tree of the substraction
     * @param target the target variable where the result of the substraction will be stored.
     * @return the LLVM code corresponding to the substraction.
     */
    private String minus(ParseTree p, String target){
        String result = new String();
        result += ";SUBSTRACTION (" + target + " = " + p.getChild(0).getLabel().getValue().toString() + " - " + p.getChild(1).getLabel().getValue().toString() + ") \n";

        String leftValueC = new String();
        String rightValueC = new String();

        String[] textAndOperationL = subOperation(p.getChild(0));
        leftValueC = textAndOperationL[1];
        result += textAndOperationL[0];

        String[] textAndOperationR = subOperation(p.getChild(1));
        rightValueC = textAndOperationR[1];
        result += textAndOperationR[0];

        result += "\t"+target+" = sub i32 "+leftValueC+", "+rightValueC+"\n";
        return result;
    }

    /**
     * This method generates the LLVM code corresponding to a unary minus.
     * Call the subOperation method to generate the LLVM code corresponding to the suboperation.
     * @param p the parse tree of the unary minus
     * @param target the target variable where the result of the unary minus will be stored.
     * @return the LLVM code corresponding to the unary minus.
     */
    private String minusUnary(ParseTree p, String target){
        String result = new String();
        result += ";UNARY SUBSTRACTION (" + target + " = -" + p.getChild(0).getLabel().getValue().toString() + ") \n";

        String valueC = new String();

        String[] textAndOperationL = subOperation(p.getChild(0));
        valueC = textAndOperationL[1];
        result += textAndOperationL[0];

        result += "\t"+target+" = sub i32 0, "+valueC+"\n";
        return result;
    }

    /**
     * This method generates the LLVM code corresponding to a division.
     * Call the subOperation method to generate the LLVM code corresponding to the suboperations.
     * @param p the parse tree of the division
     * @param target the target variable where the result of the division will be stored.
     * @return the LLVM code corresponding to the division.
     */
    private String divide(ParseTree p, String target){
        String result = new String();
        result += ";DIVISION (" + target + " = " + p.getChild(0).getLabel().getValue().toString() + " / " + p.getChild(1).getLabel().getValue().toString() + ") \n";

        String leftValueC = new String();
        String rightValueC = new String();

        String[] textAndOperationL = subOperation(p.getChild(0));
        leftValueC = textAndOperationL[1];
        result += textAndOperationL[0];

        String[] textAndOperationR = subOperation(p.getChild(1));
        rightValueC = textAndOperationR[1];
        result += textAndOperationR[0];

        result += "\t"+target+" = sdiv i32 "+leftValueC+", "+rightValueC+"\n";
        return result;
    }

    /**
     * This method generates the LLVM code corresponding to a multiplication.
     * Call the subOperation method to generate the LLVM code corresponding to the suboperations.
     * @param p the parse tree of the multiplication
     * @param target the target variable where the result of the multiplication will be stored.
     * @return the LLVM code corresponding to the multiplication.
     */
    private String times(ParseTree p, String target){
        String result = new String();
        result += ";MULTIPLICATION (" + target + " = " + p.getChild(0).getLabel().getValue().toString() + " * " + p.getChild(1).getLabel().getValue().toString() + ") \n";

        String leftValueC = new String();
        String rightValueC = new String();

        String[] textAndOperationL = subOperation(p.getChild(0));
        leftValueC = textAndOperationL[1];
        result += textAndOperationL[0];

        String[] textAndOperationR = subOperation(p.getChild(1));
        rightValueC = textAndOperationR[1];
        result += textAndOperationR[0];

        result += "\t"+target+" = mul i32 "+leftValueC+", "+rightValueC+"\n";
        return result;
    }

    /**
     * This method generates the LLVM code corresponding to a if.
     * Call the cond method to generate the LLVM code corresponding to the condition.
     * Then setup the labels and the jumps.
     * And call the code method to generate the LLVM code corresponding to the code sections of the if
     * @param p the parse tree of the if
     * @return the LLVM code corresponding to the if.
     */
    private String if_(ParseTree p){
        String result = new String();
        result += ";IF \n";
        result += ";--------------------------------------------\n";
        result += "\t"+"br label %if"+ifCounter+"\n";
        result += "    "+"if"+ifCounter+":\n";

        String condResult = generateNewVariableName();
        result += cond(p.getChild(0), condResult);

        String jmp = (p.getChildren().size() == 3) ? "false" : "end";

        result += "\t"+"br i1 "+condResult+", label %if"+ifCounter+"true, label %if"+ifCounter+jmp+"\n";

        result += ";THEN \n";

        result += "    "+"if"+ifCounter+"true:\n\n";
        result += code(p.getChild(1));
        result += "\t"+"br label %if"+ifCounter+"end\n";

        if(p.getChildren().size() == 3){
            result += ";ELSE \n";
            result += "    "+"if"+ifCounter+"false:\n\n";
            result += code(p.getChild(2));
            result += "\t"+"br label %if"+ifCounter+"end\n";
        }

        result += ";END \n";
        result += "    "+"if"+ifCounter+"end:\n\n";

        ifCounter++;
        return result;
    }

    /**
     * This method generates the LLVM code corresponding to a condition.
     * Call the subOperation method to generate the LLVM code corresponding to the suboperations of the condition.
     * @param p the parse tree of the condition
     * @param target the target variable where the result of the condition will be stored.
     * @return the LLVM code corresponding to the condition.
     */
    private String cond(ParseTree p, String target){
        String result = new String();
        result += ";CONDITION (" + target + " = node(" + p.getChild(0).getLabel().getValue().toString() + ") " + p.getLabel().getValue().toString() + " node(" + p.getChild(1).getLabel().getValue().toString() + ")) \n";

        String leftValueC = new String();
        String rightValueC = new String();

        String[] textAndOperationL = subOperation(p.getChild(0));
        leftValueC = textAndOperationL[1];
        result += textAndOperationL[0];

        String[] textAndOperationR = subOperation(p.getChild(1));
        rightValueC = textAndOperationR[1];
        result += textAndOperationR[0];

        if(p.getLabel().getType().equals(LexicalUnit.EQUAL)){
            result += "\t"+target+" = icmp eq i32 "+leftValueC+", "+rightValueC+"\n";
        }else if(p.getLabel().getType().equals(LexicalUnit.SMALLER)){
            result += "\t"+target+" = icmp slt i32 "+leftValueC+", "+rightValueC+"\n";
        }else if(p.getLabel().getType().equals(LexicalUnit.GREATER)){
            result += "\t"+target+" = icmp sgt i32 "+leftValueC+", "+rightValueC+"\n";
        }

        return result;
    }

    /**
     * This method generates the LLVM code corresponding to a while.
     * Call the cond method to generate the LLVM code corresponding to the condition.
     * Then setup the labels and the jumps.
     * And call the code method to generate the LLVM code corresponding to the code section of the while
     * @param p the parse tree of the while
     * @return the LLVM code corresponding to the while.
     */
    private String while_(ParseTree p){
        String result = new String();
        result += ";WHILE \n";
        result += ";--------------------------------------------\n";
        result += "\t"+"br label %while"+whileCounter+"\n";
        result += "    "+"while"+whileCounter+":\n";

        String condResult = generateNewVariableName();
        result += cond(p.getChild(0), condResult);

        result += "\t"+"br i1 "+condResult+", label %while"+whileCounter+"true, label %while"+whileCounter+"end\n";

        result += ";THEN \n";

        result += "    "+"while"+whileCounter+"true:\n\n";
        result += code(p.getChild(1));
        result += "\t"+"br label %while"+whileCounter+"\n";

        result += ";END \n";
        result += "    "+"while"+whileCounter+"end:\n\n";

        whileCounter++;
        return result;
    }

    /**
     * This method generates the LLVM code corresponding to the call of the print function.
     * @param p the parse tree of the print function
     * @return the LLVM code corresponding to the print function.
     */
    private String print(ParseTree p){
        String result = new String();
        String toPrint = "%"+p.getChild(0).getLabel().getValue().toString();

        result += ";PRINT (stdout = "+ toPrint+")\n";
        result += ";--------------------------------------------\n";

        String printed = generateNewVariableName();
        result += "\t"+ printed +" = load i32, i32* "+toPrint+"\n";

        result += "\t"+"call void @println(i32 "+ printed+")\n";
        return result;
    }

    /**
     * This method generates the LLVM code corresponding to the call of the read function.
     * @param p the parse tree of the read function
     * @return the LLVM code corresponding to the read function.
     */
    private String read(ParseTree p){
        String result = new String();
        String toStore = "%"+p.getChild(0).getLabel().getValue().toString();

        result += ";READ ("+ toStore+" = stdin)\n";
        result += ";--------------------------------------------\n";

        String readed = generateNewVariableName();

        if(!this.variablesName.contains(toStore)){
            this.variablesName.add(toStore);
            result += "\t"+ toStore +" = alloca i32\n";
        }

        result += "\t"+ readed +" = call i32 @readInt()\n";
        result += "\t"+"store i32 "+ readed +", i32* "+toStore+"\n";
        return result;
    }

    /**
     * This method generate new variable name.
     * @return the new variable name.
     */
    private String generateNewVariableName(){ //MAYBE MODIFY
        String varName = "%v"+generateNewVariableNameCounter;
        generateNewVariableNameCounter ++;
        while(variablesName.contains(varName)){
            varName = "%v"+generateNewVariableNameCounter;
            generateNewVariableNameCounter ++;
        }
        variablesName.add(varName);
        return varName;
    }

    /**
     * This method generates the LLVM code corresponding to the read function.
     * @return the LLVM code corresponding to the read function.
     */
    private String generateReadFunction(){
        String result = new String();
        result +=

        "@.strR = private unnamed_addr constant [3 x i8] c\"%d\\00\", align 1" + " \n\n" +

        "; Function Attrs: nounwind uwtable" + " \n" +
            "\tdefine i32 @readInt() #0 {" + " \n" +
            "\t%x = alloca i32, align 4" + " \n" +
            "\t%1 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %x)" + " \n" +
            "\t%2 = load i32, i32* %x, align 4" + " \n" +
            "\tret i32 %2" + " \n" +
        "}"+ " \n\n" +

        "declare i32 @__isoc99_scanf(i8*, ...) #1" + " \n";
        return result;
    }

    /**
     * This method generates the LLVM code corresponding to the print function.
     * @return the LLVM code corresponding to the print function.
     */
    private String generatePrintFunction(){
        String result = new String();
        result +=

        "@.strP = private unnamed_addr constant [4 x i8] c\"%d\\0A\\00\", align 1" + " \n\n" +

        "; Function Attrs: nounwind uwtable"+ " \n" +
        "define void @println(i32 %x) #0 {" + " \n" +
            "\t%1 = alloca i32, align 4" + " \n" +
            "\tstore i32 %x, i32* %1, align 4" + " \n" +
            "\t%2 = load i32, i32* %1, align 4" + " \n" +
            "\t%3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2)" + " \n" +
            "\tret void " + " \n" +
        "}"+ " \n\n" +

        "declare i32 @printf(i8*, ...) #1" + " \n\n";

        return result;
    }
}
