# mathdoku

## Purpose
Work with exploring state space.

## Background
Given a square n x n grid, within the grid, each cell is identified as part of some grouping. Each grouping is a connected set of 1 or more cells and each grouping is given an operator and a result of the operator. 

The task is to put the integers 1 to n into the cells of the grid so that:
* No integer appears twice in any row
* No integer appears twice in any column
* Applying the operator to all the values in one grouping gives the result assigned to the operator.

Valid operators are + for addition, - for subtraction, * for
multiplication, / for division, and = to specify that a grouping (of
one cell) has a given value. When applying operators, all results
must be integers. When applying the – and / operators, the
larger integer is always the leftmost operand of the operation.

## Problem
Write a class called “Mathdoku” that accepts accepts a puzzle and ultimately solves the puzzle.
5 main methods:
* boolean loadPuzzle(BufferedReader stream) – Read a puzzle in from the given stream of
data. Further description on the puzzle input structure appears below. Return true if
the puzzle is read. Return false if some other error happened.
* boolean readyToSolve( ) – determine whether or not we have all the information
needed to be able to try and solve the puzzle. If you have other tests to see if the puzzle
is at all solvable, test them too. Return true if we can call solve( ) and expect a
meaningful attempt at solving the puzzle. Return false if there is something missing or
amiss with the puzzle that is obvious (so not needing to solve the puzzle) and that would
prevent a solution. For example, if one of the groupings does not have a constraint
(operation or result) then we cannot solve the puzzle.
* boolean solve( ) -- Do whatever you need to do to find a solution to the puzzle. The
solution is stored within the class, ready to be retrieved. Return true if you solved the
puzzle and false if you could not solve the puzzle with the given set of words.
* String print( ) – print the current puzzle state to the returned string object.
* int choices( ) – return the number of guesses that your program had to make and later
undo while solving the puzzle.

## Sample Input
aabb<br>
cede<br>
cfeg<br>
ffhg<br>
a 3 –<br>
b 1 –<br>
c 2 /<br>
d 2 =<br>
e 5 +<br>
f 9 *<br>
g 2 /<br>
h 4 =<br>

## Sample Output
1423\n4231\n2314\n3142\n

This is an example puzzle from mathdoku.com
