(ns git.main (:gen-class)
  (:use
    [clojure.contrib.duck-streams]
    [clojure.contrib.json]
    [compojure.core]
    [ring.adapter.jetty]
    [git.parsers]
  )
  (:require
    [compojure.route :as route]
    [compojure.handler :as handler]
    [clojure.contrib.string :as string]
    [git.people :as people]
    [git.go-analyzers :as go])
)

(use '[clojure.string :only (join)])


(defn story-mappings [] (->> "story-mappings.txt"
                             read-lines
                             (map (fn [line] (clojure.string/split line #":")))
                             (filter #(= 2 (count %)))))
(defn today-commits [] (remove (fn [line] (string/blank? line)) (read-lines "logs/today-commits.txt")))
(defn core-lines [] (read-lines "logs/core-commits.txt"))
(defn core-code-change-lines [] (read-lines "logs/core-code-change-commits.txt"))
(defn aim-code-change-lines [] (read-lines "logs/aim-code-change-commits.txt"))
(defn aim-lines [] (read-lines "logs/aim-commits.txt"))
(defn failed-build-numbers [] (read-lines "logs/failed-builds.txt"))
(defn core-message-and-changes [] (read-lines "logs/core-message-and-changes.txt"))
(defn system-tests-complete-times [] (read-lines "logs/system-tests-complete-times"))
(defn core-start-times [] (read-lines "logs/core-start-times"))
(defn aim-start-times [] (read-lines "logs/aim-start-times"))
(defn identity-start-times [] (read-lines "logs/identity-start-times"))
(defn track-start-times [] (read-lines "logs/track-start-times"))

(defn count-maps [pairs first-key second-key] (map (fn [pair] { first-key (first pair) second-key (last pair) }) pairs))
(defn merge-count-maps [map1 map2 key] (map #(merge %1 %2) (sort-by key map1) (sort-by key map2)))


(def stories (->> (story-mappings) (map (fn [story] {:story (first story) :area (last story)  } ))))
(def commiters-stories
  (merge (git.parsers/commiters-and-stories (core-lines))
         (git.parsers/commiters-and-stories (aim-lines))))
(defn commiters-for-story [story]
  (->> commiters-stories
       (filter (fn [story-commiter] (= story (:story story-commiter))))
       (mapcat #(:people %))
       set))

(defn top-git-json [caspers aims] (json-str (sort-by (fn [x] (+ (:core x) (:aim x))) #(compare %2 %1) (merge-count-maps caspers aims :name))))

(defn casper-maps [] (count-maps (top-counts (core-lines)) :name :core))
(defn aim-maps [] (count-maps (top-counts (aim-lines)) :name :aim))
(defn top-git [] (top-git-json (casper-maps) (aim-maps)))

(defn all-lines [] (concat (core-lines) (aim-lines)))
(defn top-git-with-pairing []
  (let [all-counts (count-maps (top-counts (all-lines)) :name :all) solo-counts (count-maps (top-counts-solo (all-lines)) :name :solo)]
    (json-str (merge-count-maps all-counts solo-counts :name))))

(defn captalize-names [x] (map (fn [a] [(string/capitalize (first a)) (last a)]) x))
(defn today-top-counts [] (captalize-names (top-counts (today-commits))))
(defn today-top-counts-by-count [] (map #(apply hash-map (reverse %)) (today-top-counts)))
(defn comma-seperated [x y] (str x ", " y))
(defn today-commit-mixed-count [] (apply (partial merge-with comma-seperated) (today-top-counts-by-count)))
(defn today-commits-sorted [] (sort #(compare %2 %1) (today-commit-mixed-count)))
(defn top-names [] (take 5 (today-commits-sorted)))
(defn top-git-today [] (json-str {:total (count (today-commits)) :top (top-names)}))

(defn all-words-json [pairs] (json-str (count-maps pairs :word :count)))
(defn all-words [] (all-words-json (top-unused-words (core-lines))))

(defn form-name [names] (reduce (fn ([] "None") ([x y] (str x "-" y))) names))
(defn pair-frequencies [] (frequencies (concat (pair-names (core-lines)) (pair-names (aim-lines)))))
(defn pair-name-count [] (map (fn [line] [(form-name (first line)) (last line)]) (pair-frequencies)))
(defn pair-counts [] (json-str (count-maps (pair-name-count) :pair :count)))

(defn pair-counts-seperate [] (json-str (let [all-names (people/people-who-can-pair) freq (pair-frequencies)]
  {:names all-names :pairing (pairing-matrix all-names freq)})))

(defn code-change [commit] {:date (first commit) :message (second commit) :size (code-size (last commit)) :story (people/get-story-number (second commit))})

(defn sort-commits [objects] (sort-by (fn [x] (.parse (new java.text.SimpleDateFormat "EEE MMM d HH:mm:ss yyyy Z") (:date x))) objects))
(defn top-big-commits [objects] (take 100 (sort-by :size #(compare %2 %1) objects)))

(defn core-commits-with-size-of-change [story]
  (->> (core-code-change-lines)
       (group-commits)
       (map code-change)
       (filter (fn [codechange] (= (str "#" (:story story)) (:story codechange) )))
))

(defn aim-commits-with-size-of-change [story]
  (->> (aim-code-change-lines)
       (group-commits)
       (map code-change)
       (filter (fn [codechange] (= (str "#" (:story story)) (:story codechange) )))
))

(defn commits-with-size-of-change [story]
  (concat (core-commits-with-size-of-change story) (aim-commits-with-size-of-change story)))

(defn commit-size-map [commit] { :size (:size commit) :people (people/commiters (:message commit)) })

(defn stories-and-commiters []
  (->> stories
       (map (fn [story] (let [commit-size-people-maps (map commit-size-map (commits-with-size-of-change story))] {:story (:story story)
                        :area (:area story)
                        :commiters (set (mapcat :people commit-size-people-maps))
                        :commits commit-size-people-maps})))))


(defn stories-and-commiters-json [] (json-str (stories-and-commiters)))
(defn functional-areas-and-commiters-json []
    (json-str (group-by (fn [story] (:area story)) (stories-and-commiters))))

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
  "who-should-i-pair.html" "Who Should I Pair ?"
  "changes-by-story.html" "Who worked on which story?"
  "changes-by-story-functional-area.html" "Who worked in which app functional area?"
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

(defn code-area-worked [] (json-str (map (fn [name] {:name name :changes (code-area-worked-by name)}) (people/people-who-can-pair))))

(defn all-end-to-end-times [] {
   :core (go/latest-fastest-times (go/end-to-end-times (core-start-times) (system-tests-complete-times) go/parse-lines-for-core))
   :aim (go/latest-fastest-times (go/end-to-end-times (aim-start-times) (system-tests-complete-times) go/parse-lines-for-aim))
   :identity (go/latest-fastest-times (go/end-to-end-times (identity-start-times) (system-tests-complete-times) go/parse-lines-for-identity))
   :track (go/latest-fastest-times (go/end-to-end-times (track-start-times) (system-tests-complete-times) go/parse-lines-for-track))
   })

(defroutes main-routes
  (GET "/" [] (home-links))
  (GET "/top-git.json" [] (top-git))
  (GET "/top-git-with-pairing.json" [] (top-git-with-pairing))
  (GET "/top-git-today.json" [] (top-git-today))
  (GET "/all-words.json" [] (all-words))
  (GET "/pair-counts.json" [] (pair-counts))
  (GET "/pair-counts-seperate.json" [] (pair-counts-seperate))
  (GET "/code-changes-plain.json" [] (code-changes-plain))
  (GET "/failed-builds-by-day.json" [] (failed-builds-by-day))
  (GET "/code-commits-by-day.json" [] (code-commits-by-day))
  (GET "/code-area-worked.json" [] (code-area-worked))
  (GET "/stories-and-commiters.json" [] (stories-and-commiters-json))
  (GET "/functional-areas-and-commiters.json" [] (functional-areas-and-commiters-json))
  (GET "/commiters.json" [message] (json-str (apply vector (people/commiters message))))
  (GET "/end-to-end-times.json" [] (json-str (all-end-to-end-times)))
  (route/resources "/")
)

(defn -main [& args]
  (doseq []
;    (println (top-git))
;    (println (all-words))
;    (println (unused-words-starting "m" (core-lines)))
;    (println (pair-counts))
    (run-jetty (handler/api main-routes) {:port 9876})
  ))
