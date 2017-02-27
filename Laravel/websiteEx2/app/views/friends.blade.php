@extends('layouts.master')

@section('title')

@stop

@section('style')
	<link rel="stylesheet" type="text/css" href="/css/friends.css">
@stop

@section('content')
<div class="container-fluid">
   <div class="row-fluid">
      <div class="col-sm-3 col-md-2 sidebar">
         @if(Session::has('error_message'))
               <div class="alert alert-danger" role="alert">
                 {{Session::get('error_message')}}
               </div>
            @endif
         <h1>Search users</h1>      
         <div class="nav nav-sidebar" >
            {{Form::open(['action' => 'FriendsController@addFriend', 'method' => 'POST'])}}
            <div class="form-group">
               <div class="col-sm-offset-1 col-sm-10">
               {{ Form::email('email', null, [ 'placeholder' => 'Add with Email', 
               'class' => 'form-control', 'required']) }}
               </div>	
            </div>	
            <div class="form-group">
               <div class="col-sm-offset-8 col-sm-10">
               {{ Form:: submit('Add') }}
               </div>
            </div>
            {{Form::close()}}
         </div>
      </div>
      <div class="col-sm-3 col-md-2 sidebar" style="margin-top:220px">
         <div class="list-group ">
            @foreach($friendInfo as $friend)
               <a href="#" class="list-group-item">
                  <img src="{{$friend->profile_pic}}" class="img-thumbnail" alt="Cinque Terre" width="50" height="50">
                  {{$friend->name}}<br>{{$friend->email}}
               </a>
               <br>
            @endforeach
         </div>
      </div>
      <div class="col-sm-9 col-sm-offset-3 col-md-10 col-md-offset-2 main">
         <a class="btn btn-default pull-right" href="/feed">Go Home</a>
         <h1 class="page-header">Friends</h1>
         <div class="list-group friendsList ">
            @foreach($friends as $friend) 
               <a href="#" class="list-group-item ">
                  <img src="{{$friend->profile_pic}}" class="img-thumbnail" alt="Cinque Terre" width="100" height="100">
                  {{$friend->name}}
                  <div style="text-align:right" >Friends since {{$friend->created_at->diffForHumans()}}</div>
               </a>
               <hr>
            @endforeach
         </div>
      </div>
   </div>
</div>
@stop