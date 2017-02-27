<?php

class FriendsController extends \BaseController {

	public function showFriendsView(){
      
      if(!Auth::check()){
			return Redirect::to('/login');
		}
      
      $user = Auth::user();

      $friendsInfo = User::where('id','!=',$user->id)->get();
      $friends = Friend::where('UID', '=', $user->id)->get();
      
      return View::make('friends',[
         'friendInfo' => $friendsInfo,
         'friends' => $friends
      ]);
   }

   public function addFriend(){
      
      $validation = Validator::make(Input::all(),[
			'email' =>' required', 
		]);
      $user = Auth::user();
      
      if($validation->fails()){
            $messages = $validation->messages();
            Session::flash('validation_messages', $messages);
            return Redirect::back()->withInput();
      }
      $friendInfo = DB::table('users')->where('email', Input::get('email'))->first();
      
      if ($friendInfo == null){
         Session::flash('error_message', 'User doesn\'t exist');
			return Redirect::to('/friends');
      }
      $friends = Friend::where('UID', '=', $user->id)->where('FID','=',$friendInfo->id)->first();
	
      if($friends != null){
         Session::flash('error_message', 'Friend already exist');
         return Redirect::to('/friends');
      }else{
         $friends = Friend::Create([
			 'UID' => $user->id,
			 'FID' => $friendInfo->id,
			 'name' => $friendInfo->name,
			 'email' => $friendInfo->email,
			 'profile_pic' => $friendInfo->profile_pic,
			 'status' => 'online'
         ]);
		 
         /*$view_data =array('key' => 'value');
         $email_data = array('emailTo' => $friendInfo->email, 'subject' => 'Add you as a Friend');
         Mail::send('emails.newfriend', $view_data, function ($message) use ($email_data){
            $message->to($email_data['emailTo'])->subject($email_data['subject']);
         });*/
         return Redirect::to('/friends');
      }
	

   }
   
   public function showFriendsResult(){
      
      $friendInfo = DB::table('users')->where('name','=',Input::get('toSearch'))->get();
      
      return $friendInfo;//Redirect::to('/friends');
   }
}
