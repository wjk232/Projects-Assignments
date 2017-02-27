<?php

class Comment extends Eloquent  {

	protected $table = 'comments';

	protected $fillable = ['comment', 'fname', 'lname', 'profile_pic', 'PID', 'UID'];

}