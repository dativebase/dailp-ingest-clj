(ns dailp-ingest-clj.google-io-fiddle
  (:require [dailp-ingest-clj.google-io :as gio]))

(comment

  (->> {:spreadsheet-title "DF1975--Master"
        :worksheet-title "DF1975--Master"
        :max-col 119
        :max-row 2350}
       (get @gio/worksheet-cache)
       (drop 2)
       (take 3))

  (find {{:a 2} 2} {:a 2})

  (get {{:a 2} 2} {:a 2})

  (find {:a 2} :a)

  (get {:a 2} :a)

  (keys @gio/worksheet-cache)

  (let [spreadsheet-title "DF1975--Master Joel Mods"
        worksheet-title "DF1975--Master"]
    (gio/add-uuids-to-sheet
     :spreadsheet-title spreadsheet-title
     :worksheet-title worksheet-title
     :min-col 120
     :max-col 126
     :min-row 3
     :max-row 5
     ;; :max-row 2350
     )
    )

  (download-sheet "dog")

  (let [service (gio/init-service)]
    (gio/find-spreadsheet-by-title service "Pronominal Prefixes--Sets A & B"))

  (gio/init-service)

  (let [service (gio/init-service)]
    (gio/find-spreadsheet-by-title service "Dingus McGregor"))

  (let [service (gio/init-service)
        spreadsheet (gio/find-spreadsheet-by-title service "Dingus McGregor")
        worksheets (gio/list-worksheets service spreadsheet)]
    (map (fn [ws] (.getPlainText (.getTitle ws))) worksheets))

  (let [service (gio/init-service)
        spreadsheet (gio/find-spreadsheet-by-title service "Dingus McGregor")
        worksheets (gio/list-worksheets service spreadsheet)]
    (map (fn [ws] (.getPlainText (.getTitle ws))) worksheets))

  (let [service (gio/init-service)
        spreadsheet (gio/find-spreadsheet-by-title service "Dingus McGregor")
        worksheet (gio/find-worksheet-by-title service spreadsheet "topaz")
        cells (gio/get-cells service worksheet)]
    cells)

  (let [service (gio/init-service)]
    (gio/fetch-worksheet service
                     {:spreadsheet "Dingus McGregor"
                      :worksheet "topaz"}))

  (let [service (gio/init-service)]
    (gio/fetch-worksheet service
                     {:spreadsheet "Orthographic Inventories"
                      :worksheet "Sheet1"}))

  (gio/fetch-worksheet {:spreadsheet "Orthographic Inventories"
                    :worksheet "Sheet1"})

  (gio/fetch-worksheet-caching {:spreadsheet "Orthographic Inventories"
                            :worksheet "Sheet1"})

  (gio/fetch-worksheet-caching {:spreadsheet "Orthographic Inventories"
                            :worksheet "Sheet1"}
                           true)

)
