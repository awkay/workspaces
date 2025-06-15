(ns nubank.workspaces.card-types.react
  (:require-macros [nubank.workspaces.card-types.react])
  (:require [goog.object :as gobj]
            [nubank.workspaces.card-types.util :as ct.util]
            [com.fulcrologic.fulcro.dom :as fdom]
            ["react-dom/client" :as dom]
            [nubank.workspaces.data :as data]
            [nubank.workspaces.model :as wsm]
            [nubank.workspaces.ui :as ui]))

(defn render-at [c node]
  (let [comp (if (fn? c) (c) c)]
    (js/ReactDOM.render comp node)))

(defn react-card-init [{::wsm/keys [card-id]
                        :as        card} state-atom component]
  (let [vroot (volatile! nil)]
    (ct.util/positioned-card card
      {::wsm/dispose
       (fn [^js node]
         (if state-atom
           (remove-watch state-atom ::card-watch))

         (when @vroot
           (.unmount @vroot)))

       ::wsm/refresh
       (fn [^js node]
         (when @vroot
           (.render @vroot (component))))

       ::wsm/render
       (fn [^js node]
         (let [root (dom/createRoot node #js {})]
           (vreset! vroot root)
           (when state-atom
             (swap! data/active-cards* assoc-in [card-id ::state*] state-atom)
             (add-watch state-atom ::card-watch
               (fn [_ _ _ _]
                 (.render root (component))
                 (ui/refresh-card-container card-id))))

           (.render root (component))
           root))})))

(defn react-card* [state-atom component]
  {::wsm/init #(react-card-init % state-atom component)})
