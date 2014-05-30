;; ----------------------------------------------------------------------------------------------------------
;; Author: Chris Garcia (cgarcia@umw.edu)
;; This provides a few sample usages of the nonogram solver. The examples below illustrate this.
;; ----------------------------------------------------------------------------------------------------------

(load-file "nonogram.clj")

;; Draws a small, random-looking image.
(let [rows '((3) (2 1) (3 2) (2 2) (6) (1 5) (6) (1) (2))
      cols '((1 2) (3 1) (1 5) (7 1) (5) (3) (4) (3))]
  (print-nonogram rows cols (count cols) (count rows)))

;; Draws a soccer player with a ball. May take a minute.
(let [rows '((3) (5) (3 1) (2 1) (3 3 4) (2 2 7) (6 1 1) (4 2 2) (1 1) (3 1) (6) (2 7) (6 3 1) (1 2 2 1 1) (4 1 1 3) (4 2 2) (3 3 1) (3 3) (3) (2 1))
      cols '((2) (1 2) (2 3) (2 3) (3 1 1) (2 1 1) (1 1 1 2 2) (1 1 3 1 3) (2 6 4) (3 3 9 1) (5 3 2) (3 1 2 2) (2 1 7) (3 3 2) (2 4) (2 1 2) (2 2 1) (2 2) (1) (1) )]
  (print-nonogram rows cols (count cols) (count rows)))
