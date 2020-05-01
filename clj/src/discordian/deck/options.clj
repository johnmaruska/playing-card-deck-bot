(ns discordian.deck.options)



(def joker-code "wJ")
(def with-jokers? (partial = joker-code))
;; commands with an `n` option must specify it first, e.g. `draw n`
(def n #(Integer/parseInt (first %)))

(defn positional-opt? [opt] (or (= n opt)))
(defn existence-opt?  [opt] (or (= with-jokers? opt)))

(defn get-existence-option [options pred]
  (or (some pred options) false))

(defn get-option
  [options pred]
  (cond (positional-opt? pred) (pred options)
        (existence-opt? pred)  (get-existence-option options pred)
        :else                  (first (filter pred options))))

;;; use these two

(defn init-parse [options]
  (let [opts (clojure.string/split options #" ")]
    {:with-jokers (get-option opts with-jokers?)}))

(defn retrieve-parse [options]
  (let [opts (clojure.string/split options #" ")]
    {:n (get-option opts n)}))
