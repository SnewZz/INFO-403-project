import java.util.ArrayList;

public class LLVMGenerator {

    private ParseTree pt;
    private String result;
    private ArrayList<String> variablesName; 
    private int generateNewVariableNameCounter;

    public LLVMGenerator(ParseTree pt){
        this.pt = pt;
        this.result = new String();
        this.generateNewVariableNameCounter = 0;
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
                    break;
                case WHILE_:
                    break;
                case PRINT_:
                    break;
                case READ_:
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    private String assign(ParseTree p){
        String result = new String();
        result += ";ASSIGN "+p.getChild(0).getLabel().getValue()+" := "+p.getChild(1).getLabel().getValue()+"\n";
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
        result += ";ACCESS VARNAME VALUE " + target + " = " + p.getLabel().getValue().toString() + " \n";

        String varName = "%"+p.getLabel().getValue().toString();
        String varValue = generateNewVariableName();
        result += "\t"+varValue+" = load i32, i32* "+varName+"\n";
        result += "\t"+"store i32 "+varValue+", i32* "+target+"\n";
        return result;
    }

    private String add(ParseTree p, String target){
        String result = new String();
        result += ";ADDITION " + target + " = " + p.getChild(0).getLabel().getValue().toString() + " + " + p.getChild(1).getLabel().getValue().toString() + " \n";

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
        result += ";SUBSTRACTION " + target + " = " + p.getChild(0).getLabel().getValue().toString() + " - " + p.getChild(1).getLabel().getValue().toString() + " \n";

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
        result += ";UNARY SUBSTRACTION " + target + " = -" + p.getChild(0).getLabel().getValue().toString() + " \n";

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
        result += ";DIVISION " + target + " = " + p.getChild(0).getLabel().getValue().toString() + " / " + p.getChild(1).getLabel().getValue().toString() + " \n";

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
        result += ";MULTIPLICATION " + target + " = " + p.getChild(0).getLabel().getValue().toString() + " * " + p.getChild(1).getLabel().getValue().toString() + " \n";

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
}
