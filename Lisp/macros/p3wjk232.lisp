;;; ++ is passed a variable and increments it by one. 
;;; Then assigns the new value to that variable
(defmacro ++ (var)
   `(SETF ,var (+ 1 ,var))
)
;;; ITERATE is passed a control variable beginvalue and endvalue.
;;;

(defmacro ITERATE (cvar bvar evar &rest bodies)
	(LET ( (g (gensym)) )
	   `(DO ((,cvar ,bvar(+ 1 ,cvar))(,g ,evar))
		    ((> ,cvar ,g) T)
		    ,@bodies
        )
	)	
)
