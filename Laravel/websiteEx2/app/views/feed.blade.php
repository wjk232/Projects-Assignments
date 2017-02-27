@extends('layouts.master')

@section('title')
  <title>FB | My Feed</title>
@stop

@section('style')
  <link rel="stylesheet" type="text/css" href="/css/feed.css">
@stop

@section('content')
<nav class="navbar navbar-default">
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <a class="navbar-brand" href="/feed">My FB</a>
    </div>

    <!-- Collect the nav links, forms, and other content for toggling -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">

      <ul class="nav navbar-nav navbar-right">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">{{$user->name}} <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="/logout">Logout</a></li>
          </ul>
        </li>
      </ul>
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>		

<div class="container">
  <div class="row-fluid">
      <div class="col-md-4">
          <div class="profile">
              <img width="200" height="180" src="{{$user->profile_pic}}">
              <h1>{{$user->name}}</h1>
              <p>{{$user->gender}}</p>
              <p><a href="/friends">Friends List</a></p>
          </div>
      </div>
      <div class="col-md-8">
          <div class="post-form">
            <h4>Create New Post</h4>
            {{Form::open(['action' => 'PostController@createPost', 'method'=> 'POST', 'class' => "form"])}}
              <div class="form-group">
                <textarea name="content"></textarea>
              </div>
              <div class="form-group">
                {{Form::submit('Post',['class' => 'btn btn-primary'])}}
              </div>
            {{Form::close()}}
          </div>
          <div class="posts">
              @foreach($posts as $post)
                <div class="post">
                  <p>{{$post->fname}} posted on {{$post->updated_at}}</p>
                  <blockquote>{{$post->content}}</blockquote>
                  
                  @foreach($comments[$post->id] as $comment)
                    <div class="comment">
                      <p>{{$comment->fname}} commented on {{$comment->updated_at}}</p>
                      <p>{{$comment->comment}}</p>
                    </div>
                  @endforeach
                  <div class="comment-form">
                        {{Form::open(['action' => 'CommentsController@createComment', 'class' => 'form', 'method'=> 'POST'])}}
                          <div class="form-group">
                            <textarea name="comment"></textarea>
                          </div>
                          {{Form::hidden('PID', $post->id)}}
                          <div class="form-group">
                            {{Form::submit('Comment',['class' => 'btn btn-primary'])}}
                          </div>  
                        {{Form::close()}}
                    </div>
                </div>
              @endforeach
          </div>
      </div>
  </div>
</div>
@stop