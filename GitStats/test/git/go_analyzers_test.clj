(ns git.go-analyzers-test (:use [clojure.test] [git.go-analyzers]))

(deftest test-parse-lines
  (is (= {"0.2508" 1322096461000} (parse-lines "0.2508 2011-11-24T01:01:01Z")))
  (is (= {"0.2508" 1322096461000} (parse-lines-for-core "0.2508-0.12-0.546-0.211-0.85-0.12 2011-11-24T01:01:01Z")))
  (is (= {"0.626" 1322096461000} (parse-lines-for-aim "0.2856-0.80-0.165-0.80-0.626-0.261 2011-11-24T01:01:01Z")))
  (is (= {"0.261" 1322096461000} (parse-lines-for-identity "0.2856-0.80-0.165-0.80-0.626-0.261 2011-11-24T01:01:01Z")))
  (is (= {"0.165" 1322096461000} (parse-lines-for-track "0.2856-0.80-0.165-0.80-0.626-0.261 2011-11-24T01:01:01Z")))
  )

(deftest test-to-map
  (is (= {"0.2508" 1322096461000} (to-map ["0.2508-0.12-0.546-0.211-0.85-0.12 2011-11-24T01:01:01Z"
                                           "0.2508-0.13-0.546-0.211-0.85-0.12 2011-11-24T02:01:01Z"] parse-lines-for-core)))
  )

(deftest test-parse-system-tests
  )

(deftest test-time
  (is (= 1322096461000 (timestamp "0.2508 2011-11-24T01:01:01Z")))
  )

(deftest test-time
  (is (= 1332845810000 (timestamp "0.2508 2012-03-27T11:56:50+01:00")))
  )

(deftest test-end-to-end-times-unmatched
  (let [
         start-times ["1 2011-11-18T16:06:59Z"]
         system-tests-times ["0.2508-0.12-0.546-0.211-0.85-0.12 2011-11-24T12:30:45Z"]
       ]
    (is (= [] (end-to-end-times start-times system-tests-times parse-lines-for-core))))
)

(deftest test-end-to-end-times-matched
  (let [
         start-times ["0.2508 2011-11-24T11:34:18Z"
                     "0.2509 2011-11-25T01:00:00Z"]
         system-tests-times ["0.2508-0.12-0.546-0.211-0.85-0.12 2011-11-24T12:30:45Z"
                             "0.2509-0.12-0.546-0.211-0.85-0.12 2011-11-25T01:25:10Z"]
       ]
    (is (= [{:build "0.2508" :time 3387} {:build "0.2509" :time 1510}] (end-to-end-times start-times system-tests-times parse-lines-for-core))))
)

(deftest test-end-to-end-times-matched-multiple-times
  (let [
         start-times ["0.2508 2011-11-24T11:30:00Z"
                     "0.2509 2011-11-25T01:00:00Z"]
         system-tests-times ["0.2508-0.12-0.546-0.211-0.85-0.12 2011-11-24T12:30:00Z"
                             "0.2508-0.13-0.546-0.211-0.85-0.12 2011-11-24T13:25:00Z"]
       ]
    (is (= [{:build "0.2508" :time 3600}] (end-to-end-times start-times system-tests-times parse-lines-for-core))))
)

(deftest test-latest-fastest-times
  (is (= {:fastest {:build "0.2507" :time 2600} :latest {:build "0.2508" :time 3600}}
        (latest-fastest-times [{:build "0.2506" :time 4600} {:build "0.2507" :time 2600} {:build "0.2508" :time 3600}])))
  )


