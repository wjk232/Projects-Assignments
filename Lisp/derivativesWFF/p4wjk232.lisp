;;;WFF is a fucntion that creates a well-formed formula
;;;from a giving expression
(defun WFF (expr)
   (COND ( (NULL expr) NIL)
         ( (ATOM expr) expr)
         ( (EQL (CAR expr) 'cos) 
                (LIST (CAR expr) 
                (WFF (CADR expr))))
         ( (EQL (CAR expr) 'sin)    
                (LIST (CAR expr)
                (WFF (CADR expr)))) 
         ( (NUMBERP (CAR expr)) (BIUNARYOP expr))
         ( (ATOM (CAR expr)) (LIST 'u- (WFF (CADR expr))))
         (T ( CONS (CADR expr)(LIST (WFF (CAR expr)) (WFF (CADDR expr)))))
   )
)
;;; BIUNARY is a function to help wff contruct a wff
(defun BIUNARYOP (expr)
   (COND ( (NULL expr) NIL)
         ( (ATOM expr) expr)
         ( (NULL (CDDR expr)) (CONS 'g (APPEND expr '(1))))
         ( (AND (ATOM (CADDR expr))     ;;; to construct (2 g(x) 4)
                (NOT (NUMBERP (CADR expr))))
            (CONS 'g 
                  (LIST (CAR expr) 
                        (BIUNARYOP (CADR expr)) 
                        (CADDR expr))))
         ( (ATOM (CADDR expr)) (CONS 'g expr)) 
         (T (CONS (CADR expr) 
                  (LIST (CAR expr) (BIUNARYOP (CADDR expr)))))
   )      

)
;;; DER is a function to call the appropriate DERIVATIVE function
(defun DER(expr)
   (COND ( (NULL expr) NIL)
         ( (NUMBERP expr) 0)
         ( (ATOM expr) expr)
         (T (SIMPLI (EVAL `(,(GETP (CAR expr) DERIVATIVE)   ;;;uses SIMPLI to SIMPLIFY derivative
                           ,(LIST (quote quote) (CDR expr))))))
   )
)
;;; DERG function
(defun DERG (expr)
   (COND ( (NULL expr) NIL)
         ( (NOT (NUMBERP (CAR expr))) 
                (LIST (DER (CAR expr)) (- (CADR expr) 1)))
         (T (CONS 'g (CONS (* (CAR expr) (CADDR expr)) (DERG (CDR expr)))))
   )
)
;;; DER+ function
(defun DER+ (expr)
   (COND ( (NULL expr) NIL)
         ( (NUMBERP expr) expr)
         ( (EQL (CAR expr) 'g) (DER expr))
         (T (CONS '+ (LIST (DER (CAR expr)) (DER (CADR expr)))))
   )
)
;;; DER- function
(defun DER- (expr)
   (COND ( (NULL expr) NIL)
         ( (NUMBERP expr) expr)
         ( (EQL (CAR expr) 'g) (DER expr))
         (T (CONS '- (LIST (DER (CAR expr)) (DER (CADR expr)))))
   )
)
;;; DERU- function
(defun DERU- (expr)
    (LIST 'U- (DER (CAR expr)))    ;;; (u- (der wff))
)
;;; DER/ function
(defun DER/ (expr)
   (COND ( (NULL expr) NIL)
         ( (NUMBERP expr) (* expr 2))
         ( (EQL (CAR expr) 'g)     ;;; for the h(x)^2
                (LIST (CAR expr) 
                      (*(CADR expr) (CADR expr))
                      (CADDR expr) 
                      (* (CADDDR expr)2)))
         (T (LIST '/ (CONS '- (CONS (LIST '* (DER (CAR expr)) (CADR expr)) 
                             (LIST (LIST '* (CAR expr)(DER (CADR expr))))))
                 (LIST (DER/ (CADR expr)))) )
   )
)
;;; DERG* function
(defun DER* (expr)
   (CONS '+ (CONS (LIST '*  (CAR expr) (DER (CADR expr))) 
                  (LIST (LIST '* (DER (CAR expr))(CADR expr)))) 
   )
)
;;; DERCOS function
(defun DERCOS (expr)
   (LIST 'U- (LIST 'sin (CAR expr)))      
)
;;; DERSIN function with chain rule 
(defun DERSIN (expr)
   (COND ( (NULL expr) NIL)
         ( (NULL (CDDR expr))(LIST 'cos (CAR expr)))     
         ( (NOT (EQL (CADDAR expr) 'x)) 
                     (LIST '* (LIST 'cos (CAR expr)) 
                              (DER (CADDAR expr))) )
   )
)
;;; SIMPLI is a function to simplify the derivative
(defun SIMPLI (expr)
   (COND ( (NULL expr) NIL)
         ( (ATOM expr) expr)
         ( (AND (EQL (CAR expr) 'U-) (EQL (CAADR expr) 'U-)) (CADADR expr) )         
         ( (EQL (CADDDR expr) 0) (CADR expr))
         ( (AND (OR (EQL (CAR expr) '-)             ;;; if add/sub 0 at end
                    (EQL (CAR expr) '+))
                (EQUAL (SIMPLI (CADDR expr)) 0) (CADR expr)))     
         ( (AND (OR (EQL (CAR expr) '-)             ;;; if add/sub 0 at beginning
                    (EQL (CAR expr) '+))
                (EQUAL (SIMPLI (CADR expr)) 0) (CADDR expr))) 
         ( (AND (EQL (CAR expr) '-) (EQUAL (CADR expr) (CADDR expr))) 0)  ;;;subtracting same function
         ( (AND (EQL (CAR expr) '*) (EQL (CADDR expr) 0)) 0)
         ( (ATOM (CADR expr)) expr) 
         ( (NOT (NULL (CADDR expr)))              
                (CONS (CAR expr) 
                (LIST (SIMPLI (CADR expr)) (SIMPLI (CADDR expr)))) )
         (T (CONS (CAR expr) (LIST (SIMPLI (CADR expr)))))
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