(ns mehub.core
  (:require
   [mehub.module :as mod]
   [mehub.module.github :as mod-github]
   [mehub.module.github :as mod-twitter]))

(defn ^:export init
  [opts]
  (let [options (js->clj opts :keywordize-keys true)]
    (doseq [[mod-name mod-opts] (:modules options)]
      (let [module (mod/load-module mod-name mod-opts)]
        (mod/setup module (fn [] ))))))