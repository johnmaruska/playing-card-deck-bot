(ns discordian.dice.bag)

(defrecord Result [label sections total])

;; `keep` integer how many top rolls to keep
;; `reroll` value, consistently reroll until result above value
;; `explode` value, roll additional dice when this value is rolled
;; `drop` integer how many lowest rolls to drop
(defrecord RollPartOptions [keep reroll explode drop])
;; `count` number of dice to roll
;; `options` modifications to a standard roll (e.g. keep, drop, explode)
;; `sides` how many faces / sides the dice all have, RollParts are separated by die type
;; `tally` individual results of the dice rolls
;; `total` sum of all tallied rolls
(defrecord Cluster [count notes options sides])
(defrecord RollPart [count notes options sides tally total])


(defn parse [dice-str])  ; return parse tree


(defn- exploded? [result explode-on]
  (and explode-on (= result explode-on)))

(defn- roll-d
  "Roll a single d`faces`. Explosions are handled as part of this single roll."
  [faces & [explode]]
  (loop [coll []]
    (let [result  (+ 1 (rand-int faces))
          results (conj coll result)]
      (if (exploded? result explode)
        (recur results)
        (if (= 1 (count results))
          (first results)
          results)))))

(defn value
  "Get rolled value from a result which is either an integer or a sequence of integers."
  [result]
  (if (sequential? result)
    (reduce + result)
    result))

(defn total
  "Get total of a tally, handling explosions which cause tallies to nest."
  [tally]
  (reduce + (map value tally)))

(defn explode-value
  "Get the value to explode on. If option is `true` i.e. no value specified,
  then default to the sides of the dice. "
  [cluster]
  (let [opt (-> cluster :options :explode)]
    (cond
      (true? opt) (:sides cluster)
      (int?  opt) opt
      :else       nil)))

(defn raw-roll
  "Perform a roll without honoring keep or drop options. Explosions and rerolls
  are honored, return all dice resulting from these."
  [{:keys [sides options] :as cluster}]
  (let [reroll (or (options :reroll) 0)]
    (->>
     (repeatedly #(roll-d sides (explode-value cluster)))
     (filter #(< reroll (value %)))
     (take (:count cluster)))))

(defn unsort
  "Unsort a munged list to match original order."
  [munged original]
  (filter (partial contains? (set munged))
          original))

(defn keeps
  "Determine which rolls to keep in a tally."
  [rolls k d]
  (->> (sort-by (comp - value) rolls)
       (drop-last d)
       (take k)))

(defn ->tally
  "Get a roll tally for the cluster. Honors all options."
  [{:keys [options count] :as cluster}]
  (let [rolls (raw-roll cluster)
        d     (or (-> options :drop) 0)
        k     (or (-> options :keep) count)]
    (-> rolls
        (keeps k d)
        (unsort rolls))))

(defn Cluster->RollPart [cluster]
  (let [tally (->tally cluster)]
    (map->RollPart
     (assoc cluster
            :tally tally
            :total (sum tally)))))

(let [parsed-roll (parse dice-str)
      sections (:sections parsed-roll)])
