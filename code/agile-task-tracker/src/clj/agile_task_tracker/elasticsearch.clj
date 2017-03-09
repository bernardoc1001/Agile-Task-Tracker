(ns agile-task-tracker.elasticsearch
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as q]))

(def db-address "http://127.0.0.1:9200") ;Note that currently the database is running on localhost

(def task-info-mapping
  {"task-info-mapping" {:properties {:task-id            {:type "string" :index "not_analyzed"} ;not analyzed allows for the exact term to be queried
                                     :task-title         {:type "string"}
                                     :description        {:type "string"}
                                     :created-by         {:type "string"}
                                     :assignees          {:type "string"}
                                     :estimated-time     {:type "double"}
                                     :epic               {:type "string"}
                                     :sprint-id          {:type "string" :index "not_analyzed"} ;not analyzed allows for the exact term to be queried
                                     :priority-level     {:type "string"}
                                     :task-state         {:type "string" :index "not_analyzed"}
                                     :logged-time        {:type "double"}
                                     :project-id         {:type "string" :index "not_analyzed"}}}})

(def org-info-mapping
  {"org-info-mapping" {:properties {:organisation-id     {:type "string" :index "not_analyzed"} ;not analyzed allows for the exact term to be queried
                                    :organisation-name   {:type "string"}}}})

(def proj-info-mapping
  {"proj-info-mapping" {:properties {:project-id         {:type "string" :index "not_analyzed"} ;not analyzed allows for the exact term to be queried
                                     :project-name       {:type "string"}
                                     :organisation-id    {:type "string" :index "not_analyzed"}
                                     :man-hours          {:type "double"}
                                     :start-date         {:type "string"}
                                     :end-date           {:type "string"}}}})

(def sprint-info-mapping
  {"sprint-info-mapping" {:properties {:sprint-id          {:type "string" :index "not_analyzed"} ;not analyzed allows for the exact term to be queried
                                       :sprint-name        {:type "string"}
                                       :start-date         {:type "string"}
                                       :end-date           {:type "string"}
                                       :project-id         {:type "string" :index "not_analyzed"}
                                       :sprint-state       {:type "string" :index "not_analyzed"}}}})

(defn create-all-indices []
  (let [conn (esr/connect db-address)]

    (if (not (esi/exists? conn "task-info"))
      ;create index with mappings
      (esi/create conn "task-info" :mappings task-info-mapping))

    (if (not (esi/exists? conn "org-info"))
      ;create index with mappings
      (esi/create conn "org-info" :mappings org-info-mapping))

    (if (not (esi/exists? conn "proj-info"))
      ;create index with mappings
      (esi/create conn "proj-info" :mappings proj-info-mapping))

    (if (not (esi/exists? conn "sprint-info"))
      ;create index with mappings
      (esi/create conn "sprint-info" :mappings sprint-info-mapping))))

(defn put-task-info
  [doc]
  (let [conn (esr/connect db-address)
        index-name "task-info"
        id (:task-id doc)
        mapping "task-info-mapping"
        mapping-types task-info-mapping]

    (if (not (esi/exists? conn index-name))
      ;create index with mappings
      (esi/create conn index-name :mappings mapping-types))

    ;put the doc in the index
    (esd/put conn index-name mapping id doc)))

(defn put-org-info
  [doc]
  (let [conn (esr/connect db-address)
        index-name "org-info"
        id (:organisation-id doc)
        mapping "org-info-mapping"
        mapping-types {mapping org-info-mapping}]

    (if (not (esi/exists? conn index-name))
      ;create index with mappings
      (esi/create conn index-name :mappings mapping-types))

    ;put the doc in the index
    (esd/put conn index-name mapping id doc)))

(defn put-proj-info
  [doc]
  (let [conn (esr/connect db-address)
        index-name "proj-info"
        id (:project-id doc)
        mapping "proj-info-mapping"
        mapping-types {mapping proj-info-mapping}]

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
        mapping-types {mapping sprint-info-mapping}]

    (if (not (esi/exists? conn index-name))
      ;create index with mappings
      (esi/create conn index-name :mappings mapping-types))

    ;put the doc in the index
    (esd/put conn index-name mapping id doc)))

(defn get-active-sprint
  [index-name mapping project-id]
  (let [conn (esr/connect db-address)]
    (esd/search conn index-name mapping :query {:bool {:should [{:term {:project-id project-id}}
                                                                {:term {:sprint-state "active"}}]
                                                       :minimum_should_match 2}})))

(defn get-unassigned-tasks
  [index-name mapping project-id]
  (let [conn (esr/connect db-address)]
    (esd/search conn index-name mapping :query {:bool {:should [{:term {:project-id project-id}}
                                                                {:term {:task-state "create-sprint-col"}}]
                                                       :minimum_should_match 2}})))


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
