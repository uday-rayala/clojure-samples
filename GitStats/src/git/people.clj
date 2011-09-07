(ns git.people
  (:use [clojure.set]))

(def people
    [
    {:name "uday"  :alias #{}},
    {:name "mark"  :alias #{}},
    {:name "pat"  :alias #{}},
    {:name "charles"  :alias #{}},
    {:name "liz"  :alias #{"lix"}},
    {:name "ken"  :alias #{}},
    {:name "chris"  :alias #{"chrisj"}},
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
    {:name "harinee"  :alias #{}},
    {:name "michael" :alias #{"mike", "micheal"}},
    ]
)

(def excluded-people #{"harinee", "kief", "karl"})

(def stop-words #{"a", "an", "the", "so", "no", "to", "of", "for", "and", "in", "master", "branch", "on", "from", "it", "is", "with", "up", "into", "that"})

(defn person-names [person] (conj (:alias person) (:name person)))
(defn people-names [] (map :name people))
(defn people-all-names [] (reduce #(union %1 %2) #{} (map person-names people)))
(defn has-person [name-set person] (not (empty? (intersection name-set (person-names person)))))
(defn get-person-names [words] (set (map :name (filter #(has-person words %) people))))

(defn get-words [commit] (set (remove stop-words (re-seq #"[a-zA-Z]+(?=[^a-zA-z]?)" (.toLowerCase commit)))))
(defn commiters [commit] (let [words (get-words commit)] (get-person-names words)))

(defn get-story-number [commit] (first (re-seq #"#[0-9]+" commit)))

(defn people-who-can-pair [] (remove excluded-people (sort (people-names))))

(defn commiters-and-story [commit]
  (let [words (get-words commit)]
    {:people (get-person-names words) :story (get-story-number commit)}))


