(ns discordian.deck.options-test
  (:require [discordian.deck.options :as sut]
            [clojure.test :as t :refer [deftest is testing]]))


(deftest init-parse
  (testing "with-joker is honored"
    (is (= (sut/init-parse sut/joker-code)
           {:with-jokers true}))))

(deftest retrieve-parse
  (testing "n is honored as a first-slot argument"
    (is (= (sut/retrieve-parse "1 two three") {:n 1}))))

(deftest get-existence-option
  (testing "defaults to false"
    (is (false? (sut/get-existence-option [] sut/with-jokers?))))
  (testing "true when exists"
    (is (true? (sut/get-existence-option [sut/joker-code] sut/with-jokers?)))))

(deftest get-option
  (testing "existence defaults to false"))
