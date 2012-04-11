(ns mehub.module.twitter
  (:require-macros [hiccups.core :as hiccups])
  (:require
   [clojure.string :as string]
   [hiccups.runtime :as hiccupsrt]
   [mehub.module :as mod]
   [jayq.core :as jq]
   [jayq.util :as jqu]))

(defn pad
  [n]
  (if (< n 10) (str "0" n) n))

(defn to-ircish-date
  [date]
  (let [d (js/Date. date)]
    (str (pad (inc (.getMonth d))) "."
         (pad (inc (.getUTCDay d)))
         "@"
         (.getUTCHours d) ":"
         (.getUTCMinutes d))))


(hiccups/defhtml tweets-template [tweets]
  [:ul.tweets
   (for [t tweets]
     [:li
      [:span.timestamp (str "[" (-> t :created_at to-ircish-date) "] ")]
      [:span.text (:text t)]])])

(hiccups/defhtml header-template [user]
  [:h2 [:a {:href (str "http://twitter.com/" user)} (str "> " user "@twitter")] ])

(defn load-tweets
  [user max-tweets complete]
  (.getJSON js/jQuery
            (str "http://api.twitter.com/1/statuses/user_timeline.json?"
                 "count=" max-tweets
                 "&screen_name=" (js/encodeURIComponent user)
                 "&callback=?")
            (fn [data]
              (complete (js->clj data :keywordize-keys true)))))

(defn render-tweets
  [selector tweets]
  (jq/append (jq/$ selector)
            (tweets-template tweets)))

(defn render-header
  [selector user]
  (jq/inner (jq/$ selector)
            (header-template user)))


(defrecord TwitterModule [options]
  mod/PModule
  (setup [this complete]
    (render-header (:selector options) (:user options))
    (load-tweets (:user options)
                 (:max-tweets options)
                 (partial render-tweets (:selector options)))))

(defmethod mod/load-module :twitter
  [_ options]
  (TwitterModule. options))