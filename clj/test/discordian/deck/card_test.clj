(ns discordian.deck.card-test
  (:require [discordian.deck.card :as sut]
            [clojure.test :as t :refer [is]]))

(t/deftest ->str
  (t/testing "joker->str uses color"
    (is (= "Black Joker" (sut/->str {:rank "Joker" :color "Black"})))
    (is (= "Red Joker" (sut/->str {:rank "Joker" :color "Red"}))))
  (t/testing "card->str uses rank and suit"
    (is (= "Ace of Spades" (sut/->str {:rank "Ace" :suit "Spades" :color "Black"})))
    (is (= "Queen of Hearts" (sut/->str {:rank "Queen" :suit "Hearts" :color "Red"})))))
