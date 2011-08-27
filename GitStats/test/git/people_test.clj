(ns git.people-test
  (:use [clojure.test] [git.people])
)

(deftest test-parsing-names
  (is (= #{"uday", "mark"} (commiters "Uday/Mark Some marklogic commit")))
  (is (= #{"uday", "mark"} (commiters "uday/Mark Some commit")))
  (is (= #{"uday", "micheal"} (commiters "uday/Mike Some commit")))
)
