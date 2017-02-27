<?php 
$I = new FunctionalTester($scenario);
$I->wantTo('Login to FB');

$I->amOnPage('/login');

$I->fillField('email', 'a@gmail.com');
$I->fillField('password', '12345');
$I->click('Login');

$I->seeCurrentUrlEquals('/feed');