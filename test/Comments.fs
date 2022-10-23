BEGIN Factorial

:: everything below all the way to "IF" (line 36) should be skipped
:: except "thisshouldbevariable" (line 27)
::======================================================
::Comments with important keywords:
::---------------------------------
%%
  BEGIN IF READ PRINT ELSE DO WHILE THEN END
  Comments with blank line:
%%
::BEGIN IF READ PRINT ELSE DO WHILE THEN END

::======================================================
:: Various tests:
::---------------
%% TraditionalComment comment on one line %%
::Empty comments:
%%%%
%%
%%
::

::=======================================================
:: Ambiguous cases :
::------------------
%% %% thisshouldbevariable %% :: the 3rd percentages should start new comment

  READ(number) ,              :: another comment here
  result := 1 ,               %% ::This end the started comment above
:: So the "READ" and "result" lines should be skipped
:: So "result" should not be in the variable list
::=======================================================
:: rest of the code :

IF (number > -1) THEN
END
