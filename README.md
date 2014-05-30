Clojure-Nonogram-Solver
===============

#### A small nonogram solver in Clojure

Nonograms are a well-known type of Japanese puzzle also known as "paint-by-number". You can read all about them at
[http://en.wikipedia.org/wiki/Nonogram](http://en.wikipedia.org/wiki/Nonogram)


Nonogram puzzles are based on an m X n grid. Each row and column have a set of number, where each such number specifies the length of a contiguous block of cells
inside that row or column. Thus, if you have a row with numbers [3, 5] this means that you will have a block of 3 cells filled out, followed by at least 1 space, 
and finally followed by a block of 5 cells filled out. For example, see Figure 1 [(image taken from here)](http://ravenspoint.wordpress.com/2010/06/15/the-great-nonogram-hunt/):

![Example Nonogram](/images/nonogram-img.png "Example Nonogram")

The solver is found in nonogram.clj and a usage few examples are given in sample-nonograms.clj. The solver can be executed in the REPL as follows
(these are the same examples in sample-nonograms.clj):

### Example 1: A random blob
```clojure
user=>
(load-file "nonogram.clj")

user=> 
(let [rows '((3) (2 1) (3 2) (2 2) (6) (1 5) (6) (1) (2))
      cols '((1 2) (3 1) (1 5) (7 1) (5) (3) (4) (3))]
  (print-nonogram rows cols (count cols) (count rows)))
```

This results in the following drawing:

```
 ***    
** *    
 ***  **
  **  **
  ******
* ***** 
******  
    *   
   **   
```

### Example 2: A soccer player
```clojure
user=>
(load-file "nonogram.clj")

user=> 
(let [rows '((3) (5) (3 1) (2 1) (3 3 4) (2 2 7) (6 1 1) (4 2 2) (1 1) (3 1) (6) (2 7) (6 3 1) (1 2 2 1 1) (4 1 1 3) (4 2 2) (3 3 1) (3 3) (3) (2 1))
      cols '((2) (1 2) (2 3) (2 3) (3 1 1) (2 1 1) (1 1 1 2 2) (1 1 3 1 3) (2 6 4) (3 3 9 1) (5 3 2) (3 1 2 2) (2 1 7) (3 3 2) (2 4) (2 1 2) (2 2 1) (2 2) (1) (1) )]
  (print-nonogram rows cols (count cols) (count rows)))

```

This results in the following drawing:

```   
          ***       
         *****      
         *** *      
         **  *      
      *** *** ****  
    **  **   *******
  ****** *   *      
 ****   **  **      
        *   *       
       ***  *       
       ******       
 **   *******       
******  *** *       
* **  ** *  *       
   ****  * *  ***   
        **** ** **  
        ***  *** *  
       ***    ***   
      ***           
      ** *          
```

### License

   Copyright (c) 2014 Chris Garcia

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.