(ns discordian.deck)

(def red-suits ["Hearts" "Diamonds"])
(def black-suits ["Clubs" "Spades"])
(def suits (concat red-suits black-suits))
(def ranks ["Ace" "Two" "Three" "Four" "Five" "Six" "Seven" "Eight" "Nine" "Ten" "Jack" "Queen" "King"])

(defn- standard-deck []
  (for [suit suits
        rank ranks]
    {:suit suit :rank rank :color (if (contains? red-suits suit)
                                    "Red" "Black")}))
(defn- add-jokers [deck]
  (concat deck [{:rank "Joker" :color "Red"
                 :rank "Joker" :color "Black"}]))
(defn shuffled
  "Create a shuffled playing card deck."
  [& {:keys [with-jokers] :or {with-jokers false}}]
  (atom
   (shuffle
    (if with-jokers
      (add-jokers (standard-deck))
      (standard-deck)))))

(defn card->str [{:keys [rank suit color] :as card}]
  (if (= rank "Joker")
    (format "%s Joker" color)
    (format "%s of %s" rank suit)))

(defn peek [deck n] (subvec @deck n))

(defn draw! [deck n]
  (let [cards (peek deck n)]
    (swap! deck drop n)
    cards))
