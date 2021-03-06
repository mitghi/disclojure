(ns disclojure.sampler-test
  (:require [clojure.test :refer :all]
            [disclojure.track :as track]))

(deftest sampler-test
  (testing "data"
    (is (= [{:time 0 :sample :test :part :sampler :amp 1 :bpm 100 :beats 8 :start-beat 0 :duration 8 :cutoff 10000}]
           (track/sampler [[0 :test 8]]))))

  (testing "start-beat"
    (is (= [{:time 0 :sample :test :part :sampler :amp 1 :bpm 100 :beats 8 :start-beat 2 :duration 8 :cutoff 10000}]
           (track/sampler [[0 :test 8 :start-beat 2]]))))

  (testing "amp"
    (is (= [{:time 0 :sample :test :part :sampler :amp 2 :bpm 100 :beats 8 :start-beat 0 :duration 8 :cutoff 10000}]
           (track/sampler [[0 :test 8 :amp 2]]))))

  (testing "cutoff"
    (is (= [{:time 0 :sample :test :part :sampler :amp 1 :bpm 100 :beats 8 :start-beat 0 :duration 8 :cutoff 1000}]
           (track/sampler [[0 :test 8 :cutoff 1000]]))))

  (testing "times"
    (is (= [{:time 0 :sample :test} {:time 4 :sample :test}]
           (->> (track/sampler [[[0 4] :test 8]])
                (map #(select-keys % [:time :sample])))))
    (is (= [{:time 0 :sample :test} {:time 4 :sample :test2}]
           (->> (track/sampler [[[4] :test2 8]
                                [[0] :test 8]])
                (map #(select-keys % [:time :sample])))))))
