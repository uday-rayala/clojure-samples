(ns git.parsers-test
  (:use [clojure.test] [git.parsers])
)

(deftest test-unused-words
  (is (= #{"some", "marklogic", "commit"} (unused-words "Uday/Mark Some marklogic commit")))
)

(deftest test-total-count
  (is (= 2 (total-count [#{"uday" "mark" "new"} #{"mark" "new" "commit"}] "mark")))
)

(defn collection= [coll1 coll2] (= (into #{} coll1) (into #{} coll2)))

(deftest test-frequencies-in-set-collection
  (is (collection= [["uday" 1],["mark" 2], ["pat" 0]] (frequencies-in-set-collection [#{"uday" "mark" "new"} #{"mark" "new" "commit"}] #{"uday" "mark" "pat"})))
)

(deftest test-all-unused-words
  (is (collection= [["commit" 2], ["new" 1], ["marklogic" 1], ["some" 1]] (all-unused-words ["Uday/Mark Some marklogic commit", "Uday/Mark New commit"])))
)

(deftest test-pair-matrix-count
  (is (= 10 (pair-matrix-count [[#{"Uday" "Mark"} 10], [#{"Mark" "Pat" 5}]] "Uday" "Mark")))
  (is (= 0 (pair-matrix-count [[#{"Uday" "Mark"} 10], [#{"Mark" "Pat" 5}]] "Uday" "Pat")))
)
