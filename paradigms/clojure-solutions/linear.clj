(defn shaplessFunction [operation]
      (letfn [(f [& args]
                 (if (vector? (first args))
                   (apply mapv f args)
                   (apply operation args)))]
             f))

(defn createPartOfFunction [operation] (fn [lhs, rhs] (mapv (partial operation rhs) lhs)))
(defn buildFunction [op]
      (fn [lhs, rhs] (if (vector? lhs) (mapv (buildFunction op) lhs rhs) (op lhs rhs))))

; ============= vectors =============
(def v+ (buildFunction +))
(def v- (buildFunction -))
(def v* (buildFunction *))
(def vd (buildFunction /))

; ============= shapes =============
(def s+ (shaplessFunction +))
(def s- (shaplessFunction -))
(def s* (shaplessFunction *))
(def sd (shaplessFunction /))

(def scalar (fn [v1, v2] (reduce + (v* v1 v2))))
(defn vectorCrossProduct [vector1, vector2]
      [(- (* (nth vector1 1) (nth vector2 2)) (* (nth vector1 2) (nth vector2 1)))
       (- (* (nth vector1 2) (nth vector2 0)) (* (nth vector1 0) (nth vector2 2)))
       (- (* (nth vector1 0) (nth vector2 1)) (* (nth vector1 1) (nth vector2 0)))])
(defn vect [& vectors] (reduce vectorCrossProduct vectors))
(def v*s (createPartOfFunction *))

; ============= matrices =============
(def m+ (buildFunction +))
(def m- (buildFunction -))
(def m* (buildFunction *))
(def md (buildFunction /))
(def transpose (fn [matrix] (apply mapv vector matrix)))
(defn m*s [vector scalar]
      (mapv #(v*s % scalar) vector))
(defn m*v [m v] (mapv (partial scalar v) m))
(defn m*m [& matrices]
      (reduce (fn [a b] (let [tb (transpose b)] (mapv #(m*v tb %) a))) matrices))