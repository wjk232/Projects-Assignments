<?php
//Redirect to Login
Route::get("/", function(){
	return Redirect::to('/login');
});

//Authentication Controller
Route::get('/login', 'AuthenticationController@showLoginView');
Route::post('/login', 'AuthenticationController@loginUser');
Route::get('/logout', 'AuthenticationController@logout');

//Registration Controller
Route::get('/signup', 'RegistrationController@showSignUpView');
Route::post('/signup', 'RegistrationController@signUp');

//Feed
Route::get('/feed', 'FeedController@showFeed');

//Post
Route::post('/post', 'PostController@createPost');

//Comment
Route::post('/comment', 'CommentsController@createComment');

//Misc
Route::get('/users', 'AuthenticationController@showUsers');

//Friends List
Route::get('/friends', 'FriendsController@showFriendsView');
Route::post('/friends', 'FriendsController@addFriend');
Route::get('/friendsSearch', 'FriendsController@showFriendsResult');
