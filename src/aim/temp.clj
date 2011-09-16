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


(defn div618 [p1 p2]
    (let [ratio [0.,0.191,0.236,0.382,0.5,0.618,0.809,1.]
          price (fn [r] (if (<= p1 p2) (+ p1 (* (- p2 p1) r)) (- p1 (* (- p1 p2) r))))]

    (doall (if (<= p1 p2)
        (map #(println (format "-------%.3f   %.2f-------" %1 (price %1))) (reverse ratio))
        (map #(println (format "-------%.3f   %.2f-------" %1 (price %1)))  ratio)))))


(defn div618 [p1 p2]
    (let [ratio [0.,0.191,0.236,0.382,0.5,0.618,0.809,1.]
          price (fn [r] (if (<= p1 p2) (+ p1 (* (- p2 p1) r)) (- p1 (* (- p1 p2) r))))]

    (doall (map #(println (format "-------%.3f   %.2f-------" %1 (price %1))) (if (<= p1 p2) (reverse ratio) ratio)))))

(defn div618 [p1 p2]
    (let [ratio [0.,0.191,0.236,0.382,0.5,0.618,0.809,1.]
          price (fn [r] (if (<= p1 p2) (+ p1 (* (- p2 p1) r)) (- p1 (* (- p1 p2) r))))]

    (map (fn [x] [x (price x)]) (if (<= p1 p2) (reverse ratio) ratio))))

(doall (map #(println (format "-------%.3f   %.2f-------" (first %1) (last %1))) (div618 1 2)))