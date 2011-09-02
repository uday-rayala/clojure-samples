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

(deftest test-code-size
  (is (= 81 (code-size " 10 files changed, 78 insertions(+), 3 deletions(-)")))
)

(defn date [date month year] (.getTime (doto (java.util.Calendar/getInstance) (.set year month date 0 0 0))))

(deftest test-code-commit-date
  (is (= "02-09-2010" (code-commit-date "Fri Sep 2 14:18:59 2010 +0100")))
)
(deftest test-code-commit-time
  (is (= "14:18" (code-commit-time "Fri Sep 2 14:18:59 2010 +0100")))
)
