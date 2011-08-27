(ns git.main (:gen-class)
  (:use
    [clojure.contrib.duck-streams]
    [clojure.contrib.json]
    [compojure.core]
    [ring.adapter.jetty]
    [git.parsers]
  )
  (:require [compojure.route :as route])
)

(use '[clojure.string :only (join)])


(defn core-lines [] (read-lines "core-commits.txt"))
(defn aim-lines [] (read-lines "aim-commits.txt"))

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


(defroutes main-routes
  (GET "/" [] "<a href=\"/top-git.html\">Click here for stats</a>")
  (GET "/top-git.json" [] (top-git))
  (GET "/all-words.json" [] (all-words))
  (GET "/pair-counts.json" [] (pair-counts))
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