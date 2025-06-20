(ns nubank.workspaces.ui.highlight
  (:require [cljsjs.highlight]
            [com.fulcrologic.fulcro-css.localized-dom :as dom]
            [com.fulcrologic.fulcro.components :as fp]
            [com.fulcrologic.fulcro.react.hooks :as hooks]))

(fp/defsc Highlight [this {::keys [source language]}]
  {:query [::source ::language]
   :use-hooks? true}
  (let [^js ref (hooks/use-ref nil)]
    (hooks/useEffect
      (fn []
        (js/hljs.highlightBlock (.-current ref))
        js/undefined)
      #js [])
    (dom/pre {:ref       ref
              :className (or language "clojure")} source)))

(def highlight (fp/factory Highlight))
