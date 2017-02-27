<?php

class HomeController extends BaseController {


	public function showUserProfile($uid){

		if(!Auth::check()){
			return Redirect::to('/login');
		}

		//return a count of # of users with $uid ideally 0 or 1. 
		$count = User::where('id', '=', $uid)->count();

		//no such user found
		if($count == 0){ 
			return Redirect::to('/feed');
		}

		//single row
		$profile_user = User::where('id', '=', $uid)->first();

		//current user
		$user = Auth::user();


		//create profile view
		return View::make('profile', [
			'profile_user'	=> $profile_user,
			'user'	=> $user
		]);

	}


}
