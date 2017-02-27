<?php

class PostController extends \BaseController {

	public function createPost(){

		$validation = Validator::make(Input::all(),[
			'content' =>' required', 
		]);

		if($validation->fails()){
            $messages = $validation->messages();
            Session::flash('validation_messages', $messages);
            return Redirect::back()->withInput();
        }

        $user = Auth::user();

        try{

        	$post = Post::create([
        		'content' => Input::get('content'),
        		'UID'	=> $user->id,
        		'fname'	=> $user->name,
        		'profile_pic'	=> $user->profile_pic
        	]);


            /* Code if you have images as part of posts 
            $photo = Input::file('photo');

            $img = Image::make($photo);

            $destination = __DIR__.'/public/'.$user->id.'/'.hash::make($user->id).'-'.$photo->getClientOriginalName();

            $img->resize(300, 200);
            $img->save($destination);

            PostPictures::create([
                'PID'   => $post->id,
                'photo' => $destination
            ]);
            */

        }catch(Exception $e){
        	Session:flash('error_message', 
        	'Oops! Something is wrong');
        	return Redirect::back()->withInput();
        }

        return Redirect::to('/feed');

	}


}
