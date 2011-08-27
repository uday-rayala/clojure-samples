(ns casper-gource.main (:gen-class)
  (:use
    [clojure.contrib.duck-streams :as duck]
    [casper-gource.parsers :as parsers]
  )
)
;git log --date=raw --pretty="format:%cd%n%s" --name-status > ~/Projects/clojure-samples/CasperGource/commits.txt
(defn all-log-messages [] (duck/read-lines "commits.txt"))

(defn -main [& args] (println (->> (all-log-messages)
									(parsers/group-commits)
									(map parsers/generate-gcourse-log)
									(map parsers/serialize-gcource-log)
									(reverse)
									(flatten)
									(duck/write-lines "gcourse.log")
)))
