# test-test

A Clojure library designed to use genetic programming in order to create a program that is able to play well the card game of Whist

## Usage

please find basic instructions in the file "test-test.generations"
to run a quick game of Whist, use the functions (play-whist pl1 pl2 ) and (play-verbose pl1 pl2), where pl1 and pl2 are two player programs
to run the genetic programming framework, use the function (genetic-programming) with the following parameters:

Parameters for the function (genetic-programming) in order
population size
number of generations
depth of mutation in each generation
number 1 size of player function: recommended value 3
number 2 size of player function: recommended value 3
number 3 size of player function: recommended value 3
number 4 size of player function: recommended value 3
number 5 size of player function: recommended value 3
number 6 size of player function: recommended value 3
number 7 size of player function: recommended value 3


IN ORDER TO COMPARE TWO GENERATIONS
use the function (compare-generations) with two numerical parameters:
parameter 1: number of generation to be compared e.g. 10
parameter 2: number of generation to be compared e.g. 50

example call: (compare-generations 10 50)

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
