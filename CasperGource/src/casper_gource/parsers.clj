(ns casper-gource.parsers
  (:use [clojure.string :as string] [casper-gource.people :as people] [clojure.contrib.combinatorics :as combinatorics])
)

(defn non-empty-partition? [partition] (not (blank? (string/join partition))))
(defn group-commits [commits] (filter non-empty-partition? (partition-by string/blank? commits)))

(defn get-files[messages] (map (fn [message] (string/split message #"\s+")) messages))

(defn get-time [message] (first (string/split message #"\s+")))
(defn generate-gcourse-log [messages] {
	:time (get-time (first messages)) 
	:people (people/commiters (second messages)) 
	:files (get-files (take-last (- (count messages) 2) messages))})
	
(defn serialize-log [time person-file] 
	(let [person (first person-file) file (last person-file)] (str time "|" person "|" (string/join "|" file))))
	
(defn serialize-gcource-log [log-message] 
	(let [time (log-message :time)
		people (log-message :people)
		files (log-message :files)] 
	(map (partial serialize-log time) (combinatorics/cartesian-product people files))))