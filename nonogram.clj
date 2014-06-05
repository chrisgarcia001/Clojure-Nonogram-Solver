;; ----------------------------------------------------------------------------------------------------------
;; Author: C. Garcia
;; About: This is a nonogram-solver, and is a solution to P98 puzzle on 99 Prolog Problems:
;;        http://www.ic.unicamp.br/~meidanis/courses/mc336/2006s2/funcional/L-99_Ninety-Nine_Lisp_Problems.html
;;        More can be read about nonograms at http://en.wikipedia.org/wiki/Nonogram.
;;
;; Usage: This provides two Clojure function forms: 
;;        1. Solves a nonogram and returns an x-o list of lists - each inner list is a line, x = blackened cell
;;           o = blank cell.
;;           Form 1 Usage: (nonogram <specification of rows> <spec. of columns> <width> <height>)
;;           Example Form 1 Usage: (nonogram '((4) (1) (1) (4)) '((1 1) (1 2) (2 1) (1 1)) 4 4) 
;;                                 => ((x x x x) (o o x o) (o x o o) (x x x x))
;;           **NOTE: rows/columns with no lines should be an empty list
;;
;;        2. Prints a nonogram to the console
;;           Form 2 Usage: (print-nonogram <specification of rows> <spec. of columns> <width> <height>)
;;           Example Form 2 Usage: (print-nonogram '((4) (1) (1) (4)) '((1 1) (1 2) (2 1) (1 1)) 4 4)
;;                                  =>
;;                                      ****
;;                                        *
;;                                       *
;;                                      ****
;; ----------------------------------------------------------------------------------------------------------


;; --------------------------- Part 1: Some basic list-processing utility functions -------------------------

;; Standard fzip.
(defn fzip [f matrix]
  (cond (empty? (first matrix)) []
        true (cons (apply f (map first matrix)) (fzip f (map rest matrix)))))

;; Standard transpose.
(defn transpose [matrix]
  (fzip (fn [& elts] elts) matrix))

;; Flatten a collection.
(defn flatten [exp]
  (cond (empty? exp) '()
        (or (seq? (first exp)) (vector? (first exp))) (concat (flatten (first exp)) (flatten (rest exp)))
        true (cons (first exp) (flatten (rest exp)))))

;; Get the parts of a list other than first and last elements.
(defn inner [lst]
  (if (< (count lst) 3) '() (take (- (count lst) 2) (rest lst))))

;; Weave two lists together interleving their elements to the extent possible.
(defn weave [a b]
  (cond (empty? a) b
        (empty? b) a
        true (concat (list (first a) (first b)) (weave (rest a) (rest b)))))

;; Any true for f in lst?
(defn any? [f lst]
  (if (empty? lst) false 
      (reduce #(or %1 %2) (map f lst))))

;; Take the first value (f k) for k in lst that is not nil or false.
;; Example: (take-first #(if (> %1 3) %1 false) '(1 2 3 4 5 6 7)) => 4
(defn take-first [f lst]
  (if (empty? lst) false
        (let [soln (f (first lst))] (if soln soln (take-first f (rest lst))))))

;; Cluster adjacent identical symbols in a list.
;; Example: (cluster '(a a a b a b b a b b c c a)) => ((a a a) (b) (a) (b b) (a) (b b) (c c) (a))
(defn cluster [lst]
  (if (< (count lst) 2) (list lst)
        (let [rst (cluster (rest lst))]
          (if (= (first lst) (first (first rst))) 
              (cons (cons (first lst) (first rst)) (rest rst))
              (cons (list (first lst)) rst)))))

;; --------------------------- Part 2: Core supporting functions -------------------------

;; Construct a list of lists, where each list has elts length. The outer list contains all possible lists of numbers
;; of length elts containing numbers ranginging from 0 to n that sum to n.
;; Example: (sum-combs 2 5) => ((0 5) (1 4) (2 3) (3 2) (4 1) (5 0))
(defn sum-combs [elts n]
  (cond (= elts 1) (list (list n))
        (<= n 0) (list (repeat elts 0))
        true (let [rng (range 0 (+ n 1))
                   subs (map #(sum-combs (- elts 1) (- n %1)) rng) ;; Example: (((a b c) (d e f)) ((g h i) (j k l)) ... )
                   concater (fn [num lst] (map #(cons num %1) lst))
                   full-lists (fzip concater (list rng subs))]
               (apply concat full-lists))))

;; Given a list of x sizes and o sizes, where |osizes| = |xsizes| + 1,
;; construct the actual interleaved sequence of x's and o's.
;; Example: (decode '(1 4) '(3 2 3)) => (o o o x o o x x x x o o o)
(defn decode [xsizes osizes]
  (let [xs (map #(repeat %1 'x) xsizes)
        os (map #(repeat %1 'o) osizes)]
    (flatten (weave os xs))))

;; For a given configuration of x sizes and line length, generate all feasible combinations.
;; Examples: (gen-line-combs '(2 2) 6) => ((x x o x x o) (x x o o x x) (o x x o x x))
;;           (gen-line-combs '() 7) => ((o o o o o o o))
(defn gen-line-combs [xsizes n]
  (let [combs (sum-combs (+ (count xsizes) 1) (- n (reduce + xsizes)))
        good? (fn [comb] (not (any? #(= 0 %1) (inner comb))))]
    (map #(decode xsizes %1) (filter good? combs))))
    
;; A faster version of gen-line-combs.
(def line-combs 
  (memoize 
   (fn [xsizes n]
       (let [combs (sum-combs (+ (count xsizes) 1) (- n (reduce + xsizes)))
             good? (fn [comb] (not (any? #(= 0 %1) (inner comb))))]
         (map #(decode xsizes %1) (filter good? combs))))))

;; Do the clusters of x's and o's match thus far?
;; Examples: (cluster-test '((x x) (o) (x x x) (o)) '(2 3)) => true
;;           (cluster-test '((x x) (o) (x x x) (o)) '(2 3 2)) => true (because this column may not be complete
;;           (cluster-test '((o) (x x) (o) (x x x x) (o)) '(2 3 2))
(defn cluster-test [clusts xsizes]
  (cond (empty? clusts) true
        (and (empty? xsizes) (any? #(= %1 'x) (flatten clusts))) false
        (> (count clusts) (+ (* (count xsizes) 2) 1)) false
        (not (<= (count (filter #(= (first %1) 'x) clusts)) (count xsizes))) false
        (= (first (first clusts)) 'o) (cluster-test (rest clusts) xsizes)
        (and (= (count clusts) 1) (= (first (first clusts)) 'x)) 
            (if (> (count (first clusts)) (first xsizes)) false true)
        (and (= (first (first clusts)) 'x) (= (first xsizes) (count (first clusts)))) 
            (cluster-test (rest clusts) (rest xsizes))
        true false))
                        
;; Is a column or partial column consistent?
;; Examples: (consistent? '(o o x x o x x x o) '(2 3)) => true
;;           (consistent? '(o o x x o x x x o) '(2 4)) => false
(def consistent? 
  (memoize (fn [col xsizes] (cluster-test (cluster col) xsizes))))

;; --------------------------- Part 3: Main functions -------------------------

;; Builds the nonogram. Rows is the currently feasible rows. This is an
;; auxilary function to be used by the final nonogram function.
(defn build-nonogram [horiz-sizes vert-sizes width height rows]
  (if (= height 0) rows
      (let [combs (line-combs (first horiz-sizes) width)
            all-consistent? (fn [cols] (not (any? #(not %1) (fzip #(consistent? %1 %2) (list cols vert-sizes)))))     
            comb-consistent? (fn [cmb] (all-consistent? (transpose (concat rows (list cmb)))))]
        (take-first #(if (comb-consistent? %1) (build-nonogram (rest horiz-sizes) vert-sizes width (- height 1) (concat rows (list %1))) nil) 
                    combs))))

;; ---- MAIN NONOGRAM SOLVER FUNCTION ----
;; Given a list of horizontal specifications, vertical specifications, and a width & height,
;; construct a nonogram.
;; Example: Consider a Z as follows;
;;           ****
;;             *
;;            *
;;           ****
;; Going from top row to bottom row the line-length patterns are: 4, 1, 1, 4
;; Going from left to right starting from top to bottom, the vertical line-length patterns are: 1-1, 1-2, 2-1, 1-1
;; The width and height dimensions are 4 x 4.
;; This nonogram is solved by the function below as follows: 
;;     (nonogram '((4) (1) (1) (4)) '((1 1) (1 2) (2 1) (1 1)) 4 4) => ((x x x x) (o o x o) (o x o o) (x x x x))
;; **NOTE: x's are blackened cells, o's are blank cells.
(defn nonogram [horiz-sizes vert-sizes width height]
  (build-nonogram horiz-sizes vert-sizes width height '()))

;; --------------------------- Part 4: Nonogram printing functions -------------------------
;; This solves and prints a nonogram.
(defn print-nonogram [horiz-sizes vert-sizes width height]
  (let [soln (nonogram horiz-sizes vert-sizes width height)
        to-print-cmd (fn [lst] (concat (map #(list 'print (if (= %1 'x) "*" " ")) lst) '((print "\n"))))
        cmds (concat '((println "\n")) (apply concat (map to-print-cmd soln))  '((println "\n")))]
    (apply (fn [& args] (eval (cons 'do args))) cmds)))

 