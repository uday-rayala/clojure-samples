(ns git.main (:gen-class)
  (:use
    [clojure.contrib.duck-streams]
    [clojure.contrib.json]
    [compojure.core]
    [ring.adapter.jetty]
    [git.parsers]
  )
  (:require [compojure.route :as route] [git.people :as people])
)

(use '[clojure.string :only (join)])


(defn story-mappings [] (->> "story-mappings.txt"
                             read-lines
                             (map (fn [line] (clojure.string/split line #":")))
                             (filter #(= 2 (count %)))))
(defn core-lines [] (read-lines "core-commits.txt"))
(defn core-code-change-lines [] (read-lines "core-code-change-commits.txt"))
(defn aim-lines [] (read-lines "aim-commits.txt"))
(defn failed-build-numbers [] (read-lines "failed-builds.txt"))
(defn core-message-and-changes [] (read-lines "core-message-and-changes.txt"))

(defn count-maps [pairs first-key second-key] (map (fn [pair] { first-key (first pair) second-key (last pair) }) pairs))
(defn merge-count-maps [map1 map2 key] (map #(merge %1 %2) (sort-by key map1) (sort-by key map2)))

(defn casper-maps [] (count-maps (top-counts (core-lines)) :name :core))
(defn aim-maps [] (count-maps (top-counts (aim-lines)) :name :aim))

(defn top-git-json [caspers aims] (json-str (sort-by :core (merge-count-maps caspers aims :name))))

(defn top-git [] (top-git-json (casper-maps) (aim-maps)))

(defn all-words-json [pairs] (json-str (count-maps pairs :word :count)))
(defn all-words [] (all-words-json (top-unused-words (core-lines))))

(defn form-name [names] (reduce (fn ([] "None") ([x y] (str x "-" y))) names))
(defn pair-frequencies [] (frequencies (concat (pair-names (core-lines)) (pair-names (aim-lines)))))
(defn pair-name-count [] (map (fn [line] [(form-name (first line)) (last line)]) (pair-frequencies)))
(defn pair-counts [] (json-str (count-maps (pair-name-count) :pair :count)))

(def excluded-people #{"harinee", "kief", "karl"})

(defn people-who-can-pair [] (remove excluded-people (sort (people/people-names))))
(defn pair-counts-seperate [] (json-str (let [all-names (people-who-can-pair) freq (pair-frequencies)]
  {:names all-names :pairing (pairing-matrix all-names freq)})))

(defn code-change [commit] {:date (first commit) :message (second commit) :size (code-size (last commit))})

(defn buckets [x] (int (/ (:size x) 100)))
(defn small-commits [x] (< 300 (:size x)))
(defn group-by-size [objects] (let [grouped (group-by buckets (filter small-commits objects))]
  (map (fn [x] {:seriesKey (first x) :seriesValue (last x)}) grouped)))

(defn sort-commits [objects] (sort-by (fn [x] (.parse (new java.text.SimpleDateFormat "EEE MMM d HH:mm:ss yyyy Z") (:date x))) objects))
(defn top-big-commits [objects] (take 100 (sort-by :size #(compare %2 %1) objects)))

(defn code-changes [] (->>
  (core-code-change-lines)
  (group-commits)
  (map code-change)
  (group-by-size)
  (json-str)
))
(defn code-changes-plain [] (->>
  (core-code-change-lines)
  (group-commits)
  (map code-change)
;  (top-big-commits)
  (sort-commits)
  (json-str)
))

;2011-06-07T16:20:10+01:00
(defn get-hours [build] (Integer/parseInt (first (re-seq #"\d\d(?=:\d\d:\d\d)" build))))

(defn failing-build-details [builds] (let [details (group-by get-hours builds)] (map (fn [h] (count (details h))) (range 0 23))))

(defn failed-builds-by-day [] (->>
  (failed-build-numbers)
  (failing-build-details)
  (json-str)
))

(defn code-commits-by-day [] (->>
  (core-lines)
  (json-str)
))

(defn links [link-map] (map (fn [l] (str "<a href=\"/" (first l) "\">Click here for " (second l) "</a>")) link-map))
(defn home-links [] (apply str (interpose "<br/>" (links {
  "top-git.html" "Top Git"
  "code-changes.html" "Code Changes"
  "go-dashboard.html" "Go Dashboard"
  "changes-by-functional-area.html" "Changes by area"
}))))

(defn code-area-worked-by [name] (->>
    (core-message-and-changes)
    (group-commits)
    (map (fn [commit] {:names (people/commiters (first commit)) :files (next commit)}))
    (filter (fn [obj] (contains? (:names obj) name)))
    (map (fn [obj] (group-by-areas (:files obj))))
    (flatten)
    (merge-all-groups)
))

(defn code-area-worked [] (json-str (map (fn [name] {:name name :changes (code-area-worked-by name)}) (people-who-can-pair))))

(defroutes main-routes
  (GET "/" [] (home-links))
  (GET "/top-git.json" [] (top-git))
  (GET "/all-words.json" [] (all-words))
  (GET "/pair-counts.json" [] (pair-counts))
  (GET "/pair-counts-seperate.json" [] (pair-counts-seperate))
  (GET "/code-changes.json" [] (code-changes))
  (GET "/code-changes-plain.json" [] (code-changes-plain))
  (GET "/failed-builds-by-day.json" [] (failed-builds-by-day))
  (GET "/code-commits-by-day.json" [] (code-commits-by-day))
  (GET "/code-area-worked.json" [] (code-area-worked))
  (route/resources "/")
)

(defn -main [& args]
  (doseq []
;    (println (top-git))
;    (println (all-words))
;    (println (unused-words-starting "m" (core-lines)))
;    (println (pair-counts))
    (run-jetty main-routes {:port 9876})
  ))
