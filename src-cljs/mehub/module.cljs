(ns mehub.module)

(defprotocol PModule
  (setup [this] [this complete]  ""))

(defmulti load-module (fn [name options] name))

(defmethod load-module :default
  [name options]
  nil)
