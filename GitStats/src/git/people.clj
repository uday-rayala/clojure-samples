(ns git.people
  (:use [clojure.set]))

(def people
    [
    {:name "uday"  :alias #{}},
    {:name "mark"  :alias #{}},
    {:name "pat"  :alias #{}},
    {:name "charles"  :alias #{}},
    {:name "liz"  :alias #{}},
    {:name "ken"  :alias #{}},
    {:name "chris"  :alias #{}},
    {:name "suzuki"  :alias #{}},
    {:name "alex"  :alias #{}},
    {:name "alinoor"  :alias #{}},
    {:name "duncan"  :alias #{}},
    {:name "rob"  :alias #{}},
    {:name "karl"  :alias #{}},
    {:name "mushtaq"  :alias #{"mustaq"}},
    {:name "marc"  :alias #{}},
    {:name "kief"  :alias #{}},
    {:name "jose"  :alias #{}},
    {:name "micheal" :alias #{"mike"}},
    ]
)

(defn person-names [person] (conj (:alias person) (:name person)))
(defn people-names [] (map :name people))
(defn people-all-names [] (reduce #(union %1 %2) #{} (map person-names people)))
(defn has-person [name-set person] (not (empty? (intersection name-set (person-names person)))))
(defn get-person-names [words] (set (map :name (filter #(has-person words %) people))))
