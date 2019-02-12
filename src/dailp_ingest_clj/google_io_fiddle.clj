(ns dailp-ingest-clj.google-io-fiddle
  (:require [dailp-ingest-clj.google-io :refer [init-service
                                                find-spreadsheet-by-title
                                                list-worksheets
                                                find-worksheet-by-title
                                                get-cells
                                                fetch-worksheet
                                                fetch-worksheet-caching]]))

(comment

  (download-sheet "dog")

  (init-service)

  (let [service (init-service)]
    (find-spreadsheet-by-title service "Dingus McGregor"))

  (let [service (init-service)
        spreadsheet (find-spreadsheet-by-title service "Dingus McGregor")
        worksheets (list-worksheets service spreadsheet)]
    (map (fn [ws] (.getPlainText (.getTitle ws))) worksheets))

  (let [service (init-service)
        spreadsheet (find-spreadsheet-by-title service "Dingus McGregor")
        worksheets (list-worksheets service spreadsheet)]
    (map (fn [ws] (.getPlainText (.getTitle ws))) worksheets))

  (let [service (init-service)
        spreadsheet (find-spreadsheet-by-title service "Dingus McGregor")
        worksheet (find-worksheet-by-title service spreadsheet "topaz")
        cells (get-cells service worksheet)]
    cells)

  (let [service (init-service)]
    (fetch-worksheet service
                     {:spreadsheet "Dingus McGregor"
                      :worksheet "topaz"}))

  (let [service (init-service)]
    (fetch-worksheet service
                     {:spreadsheet "Orthographic Inventories"
                      :worksheet "Sheet1"}))

  (fetch-worksheet {:spreadsheet "Orthographic Inventories"
                    :worksheet "Sheet1"})

  (fetch-worksheet-caching {:spreadsheet "Orthographic Inventories"
                            :worksheet "Sheet1"})

  (fetch-worksheet-caching {:spreadsheet "Orthographic Inventories"
                            :worksheet "Sheet1"}
                           true)

)
