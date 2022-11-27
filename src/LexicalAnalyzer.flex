import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.lang.Error;
%%// Options of the scanner

%class Lexer	//Name
%unicode		//Use unicode
%line         	//Use line counter (yyline variable)
%column       	//Use character counter by line (yycolumn variable)
%type Symbol  	//Says that the return type is Symbol
%standalone		//Standalone mode
%scanerror Error

%{
    private int stackStateComment = 0;
	private ArrayList<Symbol> variables = new ArrayList<>();
    public ArrayList<Symbol> tokens = new ArrayList<>();

    public ArrayList<Symbol> getVariables(){
        return variables;
    }

    public ArrayList<Symbol> getTokens(){
        return tokens;
    }
%}


// Return value of the program
%eofval{
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

%eof{
    for(int i = 0; i<10000; i++){

    }
    if(stackStateComment != 0){
        throw new Error("The long comment has not been closed!");
    }

	//System.out.println("\nVariables");

	Collections.sort(variables, new Comparator<Symbol>() {
        @Override
        public int compare(Symbol s1, Symbol s2) {
            return s1.getValue().toString().compareTo(s2.getValue().toString());
        }
    });

	for(Symbol s : variables){
		//System.out.println(s.getValue()+" "+s.getLine());
	}

    this.tokens.add(new Symbol(LexicalUnit.EOS, yyline, yycolumn, "EOS"));
%eof}

// extended regex

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha          = {AlphaUpperCase}|{AlphaLowerCase}
Numeric        = [0-9]
AlphaNumeric   = {Alpha}|{Numeric}

VarName = {AlphaLowerCase}({AlphaLowerCase}|{Numeric})*
ProgName = {AlphaUpperCase}{AlphaNumeric}*{AlphaLowerCase}+{AlphaNumeric}*
Number = ([1-9]{Numeric}*)|0

LineTerminator = \r|\n|\r\n

ShortComment = ::[^\r\n]*{LineTerminator}?

TraditionalComment = "%%"~"%%"
//Comment = {ShortComment}|{TraditionalComment}

%xstate COMMENT

%% //Identification of tokens

<YYINITIAL>{
    "%%"                    {
                                stackStateComment++;
                                yybegin(COMMENT);
                            }
    "BEGIN"                 {this.tokens.add(new Symbol(LexicalUnit.BEGIN, yyline, yycolumn, yytext()));}
    "END"                   {this.tokens.add(new Symbol(LexicalUnit.END, yyline, yycolumn, yytext()));}

    ","                     {this.tokens.add(new Symbol(LexicalUnit.COMMA, yyline, yycolumn, yytext()));}
    ":="                    {this.tokens.add(new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, yytext()));}
    "("                     {this.tokens.add(new Symbol(LexicalUnit.LPAREN, yyline, yycolumn, yytext()));}
    ")"                     {this.tokens.add(new Symbol(LexicalUnit.RPAREN, yyline, yycolumn, yytext()));}
    "-"                     {this.tokens.add(new Symbol(LexicalUnit.MINUS, yyline, yycolumn, yytext()));}
    "+"                     {this.tokens.add(new Symbol(LexicalUnit.PLUS, yyline, yycolumn, yytext()));}
    "*"                     {this.tokens.add(new Symbol(LexicalUnit.TIMES, yyline, yycolumn, yytext()));}
    "/"                     {this.tokens.add(new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, yytext()));}

    "IF"                    {this.tokens.add(new Symbol(LexicalUnit.IF, yyline, yycolumn, yytext()));}
    "THEN"                  {this.tokens.add(new Symbol(LexicalUnit.THEN, yyline, yycolumn, yytext()));}
    "ELSE"                  {this.tokens.add(new Symbol(LexicalUnit.ELSE, yyline, yycolumn, yytext()));}
    "PRINT"                 {this.tokens.add(new Symbol(LexicalUnit.PRINT, yyline, yycolumn, yytext()));}
    "READ"                  {this.tokens.add(new Symbol(LexicalUnit.READ, yyline, yycolumn, yytext()));}

    "="                     {this.tokens.add(new Symbol(LexicalUnit.EQUAL, yyline, yycolumn, yytext()));}
    ">"                     {this.tokens.add(new Symbol(LexicalUnit.GREATER, yyline, yycolumn, yytext()));}
    "<"                     {this.tokens.add(new Symbol(LexicalUnit.SMALLER, yyline, yycolumn, yytext()));}

    "WHILE"                 {this.tokens.add(new Symbol(LexicalUnit.WHILE, yyline, yycolumn, yytext()));}
    "DO"                    {this.tokens.add(new Symbol(LexicalUnit.DO, yyline, yycolumn, yytext()));}
    {VarName}               {
    							Symbol var = new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext());
    							if(!variables.stream().anyMatch(s -> s.getValue().toString().equals(var.getValue().toString()))){
    								variables.add(var);
    							}
                                this.tokens.add(var);
    						}
    {Number}                {this.tokens.add(new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext()));}
    {ShortComment}               {yytext();}
    {ProgName}              {this.tokens.add(new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn, yytext()));}
    " "                     {}
    .                       {throw new Error("An unexpected symbol has been encountered : '"+yytext()+"'. At line "+yyline+" and column "+yycolumn+".");}
    {LineTerminator}		{}
}

<COMMENT>{
    "%%"                    {
                                stackStateComment--;
                                yybegin(YYINITIAL);
                            }
    .                       {}
    {LineTerminator}		{}
}