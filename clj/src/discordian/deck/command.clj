(ns discordian.deck.command
  "Collection of commands for handling a deck of playing cards.
  All fns should return a formatted `result` of the command with which to reply."
  (:require [discordian.deck.core :as deck]
            [discordian.deck.card :as card]
            [discordian.deck.options :as opts :refer [get-option n]])
  (:refer-clojure :exclude [peek shuffle]))

(def playing-cards (deck/init :with-jokers true))

(defn retrieve
  "Retrieve cards from a deck using the given `f` retrieval fn."
  [f deck cards]
  (if-not (deck/too-small? deck cards)
    (vec (map card/->str (f deck cards)))
    (format "Deck has only %s cards remaining. %s"
            (deck/remaining deck)
            (f deck (deck/remaining deck)))))

(defn peek [options]
  (let [n (:n (opts/retrieve-parse options))]
    (retrieve deck/peek playing-cards n)))

(defn draw [options]
  (let [n (:n (opts/retrieve-parse options))]
    (retrieve deck/draw! playing-cards n)))

(defn shuffle [& [_options]]
  (deck/shuffle! playing-cards))

(defn init! [options]
  (reset! playing-cards @(apply deck/init (opts/init-parse options))))
