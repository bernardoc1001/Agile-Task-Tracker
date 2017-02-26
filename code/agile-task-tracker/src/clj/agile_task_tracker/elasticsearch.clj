(ns agile-task-tracker.elasticsearch
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]))

(def db-address "http://127.0.0.1:9200") ;Note that currently the database is running on localhost

(defn post-index-doc
  [doc]
  (let [conn (esr/connect db-address)]
    (println (esd/create conn "test-backlog" "test-mapping" doc))))