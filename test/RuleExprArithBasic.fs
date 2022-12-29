BEGIN Factorial

var := 5,
exparith1 := var, :: 5

exparith2 := 1 + 2, :: 3
exparith3 := 1 - 2, :: -1
exparith4 := 1 * 2, :: 2
exparith5 := 1 / 2, :: 0.5

exparith2 := 1 + var, :: 6
exparith3 := 1 - var, :: -4
exparith4 := 1 * var, :: 5
exparith5 := 1 / var, :: 0.2

exparith2 := var + 2, :: 7
exparith3 := var - 2, :: 3
exparith4 := var * 2, :: 10
exparith5 := var / 2, :: 2.5

exparith2 := var + var, :: 10
exparith3 := var - var, :: 0
exparith4 := var * var, :: 25
exparith5 := var / var, :: 1

exparith2 := var + var + 1, :: 11
PRINT(exparith2),
exparith3 := var - var - 2, :: 2
PRINT(exparith3),
exparith4 := var * var * 3, :: 75
PRINT(exparith4),
exparith5 := var / var / 4, :: 5
PRINT(exparith5),

END