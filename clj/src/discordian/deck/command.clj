(ns discordian.deck.command
  "Collection of commands for handling a deck of playing cards.
  All fns should return a formatted `result` of the command with which to reply."
  (:require [discordian.deck.core :as deck]
            [discordian.deck.card :as card])
  (:refer-clojure :exclude [peek shuffle]))

(def playing-cards (deck/init :with-jokers true))

;;;; Options
(def joker-code "wJ")
(def with-jokers? (partial = joker-code))
;; commands with an `n` option must specify it first, e.g. `draw n`
(def n #(Integer/parseInt (first %)))

(defn positional-opt? [opt] (or (= n opt)))
(defn existence-opt?  [opt] (or (= with-jokers? opt)))
(defn get-option [options pred]
  (cond (positional-opt? pred) (pred options)
        (existence-opt? pred)  (some pred options)
        :else                  (first (filter pred options)) ))

(defn retrieve [f deck cards]
  (if-not (deck/too-small? deck cards)
    (vec (map card/->str (f deck cards)))
    (format "Deck has only %s cards remaining. %s"
            (deck/remaining deck)
            (f deck (deck/remaining deck)))))

(defn peek [options]
  (retrieve deck/peek playing-cards (get-option options n)))

(defn draw [options]
  (retrieve deck/draw! playing-cards (get-option options n)))

(defn shuffle [& [_options]]
  (println "+++")
  (println (:options @playing-cards))
  (deck/shuffle! playing-cards))

(defn init! [options]
  (reset! playing-cards @(deck/init :with-jokers (get-option options with-jokers?))))
