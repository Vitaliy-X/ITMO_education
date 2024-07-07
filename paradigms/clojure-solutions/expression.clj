(defn operation [f] (fn [& operands] (fn [args] (apply f (map #(% args) operands)))))

(def add (operation +))
(def subtract (operation -))
(def multiply (operation *))
(def divide (operation (fn [a b] (/ (double a) (double b)))))
(def negate (operation -))
(def exp (operation #(Math/exp %)))
(def ln (operation #(Math/log %)))
(defn constant [a] (fn [x] a))
(defn variable [a] (fn [x] (x a)))
(def oper {'+      add
           '-      subtract
           '*      multiply
           '/      divide
           'negate negate
           'exp    exp
           'ln     ln})

(defn parseFunction [x]
  (letfn [(parses [x]
            (cond
              (number? x) (constant x)
              (symbol? x) (variable (name x))
              :else (apply (get oper (first x)) (mapv parses (rest x)))))]
    (parses (read-string x))))

; =================================================

(defn proto-get
      "Returns object property respecting the prototype chain"
      ([obj key] (proto-get obj key nil))
      ([obj key default]
       (let [val (get obj key)]
            (if (not (nil? val)) val (let [proto (get obj :proto)]
                 (if proto (proto-get proto key default) default))))))


(defn proto-call
      "Calls object method respecting the prototype chain"
      [this key & args]
      (apply (proto-get this key) this args))

(defn field
      "Creates field"
      [key] (fn
              ([this] (proto-get this key))
              ([this def] (proto-get this key def))))

(defn method
      "Creates method"
      [key] (fn [this & args] (apply proto-call this key args)))

(defn constructor
      ([ctor, proto, func, op]
            (fn [& args]
                  (apply ctor {:proto proto, :func func :op op} args)))
      ([ctor, proto]
            (fn [& args]
                  (apply ctor {:proto proto} args))))

(defn Operation [this, & operands] (assoc this :operands operands))

(def _operands (field :operands))
(def _op (field :op))
(def _func (field :func))
(def _name (field :name))
(def _value (field :value))
(def evaluate (method :evaluate))
(def toString (method :toString))

(def OperationPrototype
  {:evaluate   (fn [this, args]
                   (apply (_func this) (map #(evaluate % args) (_operands this))))
   :toString   (fn [this]
                   (str "(" (_op this) " " (clojure.string/join " " (map #(toString %) (_operands this))) ")"))})

(defn create [f, op] (constructor Operation OperationPrototype f op))

(def Add (create + "+"))
(def Subtract (create - "-"))
(def Multiply (create * "*"))
(def Divide (create (fn ([e] (/ (double 1) (double e)))
                        ([e & other] (reduce #(/ (double %1) (double %2)) e other))) "/"))
(def Negate (create - "negate"))

;(declare Cosh)

;(def Ln (createOperation #(Math/log %), "ln"))
;(def Exp (createOperation #(Math/exp %), "exp"))
(def Sin (create #(Math/sin %) "sin"))
(def Cos (create #(Math/cos %) "cos"))
;(def Sum (createOperation (fn [& args] (reduce + args)) "sum"))
;(def Avg (createOperation (fn [& args] (/ (reduce + args) (count args))) "avg"))
;(def And (createOperation #(if (and (> %1 0) (> %2 0)) 1 0) "&&"))
;(def Or (createOperation #(if (or (> %1 0) (> %2 0)) 1 0) "||"))
;(def Xor (createOperation #(if (or (and (> %1 0) (<= %2 0)) (and (<= %1 0) (> %2 0))) 1 0) "^^"))

;(declare Constant)

(def Constant (let [prototype
      {:evaluate (fn [this, args] (_value this))
       :toString (fn [this] (str (_value this)))}]
      (constructor #(assoc %1 :value %2) prototype)))

(def Variable (let [prototype
      {:evaluate (fn [this, args] (args (str (Character/toLowerCase (get (_name this) 0)))))
       :toString (fn [this] (_name this))}]
      (constructor #(assoc %1 :name %2) prototype)))

(defn parseObject [expression]
      (let [operations
            {'+ Add
            '- Subtract
            '* Multiply
            '/ Divide
            'negate Negate
            'sin Sin
            'cos Cos
            }
            parser (fn parse [token]
                 (cond
                     (sequential? token)
                     (apply (get operations (first token)) (map parse (rest token)))
                     (symbol? token) (Variable (str token))
                     :else (Constant (double token))))]
           (parser (read-string expression))))