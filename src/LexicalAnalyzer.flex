import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
%%// Options of the scanner

%class Lexer	//Name
%unicode		//Use unicode
%line         	//Use line counter (yyline variable)
%column       	//Use character counter by line (yycolumn variable)
%type Symbol  	//Says that the return type is Symbol
%standalone		//Standalone mode

%{
	private ArrayList<Symbol> variables = new ArrayList<>();
%}


// Return value of the program
%eofval{
	return new Symbol(LexicalUnit.EOS, yyline, yycolumn);
%eofval}

%eof{
	System.out.println("\nVariables");

	Collections.sort(variables, new Comparator<Symbol>() {
        @Override
        public int compare(Symbol s1, Symbol s2) {
            return s1.getValue().toString().compareTo(s2.getValue().toString());
        }
    });

	for(Symbol s : variables){
		System.out.println(s.getValue()+" "+s.getLine());
	}
%eof}

// extended regex

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha          = {AlphaUpperCase}|{AlphaLowerCase}
Numeric        = [0-9]
AlphaNumeric   = {Alpha}|{Numeric}

VarName = {AlphaLowerCase}({AlphaLowerCase}|{Numeric})*
ProgName = {AlphaUpperCase}{AlphaNumeric}*
Number = ([1-9]{Numeric}*)|0

LineTerminator = \r|\n|\r\n

ShortComment = ::[^\r\n]*{LineTerminator}//?
TraditionalComment = "%%" [^*] ~"%%"
Comment = {ShortComment}|{TraditionalComment} /* Nested comment :  https://stackoverflow.com/questions/24666688/jflex-match-nested-comments-as-one-token  */

%% //Identification of tokens


"BEGIN"     {System.out.println(new Symbol(LexicalUnit.BEGIN, yyline, yycolumn, yytext()));}
"END"       {System.out.println(new Symbol(LexicalUnit.END, yyline, yycolumn, yytext()));}

","                     {System.out.println(new Symbol(LexicalUnit.COMMA, yyline, yycolumn, yytext()));}
":="                    {System.out.println(new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, yytext()));}
"("                     {System.out.println(new Symbol(LexicalUnit.LPAREN, yyline, yycolumn, yytext()));}
")"                     {System.out.println(new Symbol(LexicalUnit.RPAREN, yyline, yycolumn, yytext()));}
"-"                     {System.out.println(new Symbol(LexicalUnit.MINUS, yyline, yycolumn, yytext()));}
"+"                     {System.out.println(new Symbol(LexicalUnit.PLUS, yyline, yycolumn, yytext()));}
"*"                     {System.out.println(new Symbol(LexicalUnit.TIMES, yyline, yycolumn, yytext()));}
"/"                     {System.out.println(new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, yytext()));}

"IF"                    {System.out.println(new Symbol(LexicalUnit.IF, yyline, yycolumn, yytext()));}
"THEN"                  {System.out.println(new Symbol(LexicalUnit.THEN, yyline, yycolumn, yytext()));}
"ELSE"                  {System.out.println(new Symbol(LexicalUnit.ELSE, yyline, yycolumn, yytext()));}
"PRINT"                 {System.out.println(new Symbol(LexicalUnit.PRINT, yyline, yycolumn, yytext()));}
"READ"                  {System.out.println(new Symbol(LexicalUnit.READ, yyline, yycolumn, yytext()));}

"="                     {System.out.println(new Symbol(LexicalUnit.EQUAL, yyline, yycolumn, yytext()));}
">"                     {System.out.println(new Symbol(LexicalUnit.GREATER, yyline, yycolumn, yytext()));}
"<"                     {System.out.println(new Symbol(LexicalUnit.SMALLER, yyline, yycolumn, yytext()));}

"WHILE"                 {System.out.println(new Symbol(LexicalUnit.WHILE, yyline, yycolumn, yytext()));}
"DO"                    {System.out.println(new Symbol(LexicalUnit.DO, yyline, yycolumn, yytext()));}
{VarName}               {
							Symbol var = new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext());
							System.out.println(var);
							if(!variables.stream().anyMatch(s -> s.getValue().toString().equals(var.getValue().toString()))){
								variables.add(var);
							}
						}
{Number}                {System.out.println(new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext()));}
{Comment}               {yytext();}
{ProgName}              {System.out.println(new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn, yytext()));}
.                       {}
\n 						{}