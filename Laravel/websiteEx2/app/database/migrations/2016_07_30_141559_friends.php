<?php

use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class Friends extends Migration {

	/**
	 * Run the migrations.
	 *
	 * @return void
	 */
	public function up()
	{
		//
      Schema::create('friends', function(Blueprint $table)
		{
			$table->increments('id');
         $table->integer('UID');
         $table->integer('FID');
         $table->text('name');
         $table->text('username');
         $table->text('email');
         $table->text('profile_pic');
			$table->boolean('status');
			$table->timestamps();
		});
	}

	/**
	 * Reverse the migrations.
	 *
	 * @return void
	 */
	public function down()
	{
      Schema::drop('friends');
	}

}
