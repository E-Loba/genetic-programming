(ns test-test.funcs)

;;; in this namespace, functions necessary for creating a player are stored
;;; the list of possible useful functions is not exhaustive

(defn parse-value
  [card misere]
  (cond (nil? card) 0
        (= (card :value) 2) 2 
        (= (card :value) 3) 3
        (= (card :value) 4) 4
        (= (card :value) 5) 5
        (= (card :value) 6) 6
        (= (card :value) 7) 7
        (= (card :value) 8) 8
        (= (card :value) 9) 9
        (= (card :value) 10) 10
        (= (card :value) 'Jack) 11
        (= (card :value) 'Queen) 12
        (= (card :value) 'King) 13
        (= (card :value) 'Ace) ( if misere 1 14 )))

(defn is-trump
  [card trump]
  (= (card :suit) trump))

(defn card-comparator
  [card]
  (parse-value card false))

(defn wins-by-value?
  [card card2 misere]
  (if (> (parse-value card misere) (parse-value card2 misere)) true false))

(defn lead-wins-against?
  [lead resp trump misere]
  (cond (and (is-trump lead trump) (is-trump resp trump)) (wins-by-value? lead resp misere)
        (and (not (is-trump resp trump)) (is-trump lead trump)) true
        (and (not (is-trump lead trump)) (is-trump resp trump)) false
        (not (= (lead :suit) (resp :suit))) true
        :else (wins-by-value? lead resp misere)))

(defn get-suit
  "returns cards of the same suit as a given card from a set of cards"
  [cards-set card]
  (if-let
    [cards (seq (if (nil? card) cards-set
             (filter (fn [x] (= (card :suit) (x :suit))) cards-set)))]
    cards
    cards-set))

(defn get-suits
  "returns cards of the same suit as a given card from a set of cards"
  [cards-set suit1 suit2]
  (if-let
    [cards (seq (if (and (nil? suit1) (nil? suit2)) cards-set
             (filter (fn [x]
                       (or (= suit1 (x :suit))
                           (= suit2 (x :suit)))) cards-set)))]
    cards
    cards-set))

(defn get-suit-2
  "returns cards of the same suit as a given card from a set of cards"
  [cards-set suit]
  (if-let
    [cards (seq (if (nil? suit) cards-set
             (filter (fn [x] (= suit (x :suit))) cards-set)))]
    cards
    cards-set))

(defn is-suit
  [card suit]
  ( if (or (nil? card) (nil? suit)) nil
  (= (card :suit) suit)))

(defn get-trumps
  [cards trump]
  (get-suit cards {:suit trump}))

(defn get-suit-num
  [cards suit]
  (if (nil? suit) 0
  (count (get-suit cards {:suit suit}))))

(defn get-trumps-num
  [cards trump]
  (count (get-trumps cards trump)))

(defn has-trump
  [cards trump]
  (not (zero? (count (get-trumps cards trump)))))

(defn is-high-card
  [card]
  (> (parse-value card false) 9))

(defn is-low-card
  [card]
  (< (parse-value card false) 6))

(defn high-cards
  [cards]
  (if-let [x (seq (filter is-high-card cards))]
    x
    cards))

(defn low-cards
  [cards]
  (if-let [x (seq (filter is-low-card cards))]
    x
    cards))

(defn high-cards-num
  [cards]
  (count (high-cards cards)))

(defn low-cards-num
  [cards]
  (count (low-cards cards)))

(defn has-high-cards
  [cards]
  (not (zero? (high-cards-num cards))))

(defn has-low-cards
  [cards]
  (not (zero? (low-cards-num cards))))

(defn select-mode
  [boole1 boole2 boole3 modes]
  (cond (= 1 (count modes)) (first modes)
        (= 2 (count modes)) (if boole1 (first modes) (last modes))
        (= 3 (count modes)) (cond boole1 (first modes)
                                  boole2 (second modes)
                                  :else (last modes))
        :else (cond boole1 (first modes)
                    boole2 (second modes)
                    boole3 (nth modes 2)
                    :else (last modes))
        ))

(defn select-suit
  [boole1 boole2 boole3 boole4]
  (cond boole1 'Spades
        boole2 'Hearts
        boole3 'Clubs
        boole4 'Diamonds))

(defn select-choice
  [boole1 boole2 boole3 boole4 boole5 boole6 boole7]
  (cond boole1 'misere
        boole2 'no-trumps
        boole3 (select-suit boole4 boole5 boole6 boole7 )))

(defn is-misere
  [misere]
  misere)

(defn get-most-suit
  [cards]
  (let [suits-count {:hearts (count (get-suit cards {:suit 'Hearts}))
                    :diamonds (count (get-suit cards {:suit 'Diamonds}))
                    :clubs (count (get-suit cards {:suit 'Clubs}))
                    :spades (count (get-suit cards {:suit 'Spades}))}
        most-suit (apply max (vals suits-count))]
    (cond (= most-suit (suits-count :hearts)) 'Hearts
          (= most-suit (suits-count :diamonds)) 'Diamonds
          (= most-suit (suits-count :spades)) 'Spades
          (= most-suit (suits-count :clubs)) 'Clubs)))

(defn get-most-suit-2
  [cards]
  (let [suits-count {:hearts (count (get-suit cards {:suit 'Hearts}))
                    :diamonds (count (get-suit cards {:suit 'Diamonds}))
                    :clubs (count (get-suit cards {:suit 'Clubs}))
                    :spades (count (get-suit cards {:suit 'Spades}))}
        most-suit (apply max (vals suits-count))]
    (cond (= most-suit (suits-count :hearts)) (get-suit-2 cards 'Hearts)
          (= most-suit (suits-count :diamonds)) (get-suit-2 cards 'Diamonds)
          (= most-suit (suits-count :spades)) (get-suit-2 cards 'Spades)
          (= most-suit (suits-count :clubs)) (get-suit-2 cards 'Clubs))))

(defn get-highest-from-suit
  [cards suit]
  (let [sorted (reverse (sort-by card-comparator (get-suit-2 cards suit)))]
    (first sorted)))

(defn get-lowest-from-suit
  [cards suit]
  (let [sorted (sort-by card-comparator (get-suit-2 cards suit))]
    (first sorted)))

(defn get-nth-lowest-from-suit
  [cards suit n]
  (if (>= n (count cards)) (get-highest-from-suit cards suit)
  (let [sorted (sort-by card-comparator (get-suit-2 cards suit))]
    (nth sorted n))))

(defn get-nth-highest-from-suit
  [cards suit n]
  (if (>= n (count cards)) (get-lowest-from-suit cards suit)
  (let [sorted (reverse (sort-by card-comparator (get-suit-2 cards suit)))]
    (nth sorted n))))

(defn take-n
  [deck n]
  (if (>= n (count deck)) deck
    (if (= 1 n) (seq [(rand-nth deck)])
  (let [card (rand-nth deck)]
      (cons card (take-n (remove (fn [x] (= x card)) deck) (dec n)) ) ))))

(defn return-trump
  [trump]
  trump)

(defn return-modes
  [mymodes]
  mymodes)

(defn return-hand
  [hand]
  hand)

(defn return-discard
  [cards1 cards2 cards3 cards4 cards5 cards6]
  (let [unicards1 (distinct (concat cards1 cards2))
        unicards2 (distinct (concat unicards1 cards3))
        unicards3 (distinct (concat unicards2 cards4))
        unicards4 (distinct (concat unicards3 cards5))
        unicards5 (distinct (concat unicards4 cards6))]
    (cond (< 6 (count cards1)) (take 6 cards1)
          (< 6 (count unicards1)) (take 6 unicards1)
          (< 6 (count unicards2)) (take 6 unicards2)
          (< 6 (count unicards3)) (take 6 unicards3)
          (< 6 (count unicards4)) (take 6 unicards4)
          (< 6 (count unicards5)) (take 6 unicards5))))

(defn discard-random
  [hand]
  (take-n hand 6))

(def play-func '[[get-highest-from-suit [:hand :suit]]
                 [get-lowest-from-suit [:hand :suit]]
                 [get-nth-highest-from-suit [:hand :suit :arith]]
                 [get-nth-lowest-from-suit [:hand :suit :arith]]
                 ])
(def card-func '[[get-highest-from-suit [:many-cards :suit]]
                [get-lowest-from-suit [:many-cards :suit]]
                [get-nth-highest-from-suit [:many-cards :suit :arith]]
                [get-nth-lowest-from-suit [:many-cards :suit :arith]]
                ])
(def mode-func '[[select-mode [:boole :boole :boole :modes]]
                 ])
(def suit-func '[[select-suit [:boole :boole :boole :boole]]
                 [get-most-suit [:many-cards]]
                 ])
(def boole-func '[[lead-wins-against? [:card :card :trump :misere]]
                  [wins-by-value? [:card :card :misere]]
                 [> [:arith :arith]]
                 [< [:arith :arith]]
                 [= [:arith :arith]]
                 [is-trump [:card :trump]]
                 [is-suit [:card :suit]]
                 [has-trump [:many-cards :trump]]
                 [is-high-card [:card]]
                 [is-low-card [:card]]
                 [has-high-cards [:many-cards]]
                 [has-low-cards [:many-cards]]
                  [is-misere [:misere]]
                  ])
(def many-func '[[get-suit [:many-cards :card]]
                [get-trumps [:many-cards :trump]]
                [high-cards [:many-cards]]
                [low-cards [:many-cards]]
                [get-suit-2 [:many-cards :suit]]
                [get-most-suit [:many-cards]]
                [take-n [:many-cards :arith]]
                [get-suits [:many-cards :suit :suit]]
                ])
(def hand-func '[[get-suit [:hand :card]]
                [get-trumps [:hand :trump]]
                [high-cards [:hand]]
                [low-cards [:hand]]
                 [get-suit-2 [:hand :suit]]
                 [get-most-suit-2 [:hand]]
                 [take-n [:hand :arith]]
                 [get-suits [:hand :suit :suit]]
                 ])
(def arith-func '[[get-suit-num [:many-cards :suit]]
                 [get-trumps-num [:many-cards :trump]]
                  [low-cards-num [:many-cards]]
                  [high-cards-num [:many-cards]]
                  [parse-value [:card :misere]]
                  ])
(def choice-func '[[select-choice [:boole :boole :boole :boole :boole :boole :boole]]
                   ])
(def misere-func '[[is-misere [:misere]]
                   ])
(def trump-func '[[return-trump [:trump]]
                  ])
(def modes-func '[[return-modes [:modes]]
                  ])
(def discard-func '[;[return-discard [:hand :hand :hand :hand :hand :hand]]
                    [discard-random [:return-hand]]
                    ])
(def return-hand-func '[[return-hand [:return-hand]]
                    ])
(def functions {:play play-func :card card-func :mode mode-func :suit suit-func :boole boole-func
               :many-cards many-func :arith arith-func :choice choice-func :hand hand-func :misere misere-func
               :trump trump-func :discard discard-func :modes modes-func :return-hand return-hand-func})

(def functions-return-type-index
  (list '( :play get-highest-from-suit get-lowest-from-suit get-nth-highest-from-suit get-nth-lowest-from-suit)
    '( :mode select-mode)
    '( :suit select-suit get-most-suit)
    '( :boole lead-wins-against? wins-by-value? > < = is-trump is-suit has-trump is-high-card is-low-card has-high-cards has-low-cards is-misere)
    '( :arith get-suit-num get-trumps-num low-cards-num high-cards-num parse-value)
    '( :choice select-choice)
    '( :hand get-suit get-trumps high-cards low-cards get-suit-2 get-most-suit-2 take-n get-suits)
    '( :misere is-misere)
    '( :trump return-trump)
    '( :discard return-discard discard-random)
    '( :modes return-modes)
    '( :return-hand return-hand)
    )
  )