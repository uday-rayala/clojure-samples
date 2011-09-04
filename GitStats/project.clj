(defproject git-stats "0.1.0"
  :description "git"
  :dependencies [[org.clojure/clojure "1.2.0"],
		 [org.clojure/clojure-contrib "1.2.0"],
		 [ring/ring-jetty-adapter "0.3.11"],
                 [compojure "0.6.4"]]
  :dev-dependencies [[swank-clojure "1.2.1"]]
  :main git.main)
