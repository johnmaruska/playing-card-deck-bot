(ns discordian.core
  (:require [discordian.deck :as deck
             discord.bot :as bot]))

(def playing-cards (deck/shuffle :with-jokers true))

(defn parse [content]
  (let [[opts comment] (clojure.string/split content " ! ")]
    {:comment comment
     :options (clojure.string/split opts " ")}))

(bot/defcommand peek
  [client message])

(bot/defcommand draw
  [client message]
  (let [{:keys [comment options]} (parse (:content message))
        n (int (first (options)))]
    (deck/draw! playing-cards n)))

(bot/defcommand shuffle
  [client message])
