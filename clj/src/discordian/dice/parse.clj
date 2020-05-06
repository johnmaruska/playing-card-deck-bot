(ns discordian.dice.parse
  (:require [instaparse.core :as insta]))

(def rules
  ["DICE = XDX (' '? OP ' '? (XDX | NUMBER) ' '?)*"
   "OP = '+' | '-' | '*' | '/'"
   "XDX = COUNT SIDES OPTIONS?"
   "OPTIONS = ' '? (EXPLODE | KEEP | DROP | REROLL | TARGET)*"
   "EXPLODE = 'e' NUMBER? ' '?"
   "TARGET = 't' NUMBER ' '?"
   "REROLL = 'r' NUMBER ' '?"
   "DROP = 'd' NUMBER ' '?"
   "KEEP = 'k' NUMBER ' '?"
   "SIDES = 'd' NUMBER"
   "COUNT = NUMBER?"
   "NUMBER = #\"[0-9]+\""])

(defn matches? [k form] (= k (first form)))
(defn matches [k forms]
  (filter (partial matches? k) forms))

(defn get-number [form]
  (when (= :NUMBER (first form))
    (second form)))


(def parser
  (insta/parser (clojure.string/join "\n" rules)))

(defrecord RollPartOptions [keep reroll explode drop])
(defrecord Cluster [count notes options sides])

(defn xdx->Cluster
  "`xdx` is the content from the parser following an XDX tag"
  [xdx]
  (when (= :XDX (first xdx))
    (let [find         (fn [coll k] (first (matches k coll)))
          find-opt-val (fn [forms k]
                         (-> (find (rest forms) k)
                             (nth 2)
                             (second)))
          get-option   (partial find (rest (find (rest xdx) :OPTIONS)))]
      (map->Cluster
       {:count   (second (second (find (rest xdx) :COUNT)))
        :notes   ""
        :sides   (find-opt-val (rest xdx) :SIDES)
        :options (map->RollPartOptions
                  {:keep    (get-option :KEEP)
                   :explode (get-option :EXPLODE)
                   :target  (get-option :TARGET)
                   :drop    (get-option :DROP)
                   :reroll  (get-option :REROLL)})}))))
