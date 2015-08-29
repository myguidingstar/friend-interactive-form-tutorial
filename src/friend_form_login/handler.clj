(ns friend-form-login.handler
  (:use compojure.core)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.defaults :refer :all]
            [ring.middleware.reload :refer [wrap-reload]]
            [cemerick.friend :as friend]
            (cemerick.friend [workflows :as workflows]
                             [credentials :as creds])))

(def users {"admin" {:username "admin"
                     :password (creds/hash-bcrypt "password")
                     :roles #{::admin}}
            "dave" {:username "dave"
                    :password (creds/hash-bcrypt "password")
                    :roles #{::user}}})

(derive ::admin ::user)

(defroutes app-routes
  (GET "/" req
       (if-let [identity (friend/identity req)]
         (apply str "Logged in, with these roles: "
                (-> identity friend/current-authentication :roles))
         "Hey, you're **anonymous** user"))
  (GET "/authorized" request
       (->> "This page can only be seen by authenticated users."
         (friend/authorize #{::user})))
  (GET "/admin" request
       (->> "This page can only be seen by administrators."
         (friend/authorize #{::admin})))
  (GET "/never" request
       (->> "This page can never be seen."
         (friend/authorize #{::never})))
  (GET "/login" []
       (-> "login.html"
         (ring.util.response/file-response {:root "resources"})
         (ring.util.response/content-type "text/html")))
  (friend/logout
   (ANY "/logout" request (ring.util.response/redirect "/")))
  (route/not-found "Not Found"))

(def app
  (handler/site
   (-> app-routes
     (friend/authenticate
      {:credential-fn (partial creds/bcrypt-credential-fn users)
       :workflows     [(workflows/interactive-form)]})
     (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false)))))

(def reloadable-server (wrap-reload app))
