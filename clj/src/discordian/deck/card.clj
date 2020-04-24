(ns discordian.deck.card
  (:require [clojure.set :refer [union]]))

(def red-suits (set ["Hearts" "Diamonds"]))
(def black-suits (set ["Clubs" "Spades"]))
(def suits (union red-suits black-suits))
(def ranks ["Ace" "Two" "Three" "Four" "Five" "Six" "Seven" "Eight" "Nine" "Ten" "Jack" "Queen" "King"])

(defn red?   [suit] (contains? red-suits suit))
(defn joker? [card] (= "Joker" (:rank card)))

(defn init [suit rank]
  {:suit suit
   :rank rank
   :color (if (red? suit) "Red" "Black")})

(defn joker [color]
  {:rank "Joker" :color color})

(defn ->str [{:keys [rank suit color] :as card}]
  (if (joker? card)
    (format "%s Joker" color)
    (format "%s of %s" rank suit)))
