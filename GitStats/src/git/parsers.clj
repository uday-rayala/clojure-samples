(ns git.parsers
  (:use [clojure.set])
  (:require [git.people :as people])
)

(defn is-pair? [collection] (= 2 (count collection)))
(defn total-count [set-collection key] (count (filter #(% key) set-collection)))
(defn frequencies-in-set-collection [set-collection keys]
  (map vector keys (map (partial total-count set-collection) keys)))

(defn unused-words [commit] (difference (people/get-words commit) (people/people-all-names)))

(defn top-counts [commits]
  (let [commiters-collection (map people/commiters commits)]
    (frequencies-in-set-collection commiters-collection (people/people-names))))

(defn all-unused-words [commits]
  (let [unused-words-collection (map unused-words commits)]
    (let [unused-words (reduce union unused-words-collection)]
      (frequencies-in-set-collection unused-words-collection unused-words))))


(defn pair-names [commits] (filter is-pair? (map people/commiters commits)))

(defn top-unused-words [commits]
  (take 50 (sort-by last #(compare %2 %1) (all-unused-words commits))))

(defn unused-words-starting [prefix commits]
  (filter (fn [commit] (.startsWith (first commit) prefix)) (all-unused-words commits)))