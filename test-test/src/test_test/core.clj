(ns test-test.core 
  (:use test-test.funcs)
  (:use clojure.set))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn construct-player
  "combines trees into a map that is used to represent an individual player"
 [discard peek mode choice lead dolead respond] 
 {:discard discard :peek peek :mode mode :choice choice :lead lead :dolead dolead :respond respond})

(defn do-play
  "calls a play for the appropriate game stage from the player object"
  [player stage situation]
  ( (player stage) situation ))

;; here some constants are defined.
;; Some of them reappear as terminals inside the players,
;; others are used in the Whist game-playing mechanic
(def suits (list 'Spades 'Hearts 'Diamonds 'Clubs ))
(def values (list 2 3 4 5 6 7 8 9 10 'Jack 'Queen 'King 'Ace ))
(def allmodes (list 'misere 'no-trumps 'choice 'spades-trumps))
(def choices (list 'misere 'no-trumps 'spades-trumps 'hearts-trumps 'clubs-trumps 'diamonds-trumps))
(def null-card {:suit nil :value 0})
(def misere 'misere)
(def choice 'choice)

(defn get-trump-from-mode
  "infers the current trumping suit from the game mode"
  [mymode]
  (cond (= mymode 'spades-trumps) 'Spades
        (= mymode 'hearts-trumps) 'Hearts
        (= mymode 'clubs-trumps) 'Clubs
        (= mymode 'diamonds-trumps) 'Diamonds
        (= mymode 'misere) 'None
        (= mymode 'no-trumps) 'None
    ))

(defn produce-deck 
  "creates a sequence of map types corresponding to cards"
  []
   (concat (for [x suits y values] 
             {:suit x :value y}) ))


;; the terminals that are used to create players
(def suit-term [''Spades ''Hearts ''Clubs ''Diamonds 'trump ])
(def boole-term [true false 'misere])
(def many-term ['hand 'discard ])
(def arith-term [1 2 3 4 5 6 7 8 9 10 11 12 13 14 ])
(def terminals {:card ['play] :suit suit-term :boole boole-term :many-cards many-term :arith arith-term
                :trump ['trump] :misere ['misere] :hand ['hand] :discard [6] :modes ['modes] :return-hand ['hand]})

(def term-index
  (list '(:suit ''Spades ''Hearts ''Clubs ''Diamonds 'trump)
        '(:card 'play)
        '(:trump 'trump)
        '(:misere 'misere)
        '(:hand 'hand)
        '(:discard 6)
        '(:modes 'modes)
        '(:return-hand 'hand)
        '(:many-cards 'hand 'discard)
        '(:boole true false 'misere)
        '(:arith 1 2 3 4 5 6 7 8 9 10 11 12 13 14)))

(defn create-module
  "creates a tree structure from sets of functions and terminals
   functions in the form: {:type [fn1 [arg1 arg2...]...] ...}"
  [depth type]
  (cond (zero? depth) (rand-nth (terminals type))
        :else (let [[fun args] (rand-nth (functions type))]
                (cons fun
                      (for [x (range (count args))]
                        (create-module (dec depth) (nth args x)))))))
; ----------------------------->>>>>>>>>>>> TODO <<<<<<<<<<<<<<<< ----------------------------

(defn compile-module
  "creates an actual callable function from tree
   the resulting function gets 'situation' as parameter
   any parameters required by tree are extracted from 'situation'"
  [tree]
  (eval (list 'fn ['situation]
              (list 'let 
                    ['play (list 'situation :play)
                    'hand (list 'situation :hand)
                    'discard (list 'situation :discard)
                    'misere (list 'situation :misere)
                    'trump (list 'situation :trump)]
              tree))))

(defn compile-respond
  "use this to get a 'respond' function instead of above"
  [tree]
  (eval (list 'fn ['situation]
                     (list 'let 
                           ['play (list 'situation :play)
                           'trump (list 'situation :trump)
                           'discard (list 'situation :discard)
                           'misere (list 'situation :misere)
                           'hand (list 'get-suits (list 'situation :hand) (list 'play :suit) 'trump)]
                     tree)) ))

(defn compile-mode 
  "use this to create a funciton to choose modes or choices from a tree"
  [tree]
  (eval (list 'fn ['situation 'modes]
                     (list 'let 
                           ['play (list 'situation :play)
                           'hand (list 'situation :hand)
                           'discard (list 'situation :discard)
                           'misere (list 'situation :misere)
                           'trump (list 'situation :trump)]
                     tree) )))

(defn deal-hand
  "creates a list of n cards with the rest of the deck as the final list element
   use 'butlast' to extract only the hand"
  [deck n]
  (if (= 1 n) (let [card (rand-nth deck)]
                (cons card (list (remove (fn [x] (= x card)) deck))))
  (let [card (rand-nth deck)]
    (cons card (deal-hand (remove (fn [x] (= x card)) deck) (dec n)) ) )))

(defn remove-card
  "returns the set of cards without the indicated card"
  [card card-set]
  (remove #{card} card-set))

(defn remove-mode
  "removes the given mode from the stack of modes"
  [mode modes-stack]
  (remove #{mode} modes-stack))

(defn try-catch
  "tries player-function and catches exceptions, if any.
   Applies the supplied function to check if the returned results are in correct format and of correct type"
  ([player-func situation type-check-func]
  (let [result
    (try (player-func situation)
      (catch RuntimeException E -1)
      (catch Exception X -1))]
    (cond (= result -1) -1
      (type-check-func result) result
      :else -1)))
([player-func situation mymodes type-check-func]
  (let [result
    (try (player-func situation mymodes)
      (catch RuntimeException E (print (.getMessage E)) -1)
      (catch Exception X (print (.getMessage X)) -1))]
    (cond (= result -1) -1
      (type-check-func result) result
      :else -1))) )

(defn iscard?
  "checks if item is a card"
  [card]
  (and (map? card)
        (contains? card :suit)
        (contains? card :value)
        (= (count card) 2)))

(defn isdiscard?
  "checks if supplied item is a set of six cards"
  [discard]
  (and (seq? discard)
       (= (count discard) 6)
       (reduce (fn [i j] (and i (iscard? j))) true discard)))

(defn isboole?
  "checks if item is boolean"
  [item]
  (instance? (class true) item))

(defn ismode?
  "checks if item is member of the set of modes defined for the game"
  [item]
  (not (nil? (some #{item} allmodes))))

(defn ischoice?
  "checks if item is member of the set of mode-choices defined for the game"
  [item]
  (not (nil? (some #{item} choices))))

(defn create-situation
  "uses supplied context to create a data structure which can be given to a player function to perform a move"
  [play1 hand1 discard1 misere1 trump1]
  {:play play1 :hand hand1 :discard discard1 :misere misere1 :trump trump1 })

(defn remove-group
  "removes a set of cards from a larger set of cards"
  [group items-set]
  (if (empty? items-set) ()
        (concat (if (some? (some #{(first items-set)} group)) () (list (first items-set)))
                  (remove-group group (rest items-set)) )))
