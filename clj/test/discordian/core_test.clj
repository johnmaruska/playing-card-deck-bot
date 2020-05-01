(ns discordian.core-test
  (:require [discordian.core :as sut]
            [clojure.test :refer [deftest is testing]]))

(deftest parse
  (testing "parses message contents into options and comment"
    (is (= {:comment "test" :options "3" :command "peek"}
           (sut/parse "peek 3 ! test")))))
