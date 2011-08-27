(use '[clojure.contrib.duck-streams ] )

;git log --date=short --pretty="format:%cd %s" > /tmp/commits

(defn all-lines (read-lines "/private/tmp/commits"))

