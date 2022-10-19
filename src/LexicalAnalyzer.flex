%%

%class Lexer
%unicode
%line
%column
%type Symbol  	
%standalone	

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
TraditionalComment = %% [^*] %% // "%%" [^*] ~"%%"
Comment = {ShortComment}|{TraditionalComment} /* Nested comment :  https://stackoverflow.com/questions/24666688/jflex-match-nested-comments-as-one-token  */

%%


"BEGIN"     {System.out.println(new Symbol(LexicalUnit.BEGIN, yyline, yycolumn, yytext());}
ProgName    {System.out.println(new Symbol(LexicalUnit.PROGNAME, yyline, yycolumn, yytext());}
"END"       {System.out.println(new Symbol(LexicalUnit.END, yyline, yycolumn, yytext());}

","         {System.out.println(new Symbol(LexicalUnit.COMMA, yyline, yycolumn, yytext());}
VarName     {System.out.println(new Symbol(LexicalUnit.VARNAME, yyline, yycolumn, yytext());}
":="        {System.out.println(new Symbol(LexicalUnit.ASSIGN, yyline, yycolumn, yytext());}
Number      {System.out.println(new Symbol(LexicalUnit.NUMBER, yyline, yycolumn, yytext());}
"("         {System.out.println(new Symbol(LexicalUnit.LPAREN, yyline, yycolumn, yytext());}
")"         {System.out.println(new Symbol(LexicalUnit.RPAREN, yyline, yycolumn, yytext());}
"-"         {System.out.println(new Symbol(LexicalUnit.MINUS, yyline, yycolumn, yytext());}
"+"         {System.out.println(new Symbol(LexicalUnit.PLUS, yyline, yycolumn, yytext());}
"*"         {System.out.println(new Symbol(LexicalUnit.TIMES, yyline, yycolumn, yytext());}
"/"         {System.out.println(new Symbol(LexicalUnit.DIVIDE, yyline, yycolumn, yytext());}

"IF"        {System.out.println(new Symbol(LexicalUnit.IF, yyline, yycolumn, yytext());}
"THEN"      {System.out.println(new Symbol(LexicalUnit.THEN, yyline, yycolumn, yytext());}
//ENDIF??
"ESLE"      {System.out.println(new Symbol(LexicalUnit.ELSE, yyline, yycolumn, yytext());}

"="         {System.out.println(new Symbol(LexicalUnit.EQUAL, yyline, yycolumn, yytext());}
">"         {System.out.println(new Symbol(LexicalUnit.GREATER, yyline, yycolumn, yytext());}
"<"         {System.out.println(new Symbol(LexicalUnit.SMALLER, yyline, yycolumn, yytext());}

"WHILE"     {System.out.println(new Symbol(LexicalUnit.WHILE, yyline, yycolumn, yytext());}
"DO"        {System.out.println(new Symbol(LexicalUnit.DO, yyline, yycolumn, yytext());}

//PRINT
//READ
//EOS