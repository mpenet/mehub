(ns mehub.module.github
  (:require-macros [hiccups.core :as hiccups])
  (:require
   [clojure.string :as string]
   [hiccups.runtime :as hiccupsrt]
   [mehub.module :as mod]
   [jayq.core :as jq]
   [jayq.util :as jqu]))

(defn slugify
  [s]
  (-> s
      (.replace #"[-\s]+" "-")
      (.toLowerCase)))

(hiccups/defhtml repository-template [repository]
  [:div {:class (str "span3  repo"
                     (when-let [lang (:language repository)]
                       (str " "(slugify lang)))
                     (when  (:fork repository)
                       " fork")
                     (str " c" (js/Math.ceil (* 10 (js/Math.random)))))}
   [:a {:href (:html_url repository)}
    (if (:fork repository)
      [:div.ribbon.fork-ribbon.icon-random {:title "Forked repository"}]
      [:div.ribbon.owner-ribbon.icon-star {:title "Owner of this repository"}])
    [:h2 (:name repository)]
    [:h3 (:language repository)]
    [:p (:description repository)]
    [:span.meta
     [:span.watchers [:i.icon-eye-open] (:watchers repository)]
     [:span.forks [:i.icon-random] (:forks repository)]]]])

(hiccups/defhtml user-template [user]
  [:div.user
   [:h2
    [:a {:href (:html_url user)}
     (str (:login user) "@github")]]
   [:img.icon {:src (:avatar_url user)}]
   (when-let [n (:name user)]
     [:p.name
      [:i.icon-user]
      n])
   (when-let [b (:blog user)]
     [:p.blog
      [:i.icon-home]
      [:a {:href b} b]])
   (when-let [bio (:bio user)]
     [:p.bio
      [:i.icon-info-sign]
      bio])
   (when-let [c (:company user)]
     [:p
      [:i.icon-cogs]
      c])
   (when-let [l (:location user)]
     [:p.location
      [:i.icon-map-marker]
      l])])

(hiccups/defhtml activities [events]
  [:ul.gh-events
   (for [event events]
     [:li (str event)])])

(defn load-user
  [user complete]
  (.getJSON js/jQuery
            (str "https://api.github.com/users/"
                 (js/encodeURIComponent user)
                 "?callback=?")
            (fn [data]
              (complete (js->clj data :keywordize-keys true)))))

(defn load-repositories
  [user complete]
  (.getJSON js/jQuery
            (str "https://api.github.com/users/"
                 (js/encodeURIComponent user)
                 "/repos?callback=?")
            (fn [data]
              (complete (js->clj data :keywordize-keys true)))))

(defn load-activity
  [user complete]
  (.getJSON js/jQuery
            (str "https://api.github.com/users/"
                 (js/encodeURIComponent user)
                 "/events?&per_page=10&callback=?")
            (fn [data]
              (complete (js->clj data :keywordize-keys true)))))

(defn render-repositories
  [selector repositories]
  (jq/inner (jq/$ selector)
            (string/join " " (->> (:data repositories)
                                  (sort-by :updated_at)
                                  reverse
                                  (map repository-template)))))

(defn render-user
  [selector user]
  (jq/inner (jq/$ selector)
            (user-template (:data user))))

(defn render-activities
  [selector user]
  (jq/inner (jq/$ selector)
            (activities (take 3 (:data user)))))

(defrecord GitHubModule [options]
  mod/PModule
  (setup [this complete]
    (load-repositories (:user options)
                       (partial render-repositories (:repo-selector options)))
    (load-user (:user options)
               (partial render-user (:user-selector options)))
    ;; (load-activity (:user options)
    ;;                (partial render-activities (:activity-selector options)))
    ))

(defmethod mod/load-module :github
  [_ options]
  (GitHubModule. options))