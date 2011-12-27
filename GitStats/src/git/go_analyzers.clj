(ns git.go-analyzers
  (:require [clojure.contrib.string :as string])
)

(defn get-seconds [date-time] (let
                                     [input-formatter (new java.text.SimpleDateFormat "yyyy-MM-dd'T'HH:mm:ss'Z'")]
                                     (.getTime (.parse input-formatter date-time))))

(defn build [line] (first (string/split #"\s+" line)))
(defn timestamp [line] (get-seconds (last (string/split #"\s+" line))))

(defn parse-lines [line] {(first (string/split #"-" (build line))) (timestamp line)})
(defn parse-lines-for-core [line] {(first (string/split #"-" (build line))) (timestamp line)})
(defn parse-lines-for-aim [line] {(nth (string/split #"-" (build line)) 4) (timestamp line)})
(defn parse-lines-for-identity [line] {(nth (string/split #"-" (build line)) 5) (timestamp line)})
(defn parse-lines-for-track [line] {(nth (string/split #"-" (build line)) 2) (timestamp line)})

(defn time-take [start-map end-entry]
  (let [build (key end-entry) end-time (val end-entry)]
    {:build build :time (/ (- end-time (start-map build end-time)) 1000)}))

(defn to-map [lines parse-fn] (apply (partial merge-with min) (map parse-fn lines)))

(defn end-to-end-times [start-lines system-tests-lines parse-fn]
  (let [start-map (to-map start-lines parse-lines)
        system-tests-map (to-map system-tests-lines parse-fn)]
    (vec (take-last 3 (sort-by :build (remove #(>= 0 (%1 :time)) (map (partial time-take start-map) system-tests-map)))))))

(defn latest-fastest-times [ build-times]
  {:fastest (first (sort-by :time build-times))
   :latest (last (sort-by :build build-times))})


