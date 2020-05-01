(ns discordian.dice.core)

(defn- exploded? [result explode-on]
  (and explode-on (= result explode-on)))

(defn- roll-d [faces & {:keys [explode-on]}]
  (loop [coll []]
    (let [result  (+ 1 (rand-int faces))
          results (conj coll result)]
      (if (exploded? result explode-on)
        (recur results)
        (if (= 1 (count results))
          (first results)
          results)))))

(defn value [result]
  (if (sequential? result)
    (reduce + result)
    result))

;; TODO: honor best/worst
(defn keeps [rolls k drp]
  (cond->> (sort-by (comp - value) rolls)
    drp (drop (:n drp))
    k   (take (:n k))))

(defn roll
  [{:keys [n d e k D] :or {n 1}}]
  {:pre [(every? int? [n d])
         (or (int? e) (nil? e))
         (every? #(or (nil? %1) (int? (:n %1))) [k D])]}
  (let [rolls      (vec (repeatedly n #(roll-d d :explode-on e)))
        kept-rolls (vec (keeps rolls k D))]
    {:raw-rolls  rolls
     :kept-rolls kept-rolls
     :result     (reduce + (flatten kept-rolls))}))

(defn get-value [term]
  (if (number? term)
    {:raw-rolls term :kept-rolls term :result term}
    (roll term)))
