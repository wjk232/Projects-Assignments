<?php

return array(

	/*
	|--------------------------------------------------------------------------
	| Third Party Services
	|--------------------------------------------------------------------------
	|
	| This file is for storing the credentials for third party services such
	| as Stripe, Mailgun, Mandrill, and others. This file provides a sane
	| default location for this type of information, allowing packages
	| to have a conventional place to find your various credentials.
	|
	*/
   
   /*'mailgun' => array(
		'domain' => 'sandbox2b5b54026b6a46979befff47f8b4f113.mailgun.org',
		'secret' => 'key-5f6fae922304796570bc5c70873022ba',
	),*/
   
	'mailgun' => array(
		'domain' => 'sandbox5b9dde1f811e42b8b3bbb7e75531976e.mailgun.org',
		'secret' => 'key-e26db443790836deaeb21d48f26b4167',
	),

	'mandrill' => array(
		'secret' => '',
	),

	'stripe' => array(
		'model'  => 'User',
		'secret' => '',
	),

);
