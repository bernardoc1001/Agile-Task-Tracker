(ns agile-task-tracker.elasticsearch
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]))

(def db-address "http://127.0.0.1:9200") ;Note that currently the database is running on localhost

(defn put-task-info
  [doc]
  (let [conn (esr/connect db-address)
        index-name "task-info"
        id (:task-id doc)
        mapping "task-info-mapping"
        mapping-types {mapping {:properties {:task-id            {:type "string"}
                                             :task-title         {:type "string"}
                                             :description        {:type "string"}
                                             :created-by         {:type "string"}
                                             :assignees          {:type "string"}
                                             :original-estimate  {:type "double"}
                                             :remaining-estimate {:type "double"}
                                             :epic               {:type "string"}
                                             :assigned-sprint    {:type "string"}
                                             :priority-level     {:type "integer"}
                                             :task-state         {:type "string"}
                                             :logged-time        {:type "double"}}}}]

    (if (not (esi/exists? conn index-name))
      ;create index with mappings
      (esi/create conn index-name :mappings mapping-types))

    ;put the doc in the index
    (esd/put conn index-name mapping id doc)))

(defn get-doc-by-id
  [index-name mapping id]
  (let [conn (esr/connect db-address)]
    (esd/get conn index-name mapping id)))


