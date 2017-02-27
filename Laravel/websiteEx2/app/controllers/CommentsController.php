<?php

class CommentsController extends \BaseController {

	public function createComment(){


		$validation = Validator::make(Input::all(),[
			'comment' =>' required', 
			'PID'	=> 'required'
		]);

		if($validation->fails()){
            $messages = $validation->messages();
            Session::flash('validation_messages', $messages);
            return Redirect::back()->withInput();
        }

        $user = Auth::user();

        try{

        	$comment = Comment::create([
        		'comment' => Input::get('comment'),
        		'UID'	=> $user->id,
        		'PID'	=> Input::get('PID'), //Post ID
        		'fname'	=> $user->name,
        		'profile_pic'	=> $user->profile_pic
        	]);


        }catch(Exception $e){
        	Session:flash('error_message', 
        	'Oops! Something is wrong');
        	return Redirect::back()->withInput();
        }

        return Redirect::to('/feed');

	}

}
