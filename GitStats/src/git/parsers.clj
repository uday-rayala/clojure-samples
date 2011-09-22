(ns git.parsers
  (:use [clojure.set])
  (:require [git.people :as people] [clojure.string :as string])
)
(use '[clojure.contrib.string :only (substring? split)])

(def functional-areas [
  {:name "Xquery" :location "src/main/xquery/"}
  {:name "Code" :location "src/main/scala/"}
  {:name "Unit" :location "src/test/unit/"}
  {:name "Functional" :location "src/test/functional/"}
  {:name "Integration" :location "src/test/integration/"}
])

(defn group-and-filter [f coll] (remove (fn [group] (nil? (first group))) (group-by f coll)))

(defn find-functional-area [file] (first (filter (fn [area] (substring? (:location area) file)) functional-areas)))
(defn change-size [change] (apply + (map #(Integer/parseInt %1) (take 2 (split #"\s+" change)))))
(defn to-area-object [area-group] {:area (:name (first area-group)) :changes (apply + (map change-size (last area-group)))})
(defn group-by-areas [files] (map to-area-object (group-and-filter find-functional-area files)))
(defn merge-all-groups [groups] (map (fn [g] {:area (first g) :size (apply + (map :changes (last g)))}) (group-by :area groups)))

(defn non-empty-partition? [partition] (not (string/blank? (string/join partition))))
(defn group-commits [commits] (filter non-empty-partition? (partition-by string/blank? commits)))

(defn is-pair? [collection] (= 2 (count collection)))
(defn total-count [set-collection key] (count (filter #(% key) set-collection)))
(defn frequencies-in-set-collection [set-collection keys]
  (map vector keys (map (partial total-count set-collection) keys)))

(defn unused-words [commit] (difference (people/get-words commit) (people/people-all-names)))

(defn top-counts [commits]
  (let [commiters-collection (map people/commiters commits)]
    (frequencies-in-set-collection commiters-collection (people/people-names))))

(defn top-counts-solo [commits]
  (let [solo-commiters-collection (filter (fn [commiters] (= 1 (count commiters))) (map people/commiters commits))]
    (frequencies-in-set-collection solo-commiters-collection (people/people-names))))

(defn commiters-and-stories [commits]
  (map people/commiters-and-story commits))

(defn all-unused-words [commits]
  (let [unused-words-collection (map unused-words commits)]
    (let [unused-words (reduce union unused-words-collection)]
      (frequencies-in-set-collection unused-words-collection unused-words))))


(defn pair-names [commits] (filter is-pair? (map people/commiters commits)))

(defn top-unused-words [commits]
  (filter #(< 10 (last %1)) (sort-by last #(compare %2 %1) (all-unused-words commits))))


;(filter #(> 100 (last %1)) (sort-by last #(compare %2 %1) (all-unused-words commits)))
(defn unused-words-starting [prefix commits]
  (filter (fn [commit] (.startsWith (first commit) prefix)) (all-unused-words commits)))

(defn default-if-nil [value default] (if value value default))

(defn pair-matrix-count [freq name otherName]
  (let [matched (filter (fn [x] (= (set [name otherName]) (first x))) freq)]
    (default-if-nil (second (first matched)) 0)))

(defn pairing-matrix [all-names freq]
  (map (fn [name] (map (fn [otherName] (pair-matrix-count freq name otherName)) all-names)) all-names))

(defn insertions [diff] (Integer/parseInt (first (re-seq #"\d+(?= insertions)" diff))))
(defn deletions [diff] (Integer/parseInt (first (re-seq #"\d+(?= deletions)" diff))))
(defn code-size [diff] (+ (insertions diff) (deletions diff)))

(defn code-commit-date [date-time] (let
  [input-formatter (new java.text.SimpleDateFormat "EEE MMM d HH:mm:ss yyyy Z"),
   date-formatter (new java.text.SimpleDateFormat "dd-MM-yyyy")]
    (.format date-formatter (.parse input-formatter date-time))))

(defn code-commit-time [date-time] (let
  [input-formatter (new java.text.SimpleDateFormat "EEE MMM d HH:mm:ss yyyy Z"),
   time-formatter (new java.text.SimpleDateFormat "HH:mm")]
    (.format time-formatter (.parse input-formatter date-time))))
