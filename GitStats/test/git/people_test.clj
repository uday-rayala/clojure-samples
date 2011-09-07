(ns git.people-test
  (:use [clojure.test] [git.people])
)

(deftest test-parsing-names
  (is (= #{"uday", "mark"} (commiters "Uday/Mark Some marklogic commit")))
  (is (= #{"uday", "mark"} (commiters "uday/Mark Some commit")))
  (is (= #{"uday", "michael"} (commiters "uday/Mike Some commit")))
)
(deftest test-get-words
  (is (= #{"chris", "mushtaq", "added"} (get-words "Tue Sep 6 16:05:07 2011 +0100 | Chris/Mushtaq #690 Added")))
  (is (= #{"chris", "mushtaq", "added"} (get-words "Chris/Mushtaq #690 Added")))
)
