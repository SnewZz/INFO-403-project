all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/part3.jar Main -C bin .

basic:
	java -jar dist/part3.jar test/Factorial.fs -wt Factorial.tex

testing:
	java -jar dist/part3.jar test/Factorial.fs -wt Factorial.tex
	java -jar dist/part3.jar test/RuleAssignAndAtom.fs -wt RuleAssignAndAtom.tex
	java -jar dist/part3.jar test/RuleCode.fs -wt RuleCode.tex
	java -jar dist/part3.jar test/RuleExprArithBasic.fs -wt RuleExprArithBasic.tex
	java -jar dist/part3.jar test/RuleExprArithPriority.fs -wt RuleExprArithPriority.tex
	java -jar dist/part3.jar test/RuleIf.fs -wt RuleIf.tex
	java -jar dist/part3.jar test/RuleInstruction.fs -wt RuleInstruction.tex
	java -jar dist/part3.jar test/RulePrintAndRead.fs -wt RulePrintAndRead.tex
	java -jar dist/part3.jar test/RuleProgram_tooMuchCode.fs -wt RuleProgram_tooMuchCode.tex
	java -jar dist/part3.jar test/RuleProgram_unfinished.fs -wt RuleProgram_unfinished.tex
	java -jar dist/part3.jar test/RuleProgram.fs -wt RuleProgram.tex
	java -jar dist/part3.jar test/RuleWhileAndCond.fs -wt RuleWhileAndCond.tex