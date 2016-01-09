; Model-View-Signal-Action pattern
(ns pickings.mvsa)

(defn connect-seesaw
  "Given a component spec map returns a connected component which can be rendered using Seesaw API.

  :control can be a non-pure function, :init, :view and :reconcile must be pure functions.

  init-args will be passed to :init function to construct initial model.

  :view gets a model atom and signal dispatch function and is expected to return Seesaw frame.
  Frame is responsible for redrawing itself on model changes.

  Dispatches :on-connect signal and returns a map with following keys:
      :view - Seesaw frame
      :dispatch-signal - it can be used to dispatch signals not only from the view, always returns nil

      these are exposed mainly for debugging:
      :model - atom
      :dispatch-action - the same function which is passed into control, returns a new model

  Data flow:
  (init)
    |
    V
  model -> (view-model) -> (view) -signal-> (control) -action-> (reconcile) -> model -> etc."
  [{:keys [init view control reconcile] :as _spec}
   init-args]
  (let [model (apply init init-args)
        model-atom (atom model)
        dispatch-action (fn [action] (swap! model-atom reconcile action))
        dispatch-signal (fn [signal] (control @model-atom signal dispatch-action) nil)
        frame (view model-atom dispatch-signal)]
    (dispatch-signal :on-connect)

    {:view            frame
     :dispatch-signal dispatch-signal
     :model           model-atom
     :dispatch-action dispatch-action}))

;;;;;;;;;;;;;;;;;;;;;;;; Middleware
(defn wrap-log
  ([spec] (wrap-log spec ""))
  ([spec prefix]
   (-> spec
       (update :control #(fn control
                          [model signal dispatch]
                          (println prefix "signal =" (pr-str signal))
                          (% model signal dispatch)))
       (update :reconcile #(fn reconcile
                            [model action]
                            (println prefix "  action =" (pr-str action))
                            (let [result (% model action)]
                              (println prefix "   " (pr-str model))
                              (println prefix "     ->")
                              (println prefix "   " (pr-str result))
                              result))))))