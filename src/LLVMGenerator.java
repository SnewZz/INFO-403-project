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
    }

    public ParseTree getParseTree(){
        return this.pt;
    }

    public String getResult(){
        return this.result;
    }
    
    public void generateCorrespondingLLVM(){
        this.result += "; This is the code corresponding to the "+this.pt.getChild(1).getLabel().getValue()+" program.";
        this.result += "define i32 @main() {\n";
        this.result += code(pt);
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
        String varName = p.getChild(0).getLabel().getValue().toString();
        if(this.variablesName.contains(varName)){
            //load
        } else {
            this.variablesName.add(varName);
            result += "%"+varName+"="+p.getChild(1);
            //implement
        }
        return result;
    }

    private String assignHandler(ParseTree p){
        String result = new String();
        if(p.getChildren().size() == 0){
            result += p.getLabel().getValue().toString();
        }else if(p.getChildren().size() == 1){
            result += 
        }else{

        }
        return result;
    }

    private String add(String val1, String val2, String varName, int bothVariable){
        String result = new String();
        if(bothVariable == 0){
            result+="add "
            result+="store i32 "+val1+", %"+val2;
        }else if(bothVariable == 1){
            result+="%"+val1+", store i32 "+val2;
        }else{
            result+=""
        }
    }

    private String generateNewVariableName(){
        String varName = "v"+generateNewVariableNameCounter;
        generateNewVariableNameCounter ++;
        while(variablesName.contains(varName)){
            varName = "v"+generateNewVariableNameCounter;
            generateNewVariableNameCounter ++;
        }
        variablesName.add(varName);
        return varName;
    }
}
