(ns discordian.deck.core-test
  (:require [discordian.deck.core :as sut]
            [clojure.test :as t :refer [deftest testing is]])
  (:refer-clojure :exclude [peek]))

(deftest standard-deck
  (testing "standard deck includes all 52 cards"
    (is (= 52 (count (set sut/standard-deck))))))

(deftest add-jokers
  (testing "adding jokers results in 52+2 entries, not single vector"
    (is (= 54 (count (set sut/standard-deck-with-jokers))))))

;;; deck methods

(defn init-standard [] (atom {:options {:with-jokers false}
                              :cards sut/standard-deck}))

(deftest too-small?
(let [deck (init-standard)]
  (testing "too-small? false when deck not too small"
    (is (false? (sut/too-small? deck 10))))
  (testing "too-small? true when deck is too small"
    (is (true? (sut/too-small? deck 60))))))

(deftest peek
(let [deck (init-standard)]
  (testing "peek is from the beginning of the deck"
    (is (= [{:suit "Diamonds", :rank "Ace", :color "Red"}
            {:suit "Diamonds", :rank "Two", :color "Red"}
            {:suit "Diamonds", :rank "Three", :color "Red"}]
           (sut/peek deck 3))))
  (testing "peek does something when too many"
    (is (thrown? java.lang.IndexOutOfBoundsException (sut/peek deck 60))))))

(deftest draw!
  (let [deck (init-standard)]
    (testing "draw! is from the beginning of the deck"
      (is (= [{:suit "Diamonds", :rank "Ace", :color "Red"}
              {:suit "Diamonds", :rank "Two", :color "Red"}
              {:suit "Diamonds", :rank "Three", :color "Red"}]
             (sut/draw! deck 3)))
      (is (= 49 (sut/remaining deck))))
    (testing "draw! does something when the deck is expended"
      (sut/draw! deck (sut/remaining deck))
      (is (= [] (:cards @deck))))
    (testing "draw! does something when too many"
      (is (thrown? java.lang.IndexOutOfBoundsException (sut/draw! deck 60))))))

(deftest init
  (testing "shuffled results in not the same order as before"
    (let [x @(init-standard)
          y @(sut/init)
          z @(sut/init)]
      (is (not (= x y)))
      (is (not (= x z)))
      (is (not (= y z)))))
  (testing "joker inclusion is honored"
    (is (= 54 (sut/remaining (sut/init :with-jokers true))))))

(deftest shuffle!
  (testing "shuffle! updates previous atom"
    (let [deck (init-standard)]
      (is (not (= @deck (sut/shuffle! deck))))
      (is (= 52 (sut/remaining deck)))))
  (testing "shuffle! honors joker options"
    (let [deck (sut/init :with-jokers true)]
      (sut/shuffle! deck)
      (is (= 54 (sut/remaining deck))))))
