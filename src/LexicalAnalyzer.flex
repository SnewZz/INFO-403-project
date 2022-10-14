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
