(ns aim (:use clojure.contrib.zip-filter.xml)
  (:require   [clojure.zip :as zip]
              [clojure.xml :as xml]
              [clojure.contrib.zip-filter :as zf]
  ))

(defn parse-str [s] (zip/xml-zip (xml/parse (new org.xml.sax.InputSource (new java.io.StringReader s)))))

(def book [:book])
(def article [:article])
(def book-title [:book :title])

(defn locate [xml, locator] (apply xml-> xml locator))
(defn children [xml, locator] (locate xml (concat locator [zf/children])))
(defn children-count [xml, locator] (count (children xml locator)))

(defn exists? [xml, locator] (not-empty (locate xml locator)))
(defn not-exists? [xml, locator] (not (exists? xml locator)))

(defn has-markup? [xml, locator] (< 1 (children-count xml locator)))
(defn none-exists? [xml, locators] (every? (partial not-exists? xml) locators))

(defn document-is [other-type & condition] (if (every? identity condition) other-type :valid))

(defn only-supports [supported-types] (fn [xml] (document-is :unsupported (none-exists? xml supported-types))))
(defn mandatory [parent, locator] (fn [xml] (document-is :invalid (exists? xml parent) (not-exists? xml locator))))
(defn markup-in [parent, locator] (fn [xml] (document-is :unsupported (exists? xml parent) (has-markup? xml locator))))

(def all-criteria [
  (only-supports [book, article])
  (mandatory book book-title)
  (markup-in book book-title)
])

(defn is-valid [types] (let [value (some #{:invalid, :unsupported} types)] (if value value :valid)))

(defn document-type [xml] (is-valid (map (fn [criteria] (criteria xml)) all-criteria)))

(assert (= :valid (is-valid [])))
(assert (= :valid (is-valid [:valid, :valid])))
(assert (= :invalid (is-valid [:valid, :invalid])))
(assert (= :unsupported (is-valid [:valid, :unsupported])))

(assert (= :invalid (document-is :invalid true)))
(assert (= :valid (document-is :invalid true false)))
(assert (= :valid (document-is :invalid false)))

(def unsupported-content (parse-str "
  <publisher>
    <issue></issue>
  </publisher>
"))

(def valid-book (parse-str "
  <publisher>
    <book>
      <title>Some title</title>
    </book>
  </publisher>
"))

(def book-title-with-markup (parse-str "
  <publisher>
    <book>
      <title><a>Some</a> title</title>
    </book>
  </publisher>
"))

(def book-no-title (parse-str "
  <publisher>
    <book>
    </book>
  </publisher>
"))


(assert (= :unsupported ((only-supports [book, article]) unsupported-content)))
(assert (= :valid ((only-supports [book, article]) valid-book )))
(assert (= :valid ((mandatory book book-title) valid-book )))
(assert (= :valid ((mandatory book book-title) unsupported-content)))
(assert (= :invalid ((mandatory book book-title) book-no-title)))

(assert (= :valid (document-type valid-book)))
(assert (= :unsupported (document-type unsupported-content)))
(assert (= :unsupported (document-type book-title-with-markup)))
