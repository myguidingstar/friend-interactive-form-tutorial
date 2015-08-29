(defproject friend-form-login "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [ring/ring-defaults "0.1.5"]
                 [compojure "1.4.0"]
                 [com.cemerick/friend "0.2.1"]]
  :profiles
  {:dev {:plugins [[lein-ring "0.9.6"]
                   [cider/cider-nrepl "0.9.1"]]
         :dependencies [[ring/ring-devel "1.4.0"]
                        [ring-mock "0.1.5"]]
         :ring {:handler friend-form-login.handler/reloadable-server}}})
