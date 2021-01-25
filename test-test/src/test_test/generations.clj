(ns test-test.generations
 (:use test-test.core)
 (:use test-test.funcs)
 (:use clojure.tools.trace)
 (require test-test.funcs))


;;;;;  ------------- TO COMPARE TWO GENERATIONS ---------------
;;;;; (compare-generations x y)
;;;;; where x and y are numbers for enerations e.g. 10th generation and 50th generation
;;;;; (compare-generations 10 50)

;; to play a game:
;;(play-verbose (compile-player (create-player-trees 2 2 2 2 2 2 2)) (compile-player (create-player-trees 2 2 2 2 2 2 2)))

;;; to run a genetic algorithm
;;; (genetic-programming 10 5 2 2 2 2 2 2 2 2)
;;; to store the winning player
;;; (def mywinner (first (genetic-programming 100 20 2 2 2 2 2 2 2 2)))

;; for testing purposes:
(def mysit (let [temp (deal-hand (produce-deck) 12)] {:discard nil :hand (butlast temp) :play null-card :trump 'Hearts :misere false} ))
;; for testing purposes
(def mysit2 {:play {:suit nil :value 0} :hand '({:suit 'Clubs :value 6} {:suit 'Diamonds :value 2} {:suit 'Diamonds :value 9} {:suit 'Diamonds :value 5} {:suit 'Spades :value 'Ace} {:suit 'Spades :value 3} {:suit 'Clubs :value 10} {:suit 'Hearts :value 6} {:suit 'Clubs :value 'King} {:suit 'Clubs :value 5} {:suit 'Clubs :value 7} {:suit 'Clubs :value 4}) :discard () :misere false :trump nil})

(defn log2 [n]
  "mathematical logarithm function with base two"
  (/ (Math/log n) (Math/log 2)))

(defn create-player-trees
  "generates random trees for player-datastructure"
  [a b c d e f g]
  (construct-player (create-module a :discard)
                    (create-module b :boole)
                    (create-module c :mode)
                    (create-module d :choice)
                    (create-module e :boole)
                    (create-module f :card)
                    (create-module g :card)
                    ))

(defn compile-player
  "takes a data structure with trees and outputs its corresponding functional player"
     [player-tree]
     (let [mydiscard (compile-module (player-tree :discard))
           mypeek (compile-module (player-tree :peek))
           mymode (compile-mode (player-tree :mode))
           mychoice (compile-module (player-tree :choice))
           mylead (compile-module (player-tree :lead))
           mydolead (compile-module (player-tree :dolead))
           myrespond (compile-respond (player-tree :respond))]
       {:discard mydiscard :peek mypeek :mode mymode :choice mychoice :lead mylead :dolead mydolead :respond myrespond}))

;; a macro that allows to quickly start a game of Whist
(defn play-whist [pl1 pl2] (test_test.WhistGame/playWhist pl1 pl2 false))

;; if this macro is used, the game will print information to the console regarding players' moves
(defn play-verbose [pl1 pl2] (test_test.WhistGame/playWhist pl1 pl2 true))

;; to play a game:
;;(play-verbose (compile-player (create-player-trees 2 3 3 3 3 5 5)) (compile-player (create-player-trees 2 3 3 3 3 5 5)))

(defn best-of-three
  "stages three games between two players and decides on the winner"
  [pl1 pl2]
  (let [match1 (play-whist pl1 pl2)
        match2 (play-whist pl1 pl2)]
    (cond (or (= 0 match1) (= 0 match2)) (let [match3 (play-whist pl1 pl2)]
                                           (if (= (+ match1 match2) match3)
                                             match3
                                             0))
          :else (if (= match1 match2) match1 (play-whist pl1 pl2))
      )))

(defn create-generation
  "create a list of randomly assembled players"
  [pop-size a b c d e f g]
  (if (<= pop-size 0)
    (list (create-player-trees a b c d e f g))
    (concat (list (create-player-trees a b c d e f g))
            (create-generation (dec pop-size) a b c d e f g) )))

(defn ^:dynamic tournament-round
  "takes a collection of player-trees, lets pairs of players play against each other.
   If the number of players is odd, one player qualifies automatically"
  [generation]
  (cond (empty? generation) '()
        (= 1 (count generation)) (list 1)
        :else (concat
                (list (best-of-three (compile-player (first generation))
                                  (compile-player (second generation))))
                (tournament-round (rest (rest generation))))))

(defn ^:dynamic extract-winners
  "get the winners of a round of tournament in a list based on the array of results"
  [results generation]
  (cond (empty? results) generation
        (= 1 (count generation)) generation
        (= 0 (first results)) (concat (list (first generation)
                                            (second generation))
                                      (extract-winners (rest results) (rest (rest generation))))
        (= 1 (first results)) (concat (list (first generation))
                                      (extract-winners (rest results) (rest (rest generation))))
        (= 2 (first results)) (concat (list (second generation))
                                      (extract-winners (rest results) (rest (rest generation))))))

(defn ^:dynamic host-tournament
  "hosts a tournament and returns a list containing the winner(s)"
  [generation rounds-num]
  (cond (>= 0 rounds-num) generation
        (= 1 (count generation)) generation
        :else (host-tournament
                (extract-winners (tournament-round generation)
                                 generation)
                (dec rounds-num))))

(defn identify-term
  "finds the return type of a leaf of a tree"
  ([term]
  (identify-term term 0))
  ([term n]
  (cond (= n (- (count term-index) 1)) (if (some? (some #{term} (nth term-index n)))
                   (first (nth term-index n))
                   nil)
        :else (if (some? (some #{term} (nth term-index n)))
                   (first (nth term-index n))
                   (identify-term term (inc n)) ))))

(defn identify-sub
  "finds the return-type of a given sub-tree"
  ([sub-tree]
  (identify-sub sub-tree 0))
  ([sub-tree n]
  (cond (= n (- (count functions-return-type-index) 1)) (if
                                                          (some? (some #{(first sub-tree)}
                                                                       (nth functions-return-type-index n)))
                         (first (nth functions-return-type-index n))
                         (identify-term (first sub-tree)))
        :else (if (some? (some #{(first sub-tree)} (nth functions-return-type-index n)))
                   (first (nth functions-return-type-index n))
                   (identify-sub sub-tree (inc n)) ))))

(defn max-tree-height
  "Find the maximum height of a tree. The max height is the distance from the root to the
   deepest leaf."
  [tree]
  (if (not (seq? tree)) 0
    (+ 1 (reduce max (map max-tree-height tree)))))

(defn rand-subtree
  "Return a random subtree of a player component. Takes an optional second parameter that limits
   the depth to go before selecting a crossover point."
  ([tree]
    (rand-subtree tree (rand-int (inc (max-tree-height tree)))))
  ([tree n]
    (if (or (zero? n) (and (seq? tree) (= (count tree) 1)) ;; don't split up (leaf)
                           (not (seq? tree))) tree
      (recur (rand-nth (rest tree))
             (rand-int n)))))

(defn replace-subtree
  "Replace a random subtree with subtree of depth x, with fitting return type. Takes an optional second parameter
   that limits the depth to go before selecting a crossover point."
  ([tree x]
    (replace-subtree tree x (rand-int (+ 1 (max-tree-height tree)))))
  ([tree x n]
    (if (or (zero? n) (and (seq? tree) (= (count tree) 1)) ;; don't split up (leaf)
                           (not (seq? tree))) (let
                                                [type (if (seq? tree) 
                                                        (identify-sub tree) 
                                                        (identify-term tree))]
                                                (if (nil? type) tree (create-module x type)))
      (let [r (+ 1 (rand-int (count (rest tree))))]
        (concat (take r tree)
                (list (replace-subtree
                        (nth tree r) x
                        (rand-int n)))
                (nthrest tree (inc r)))))))

(defn sub-tree-of-type
  "returns a subtree of given type from given tree
   gives up after n attempts."
  [tree type n]
  (if (> 0 n) nil
    (let
      [mysub (rand-subtree tree)]
      (if (= type (identify-sub (if (seq? mysub) mysub '(mysub)))) mysub
        (sub-tree-of-type tree type (dec n))))))

(defn crossover
  "tries to cross over two trees.
   If it fails to extract a fitting subtree for crossover, inserts terminal instead
   x = number of attempts to extract subtree
   n = depth of crossover insertion point"
  ([tree tree2 x]
    (crossover tree tree2 x (rand-int (+ 1 (max-tree-height tree)))))
  ([tree tree2 x n]
  (if (or (zero? n) (and (seq? tree) (= (count tree) 1)) ;; don't split up (leaf)
                           (not (seq? tree))) (let
                                                [type (if (seq? tree) 
                                                        (identify-sub tree) 
                                                        (identify-term tree))]
                                                (let
                                                  [sub (sub-tree-of-type tree2 type x)]
                                                  (if (nil? sub) (rand-nth (terminals type))
                                                    sub)))
      (let [r (+ 1 (rand-int (count (rest tree))))]
        (concat (take r tree)
                (list (replace-subtree
                        (nth tree r) x
                        (rand-int n)))
                (nthrest tree (inc r)))))))

(defn crossover-players
  "applies the crossove rmutation method to the components of two players, producing a single 'child' player
   x = numebr of attempts to crossover"
  [player1 player2 x]
  (construct-player (crossover (player1 :discard) (player2 :discard) x)
                    (crossover (player1 :peek) (player2 :peek) x)
                    (crossover (player1 :mode) (player2 :mode) x)
                    (crossover (player1 :choice) (player2 :choice) x)
                    (crossover (player1 :lead) (player2 :lead) x)
                    (crossover (player1 :dolead) (player2 :dolead) x)
                    (crossover (player1 :respond) (player2 :respond) x)
                    ))

(defn mutate-player
  "mutates every component of a player data structure by means of mutation"
  [player1 x]
  (construct-player (replace-subtree (player1 :discard) x)
                    (replace-subtree (player1 :peek) x)
                    (replace-subtree (player1 :mode) x)
                    (replace-subtree (player1 :choice) x)
                    (replace-subtree (player1 :lead) x)
                    (replace-subtree (player1 :dolead) x)
                    (replace-subtree (player1 :respond) x)
                    ))

(defn ^:dynamic mutate-generation
  "x = population of new generation
   n = depth of expansion
   in order to prevent de-evolution, the last generation is completely preserved"
  [generation x n]
  (cond (>= 0 x) generation
        (empty? generation) '()
        (= 1 (count generation)) (concat
                                (list (mutate-player (rand-nth generation) n))
                                (mutate-generation generation (dec x) n))
        (> 0.5 (Math/random)) (concat
                                (list (mutate-player (rand-nth generation) n))
                                (mutate-generation generation (dec x) n))
        :else (concat
                (list (crossover-players (rand-nth generation) (rand-nth generation) n))
                (mutate-generation generation (dec x) n))))


;;; (genetic-programming 1024 50 2 3 3 3 3 3 5 5)
(defn ^:dynamic genetic-programming
  "q = number of generations
   n = depth of expansion on each generation
   records the winners of each tenth generation"
  ([pop-size q n a b c d e f g]
    (genetic-programming (create-generation pop-size a b c d e f g) q n))
  ([generation q n]
  (cond (>= 0 q) (host-tournament generation (+ 1 (log2 (count generation))))
;        (= 0 (mod q 10)) (let [winners (host-tournament generation (+ 1 (log2 (count generation))))] 
;                           (print "completed 10th gen")
;                           (concat winners
;                                   (genetic-programming (mutate-generation winners (count generation) n)
;                                                        (dec q) n)))
        :else (genetic-programming
               (mutate-generation
                 (host-tournament generation (+ 1 (log2 (count generation))))
                 (count generation) n)
               (dec q) n)
    )))

(defn ^:dynamic win-percentages
  [player1 name1 player2 name2 round-n win1 win2]
  (let [result (best-of-three player1 player2)]
  (cond (= 0 round-n)
        (print "\ngeneration " name1 " won " win1 " out of 100 times\ngeneration " name2 " won " win2 " out of 100 times\n")
        (= 1 result)
        (win-percentages player1 name1 player2 name2 (dec round-n) (inc win1) win2)
        (= 2 result)
        (win-percentages player1 name1 player2 name2 (dec round-n) win1 (inc win2))
        :else (win-percentages player1 name1 player2 name2 (dec round-n) win1 win2)
        )))

(defn ^:dynamic compare-generations
  ([gen1 gen2]
  (let [gen1-winner (compile-player (first (genetic-programming 100 gen1 2 2 2 2 2 2 2 2)))
        gen2-winner (compile-player  (first (genetic-programming 100 gen2 2 2 2 2 2 2 2 2)))]
    (win-percentages gen1-winner gen1 gen2-winner gen2 100 0 0) )))


(defn create-statistics
  [n]
  (cond (>= 10 n) (print (compare-generations n 5) (compare-generations 5 n))
        :else (let
                [m (- n 5)]
                (compare-generations n 5)
                (compare-generations 5 n)
                (create-statistics m))))






















