(ns discordian.core
  (:require [discordian.deck.command :as deck]
            [discord.bot :as bot]))

(defn parse [content]
  (let [[opts comment] (clojure.string/split content " ! ")]
    {:comment comment
     :options (clojure.string/split opts " ")}))

;; parse, execute, reply

(defn reply* [message result]
  (println ;; bot/say
   (format "@%s - %s" (:author message) result)))

(defmacro execute [body]
  `(try ~body (catch Exception e# (str "caught exception " (.getMessage e#)))))

(defmacro reply [command message body]
  `(reply* ~message (->output ~command ~message (execute ~body))))

(defn ->output
  [command message result]
  (format "%s%s - Result: %s%s"
          command
          (if-let [options (:options message)] (str " " options) "")
          result
          (if-let [comment (:comment message)] (str " ! " comment) "")))

(defmacro defcommand [name f]
  `(bot/defcommand ~name
     [client# message#]
     (let [{:keys [comment# options#] :as msg} (parse :content message#)]
       (reply (str ~name) message# (f options# comment#)))))

;; TODO: formatting message results, replying

;; TODO: use an extension instead, e.g. `!deck draw n`
(defcommand peek deck/peek)
(defcommand draw deck/draw)
(defcommand shuffle deck/shuffle)
