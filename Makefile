all:
	jflex src/LexicalAnalyzer.flex
	javac -d bin -cp src/ src/Main.java
	jar cfe dist/part1.jar Main -C bin .

testing:
	java -jar dist/part1.jar test/Factorial.fs
	java -jar dist/part1.jar test/AllRegex.fs
	java -jar dist/part1.jar test/Comments.fs
	java -jar dist/part1.jar test/SortVariable.fs
