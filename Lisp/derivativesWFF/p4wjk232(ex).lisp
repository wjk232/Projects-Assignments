;;;WFF is a fucntion that creates a well-formed formula
;;;from a giving expression
(defun WFF (expr)
   (COND ( (NULL expr) NIL)
         ( (ATOM expr) expr)
         ( (EQL (CAR expr) 'cos) (LIST (CAR expr) (WFF (CADR expr))))
         ( (EQL (CAR expr) 'sin) (LIST (CAR expr) (WFF (CADR expr)))) 
         ( (NUMBERP (CAR expr)) (BIUNARYOP expr))
         ( (ATOM (CAR expr)) (LIST 'u- (WFF (CADR expr))))
         ;;;(T (BINARYOPADD expr))
         (T ( CONS (CADR expr)(LIST (WFF (CAR expr)) (WFF (CADDR expr)))))
   )
)


(defun BIUNARYOP (expr)
   (COND ( (NULL expr) NIL)
         ( (ATOM expr) expr)
         ;;;( (EQL (CAR expr) 'cos) (LIST (CAR expr) (BINARYOPADD (CADR expr))))
         ;;;( (EQL (CAR expr) 'sin) (LIST (CAR expr) (BINARYOPADD (CADR expr))))
         ( (NULL (CDDR expr)) (CONS 'g (APPEND expr '(1))))
         ( (ATOM (CADDR expr)) (CONS 'g expr)) 
         (T (CONS (CADR expr) (LIST (CAR expr) (BIUNARYOP (CADDR expr)))))
   )      

)

(defun BINARYOPADD (expr)
   (COND ( (NULL expr) NIL)
         ( (ATOM expr) expr)
         ( (NULL (CDR expr)) (BIUNARYOP (CAR expr)))
         ( (ATOM (CAR expr)) (BIUNARYOP expr))
         (T ( CONS (CADR expr)(LIST (BINARYOPADD (CAR expr)) (BINARYOPADD (CADDR expr)))))
   )
)

(defun DER(expr)
   (COND ( (NULL expr) NIL)
         ( (ATOM expr) 0)
         ( (SIMPLI (SIMPLI (EVAL `(,(GETP (CAR expr) DERIVATIVE) ,(LIST (quote quote) (CDR expr)))))))
   )
)

(defun DERG (expr)
   (COND ( (NULL expr) NIL)
         ( (NOT (NUMBERP (CAR expr))) (LIST (CAR expr) (- (CADR expr) 1)))
         (T (CONS 'g (CONS (* (CAR expr) (CADDR expr)) (DERG (CDR expr)))))
   )
)

(defun DER+ (expr)
   (COND ( (NULL expr) NIL)
         ( (NUMBERP expr) expr)
         ( (EQL (CAR expr) 'g) (DER expr))
         (T (CONS '+ (LIST (DER (CAR expr)) (DER (CADR expr)))))
   )
)

(defun DER- (expr)
   (COND ( (NULL expr) NIL)
         ( (NUMBERP expr) expr)
         ( (EQL (CAR expr) 'g) (DER expr))
         (T (CONS '- (LIST (DER (CAR expr)) (DER (CADR expr)))))
   )
)

(defun derU- (expr)
    (list 'U- (der (car expr)))           ;;; (u- (der wff))
)

(defun DER/ (expr)
   (COND ( (NULL expr) NIL)
         ( (CONS '- (CONS (LIST '* (DER (CAR expr)) (CADR expr)) 
                          (LIST (LIST '* (CAR expr)(DER (CADR expr))))
                  )
                  
            )
         )
   )
)

(defun DER* (expr)


)

(defun SIMPLI (expr)
   (COND ( (NULL expr) NIL)
         ( (ATOM expr) expr)
         ( (AND (EQL (CAR expr) '+) (EQL (CADDR expr) 0)) (CADR expr))
         ( (AND (EQL (CAR expr) '+) (EQL (CADR expr) 0) (CADDR expr)))
         ( (AND (EQL (CAR expr) '-) (EQL (CADDR expr) 0)) (CADR expr))
         ( (AND (EQL (CAR expr) '-) (EQL (CADR expr) 0) (CADDR expr)))
         ( (EQL (CADDDR expr) 0) (CADR expr))
         ( (AND (EQL (CAR expr) '-) (EQUAL (CADR expr) (CADDR expr))) 0)
         ( (AND (EQL (CAR expr) '*) (EQL (CADDR expr) 0)) 0)
         ( (ATOM (CADR expr)) expr) 
         (T (CONS (CAR expr) (LIST (SIMPLI (CADR expr)) (SIMPLI (CADDR expr)))))
   
   )
)

;;; ****** P U T P ******
;;; (PUTP atm ht value) 
;;;    atm - atom receiving the property
;;;    ht  - a hash table representing the property
;;;    value - the value for the specified atm
;;;  Inserts an entry in the hash-table HT for the specified
;;;  atm and value.
(defun putp (atm ht value )
    (setf (gethash atm ht) value)
)
    
;;; ****** G E T P ******
;;; (GETP atm ht) 
;;;    atm - atom receiving the property
;;;    ht  - a hash table representing the property
;;;  Returns the value for atm as the key in the 
;;;  hash-table HT
(defun getp (atm ht)
    (gethash atm ht)
)

;;; Define the derivative properties for each function
(setf DERIVATIVE (make-hash-table))
(putp 'g DERIVATIVE 'derg )
(putp '+ DERIVATIVE 'der+ )
(putp '- DERIVATIVE 'der-)
(putp 'U- DERIVATIVE 'derU-)
(putp '* DERIVATIVE 'der*)
(putp '/ DERIVATIVE 'der/ )
(putp 'sin DERIVATIVE 'dersin)
(putp 'cos DERIVATIVE 'dercos)
;;;(cd "C:\\Users\\Roger\\Desktop\\classes S2016\\cs3723\\assign4")