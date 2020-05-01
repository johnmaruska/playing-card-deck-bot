(ns discordian.core
  (:require [discordian.deck.command :as deck]
            [discord.bot :as bot])
  (:refer-clojure :exclude [peek shuffle]))

(defn parse [content]
  (let [[opts comment] (clojure.string/split content #" ! ")]
    {:command (first (clojure.string/split opts #" "))
     :comment comment
     :options (clojure.string/join " " (rest (clojure.string/split opts #" ")))}))

;; parse, execute, reply

;; BIG BIG BIG TODO. For some reason it only shows as chars and doesn't ping anybody,
;; no matter what I try. discord.clj may not support??
(defn mention-author [message]
  (str "@" (-> message :author :username)))

(defn ->output
  [message result & {:keys [type]}]
  (let [{:keys [command options comment]} message]
    (format (str "%s %s - " (if (= type :command) "" "Result: ") "`%s` %s")
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
  (:shuffle (execute-command deck/shuffle message :type :command))
  (:init    (execute-command deck/init!   message :type :command)))

(bot/defcommand roll [client message]
  )

(defn -main
  "Starts a Discord bot."
  [& args]
  (bot/start))
