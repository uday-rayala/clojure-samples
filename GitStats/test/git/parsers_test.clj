(ns git.parsers-test
  (:use [clojure.test] [git.parsers])
)

(deftest test-parsing-names
  (is (= #{"uday", "mark"} (parse-names "Uday/Mark Some marklogic commit")))
  (is (= #{"uday", "mark"} (parse-names "uday/Mark Some commit")))
  (is (= #{"uday", "micheal"} (parse-names "uday/Mike Some commit")))
)

(deftest test-unused-words
  (is (= #{"some", "marklogic", "commit"} (unused-words "Uday/Mark Some marklogic commit")))
)

(deftest test-all-unused-words
  (is (= [["commit" 2], ["new" 1], ["marklogic" 1], ["some" 1]] (all-unused-words ["Uday/Mark Some marklogic commit", "Uday/Mark New commit"])))
)

(deftest test-top-count
  (is (= 2 (top-count [#{"uday" "mark"}, #{"uday"} #{"mark"}] "uday")))
)
