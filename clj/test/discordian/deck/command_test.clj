(ns discordian.deck.command-test
  (:require [discordian.deck.command :as sut]
            [discordian.deck.core :as deck]
            [clojure.test :as t :refer [deftest is testing]])
  (:refer-clojure :exclude [peek]))

(deftest peek
  (testing "peek at specified cards"
    (sut/init! [sut/joker-code])
    (is (= 3 (count (sut/peek ["3"])))))
  (testing "peek at remaining when deck is too small"
    (sut/init! [sut/joker-code])
    (deck/draw! sut/playing-cards 50)
    (is (clojure.string/starts-with? (sut/peek ["10"])
                                     "Deck has only 4 cards remaining."))))

(deftest draw
  (testing "draw specified cards"
    (sut/init! [sut/joker-code])
    (is (= 3 (count (sut/draw ["3"])))))
  (testing "draw remaining when deck is too small"
    (sut/init! [sut/joker-code])
    (deck/draw! sut/playing-cards 50)
    (is (clojure.string/starts-with? (sut/draw ["10"])
                                     "Deck has only 4 cards remaining."))))

(deftest init!
  (testing "default no jokers"
    (sut/init! [])
    (is (= 52 (deck/remaining sut/playing-cards))))
  (testing "joker-code honored"
    (sut/init! [sut/joker-code])
    (is (= 54 (deck/remaining sut/playing-cards)))))

(deftest shuffle
  (testing "shuffle keeps jokers"
    (sut/init! [sut/joker-code])
    (sut/shuffle)
    (is (= 54 (deck/remaining sut/playing-cards))))
  (testing "shuffle does not add jokers"
    (sut/init! [])
    (sut/shuffle)
    (is (= 52 (deck/remaining sut/playing-cards)))))
