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


(defn core-lines [] (read-lines "/Users/rudayaku/Projects/clojure-samples/GitStats/core-commits.txt"))
(defn aim-lines [] (read-lines "/Users/rudayaku/Projects/clojure-samples/GitStats/aim-commits.txt"))

(defn object-maps [pairs first-key second-key] (map (fn [pair] { first-key (first pair) second-key (last pair) }) pairs))

(defn casper-maps [casper-pairs] (object-maps casper-pairs :name :core))
(defn aim-maps [aim-pairs] (object-maps aim-pairs :name :aim))
(defn top-git-json [casper-pairs aim-pairs] (json-str (map #(merge %1 %2) (casper-maps casper-pairs) (aim-maps aim-pairs))))
(defn top-git [] (top-git-json (top-counts (core-lines)) (top-counts (aim-lines))))

(defn all-words-json [pairs] (json-str (object-maps pairs :word :count)))
(defn all-words [] (all-words-json (top-unused-words (core-lines))))

(defn form-name [names] (reduce (fn ([] "None") ([x y] (str x "-" y))) names))
(defn pair-frequencies [] (frequencies (concat (pair-names (core-lines)) (pair-names (aim-lines)))))
(defn pair-name-count [] (map (fn [line] [(form-name (first line)) (last line)]) (pair-frequencies)))
(defn pair-counts [] (json-str (object-maps (pair-name-count) :pair :count)))


(defroutes main-routes
  (GET "/" [] "<h1>Hello World</h1>")
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
    (run-jetty main-routes {:port 9090})
  ))