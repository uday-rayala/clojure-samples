(ns casper-gource.parsers
  (:use [clojure.string :as string])
)

(defn non-empty-partition? [partition] (not (blank? (string/join partition))))
(defn group-commits [commits] (filter non-empty-partition? (partition-by string/blank? commits)))

(defn get-time [message] (first (string/split message #"[^\w]+")))
(defn generate-gcourse-logs [messages] {:name "name" :people ["1" "2"] :files [["A" "B"], ["D" "C"]]})
