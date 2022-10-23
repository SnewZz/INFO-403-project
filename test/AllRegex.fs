BEGIN
END

,
:=
(
)
-
+
*
/

IF
THEN
ELSE
PRINT
READ

=
>
<

WHILE
DO

::Varnames tests:
::---------------
varname
v
v42
v42v
vv42vv
42var ::"42" should be number then "var" should be a variable

::Numbers tests:
::--------------
154
99999909999
16548956
012 ::should be number "0" then number "12" 
1 5

::Comments:
::---------
::Comment (should be skipped)
SpacerAsProgName ::should be a Progname
%%Long Comment (should be skipped)%%
SpacerAsProgName2 ::should be a Progname
%% (should be skipped)
Multiline comment (should be skipped)
%%
SpacerAsProgName3 ::should be a Progname

::ProgName:
::---------
P ::should be skipped
PROGNAME ::should be skipped
ProgName
PrOGNAMEE
P23OGNAME :: "P" should be skipped then "23" as number then OGNAME skipped
PrOGNAME42
P24ogName
