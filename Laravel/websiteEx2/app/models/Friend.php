<?php

class Friend extends Eloquent  {

	protected $table = 'friends';

	protected $fillable = ['id','UID', 'FID','name','username', 'email','profile_pic','status'];

}