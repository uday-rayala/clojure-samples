(ns casper-gource.parsers_test
  (:use [clojure.test] [casper-gource.parsers])
)

(deftest test-group-commits
	(is (= [["a" "b" "c"] ["d" "e"] ["f"]] (group-commits ["a" "b" "c" "" "d" "e" " " "f"]))))

(deftest test-get-time
	(is (= "1314380859" (get-time "1314380859 +0100"))))

; (deftest test-generate-gcourse-logs 
; 	(is (= {:time "1314380859" :people ["uday", "mark"] :files [["A" "file1"], ["M" "file2"], ["D" "file3"] ]} 
; 	(generate-gcourse-logs ["1314380859 +0100" "uday/mark new commit" "A	file1" "M	file2" "D	file3"]))))
