import java.util.ArrayList;

public class LLVMGenerator {

    private ParseTree pt;
    private String result;
    private ArrayList<String> variablesName; 
    private int generateNewVariableNameCounter;
    private int ifCounter;
    private int whileCounter;

    public LLVMGenerator(ParseTree pt){
        this.pt = pt;
        this.result = new String();
        this.generateNewVariableNameCounter = 0;
        this.ifCounter = 0;
        this.whileCounter = 0;
        this.variablesName = new ArrayList<String>();
    }

    public ParseTree getParseTree(){
        return this.pt;
    }

    public String getResult(){
        return this.result;
    }
    
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
        this.result += "}";
    }

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
        String value = new String();
        if(rightSubT.getLabel().getType().equals(LexicalUnit.NUMBER)){
            value = rightSubT.getLabel().getValue().toString();
        }else{
            value = generateNewVariableName();
            result += "\t"+value+" = alloca i32\n";
            result += "\t"+operationHandler(rightSubT, value);
        }

        result += "\t"+"store i32 "+value+", i32* "+varName + "\n\n";

        return result;
    }

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
            case VARNAME:
                //VARNAME
                result += accessVarnameValue(p,target);
                break;
            default:
                break;
        }
        return result;
    }

    private String accessVarnameValue(ParseTree p, String target){
        String result = new String();
        result += ";ACCESS VARNAME VALUE (" + target + " = " + p.getLabel().getValue().toString() + ") \n";

        String varName = "%"+p.getLabel().getValue().toString();
        String varValue = generateNewVariableName();
        result += "\t"+varValue+" = load i32, i32* "+varName+"\n";
        result += "\t"+"store i32 "+varValue+", i32* "+target+"\n";
        return result;
    }

    private String add(ParseTree p, String target){
        String result = new String();
        result += ";ADDITION (" + target + " = " + p.getChild(0).getLabel().getValue().toString() + " + " + p.getChild(1).getLabel().getValue().toString() + ") \n";

        String leftValue = new String();
        String rightValue = new String();
        String addResult = generateNewVariableName();

        if(p.getChild(0).getLabel().getType().equals(LexicalUnit.NUMBER)){
            leftValue = p.getChild(0).getLabel().getValue().toString();
        }else{
            leftValue = generateNewVariableName();
            result += "\t"+leftValue+" = alloca i32\n";
            result += "\t"+operationHandler(p.getChild(0), leftValue);
        }

        if(p.getChild(1).getLabel().getType().equals(LexicalUnit.NUMBER)){
            rightValue = p.getChild(1).getLabel().getValue().toString();
        }else{
            rightValue = generateNewVariableName();
            result += "\t"+rightValue+" = alloca i32\n";
            result += "\t"+operationHandler(p.getChild(1), rightValue);
        }

        result += "\t"+addResult+" = add i32 "+leftValue+", "+rightValue+"\n";
        result += "\t"+"store i32 "+addResult+", i32* "+target+"\n";
        return result;
    }

    private String minus(ParseTree p, String target){
        String result = new String();
        result += ";SUBSTRACTION (" + target + " = " + p.getChild(0).getLabel().getValue().toString() + " - " + p.getChild(1).getLabel().getValue().toString() + ") \n";

        String leftValue = new String();
        String rightValue = new String();
        String minusResult = generateNewVariableName();

        if(p.getChild(0).getLabel().getType().equals(LexicalUnit.NUMBER)){
            leftValue = p.getChild(0).getLabel().getValue().toString();
        }else{
            leftValue = generateNewVariableName();
            result += "\t"+leftValue+" = alloca i32\n";
            result += "\t"+operationHandler(p.getChild(0), leftValue);
        }

        if(p.getChild(1).getLabel().getType().equals(LexicalUnit.NUMBER)){
            rightValue = p.getChild(1).getLabel().getValue().toString();
        }else{
            rightValue = generateNewVariableName();
            result += "\t"+rightValue+" = alloca i32\n";
            result += "\t"+operationHandler(p.getChild(1), rightValue);
        }

        result += "\t"+minusResult+" = sub i32 "+leftValue+", "+rightValue+"\n";
        result += "\t"+"store i32 "+minusResult+", i32* "+target+"\n";
        return result;
    }

    private String minusUnary(ParseTree p, String target){
        String result = new String();
        result += ";UNARY SUBSTRACTION (" + target + " = -" + p.getChild(0).getLabel().getValue().toString() + ") \n";

        String value = new String();
        String minusResult = generateNewVariableName();

        if(p.getChild(0).getLabel().getType().equals(LexicalUnit.NUMBER)){
            value = p.getChild(0).getLabel().getValue().toString();
        }else{
            value = generateNewVariableName();
            result += "\t"+value+" = alloca i32\n";
            result += "\t"+operationHandler(p.getChild(0), value);
        }

        result += "\t"+minusResult+" = sub i32 0, "+value+"\n";
        result += "\t"+"store i32 "+minusResult+", i32* "+target+"\n";
        return result;
    }

    private String divide(ParseTree p, String target){
        String result = new String();
        result += ";DIVISION (" + target + " = " + p.getChild(0).getLabel().getValue().toString() + " / " + p.getChild(1).getLabel().getValue().toString() + ") \n";

        String leftValue = new String();
        String rightValue = new String();
        String divResult = generateNewVariableName();

        if(p.getChild(0).getLabel().getType().equals(LexicalUnit.NUMBER)){
            leftValue = p.getChild(0).getLabel().getValue().toString();
        }else{
            leftValue = generateNewVariableName();
            result += "\t"+leftValue+" = alloca i32\n";
            result += "\t"+operationHandler(p.getChild(0), leftValue);
        }

        if(p.getChild(1).getLabel().getType().equals(LexicalUnit.NUMBER)){
            rightValue = p.getChild(1).getLabel().getValue().toString();
        }else{
            rightValue = generateNewVariableName();
            result += "\t"+rightValue+" = alloca i32\n";
            result += "\t"+operationHandler(p.getChild(1), rightValue);
        }

        result += "\t"+divResult+" = sdiv i32 "+leftValue+", "+rightValue+"\n";
        result += "\t"+"store i32 "+divResult+", i32* "+target+"\n";
        return result;
    }

    private String times(ParseTree p, String target){
        String result = new String();
        result += ";MULTIPLICATION (" + target + " = " + p.getChild(0).getLabel().getValue().toString() + " * " + p.getChild(1).getLabel().getValue().toString() + ") \n";

        String leftValue = new String();
        String rightValue = new String();
        String mulResult = generateNewVariableName();

        if(p.getChild(0).getLabel().getType().equals(LexicalUnit.NUMBER)){
            leftValue = p.getChild(0).getLabel().getValue().toString();
        }else{
            leftValue = generateNewVariableName();
            result += "\t"+leftValue+" = alloca i32\n";
            result += "\t"+operationHandler(p.getChild(0), leftValue);
        }

        if(p.getChild(1).getLabel().getType().equals(LexicalUnit.NUMBER)){
            rightValue = p.getChild(1).getLabel().getValue().toString();
        }else{
            rightValue = generateNewVariableName();
            result += "\t"+rightValue+" = alloca i32\n";
            result += "\t"+operationHandler(p.getChild(1), rightValue);
        }

        result += "\t"+mulResult+" = mul i32 "+leftValue+", "+rightValue+"\n";
        result += "\t"+"store i32 "+mulResult+", i32* "+target+"\n";
        return result;
    }

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

    private String cond(ParseTree p, String target){
        String result = new String();
        result += ";CONDITION (" + target + " = node(" + p.getChild(0).getLabel().getValue().toString() + ") " + p.getLabel().getValue().toString() + " node(" + p.getChild(1).getLabel().getValue().toString() + ")) \n";

        String leftValue = new String();
        String rightValue = new String();
        String condResult = generateNewVariableName();

        if(p.getChild(0).getLabel().getType().equals(LexicalUnit.NUMBER)){
            leftValue = p.getChild(0).getLabel().getValue().toString();
        }else{
            leftValue = generateNewVariableName();
            result += "\t"+leftValue+" = alloca i32\n";
            result += "\t"+operationHandler(p.getChild(0), leftValue);
        }

        if(p.getChild(1).getLabel().getType().equals(LexicalUnit.NUMBER)){
            rightValue = p.getChild(1).getLabel().getValue().toString();
        }else{
            rightValue = generateNewVariableName();
            result += "\t"+rightValue+" = alloca i32\n";
            result += "\t"+operationHandler(p.getChild(1), rightValue);
        }

        if(p.getLabel().getType().equals(LexicalUnit.EQUAL)){
            result += "\t"+condResult+" = icmp eq i32 "+leftValue+", "+rightValue+"\n";
        }else if(p.getLabel().getType().equals(LexicalUnit.SMALLER)){
            result += "\t"+condResult+" = icmp slt i32 "+leftValue+", "+rightValue+"\n";
        }else if(p.getLabel().getType().equals(LexicalUnit.GREATER)){
            result += "\t"+condResult+" = icmp s/u i32 "+leftValue+", "+rightValue+"\n";
        }

        
        result += "\t"+"store i32 "+condResult+", i32* "+target+"\n";
        return result;
    }

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

    private String read(ParseTree p){
        String result = new String();
        String toStore = "%"+p.getChild(0).getLabel().getValue().toString();

        result += ";READ ("+ toStore+" = stdin)\n";
        result += ";--------------------------------------------\n";

        String readed = generateNewVariableName();
        result += "\t"+ toStore +" = alloca i32\n";
        result += "\t"+ readed +" = call i32 @readInt()\n";
        result += "\t"+"store i32 "+ readed +", i32* "+toStore+"\n";
        return result;
    }

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
