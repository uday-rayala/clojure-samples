(ns casper-gource.parsers_test
  (:use [clojure.test] [casper-gource.parsers])
)

(deftest test-group-commits
	(is (= [["a" "b" "c"] ["d" "e"] ["f"]] (group-commits ["a" "b" "c" "" "d" "e" " " "f"]))))

(deftest test-get-time
	(is (= "1314380859" (get-time "1314380859 +0100"))))

(deftest test-get-files
	(is (= [["A" "file1"] ["M" "file2"] ["D" "file3"]] (get-files ["A	file1", "M	file2", "D	file3"]))))

(def gcourse-object {:time "1314380859" 
					:people #{"uday", "mark"} 
					:files [["A" "file1"], ["M" "file2"], ["D" "file3"] ]})
					
(deftest test-generate-gcourse-log 
	(is (= gcourse-object
	(generate-gcourse-log ["1314380859 +0100" "uday/mark new commit" "A	file1" "M	file2" "D	file3"]))))

(deftest test-serialize-gcource-log
	(is (= [
		"1314380859|uday|A|file1", "1314380859|uday|M|file2", "1314380859|uday|D|file3",
		"1314380859|mark|A|file1", "1314380859|mark|M|file2", "1314380859|mark|D|file3",
	] 
	(serialize-gcource-log gcourse-object))))