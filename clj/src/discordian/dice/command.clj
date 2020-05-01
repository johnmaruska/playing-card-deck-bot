(ns discordian.dice.regex)

(defn find-match [regex s] (rest (re-find (re-matcher regex s))))

;;; Single Cluster

(defn parse-dice [roll]
  (let [[n d] (find-match #"(\d+)?d(\d+)" roll)]
    {:n n :d d}))
;; should default to {:n nil :d nil} if not found

(defn parse-explode [roll]
  (first (find-match #"e(\d+)" roll)))
;; should default to nil if not found

(defn parse-keep [roll]
  (let [[bw n] (find-match #"k([bw]?)(\d+)" roll)]
    (when (not (empty? n))
      {:n n
       :bw (if (= bw "w") :worst :best)})))

(defn parse-drop [roll]
  (let [[bw n] (find-match #"D([bw]?)(\d+)" roll)]
    (when (not (empty? n))
      {:n n
       :bw (if (= bw "w") :worst :best)})))
;; should default to nil if no match
;; should {:n n :bw :best} if n but no bw
;; should {:n n :bw :worst} if w specified

(defn roll? [term]
  (not (empty? (find-match #"d\d+" term))))

(defn parse-roll [term]
  (assoc (parse-dice term)
         :e (parse-explode term)
         :k (parse-keep term)
         :D (parse-drop term)))

(defn parse-modifier [term] (Integer/parseInt term))

(defn parse-term [term]
  (println term)
  (if (roll? term)
    (parse-roll term)
    (parse-modifier term)))

;;; Multi-Cluster
(def operators #"\s*([\*\-\+\/])\s*")
(defn parse [command]
  {:ops   (map last (re-seq operators command))
   :terms (->> (clojure.string/split command #"\s*[\*\-\+\/]\s*")
               (map parse-term))})

(parse "d12e12k1 + d10 e10 - 5d8 * 1d6e6 - 4")
