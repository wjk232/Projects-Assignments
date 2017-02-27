<?php

class Post extends Eloquent  {

	protected $table = 'posts';

	protected $fillable = ['content', 'UID', 'fname', 'lname', 'profile_pic', 'likes'];

	
}