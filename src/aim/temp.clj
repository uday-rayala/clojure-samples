(ns student.dialect (:require [clojure.string :as str]))
(defn germanize
  [sentence]
  (def german-letters {"a" "ä" "u" "ü" "o" "ö" "ss" "ß"})
  (doseq [[original-letter new-letter] german-letters]
    (str/replace sentence original-letter new-letter)))

(defn germanize1
  [sentence]
  (def german-letters {"a" "ä" "u" "ü" "o" "ö" "ss" "ß"})
  (doseq [[original-letter new-letter] german-letters]
    (str/replace sentence "a" "ä")))

(print (germanize1 "elosska"))