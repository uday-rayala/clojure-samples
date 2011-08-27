(ns git.parsers
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
    {:name "micheal" :alias #{"mike"}},
    ]
)
(defn person-names [person] (conj (:alias person) (:name person)))
(defn people-names [] (map :name people))
(defn people-all-names [] (reduce #(union %1 %2) #{} (map person-names people)))
(defn has-person [name-set person] (not (empty? (intersection name-set (person-names person)))))

(def stop-words #{"a", "an", "the", "so", "no", "to", "of", "for", "and", "in", "master", "branch", "on", "from", "it", "is", "with", "up", "into", "that"})

(defn get-words [commit] (set (remove stop-words (re-seq #"[a-zA-Z]+(?=[^a-zA-z]?)" (.toLowerCase commit)))))
(defn parse-names [commit] (let [words (get-words commit)] (set (map :name (filter #(has-person words %) people)))))

(defn parse-all-names [commits] (map parse-names commits))

(defn pair-names [commits] (filter (fn [names] (= 2 (count names))) (parse-all-names commits)))

(defn top-count [name-set-seq person-name] (count (filter (fn [name-set] (name-set person-name)) name-set-seq)))

(defn top-counts [commits]
  (let [name-set-seq (parse-all-names commits)]
    (sort-by (fn [pair] (first pair)) #(compare %2 %1) (map vector (people-names) (map (partial top-count name-set-seq) (people-names))))))

(defn unused-words [commit] (difference (get-words commit) (people-all-names)))

(defn all-unused-words [commits]
  (let [unused-words-set (map unused-words commits)]
    (let [unused-words (reduce union unused-words-set)]
      (sort-by (fn [pair] (last pair)) #(compare %2 %1) (map vector unused-words (map (partial top-count unused-words-set) unused-words))))))

(defn top-unused-words [commits]
  (take 50 (all-unused-words commits)))

(defn unused-words-starting [prefix commits]
  (filter (fn [commit] (.startsWith (first commit) prefix)) (all-unused-words commits)))