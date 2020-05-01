(ns discordian.deck.core
  (:require [discordian.deck.card :as card])
  (:refer-clojure :exclude [peek]))

;; internal

(def standard-deck
  (vec (for [suit card/suits
             rank card/ranks]
         (card/init suit rank))))

(def standard-deck-with-jokers
  (vec (concat standard-deck [(card/joker "Red") (card/joker "Black")])))

;;; external
(defn remaining [deck] (count (:cards @deck)))
(defn discarded [deck] (count (:discards @deck)))
(defn too-small? [deck n] (< (remaining deck) n))

(defn peek [deck n] (subvec (:cards @deck) 0 n))

(defn draw! [deck n]
  (let [cards (peek deck n)]
    (swap! deck update :cards #(vec (drop n %)))
    (swap! deck update :discards #(flatten (concat % cards)))
    cards))

(defn init
  "Create a shuffled playing card deck."
  [& {:keys [with-jokers]
      :or   {with-jokers false}
      :as   opts}]
  (atom {:options  opts
         :cards    (shuffle (if with-jokers
                              standard-deck-with-jokers
                              standard-deck))
         :discards []}))

(defn shuffle! [deck]
  (swap! deck update :cards #(shuffle (concat % (:discards @deck))))
  (reset! deck (assoc @deck :discards [])))
