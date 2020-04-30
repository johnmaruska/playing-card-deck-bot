(ns discordian.deck.options)

(def joker-code "wJ")
(def with-jokers? (partial = joker-code))
;; commands with an `n` option must specify it first, e.g. `draw n`
(def n #(Integer/parseInt (first %)))

(defn positional-opt? [opt] (or (= n opt)))
(defn existence-opt?  [opt] (or (= with-jokers? opt)))

(defn get-option
  [options pred]
  (cond (positional-opt? pred) (pred options)
        (existence-opt? pred)  (some pred options)
        :else                  (first (filter pred options))))

;;; use these two

(defn init-parse [options]
  {:with-jokers (get-option options with-jokers?)})

(defn retrieve-parse [options]
  {:n (get-option options n)})
