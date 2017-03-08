(ns agile-task-tracker.elasticsearch
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]))

(def db-address "http://127.0.0.1:9200") ;Note that currently the database is running on localhost

(defn put-task-info
  [doc]
  (let [conn (esr/connect db-address)
        index-name "task-info"
        id (:task-id doc)
        mapping "task-info-mapping"
        mapping-types {mapping {:properties {:task-id            {:type "string" :index "not_analyzed"} ;not analyzed allows for the exact term to be queried
                                             :task-title         {:type "string"}
                                             :description        {:type "string"}
                                             :created-by         {:type "string"}
                                             :assignees          {:type "string"}
                                             :estimated-time     {:type "double"}
                                             :epic               {:type "string"}
                                             :sprint-id          {:type "string" :index "not_analyzed"} ;not analyzed allows for the exact term to be queried
                                             :priority-level     {:type "string"}
                                             :task-state         {:type "string"}
                                             :logged-time        {:type "double"}
                                             :project-id         {:type "string" :index "not_analyzed"}}}}]

    (if (not (esi/exists? conn index-name))
      ;create index with mappings
      (esi/create conn index-name :mappings mapping-types))

    ;put the doc in the index
    (esd/put conn index-name mapping id doc)))

(defn put-org-info
  [doc]
  (let [conn (esr/connect db-address)
        index-name "org-info"
        id (:org-id doc)
        mapping "org-info-mapping"
        mapping-types {mapping {:properties {:org-id            {:type "string" :index "not_analyzed"} ;not analyzed allows for the exact term to be queried
                                             :org-name          {:type "string"}}}}]

    (if (not (esi/exists? conn index-name))
      ;create index with mappings
      (esi/create conn index-name :mappings mapping-types))

    ;put the doc in the index
    (esd/put conn index-name mapping id doc)))

(defn put-proj-info
  [doc]
  (let [conn (esr/connect db-address)
        index-name "proj-info"
        id (:proj-id doc)
        mapping "proj-info-mapping"
        mapping-types {mapping {:properties {:proj-id            {:type "string":index "not_analyzed"} ;not analyzed allows for the exact term to be queried
                                             :proj-name          {:type "string"}
                                             :org-id             {:type "string"}
                                             :man-hours          {:type "double"}
                                             :start-date         {:type "string"}
                                             :end-date           {:type "string"}}}}]

    (if (not (esi/exists? conn index-name))
      ;create index with mappings
      (esi/create conn index-name :mappings mapping-types))

    ;put the doc in the index
    (esd/put conn index-name mapping id doc)))

(defn put-sprint-info
  [doc]
  (let [conn (esr/connect db-address)
        index-name "sprint-info"
        id (:sprint-id doc)
        mapping "sprint-info-mapping"
        mapping-types {mapping {:properties {:sprint-id          {:type "string":index "not_analyzed"} ;not analyzed allows for the exact term to be queried
                                             :sprint-name        {:type "string"}
                                             :start-date         {:type "string"}
                                             :end-date           {:type "string"}
                                             :proj-id            {:type "double"}
                                             :sprint-state       {:type "string"}}}}]

    (if (not (esi/exists? conn index-name))
      ;create index with mappings
      (esi/create conn index-name :mappings mapping-types))

    ;put the doc in the index
    (esd/put conn index-name mapping id doc)))

(defn get-doc-by-id
  [index-name mapping id]
  (let [conn (esr/connect db-address)]
    (esd/get conn index-name mapping id)))

(defn delete-doc-by-id
  [index-name mapping id]
  (let [conn (esr/connect db-address)]
    (esd/delete conn index-name mapping id)))

(defn query-by-term
  [index-name mapping key value]
  (let [conn (esr/connect db-address)]
    (esd/search conn index-name mapping :query (q/term key value))))
