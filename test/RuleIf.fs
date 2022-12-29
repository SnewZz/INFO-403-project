BEGIN Factorial

a := 2,
b := 1,
IF( a > b) THEN END,

IF( a > b) THEN
PRINT(a), :: 2
END,

IF( a > b) THEN ELSE END,

IF( a < b) THEN
PRINT(b), :: 1
ELSE
PRINT(a), :: 2
END,

IF( a = b) THEN
PRINT(a), :: 2
ELSE
PRINT(b), :: 1
END,

:: should print 2 2 1

END