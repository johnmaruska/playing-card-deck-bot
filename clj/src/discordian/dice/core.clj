(ns discordian.dice.core)

(defn- exploded? [result explode-on]
  (and explode-on (= result explode-on)))

(defn- roll-d [faces & {:keys [explode-on] :as opts}]
  (loop [coll []]
    (let [result  (+ 1 (rand-int faces))
          results (conj coll result)]
      (if (exploded? result explode-on)
        (recur results)
        results))))

(defn value [result]
  (if (sequential? result)
    (reduce + result)
    result))

(value 12)
(value [12 12 1])

(defn keeps [rolls k drp]
  (cond->> (sort-by (comp - value) rolls)
    drp (drop drp)
    k   (take k)))

(keeps [12] 1 nil)
(keeps [12 12 1] 1 nil)

(defn roll
  [{:keys [n d e k drp modifier]
    :or   {n 1 modifier 0}
    :as   opts}]
  {:pre [(int? d)
         (every? #(or (int? %1) (nil? %1)) [n e k drp modifier])]}
  (let [rolls      (vec (repeatedly n #(roll-d d :explode-on e)))
        kept-rolls (vec (keeps rolls k drp))]
    {:raw-rolls  rolls
     :kept-rolls kept-rolls
     :result     (+ modifier (reduce + (flatten kept-rolls)))}))

(roll {:n 5 :d 12 :e 12 :k 4 :drp 2 :modifier 3})
