(ns discordian.core
  (:require [discordian.deck.command :as deck]
            [discordian.dice.command :as dice]
            [discord.bot :as bot])
  (:refer-clojure :exclude [peek shuffle]))

(defn words [str]
  (clojure.string/split str #" "))

(defn separate-comment [content]
  (let [[statement comment] (clojure.string/split content #" ! ")]
    {:statement statement :comment comment }))

(defn parse [content]
  (let [{:keys [statement comment]} (separate-comment content)]
    {:command (first (words statement))
     :comment comment
     :options (clojure.string/join " " (rest (words statement)))}))

;; parse, execute, reply

;; BIG BIG BIG TODO. For some reason it only shows as chars and doesn't ping anybody,
;; no matter what I try. discord.clj may not support??
(defn mention-author [message]
  (str "@" (-> message :author :username)))

(defn ->output
  [message result & {:keys [type]}]
  (let [{:keys [command options comment]} message]
    (format (str "%s %s - " (if (= type :no-val) "" "Result: ") "`%s` %s")
            command options result
            (if comment (format " Reason: %s" comment) ""))))

(defn reply [message result]
  (bot/say (format "%s - %s" (mention-author message) result)))

(defn execute-command [f message & {:keys [type]}]
  (println "Received message with content: deck" (:content message))
  (let [{:keys [options] :as msg} (parse (:content message))
        result                    (f options)]
    (reply message (->output msg result :type type))))

(bot/defextension deck [client message]
  (:peek    (execute-command deck/peek    message))
  (:draw    (execute-command deck/draw    message))
  (:shuffle (execute-command deck/shuffle message :type :no-val))
  (:init    (execute-command deck/init!   message :type :no-val)))

(bot/defcommand roll [client message]
  (let [{:keys [statement comment]} (separate-comment (:content message))
        msg {:command "roll" :options statement :comment comment}]
    (reply message (->output msg (dice/roll statement)))))

(defn -main
  "Starts a Discord bot."
  [& args]
  (bot/start))
