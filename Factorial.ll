; This is the code corresponding to the Factorial program.
;--------------------------------------------
;First, we define read and print functions

@.strR = private unnamed_addr constant [3 x i8] c"%d\00", align 1 

; Function Attrs: nounwind uwtable 
	define i32 @readInt() #0 { 
	%x = alloca i32, align 4 
	%1 = call i32 (i8*, ...) @__isoc99_scanf(i8* getelementptr inbounds ([3 x i8], [3 x i8]* @.strR, i32 0, i32 0), i32* %x) 
	%2 = load i32, i32* %x, align 4 
	ret i32 %2 
} 

declare i32 @__isoc99_scanf(i8*, ...) #1 
@.strP = private unnamed_addr constant [4 x i8] c"%d\0A\00", align 1 

; Function Attrs: nounwind uwtable 
define void @println(i32 %x) #0 { 
	%1 = alloca i32, align 4 
	store i32 %x, i32* %1, align 4 
	%2 = load i32, i32* %1, align 4 
	%3 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.strP, i32 0, i32 0), i32 %2) 
	ret void  
} 

declare i32 @printf(i8*, ...) #1 

;--------------------------------------------
;Then, we define the main function
;==============================================================================

define i32 @main() {
;READ (%number = stdin)
;--------------------------------------------
	%number = alloca i32
	%v0 = call i32 @readInt()
	store i32 %v0, i32* %number
;ASSIGN (result := node(1))
;--------------------------------------------
	%result = alloca i32
	store i32 1, i32* %result

;IF 
;--------------------------------------------
	br label %if0
    if0:
;CONDITION (%v1 = node(number) > node(-)) 
	%v2 = load i32, i32* %number
	;UNARY SUBSTRACTION (%v3 = -1) 
	%v3 = sub i32 0, 1
	%v1 = icmp sgt i32 %v2, %v3
	br i1 %v1, label %if0true, label %if0false
;THEN 
    if0true:

;WHILE 
;--------------------------------------------
	br label %while0
    while0:
;CONDITION (%v4 = node(number) > node(0)) 
	%v5 = load i32, i32* %number
	%v4 = icmp sgt i32 %v5, 0
	br i1 %v4, label %while0true, label %while0end
;THEN 
    while0true:

;ASSIGN (result := node(*))
;--------------------------------------------
	;MULTIPLICATION (%v6 = result * number) 
	%v7 = load i32, i32* %result
	%v8 = load i32, i32* %number
	%v6 = mul i32 %v7, %v8
	store i32 %v6, i32* %result

;ASSIGN (number := node(-))
;--------------------------------------------
	;SUBSTRACTION (%v9 = number - 1) 
	%v10 = load i32, i32* %number
	%v9 = sub i32 %v10, 1
	store i32 %v9, i32* %number

	br label %while0
;END 
    while0end:

	br label %if0end
;ELSE 
    if0false:

;ASSIGN (result := node(-))
;--------------------------------------------
	;UNARY SUBSTRACTION (%v11 = -1) 
	%v11 = sub i32 0, 1
	store i32 %v11, i32* %result

	br label %if0end
;END 
    if0end:

;PRINT (stdout = %result)
;--------------------------------------------
	%v12 = load i32, i32* %result
	call void @println(i32 %v12)
ret i32 0
}
