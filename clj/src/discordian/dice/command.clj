(ns discordian.dice.command
  (:require [clojure.string :as str]
            [discordian.dice.core :as core]))

(defn find-match [regex s] (rest (re-find (re-matcher regex s))))
(defn ->int [s] (Integer/parseInt s))

;;; Single Cluster

(defn parse-dice [roll]
  (let [[n d] (find-match #"(\d+)?d(\d+)" roll)]
    {:n (->int n) :d (->int d)}))
;; should default to {:n nil :d nil} if not found

(defn parse-explode [roll]
  (when-let [result (first (find-match #"e(\d+)" roll))]
    (->int result)))
;; should default to nil if not found

(defn parse-keep [roll]
  (let [[bw n] (find-match #"k([bw]?)(\d+)" roll)]
    (when (not (empty? n))
      {:n (->int n)
       :bw (if (= bw "w") :worst :best)})))

(defn parse-drop [roll]
  (let [[bw n] (find-match #"D([bw]?)(\d+)" roll)]
    (when (not (empty? n))
      {:n (->int n)
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

(defn parse-modifier [term] (->int term))

(defn parse-term [term]
  (if (roll? term)
    (parse-roll term)
    (parse-modifier term)))

;;; Multi-Cluster
(def ops-re #"\s*([\*\-\+\/])\s*")
(defn get-ops [command]
  (map last (re-seq ops-re command)))

(defn get-terms [command]
  (str/split command #"\s*[\*\-\+\/]\s*"))

(defn parse [command]
  (let [ops   (get-ops command)
        terms (map parse-term (get-terms command))]
    {:ops ops :terms terms}))

(defn infix [ops terms]
  (cons (first terms) (interleave ops (drop 1 terms))))

(defn ->display [results k]
  (let [infixed (->> (:terms results)
                     (map k)
                     (infix (:ops results)))]
    (str/join " " infixed)))

(def example {:terms [1 2 3]
              :ops ["+" "-"]})

(defn total [results]
  (let [term-vals (map :result (:terms results))]
    (reduce (fn [coll [op term]]
              ((eval (symbol op)) coll term))
            (first term-vals)
            (zipmap (:ops results) (rest term-vals)))))

(defn format-message [results]
  (str "\nRaw Results: "
       (->display results :raw-rolls)
       "\nKept Results: "
       (->display results :kept-rolls)
       "\nFinal Results: "
       (->display results :result)
       "\nTotal: " (total results)))

(defn roll [command]
  (let [{:keys [ops terms]} (parse command)
        term-vals (vec (map core/get-value terms))]
    (format-message {:ops ops :terms term-vals})))
