(ns casper-gource.main (:gen-class)
  (:use
    [clojure.contrib.duck-streams]
  )
)
;git log --date=raw --pretty="format:%cd%n%s" --name-status > ~/Projects/clojure-samples/CasperGource/commits.txt

(defn -main [& args] (println "Hello Casper Gource"))
